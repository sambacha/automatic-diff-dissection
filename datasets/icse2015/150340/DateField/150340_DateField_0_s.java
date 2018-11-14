 package org.apache.lucene.document;
 
 /**
  * Copyright 2004 The Apache Software Foundation
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 import java.util.Date;
 
 /**
  * Provides support for converting dates to strings and vice-versa.
 * The strings are structured so that lexicographic sorting orders by date.
 * This makes them suitable for use as field values and search terms.
  * <P>
 * Note: currenly dates before 1970 cannot be used, and therefore cannot be
 * indexed.
  */
 public class DateField {
   private DateField() {}
 
   // make date strings long enough to last a millenium
   private static int DATE_LEN = Long.toString(1000L*365*24*60*60*1000,
 					       Character.MAX_RADIX).length();
 
   public static String MIN_DATE_STRING() {
     return timeToString(0);
   }
 
   public static String MAX_DATE_STRING() {
     char[] buffer = new char[DATE_LEN];
     char c = Character.forDigit(Character.MAX_RADIX-1, Character.MAX_RADIX);
     for (int i = 0 ; i < DATE_LEN; i++)
       buffer[i] = c;
     return new String(buffer);
   }
 
   /**
    * Converts a Date to a string suitable for indexing.
   * This method will throw a RuntimeException if the date specified in the
   * method argument is before 1970.
    */
   public static String dateToString(Date date) {
     return timeToString(date.getTime());
   }
   /**
    * Converts a millisecond time to a string suitable for indexing.
   * This method will throw a RuntimeException if the time specified in the
   * method argument is negative, that is, before 1970.
    */
   public static String timeToString(long time) {
     if (time < 0)
       throw new RuntimeException("time too early");
 
     String s = Long.toString(time, Character.MAX_RADIX);
 
     if (s.length() > DATE_LEN)
       throw new RuntimeException("time too late");
 
     // Pad with leading zeros
     if (s.length() < DATE_LEN) {
       StringBuffer sb = new StringBuffer(s);
       while (sb.length() < DATE_LEN)
         sb.insert(0, 0);
       s = sb.toString();
     }
 
     return s;
   }
 
   /** Converts a string-encoded date into a millisecond time. */
   public static long stringToTime(String s) {
     return Long.parseLong(s, Character.MAX_RADIX);
   }
   /** Converts a string-encoded date into a Date object. */
   public static Date stringToDate(String s) {
     return new Date(stringToTime(s));
   }
 }
