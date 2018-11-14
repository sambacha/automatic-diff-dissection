 /**
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 package org.apache.solr.search.function;
 
 import org.apache.lucene.index.IndexReader;
 import org.apache.solr.search.function.DocValues;
 import org.apache.lucene.search.FieldCache;
 
 import java.io.IOException;
 import java.util.Map;
 
 /**
  * Obtains int field values from the {@link org.apache.lucene.search.FieldCache}
  * using <code>getInts()</code>
  * and makes those values available as other numeric types, casting as needed. *
  * @version $Id$
  */
 
 public class IntFieldSource extends FieldCacheSource {
   FieldCache.IntParser parser;
 
   public IntFieldSource(String field) {
     this(field, null);
   }
 
   public IntFieldSource(String field, FieldCache.IntParser parser) {
     super(field);
     this.parser = parser;
   }
 
   @Override
   public String description() {
     return "int(" + field + ')';
   }
 
 
   @Override
   public DocValues getValues(Map context, IndexReader reader) throws IOException {
     final int[] arr = (parser==null) ?
             cache.getInts(reader, field) :
             cache.getInts(reader, field, parser);
     return new DocValues() {
       @Override
       public float floatVal(int doc) {
         return (float)arr[doc];
       }
 
       @Override
       public int intVal(int doc) {
         return arr[doc];
       }
 
       @Override
       public long longVal(int doc) {
         return (long)arr[doc];
       }
 
       @Override
       public double doubleVal(int doc) {
         return (double)arr[doc];
       }
 
       @Override
       public String strVal(int doc) {
         return Float.toString(arr[doc]);
       }
 
       @Override
       public String toString(int doc) {
         return description() + '=' + intVal(doc);
       }
 
       @Override
       public ValueSourceScorer getRangeScorer(IndexReader reader, String lowerVal, String upperVal, boolean includeLower, boolean includeUpper) {
         int lower,upper;
 
         // instead of using separate comparison functions, adjust the endpoints.
 
         if (lowerVal==null) {
           lower = Integer.MIN_VALUE;
         } else {
           lower = Integer.parseInt(lowerVal);
           if (!includeLower && lower < Integer.MAX_VALUE) lower++;
         }
 
          if (upperVal==null) {
           upper = Integer.MAX_VALUE;
         } else {
           upper = Integer.parseInt(upperVal);
           if (!includeUpper && upper > Integer.MIN_VALUE) upper--;
         }
 
         final int ll = lower;
         final int uu = upper;
 
         return new ValueSourceScorer(reader, this) {
           @Override
           public boolean matchesValue(int doc) {
             int val = arr[doc];
             // only check for deleted if it's the default value
             // if (val==0 && reader.isDeleted(doc)) return false;
             return val >= ll && val <= uu;
           }
         };
       }
     };
   }
 
   @Override
   public boolean equals(Object o) {
     if (o.getClass() !=  IntFieldSource.class) return false;
     IntFieldSource other = (IntFieldSource)o;
     return super.equals(other)
           && (this.parser==null ? other.parser==null :
              this.parser.getClass() == other.parser.getClass());
   }
 
   @Override
   public int hashCode() {
     int h = parser==null ? Integer.class.hashCode() : parser.getClass().hashCode();
     h += super.hashCode();
     return h;
  }
 
 }
