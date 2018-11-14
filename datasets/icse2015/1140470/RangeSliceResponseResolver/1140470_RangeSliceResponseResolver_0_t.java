 /**
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 package org.apache.cassandra.service;
 
 import java.io.IOException;
 import java.net.InetAddress;
 import java.util.*;
 import java.util.concurrent.LinkedBlockingQueue;
 
 import com.google.common.collect.AbstractIterator;
 import org.apache.commons.collections.iterators.CollatingIterator;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.apache.cassandra.db.ColumnFamily;
 import org.apache.cassandra.db.DecoratedKey;
 import org.apache.cassandra.db.RangeSliceReply;
 import org.apache.cassandra.db.Row;
 import org.apache.cassandra.net.Message;
 import org.apache.cassandra.utils.Pair;
 import org.apache.cassandra.utils.ReducingIterator;
 
 /**
  * Turns RangeSliceReply objects into row (string -> CF) maps, resolving
  * to the most recent ColumnFamily and setting up read repairs as necessary.
  */
 public class RangeSliceResponseResolver implements IResponseResolver<Iterable<Row>>
 {
     private static final Logger logger_ = LoggerFactory.getLogger(RangeSliceResponseResolver.class);
     private final String table;
     private final List<InetAddress> sources;
     protected final Collection<Message> responses = new LinkedBlockingQueue<Message>();;
 
     public RangeSliceResponseResolver(String table, List<InetAddress> sources)
     {
         this.sources = sources;
         this.table = table;
     }
 
     public List<Row> getData() throws IOException
     {
         Message response = responses.iterator().next();
         RangeSliceReply reply = RangeSliceReply.read(response.getMessageBody(), response.getVersion());
         return reply.rows;
     }
 
     // Note: this would deserialize the response a 2nd time if getData was called first.
     // (this is not currently an issue since we don't do read repair for range queries.)
     public Iterable<Row> resolve() throws IOException
     {
         CollatingIterator collator = new CollatingIterator(new Comparator<Pair<Row,InetAddress>>()
         {
             public int compare(Pair<Row,InetAddress> o1, Pair<Row,InetAddress> o2)
             {
                 return o1.left.key.compareTo(o2.left.key);
             }
         });
 
         int n = 0;
         for (Message response : responses)
         {
             RangeSliceReply reply = RangeSliceReply.read(response.getMessageBody(), response.getVersion());
             n = Math.max(n, reply.rows.size());
             collator.addIterator(new RowIterator(reply.rows.iterator(), response.getFrom()));
         }
 
         // for each row, compute the combination of all different versions seen, and repair incomplete versions
         return new ReducingIterator<Pair<Row,InetAddress>, Row>(collator)
         {
             List<ColumnFamily> versions = new ArrayList<ColumnFamily>(sources.size());
             List<InetAddress> versionSources = new ArrayList<InetAddress>(sources.size());
             DecoratedKey key;
 
             @Override
             protected boolean isEqual(Pair<Row, InetAddress> o1, Pair<Row, InetAddress> o2)
             {
                 return o1.left.key.equals(o2.left.key);
             }
 
             public void reduce(Pair<Row,InetAddress> current)
             {
                 key = current.left.key;
                 versions.add(current.left.cf);
                 versionSources.add(current.right);
             }
 
             protected Row getReduced()
             {
                 ColumnFamily resolved = versions.size() > 1
                                       ? RowRepairResolver.resolveSuperset(versions)
                                       : versions.get(0);
                 if (versions.size() < sources.size())
                 {
                     // add placeholder rows for sources that didn't have any data, so maybeScheduleRepairs sees them
                     for (InetAddress source : sources)
                     {
                         if (!versionSources.contains(source))
                         {
                             versions.add(null);
                             versionSources.add(source);
                         }
                     }
                 }
                // resolved can be null even if versions doesn't have all nulls because of the call to removeDeleted in resolveSuperSet
                if (resolved != null)
                 RowRepairResolver.maybeScheduleRepairs(resolved, table, key, versions, versionSources);
                 versions.clear();
                 versionSources.clear();
                 return new Row(key, resolved);
             }
         };
     }
 
     public void preprocess(Message message)
     {
         responses.add(message);
     }
 
     public boolean isDataPresent()
     {
         return !responses.isEmpty();
     }
 
     private static class RowIterator extends AbstractIterator<Pair<Row,InetAddress>>
     {
         private final Iterator<Row> iter;
         private final InetAddress source;
 
         private RowIterator(Iterator<Row> iter, InetAddress source)
         {
             this.iter = iter;
             this.source = source;
         }
 
         protected Pair<Row,InetAddress> computeNext()
         {
             return iter.hasNext() ? new Pair<Row, InetAddress>(iter.next(), source) : endOfData();
         }
     }
 
     public Iterable<Message> getMessages()
     {
         return responses;
     }
 }
