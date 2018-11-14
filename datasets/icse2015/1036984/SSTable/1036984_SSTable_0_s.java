 /**
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  * 
  *   http://www.apache.org/licenses/LICENSE-2.0
  * 
  * Unless required by applicable law or agreed to in writing,
  * software distributed under the License is distributed on an
  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  * KIND, either express or implied.  See the License for the
  * specific language governing permissions and limitations
  * under the License.
  */
 
 package org.apache.cassandra.io.sstable;
 
 import java.io.File;
 import java.io.FilenameFilter;
 import java.io.IOError;
 import java.io.IOException;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.apache.cassandra.config.CFMetaData;
 import org.apache.cassandra.dht.IPartitioner;
 import org.apache.cassandra.io.util.FileUtils;
 import org.apache.cassandra.io.util.BufferedRandomAccessFile;
 import org.apache.cassandra.utils.EstimatedHistogram;
 import org.apache.cassandra.utils.FBUtilities;
 import org.apache.cassandra.utils.Pair;
 
 /**
  * This class is built on top of the SequenceFile. It stores
  * data on disk in sorted fashion. However the sorting is upto
  * the application. This class expects keys to be handed to it
  * in sorted order.
  *
  * A separate index file is maintained as well, containing the
  * SSTable keys and the offset into the SSTable at which they are found.
  * Every 1/indexInterval key is read into memory when the SSTable is opened.
  *
  * Finally, a bloom filter file is also kept for the keys in each SSTable.
  */
 public abstract class SSTable
 {
     static final Logger logger = LoggerFactory.getLogger(SSTable.class);
 
     // TODO: replace with 'Component' objects
     public static final String COMPONENT_DATA = Component.Type.DATA.repr;
     public static final String COMPONENT_INDEX = Component.Type.PRIMARY_INDEX.repr;
     public static final String COMPONENT_FILTER = Component.Type.FILTER.repr;
     public static final String COMPONENT_STATS = Component.Type.STATS.repr;
 
    public static final String COMPONENT_COMPACTED = Component.Type.COMPACTED_MARKER.repr;

     public static final String TEMPFILE_MARKER = "tmp";
 
     public final Descriptor descriptor;
    public final Set<Component> components;
     public final CFMetaData metadata;
     public final IPartitioner partitioner;
 
     protected final EstimatedHistogram estimatedRowSize;
     protected final EstimatedHistogram estimatedColumnCount;
 
     protected SSTable(Descriptor descriptor, CFMetaData metadata, IPartitioner partitioner)
     {
         this(descriptor, new HashSet<Component>(), metadata, partitioner);
     }
 
     protected SSTable(Descriptor descriptor, Set<Component> components, CFMetaData metadata, IPartitioner partitioner)
     {
         this(descriptor, components, metadata, partitioner, defaultRowHistogram(), defaultColumnHistogram());
     }
 
     static EstimatedHistogram defaultColumnHistogram()
     {
         return new EstimatedHistogram(114);
     }
 
     static EstimatedHistogram defaultRowHistogram()
     {
         return new EstimatedHistogram(150);
     }
 
     protected SSTable(Descriptor descriptor, Set<Component> components, CFMetaData metadata, IPartitioner partitioner, EstimatedHistogram rowSizes, EstimatedHistogram columnCounts)
     {
         this.descriptor = descriptor;
        this.components = components;
         this.metadata = metadata;
         this.partitioner = partitioner;
         estimatedRowSize = rowSizes;
         estimatedColumnCount = columnCounts;
     }
 
     public EstimatedHistogram getEstimatedRowSize()
     {
         return estimatedRowSize;
     }
 
     public EstimatedHistogram getEstimatedColumnCount()
     {
         return estimatedColumnCount;
     }
 
     /**
      * We use a ReferenceQueue to manage deleting files that have been compacted
      * and for which no more SSTable references exist.  But this is not guaranteed
      * to run for each such file because of the semantics of the JVM gc.  So,
      * we write a marker to `compactedFilename` when a file is compacted;
      * if such a marker exists on startup, the file should be removed.
      *
      * This method will also remove SSTables that are marked as temporary.
      *
      * @return true if the file was deleted
      */
    public static boolean conditionalDelete(Descriptor desc, Set<Component> components)
     {
        if (!components.contains(Component.COMPACTED_MARKER) && !desc.temporary)
            // not compacted or temporary
            return false;
         try
         {
             // remove the DATA component first if it exists
             if (components.contains(Component.DATA))
                 FileUtils.deleteWithConfirm(desc.filenameFor(Component.DATA));
             for (Component component : components)
             {
                 if (component.equals(Component.DATA) || component.equals(Component.COMPACTED_MARKER))
                     continue;
                 FileUtils.deleteWithConfirm(desc.filenameFor(component));
             }
             // remove the COMPACTED_MARKER component last if it exists
            if (components.contains(Component.COMPACTED_MARKER))
                FileUtils.deleteWithConfirm(desc.filenameFor(Component.COMPACTED_MARKER));
         }
         catch (IOException e)
         {
             throw new IOError(e);
         }
         logger.info("Deleted " + desc);
         return true;
     }
 
     public String getFilename()
     {
         return descriptor.filenameFor(COMPONENT_DATA);
     }
 
     public String getColumnFamilyName()
     {
         return descriptor.cfname;
     }
 
     public String getTableName()
     {
         return descriptor.ksname;
     }
 
     /**
      * @return A Descriptor,Component pair, or null if not a valid sstable component.
      */
     public static Pair<Descriptor,Component> tryComponentFromFilename(File dir, String name)
     {
         try
         {
             return Component.fromFilename(dir, name);
         }
         catch (Exception e)
         {
             if (!"snapshots".equals(name))
                 logger.warn("Invalid file '{}' in data directory {}.", name, dir);
             return null;
         }
     }
 
     /**
      * Discovers existing components for the descriptor. Slow: only intended for use outside the critical path.
      */
     static Set<Component> componentsFor(final Descriptor desc) throws IOException
     {
         final Set<Component> components = new HashSet<Component>();
         desc.directory.list(new FilenameFilter()
         {
             public boolean accept(File dir, String name)
             {
                 Pair<Descriptor,Component> component = tryComponentFromFilename(dir, name);
                 if (component != null && component.left.equals(desc))
                     components.add(component.right);
                 return false;
             }
         });
         return components;
     }
 
     /** @return An estimate of the number of keys contained in the given data file. */
     static long estimateRowsFromData(Descriptor desc, BufferedRandomAccessFile dfile) throws IOException
     {
         // collect sizes for the first 1000 keys, or first 100 megabytes of data
         final int SAMPLES_CAP = 1000, BYTES_CAP = (int)Math.min(100000000, dfile.length());
         int keys = 0;
         long dataPosition = 0;
         while (dataPosition < BYTES_CAP && keys < SAMPLES_CAP)
         {
             dfile.seek(dataPosition);
             FBUtilities.skipShortByteArray(dfile);
             long dataSize = SSTableReader.readRowSize(dfile, desc);
             dataPosition = dfile.getFilePointer() + dataSize;
             keys++;
         }
         dfile.seek(0);
         return dfile.length() / (dataPosition / keys);
     }
 
     /** @return An estimate of the number of keys contained in the given index file. */
     static long estimateRowsFromIndex(BufferedRandomAccessFile ifile) throws IOException
     {
         // collect sizes for the first 10000 keys, or first 10 megabytes of data
         final int SAMPLES_CAP = 10000, BYTES_CAP = (int)Math.min(10000000, ifile.length());
         int keys = 0;
         while (ifile.getFilePointer() < BYTES_CAP && keys < SAMPLES_CAP)
         {
             FBUtilities.skipShortByteArray(ifile);
             ifile.skipBytes(8);
             keys++;
         }
         long estimatedRows = ifile.length() / (ifile.getFilePointer() / keys);
         ifile.seek(0);
         return estimatedRows;
     }
 
     public static long getTotalBytes(Iterable<SSTableReader> sstables)
     {
         long sum = 0;
         for (SSTableReader sstable : sstables)
         {
             sum += sstable.length();
         }
         return sum;
     }
 
     public long bytesOnDisk()
     {
         long bytes = 0;
         for (Component component : components)
         {
             bytes += new File(descriptor.filenameFor(component)).length();
         }
         return bytes;
     }
 
     @Override
     public String toString()
     {
         return getClass().getName() + "(" +
                "path='" + getFilename() + '\'' +
                ')';
     }
 }
