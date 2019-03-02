 package org.apache.lucene.analysis.el;
 
 import org.apache.lucene.analysis.util.CharArraySet;
 import org.apache.lucene.util.Version;
 
 import java.util.Arrays;
 
 /*
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
 
 /**
  * A stemmer for Greek words, according to: <i>Development of a Stemmer for the
  * Greek Language.</i> Georgios Ntais
  * <p>
  * NOTE: Input is expected to be casefolded for Greek (including folding of final
  * sigma to sigma), and with diacritics removed. This can be achieved with 
  * either {@link GreekLowerCaseFilter} or ICUFoldingFilter.
  * @lucene.experimental
  */
 public class GreekStemmer {
   public int stem(char s[], int len) {
     if (len < 4) // too short
       return len;
     
     final int origLen = len;
     // "short rules": if it hits one of these, it skips the "long list"
     len = rule0(s, len);
     len = rule1(s, len);
     len = rule2(s, len);
     len = rule3(s, len);
     len = rule4(s, len);
     len = rule5(s, len);
     len = rule6(s, len);
     len = rule7(s, len);
     len = rule8(s, len);
     len = rule9(s, len);
     len = rule10(s, len);
     len = rule11(s, len);
     len = rule12(s, len);
     len = rule13(s, len);
     len = rule14(s, len);
     len = rule15(s, len);
     len = rule16(s, len);
     len = rule17(s, len);
     len = rule18(s, len);
     len = rule19(s, len);
     len = rule20(s, len);
     // "long list"
     if (len == origLen)
       len = rule21(s, len);
     
     return rule22(s, len);
   }
 
   private int rule0(char s[], int len) {
     if (len > 9 && (endsWith(s, len, "ÎºÎ±Î¸ÎµÏÏÏÏÎ¿Ï")
         || endsWith(s, len, "ÎºÎ±Î¸ÎµÏÏÏÏÏÎ½")))
       return len - 4;
     
     if (len > 8 && (endsWith(s, len, "Î³ÎµÎ³Î¿Î½Î¿ÏÎ¿Ï")
         || endsWith(s, len, "Î³ÎµÎ³Î¿Î½Î¿ÏÏÎ½")))
       return len - 4;
     
     if (len > 8 && endsWith(s, len, "ÎºÎ±Î¸ÎµÏÏÏÏÎ±"))
       return len - 3;
     
     if (len > 7 && (endsWith(s, len, "ÏÎ±ÏÎ¿Î³Î¹Î¿Ï")
         || endsWith(s, len, "ÏÎ±ÏÎ¿Î³Î¹ÏÎ½")))
       return len - 4;
     
     if (len > 7 && endsWith(s, len, "Î³ÎµÎ³Î¿Î½Î¿ÏÎ±"))
       return len - 3;
     
     if (len > 7 && endsWith(s, len, "ÎºÎ±Î¸ÎµÏÏÏÏ"))
       return len - 2;
     
     if (len > 6 && (endsWith(s, len, "ÏÎºÎ±Î³Î¹Î¿Ï"))
         || endsWith(s, len, "ÏÎºÎ±Î³Î¹ÏÎ½")
         || endsWith(s, len, "Î¿Î»Î¿Î³Î¹Î¿Ï")
         || endsWith(s, len, "Î¿Î»Î¿Î³Î¹ÏÎ½")
         || endsWith(s, len, "ÎºÏÎµÎ±ÏÎ¿Ï")
         || endsWith(s, len, "ÎºÏÎµÎ±ÏÏÎ½")
         || endsWith(s, len, "ÏÎµÏÎ±ÏÎ¿Ï")
         || endsWith(s, len, "ÏÎµÏÎ±ÏÏÎ½")
         || endsWith(s, len, "ÏÎµÏÎ±ÏÎ¿Ï")
         || endsWith(s, len, "ÏÎµÏÎ±ÏÏÎ½"))
       return len - 4;
     
     if (len > 6 && endsWith(s, len, "ÏÎ±ÏÎ¿Î³Î¹Î±"))
       return len - 3;
     
     if (len > 6 && endsWith(s, len, "Î³ÎµÎ³Î¿Î½Î¿Ï"))
       return len - 2;
     
     if (len > 5 && (endsWith(s, len, "ÏÎ±Î³Î¹Î¿Ï")
         || endsWith(s, len, "ÏÎ±Î³Î¹ÏÎ½")
         || endsWith(s, len, "ÏÎ¿Î³Î¹Î¿Ï")
         || endsWith(s, len, "ÏÎ¿Î³Î¹ÏÎ½")))
       return len - 4;
     
     if (len > 5 && (endsWith(s, len, "ÏÎºÎ±Î³Î¹Î±")
         || endsWith(s, len, "Î¿Î»Î¿Î³Î¹Î±")
         || endsWith(s, len, "ÎºÏÎµÎ±ÏÎ±")
         || endsWith(s, len, "ÏÎµÏÎ±ÏÎ±")
         || endsWith(s, len, "ÏÎµÏÎ±ÏÎ±")))
       return len - 3;
     
     if (len > 4 && (endsWith(s, len, "ÏÎ±Î³Î¹Î±")
         || endsWith(s, len, "ÏÎ¿Î³Î¹Î±")
         || endsWith(s, len, "ÏÏÏÎ¿Ï")
         || endsWith(s, len, "ÏÏÏÏÎ½")))
       return len - 3;
     
     if (len > 4 && (endsWith(s, len, "ÎºÏÎµÎ±Ï")
         || endsWith(s, len, "ÏÎµÏÎ±Ï")
         || endsWith(s, len, "ÏÎµÏÎ±Ï")))
       return len - 2;
     
     if (len > 3 && endsWith(s, len, "ÏÏÏÎ±"))
       return len - 2;
     
     if (len > 2 && endsWith(s, len, "ÏÏÏ"))
       return len - 1;
     
     return len;
   }
 
   private int rule1(char s[], int len) {
     if (len > 4 && (endsWith(s, len, "Î±Î´ÎµÏ") || endsWith(s, len, "Î±Î´ÏÎ½"))) {
       len -= 4;
       if (!(endsWith(s, len, "Î¿Îº") ||
           endsWith(s, len, "Î¼Î±Î¼") ||
           endsWith(s, len, "Î¼Î±Î½") ||
           endsWith(s, len, "Î¼ÏÎ±Î¼Ï") ||
           endsWith(s, len, "ÏÎ±ÏÎµÏ") ||
           endsWith(s, len, "Î³Î¹Î±Î³Î¹") ||
           endsWith(s, len, "Î½ÏÎ±Î½Ï") ||
           endsWith(s, len, "ÎºÏÏ") ||
           endsWith(s, len, "Î¸ÎµÎ¹") ||
           endsWith(s, len, "ÏÎµÎ¸ÎµÏ")))
         len += 2; // add back -Î±Î´
     }
     return len;
   }
   
   private int rule2(char s[], int len) {
     if (len > 4 && (endsWith(s, len, "ÎµÎ´ÎµÏ") || endsWith(s, len, "ÎµÎ´ÏÎ½"))) {
       len -= 4;
       if (endsWith(s, len, "Î¿Ï") ||
           endsWith(s, len, "Î¹Ï") ||
           endsWith(s, len, "ÎµÎ¼Ï") ||
           endsWith(s, len, "ÏÏ") ||
           endsWith(s, len, "Î³Î·Ï") ||
           endsWith(s, len, "Î´Î±Ï") ||
           endsWith(s, len, "ÎºÏÎ±ÏÏ") ||
           endsWith(s, len, "Î¼Î¹Î»"))
         len += 2; // add back -ÎµÎ´
     }
     return len;
   }
   
   private int rule3(char s[], int len) {
     if (len > 5 && (endsWith(s, len, "Î¿ÏÎ´ÎµÏ") || endsWith(s, len, "Î¿ÏÎ´ÏÎ½"))) {
       len -= 5;
       if (endsWith(s, len, "Î±ÏÎº") ||
           endsWith(s, len, "ÎºÎ±Î»Î¹Î±Îº") ||
           endsWith(s, len, "ÏÎµÏÎ±Î»") ||
           endsWith(s, len, "Î»Î¹Ï") ||
           endsWith(s, len, "ÏÎ»ÎµÎ¾") ||
           endsWith(s, len, "ÏÎº") ||
           endsWith(s, len, "Ï") ||
           endsWith(s, len, "ÏÎ»") ||
           endsWith(s, len, "ÏÏ") ||
           endsWith(s, len, "Î²ÎµÎ»") ||
           endsWith(s, len, "Î»Î¿ÏÎ»") ||
           endsWith(s, len, "ÏÎ½") ||
           endsWith(s, len, "ÏÏ") ||
           endsWith(s, len, "ÏÏÎ±Î³") ||
           endsWith(s, len, "ÏÎµ"))
         len += 3; // add back -Î¿ÏÎ´
     }
     return len;
   }
   
  private static final CharArraySet exc4 = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("Î¸", "Î´", "ÎµÎ»", "Î³Î±Î»", "Î½", "Ï", "Î¹Î´", "ÏÎ±Ï"),
       false);
   
   private int rule4(char s[], int len) {   
     if (len > 3 && (endsWith(s, len, "ÎµÏÏ") || endsWith(s, len, "ÎµÏÎ½"))) {
       len -= 3;
       if (exc4.contains(s, 0, len))
         len++; // add back -Îµ
     }
     return len;
   }
   
   private int rule5(char s[], int len) {
     if (len > 2 && endsWith(s, len, "Î¹Î±")) {
       len -= 2;
       if (endsWithVowel(s, len))
         len++; // add back -Î¹
     } else if (len > 3 && (endsWith(s, len, "Î¹Î¿Ï") || endsWith(s, len, "Î¹ÏÎ½"))) {
       len -= 3;
       if (endsWithVowel(s, len))
         len++; // add back -Î¹
     }
     return len;
   }
 
  private static final CharArraySet exc6 = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("Î±Î»", "Î±Î´", "ÎµÎ½Î´", "Î±Î¼Î±Î½", "Î±Î¼Î¼Î¿ÏÎ±Î»", "Î·Î¸", "Î±Î½Î·Î¸",
           "Î±Î½ÏÎ¹Î´", "ÏÏÏ", "Î²ÏÏÎ¼", "Î³ÎµÏ", "ÎµÎ¾ÏÎ´", "ÎºÎ±Î»Ï", "ÎºÎ±Î»Î»Î¹Î½", "ÎºÎ±ÏÎ±Î´",
           "Î¼Î¿ÏÎ»", "Î¼ÏÎ±Î½", "Î¼ÏÎ±Î³Î¹Î±Ï", "Î¼ÏÎ¿Î»", "Î¼ÏÎ¿Ï", "Î½Î¹Ï", "Î¾Î¹Îº", "ÏÏÎ½Î¿Î¼Î·Î»",
           "ÏÎµÏÏ", "ÏÎ¹ÏÏ", "ÏÎ¹ÎºÎ±Î½Ï", "ÏÎ»Î¹Î±ÏÏ", "ÏÎ¿ÏÏÎµÎ»Î½", "ÏÏÏÏÎ¿Î´", "ÏÎµÏÏ",
           "ÏÏÎ½Î±Î´", "ÏÏÎ±Î¼", "ÏÏÎ¿Î´", "ÏÎ¹Î»Î¿Î½", "ÏÏÎ»Î¿Î´", "ÏÎ±Ï"), 
        false);
 
   private int rule6(char s[], int len) {
     boolean removed = false;
     if (len > 3 && (endsWith(s, len, "Î¹ÎºÎ±") || endsWith(s, len, "Î¹ÎºÎ¿"))) {
       len -= 3;
       removed = true;
     } else if (len > 4 && (endsWith(s, len, "Î¹ÎºÎ¿Ï") || endsWith(s, len, "Î¹ÎºÏÎ½"))) {
       len -= 4;
       removed = true;
     }
     
     if (removed) {
       if (endsWithVowel(s, len) || exc6.contains(s, 0, len))
         len += 2; // add back -Î¹Îº
     }
     return len;
   }
   
  private static final CharArraySet exc7 = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("Î±Î½Î±Ï", "Î±ÏÎ¿Î¸", "Î±ÏÎ¿Îº", "Î±ÏÎ¿ÏÏ", "Î²Î¿ÏÎ²", "Î¾ÎµÎ¸", "Î¿ÏÎ»",
           "ÏÎµÎ¸", "ÏÎ¹ÎºÏ", "ÏÎ¿Ï", "ÏÎ¹Ï", "Ï"), 
       false);
   
   private int rule7(char s[], int len) {
     if (len == 5 && endsWith(s, len, "Î±Î³Î±Î¼Îµ"))
       return len - 1;
     
     if (len > 7 && endsWith(s, len, "Î·Î¸Î·ÎºÎ±Î¼Îµ"))
       len -= 7;
     else if (len > 6 && endsWith(s, len, "Î¿ÏÏÎ±Î¼Îµ"))
       len -= 6;
     else if (len > 5 && (endsWith(s, len, "Î±Î³Î±Î¼Îµ") ||
              endsWith(s, len, "Î·ÏÎ±Î¼Îµ") ||
              endsWith(s, len, "Î·ÎºÎ±Î¼Îµ")))
       len -= 5;
     
     if (len > 3 && endsWith(s, len, "Î±Î¼Îµ")) {
       len -= 3;
       if (exc7.contains(s, 0, len))
         len += 2; // add back -Î±Î¼
     }
 
     return len;
   }
 
  private static final CharArraySet exc8a = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("ÏÏ", "ÏÏ"),
       false);
 
  private static final CharArraySet exc8b = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("Î²ÎµÏÎµÏ", "Î²Î¿ÏÎ»Îº", "Î²ÏÎ±ÏÎ¼", "Î³", "Î´ÏÎ±Î´Î¿ÏÎ¼", "Î¸", "ÎºÎ±Î»ÏÎ¿ÏÎ¶",
           "ÎºÎ±ÏÏÎµÎ»", "ÎºÎ¿ÏÎ¼Î¿Ï", "Î»Î±Î¿ÏÎ»", "Î¼ÏÎ±Î¼ÎµÎ¸", "Î¼", "Î¼Î¿ÏÏÎ¿ÏÎ»Î¼", "Î½", "Î¿ÏÎ»",
           "Ï", "ÏÎµÎ»ÎµÎº", "ÏÎ»", "ÏÎ¿Î»Î¹Ï", "ÏÎ¿ÏÏÎ¿Î»", "ÏÎ±ÏÎ±ÎºÎ±ÏÏ", "ÏÎ¿ÏÎ»Ï",
           "ÏÏÎ±ÏÎ»Î±Ï", "Î¿ÏÏ", "ÏÏÎ¹Î³Î³", "ÏÏÎ¿Ï", "ÏÏÏÎ¿ÏÏÎµÏ", "Ï", "ÏÏÏÎ¿ÏÎ»", "Î±Î³",
           "Î¿ÏÏ", "Î³Î±Î»", "Î³ÎµÏ", "Î´ÎµÎº", "Î´Î¹ÏÎ»", "Î±Î¼ÎµÏÎ¹ÎºÎ±Î½", "Î¿ÏÏ", "ÏÎ¹Î¸",
           "ÏÎ¿ÏÏÎ¹Ï", "Ï", "Î¶ÏÎ½Ï", "Î¹Îº", "ÎºÎ±ÏÏ", "ÎºÎ¿Ï", "Î»Î¹Ï", "Î»Î¿ÏÎ¸Î·Ï", "Î¼Î±Î¹Î½Ï",
           "Î¼ÎµÎ»", "ÏÎ¹Î³", "ÏÏ", "ÏÏÎµÎ³", "ÏÏÎ±Î³", "ÏÏÎ±Î³", "Ï", "ÎµÏ", "Î±Î´Î±Ï",
           "Î±Î¸Î¹Î³Î³", "Î±Î¼Î·Ï", "Î±Î½Î¹Îº", "Î±Î½Î¿ÏÎ³", "Î±ÏÎ·Î³", "Î±ÏÎ¹Î¸", "Î±ÏÏÎ¹Î³Î³", "Î²Î±Ï",
           "Î²Î±ÏÎº", "Î²Î±Î¸ÏÎ³Î±Î»", "Î²Î¹Î¿Î¼Î·Ï", "Î²ÏÎ±ÏÏÎº", "Î´Î¹Î±Ï", "Î´Î¹Î±Ï", "ÎµÎ½Î¿ÏÎ³",
           "Î¸ÏÏ", "ÎºÎ±ÏÎ½Î¿Î²Î¹Î¿Î¼Î·Ï", "ÎºÎ±ÏÎ±Î³Î±Î»", "ÎºÎ»Î¹Î²", "ÎºÎ¿Î¹Î»Î±ÏÏ", "Î»Î¹Î²",
           "Î¼ÎµÎ³Î»Î¿Î²Î¹Î¿Î¼Î·Ï", "Î¼Î¹ÎºÏÎ¿Î²Î¹Î¿Î¼Î·Ï", "Î½ÏÎ±Î²", "Î¾Î·ÏÎ¿ÎºÎ»Î¹Î²", "Î¿Î»Î¹Î³Î¿Î´Î±Î¼",
           "Î¿Î»Î¿Î³Î±Î»", "ÏÎµÎ½ÏÎ±ÏÏ", "ÏÎµÏÎ·Ï", "ÏÎµÏÎ¹ÏÏ", "ÏÎ»Î±Ï", "ÏÎ¿Î»ÏÎ´Î±Ï", "ÏÎ¿Î»ÏÎ¼Î·Ï",
           "ÏÏÎµÏ", "ÏÎ±Î²", "ÏÎµÏ", "ÏÏÎµÏÎ·Ï", "ÏÏÎ¿ÎºÎ¿Ï", "ÏÎ±Î¼Î·Î»Î¿Î´Î±Ï", "ÏÎ·Î»Î¿ÏÎ±Î²"),
       false);
   
   private int rule8(char s[], int len) {
     boolean removed = false;
     
     if (len > 8 && endsWith(s, len, "Î¹Î¿ÏÎ½ÏÎ±Î½Îµ")) {
       len -= 8;
       removed = true;
     } else if (len > 7 && endsWith(s, len, "Î¹Î¿Î½ÏÎ±Î½Îµ") ||
         endsWith(s, len, "Î¿ÏÎ½ÏÎ±Î½Îµ") ||
         endsWith(s, len, "Î·Î¸Î·ÎºÎ±Î½Îµ")) {
       len -= 7;
       removed = true;
     } else if (len > 6 && endsWith(s, len, "Î¹Î¿ÏÎ±Î½Îµ") ||
         endsWith(s, len, "Î¿Î½ÏÎ±Î½Îµ") ||
         endsWith(s, len, "Î¿ÏÏÎ±Î½Îµ")) {
       len -= 6;
       removed = true;
     } else if (len > 5 && endsWith(s, len, "Î±Î³Î±Î½Îµ") ||
         endsWith(s, len, "Î·ÏÎ±Î½Îµ") ||
         endsWith(s, len, "Î¿ÏÎ±Î½Îµ") ||
         endsWith(s, len, "Î·ÎºÎ±Î½Îµ")) {
       len -= 5;
       removed = true;
     }
     
     if (removed && exc8a.contains(s, 0, len)) {
       // add -Î±Î³Î±Î½ (we removed > 4 chars so its safe)
       len += 4;
       s[len - 4] = 'Î±';
       s[len - 3] = 'Î³';
       s[len - 2] = 'Î±';
       s[len - 1] = 'Î½';
     }
     
     if (len > 3 && endsWith(s, len, "Î±Î½Îµ")) {
       len -= 3;
       if (endsWithVowelNoY(s, len) || exc8b.contains(s, 0, len)) {
         len += 2; // add back -Î±Î½
       }
     }
     
     return len;
   }
   
  private static final CharArraySet exc9 = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("Î±Î²Î±Ï", "Î²ÎµÎ½", "ÎµÎ½Î±Ï", "Î±Î²Ï", "Î±Î´", "Î±Î¸", "Î±Î½", "Î±ÏÎ»",
           "Î²Î±ÏÎ¿Î½", "Î½ÏÏ", "ÏÎº", "ÎºÎ¿Ï", "Î¼ÏÎ¿Ï", "Î½Î¹Ï", "ÏÎ±Î³", "ÏÎ±ÏÎ±ÎºÎ±Î»", "ÏÎµÏÏ",
           "ÏÎºÎµÎ»", "ÏÏÏÏ", "ÏÎ¿Îº", "Ï", "Î´", "ÎµÎ¼", "Î¸Î±ÏÏ", "Î¸"), 
       false);
   
   private int rule9(char s[], int len) {
     if (len > 5 && endsWith(s, len, "Î·ÏÎµÏÎµ"))
       len -= 5;
     
     if (len > 3 && endsWith(s, len, "ÎµÏÎµ")) {
       len -= 3;
       if (exc9.contains(s, 0, len) ||
           endsWithVowelNoY(s, len) ||
           endsWith(s, len, "Î¿Î´") ||
           endsWith(s, len, "Î±Î¹Ï") ||
           endsWith(s, len, "ÏÎ¿Ï") ||
           endsWith(s, len, "ÏÎ±Î¸") ||
           endsWith(s, len, "Î´Î¹Î±Î¸") ||
           endsWith(s, len, "ÏÏ") ||
           endsWith(s, len, "ÎµÎ½Î´") ||
           endsWith(s, len, "ÎµÏÏ") ||
           endsWith(s, len, "ÏÎ¹Î¸") ||
           endsWith(s, len, "ÏÏÎµÏÎ¸") ||
           endsWith(s, len, "ÏÎ±Î¸") ||
           endsWith(s, len, "ÎµÎ½Î¸") ||
           endsWith(s, len, "ÏÎ¿Î¸") ||
           endsWith(s, len, "ÏÎ¸") ||
           endsWith(s, len, "ÏÏÏ") ||
           endsWith(s, len, "Î±Î¹Î½") ||
           endsWith(s, len, "ÏÏÎ½Î´") ||
           endsWith(s, len, "ÏÏÎ½") ||
           endsWith(s, len, "ÏÏÎ½Î¸") ||
           endsWith(s, len, "ÏÏÏ") ||
           endsWith(s, len, "ÏÎ¿Î½") ||
           endsWith(s, len, "Î²Ï") ||
           endsWith(s, len, "ÎºÎ±Î¸") ||
           endsWith(s, len, "ÎµÏÎ¸") ||
           endsWith(s, len, "ÎµÎºÎ¸") ||
           endsWith(s, len, "Î½ÎµÏ") ||
           endsWith(s, len, "ÏÎ¿Î½") ||
           endsWith(s, len, "Î±ÏÎº") ||
           endsWith(s, len, "Î²Î±Ï") ||
           endsWith(s, len, "Î²Î¿Î»") ||
           endsWith(s, len, "ÏÏÎµÎ»")) {
         len += 2; // add back -ÎµÏ
       }
     }
     
     return len;
   }
 
   private int rule10(char s[], int len) {
     if (len > 5 && (endsWith(s, len, "Î¿Î½ÏÎ±Ï") || endsWith(s, len, "ÏÎ½ÏÎ±Ï"))) {
       len -= 5;
       if (len == 3 && endsWith(s, len, "Î±ÏÏ")) {
         len += 3; // add back *Î½Ï
         s[len - 3] = 'Î¿';
       }
       if (endsWith(s, len, "ÎºÏÎµ")) {
         len += 3; // add back *Î½Ï
         s[len - 3] = 'Ï';
       }
     }
     
     return len;
   }
   
   private int rule11(char s[], int len) {
     if (len > 6 && endsWith(s, len, "Î¿Î¼Î±ÏÏÎµ")) {
       len -= 6;
       if (len == 2 && endsWith(s, len, "Î¿Î½")) {
         len += 5; // add back -Î¿Î¼Î±ÏÏ
       }
     } else if (len > 7 && endsWith(s, len, "Î¹Î¿Î¼Î±ÏÏÎµ")) {
       len -= 7;
       if (len == 2 && endsWith(s, len, "Î¿Î½")) {
         len += 5;
         s[len - 5] = 'Î¿';
         s[len - 4] = 'Î¼';
         s[len - 3] = 'Î±';
         s[len - 2] = 'Ï';
         s[len - 1] = 'Ï';
       }
     }
     return len;
   }
 
  private static final CharArraySet exc12a = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("Ï", "Î±Ï", "ÏÏÎ¼Ï", "Î±ÏÏÎ¼Ï", "Î±ÎºÎ±ÏÎ±Ï", "Î±Î¼ÎµÏÎ±Î¼Ï"),
       false);
 
  private static final CharArraySet exc12b = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("Î±Î»", "Î±Ï", "ÎµÎºÏÎµÎ»", "Î¶", "Î¼", "Î¾", "ÏÎ±ÏÎ±ÎºÎ±Î»", "Î±Ï", "ÏÏÎ¿", "Î½Î¹Ï"),
       false);
   
   private int rule12(char s[], int len) {
     if (len > 5 && endsWith(s, len, "Î¹ÎµÏÏÎµ")) {
       len -= 5;
       if (exc12a.contains(s, 0, len))   
         len += 4; // add back -Î¹ÎµÏÏ
     }
     
     if (len > 4 && endsWith(s, len, "ÎµÏÏÎµ")) {
       len -= 4;
       if (exc12b.contains(s, 0, len))
         len += 3; // add back -ÎµÏÏ
     }
     
     return len;
   }
   
  private static final CharArraySet exc13 = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("Î´Î¹Î±Î¸", "Î¸", "ÏÎ±ÏÎ±ÎºÎ±ÏÎ±Î¸", "ÏÏÎ¿ÏÎ¸", "ÏÏÎ½Î¸"),
       false);
   
   private int rule13(char s[], int len) {
     if (len > 6 && endsWith(s, len, "Î·Î¸Î·ÎºÎµÏ")) {
       len -= 6;
     } else if (len > 5 && (endsWith(s, len, "Î·Î¸Î·ÎºÎ±") || endsWith(s, len, "Î·Î¸Î·ÎºÎµ"))) {
       len -= 5;
     }
     
     boolean removed = false;
     
     if (len > 4 && endsWith(s, len, "Î·ÎºÎµÏ")) {
       len -= 4;
       removed = true;
     } else if (len > 3 && (endsWith(s, len, "Î·ÎºÎ±") || endsWith(s, len, "Î·ÎºÎµ"))) {
       len -= 3;
       removed = true;
     }
 
     if (removed && (exc13.contains(s, 0, len) 
         || endsWith(s, len, "ÏÎºÏÎ»")
         || endsWith(s, len, "ÏÎºÎ¿ÏÎ»")
         || endsWith(s, len, "Î½Î±ÏÎ¸")
         || endsWith(s, len, "ÏÏ")
         || endsWith(s, len, "Î¿Î¸")
         || endsWith(s, len, "ÏÎ¹Î¸"))) { 
       len += 2; // add back the -Î·Îº
     }
     
     return len;
   }
   
  private static final CharArraySet exc14 = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("ÏÎ±ÏÎ¼Î±Îº", "ÏÎ±Î´", "Î±Î³Îº", "Î±Î½Î±ÏÏ", "Î²ÏÎ¿Î¼", "ÎµÎºÎ»Î¹Ï", "Î»Î±Î¼ÏÎ¹Î´",
           "Î»ÎµÏ", "Î¼", "ÏÎ±Ï", "Ï", "Î»", "Î¼ÎµÎ´", "Î¼ÎµÏÎ±Î¶", "ÏÏÎ¿ÏÎµÎ¹Î½", "Î±Î¼", "Î±Î¹Î¸",
           "Î±Î½Î·Îº", "Î´ÎµÏÏÎ¿Î¶", "ÎµÎ½Î´Î¹Î±ÏÎµÏ", "Î´Îµ", "Î´ÎµÏÏÎµÏÎµÏ", "ÎºÎ±Î¸Î±ÏÎµÏ", "ÏÎ»Îµ",
           "ÏÏÎ±"), 
       false);
 
   private int rule14(char s[], int len) {
     boolean removed = false;
     
     if (len > 5 && endsWith(s, len, "Î¿ÏÏÎµÏ")) {
       len -= 5;
       removed = true;
     } else if (len > 4 && (endsWith(s, len, "Î¿ÏÏÎ±") || endsWith(s, len, "Î¿ÏÏÎµ"))) {
       len -= 4;
       removed = true;
     }
     
     if (removed && (exc14.contains(s, 0, len) 
         || endsWithVowel(s, len)
         || endsWith(s, len, "ÏÎ¿Î´Î±Ï")
         || endsWith(s, len, "Î²Î»ÎµÏ")
         || endsWith(s, len, "ÏÎ±Î½ÏÎ±Ï")
         || endsWith(s, len, "ÏÏÏÎ´") 
         || endsWith(s, len, "Î¼Î±Î½ÏÎ¹Î»")
         || endsWith(s, len, "Î¼Î±Î»Î»")
         || endsWith(s, len, "ÎºÏÎ¼Î±Ï")
         || endsWith(s, len, "Î»Î±Ï")
         || endsWith(s, len, "Î»Î·Î³")
         || endsWith(s, len, "ÏÎ±Î³")
         || endsWith(s, len, "Î¿Î¼")
         || endsWith(s, len, "ÏÏÏÏ"))) {
       len += 3; // add back -Î¿ÏÏ
     }
 
    return len;
   }
   
  private static final CharArraySet exc15a = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("Î±Î²Î±ÏÏ", "ÏÎ¿Î»ÏÏ", "Î±Î´Î·Ï", "ÏÎ±Î¼Ï", "Ï", "Î±ÏÏ", "Î±Ï", "Î±Î¼Î±Î»",
           "Î±Î¼Î±Î»Î»Î¹", "Î±Î½ÏÏÏ", "Î±ÏÎµÏ", "Î±ÏÏÎ±Ï", "Î±ÏÎ±Ï", "Î´ÎµÏÎ²ÎµÎ½", "Î´ÏÎ¿ÏÎ¿Ï",
           "Î¾ÎµÏ", "Î½ÎµÎ¿Ï", "Î½Î¿Î¼Î¿Ï", "Î¿Î»Î¿Ï", "Î¿Î¼Î¿Ï", "ÏÏÎ¿ÏÏ", "ÏÏÎ¿ÏÏÏÎ¿Ï", "ÏÏÎ¼Ï",
           "ÏÏÎ½Ï", "Ï", "ÏÏÎ¿Ï", "ÏÎ±Ï", "Î±ÎµÎ¹Ï", "Î±Î¹Î¼Î¿ÏÏ", "Î±Î½ÏÏ", "Î±ÏÎ¿Ï",
           "Î±ÏÏÎ¹Ï", "Î´Î¹Î±Ï", "ÎµÎ½", "ÎµÏÎ¹Ï", "ÎºÏÎ¿ÎºÎ±Î»Î¿Ï", "ÏÎ¹Î´Î·ÏÎ¿Ï", "Î»", "Î½Î±Ï",
           "Î¿ÏÎ»Î±Î¼", "Î¿ÏÏ", "Ï", "ÏÏ", "Î¼"), 
       false);
   
  private static final CharArraySet exc15b = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("ÏÎ¿Ï", "Î½Î±ÏÎ»Î¿Ï"),
       false);
   
   private int rule15(char s[], int len) {
     boolean removed = false;
     if (len > 4 && endsWith(s, len, "Î±Î³ÎµÏ")) {
       len -= 4;
       removed = true;
     } else if (len > 3 && (endsWith(s, len, "Î±Î³Î±") || endsWith(s, len, "Î±Î³Îµ"))) {
       len -= 3;
       removed = true;
     }
     
     if (removed) {
       final boolean cond1 = exc15a.contains(s, 0, len) 
         || endsWith(s, len, "Î¿Ï")
         || endsWith(s, len, "ÏÎµÎ»")
         || endsWith(s, len, "ÏÎ¿ÏÏ")
         || endsWith(s, len, "Î»Î»")
         || endsWith(s, len, "ÏÏ")
         || endsWith(s, len, "ÏÏ")
         || endsWith(s, len, "ÏÏ")
         || endsWith(s, len, "ÏÏ")
         || endsWith(s, len, "Î»Î¿Ï")
         || endsWith(s, len, "ÏÎ¼Î·Î½");
       
       final boolean cond2 = exc15b.contains(s, 0, len)
         || endsWith(s, len, "ÎºÎ¿Î»Î»");
       
       if (cond1 && !cond2)
         len += 2; // add back -Î±Î³  
     }
     
     return len;
   }
   
  private static final CharArraySet exc16 = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("Î½", "ÏÎµÏÏÎ¿Î½", "Î´ÏÎ´ÎµÎºÎ±Î½", "ÎµÏÎ·Î¼Î¿Î½", "Î¼ÎµÎ³Î±Î»Î¿Î½", "ÎµÏÏÎ±Î½"),
       false);
   
   private int rule16(char s[], int len) {
     boolean removed = false;
     if (len > 4 && endsWith(s, len, "Î·ÏÎ¿Ï")) {
       len -= 4;
       removed = true;
     } else if (len > 3 && (endsWith(s, len, "Î·ÏÎµ") || endsWith(s, len, "Î·ÏÎ±"))) {
       len -= 3;
       removed = true;
     }
     
     if (removed && exc16.contains(s, 0, len))
       len += 2; // add back -Î·Ï
     
     return len;
   }
   
  private static final CharArraySet exc17 = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("Î±ÏÎ²", "ÏÎ²", "Î±ÏÏ", "ÏÏ", "Î±ÏÎ»", "Î±ÎµÎ¹Î¼Î½", "Î´ÏÏÏÏ", "ÎµÏÏÏ", "ÎºÎ¿Î¹Î½Î¿ÏÏ", "ÏÎ±Î»Î¹Î¼Ï"),
       false);
   
   private int rule17(char s[], int len) {
     if (len > 4 && endsWith(s, len, "Î·ÏÏÎµ")) {
       len -= 4;
       if (exc17.contains(s, 0, len))
         len += 3; // add back the -Î·ÏÏ
     }
     
     return len;
   }
   
  private static final CharArraySet exc18 = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("Î½", "Ï", "ÏÏÎ¹", "ÏÏÏÎ±Î²Î¿Î¼Î¿ÏÏÏ", "ÎºÎ±ÎºÎ¿Î¼Î¿ÏÏÏ", "ÎµÎ¾ÏÎ½"),
       false);
   
   private int rule18(char s[], int len) {
     boolean removed = false;
     
     if (len > 6 && (endsWith(s, len, "Î·ÏÎ¿ÏÎ½Îµ") || endsWith(s, len, "Î·Î¸Î¿ÏÎ½Îµ"))) {
       len -= 6;
       removed = true;
     } else if (len > 4 && endsWith(s, len, "Î¿ÏÎ½Îµ")) {
       len -= 4;
       removed = true;
     }
     
     if (removed && exc18.contains(s, 0, len)) {
       len += 3;
       s[len - 3] = 'Î¿';
       s[len - 2] = 'Ï';
       s[len - 1] = 'Î½';
     }
     return len;
   }
   
  private static final CharArraySet exc19 = new CharArraySet(Version.LUCENE_CURRENT,
       Arrays.asList("ÏÎ±ÏÎ±ÏÎ¿ÏÏ", "Ï", "Ï", "ÏÏÎ¹Î¿ÏÎ»", "Î±Î¶", "Î±Î»Î»Î¿ÏÎ¿ÏÏ", "Î±ÏÎ¿ÏÏ"),
       false);
   
   private int rule19(char s[], int len) {
     boolean removed = false;
     
     if (len > 6 && (endsWith(s, len, "Î·ÏÎ¿ÏÎ¼Îµ") || endsWith(s, len, "Î·Î¸Î¿ÏÎ¼Îµ"))) {
       len -= 6;
       removed = true;
     } else if (len > 4 && endsWith(s, len, "Î¿ÏÎ¼Îµ")) {
       len -= 4;
       removed = true;
     }
     
     if (removed && exc19.contains(s, 0, len)) {
       len += 3;
       s[len - 3] = 'Î¿';
       s[len - 2] = 'Ï';
       s[len - 1] = 'Î¼';
     }
     return len;
   }
   
   private int rule20(char s[], int len) {
     if (len > 5 && (endsWith(s, len, "Î¼Î±ÏÏÎ½") || endsWith(s, len, "Î¼Î±ÏÎ¿Ï")))
       len -= 3;
     else if (len > 4 && endsWith(s, len, "Î¼Î±ÏÎ±"))
       len -= 2;
     return len;
   }
 
   private int rule21(char s[], int len) {
     if (len > 9 && endsWith(s, len, "Î¹Î¿Î½ÏÎ¿ÏÏÎ±Î½"))
       return len - 9;
     
     if (len > 8 && (endsWith(s, len, "Î¹Î¿Î¼Î±ÏÏÎ±Î½") ||
         endsWith(s, len, "Î¹Î¿ÏÎ±ÏÏÎ±Î½") ||
         endsWith(s, len, "Î¹Î¿ÏÎ¼Î±ÏÏÎµ") ||
         endsWith(s, len, "Î¿Î½ÏÎ¿ÏÏÎ±Î½")))
       return len - 8;
     
     if (len > 7 && (endsWith(s, len, "Î¹ÎµÎ¼Î±ÏÏÎµ") ||
         endsWith(s, len, "Î¹ÎµÏÎ±ÏÏÎµ") ||
         endsWith(s, len, "Î¹Î¿Î¼Î¿ÏÎ½Î±") ||
         endsWith(s, len, "Î¹Î¿ÏÎ±ÏÏÎµ") ||
         endsWith(s, len, "Î¹Î¿ÏÎ¿ÏÎ½Î±") ||
         endsWith(s, len, "Î¹Î¿ÏÎ½ÏÎ±Î¹") ||
         endsWith(s, len, "Î¹Î¿ÏÎ½ÏÎ±Î½") ||
         endsWith(s, len, "Î·Î¸Î·ÎºÎ±ÏÎµ") ||
         endsWith(s, len, "Î¿Î¼Î±ÏÏÎ±Î½") ||
         endsWith(s, len, "Î¿ÏÎ±ÏÏÎ±Î½") ||
         endsWith(s, len, "Î¿ÏÎ¼Î±ÏÏÎµ")))
       return len - 7;
     
     if (len > 6 && (endsWith(s, len, "Î¹Î¿Î¼Î¿ÏÎ½") ||
         endsWith(s, len, "Î¹Î¿Î½ÏÎ±Î½") ||
         endsWith(s, len, "Î¹Î¿ÏÎ¿ÏÎ½") ||
         endsWith(s, len, "Î·Î¸ÎµÎ¹ÏÎµ") ||
         endsWith(s, len, "Î·Î¸Î·ÎºÎ±Î½") ||
         endsWith(s, len, "Î¿Î¼Î¿ÏÎ½Î±") ||
         endsWith(s, len, "Î¿ÏÎ±ÏÏÎµ") ||
         endsWith(s, len, "Î¿ÏÎ¿ÏÎ½Î±") ||
         endsWith(s, len, "Î¿ÏÎ½ÏÎ±Î¹") ||
         endsWith(s, len, "Î¿ÏÎ½ÏÎ±Î½") ||
         endsWith(s, len, "Î¿ÏÏÎ±ÏÎµ")))
       return len - 6;
     
     if (len > 5 && (endsWith(s, len, "Î±Î³Î±ÏÎµ") ||
         endsWith(s, len, "Î¹ÎµÎ¼Î±Î¹") ||
         endsWith(s, len, "Î¹ÎµÏÎ±Î¹") ||
         endsWith(s, len, "Î¹ÎµÏÎ±Î¹") ||
         endsWith(s, len, "Î¹Î¿ÏÎ±Î½") ||
         endsWith(s, len, "Î¹Î¿ÏÎ¼Î±") ||
         endsWith(s, len, "Î·Î¸ÎµÎ¹Ï") ||
         endsWith(s, len, "Î·Î¸Î¿ÏÎ½") ||
         endsWith(s, len, "Î·ÎºÎ±ÏÎµ") ||
         endsWith(s, len, "Î·ÏÎ±ÏÎµ") ||
         endsWith(s, len, "Î·ÏÎ¿ÏÎ½") ||
         endsWith(s, len, "Î¿Î¼Î¿ÏÎ½") ||
         endsWith(s, len, "Î¿Î½ÏÎ±Î¹") ||
         endsWith(s, len, "Î¿Î½ÏÎ±Î½") ||
         endsWith(s, len, "Î¿ÏÎ¿ÏÎ½") ||
         endsWith(s, len, "Î¿ÏÎ¼Î±Î¹") ||
         endsWith(s, len, "Î¿ÏÏÎ±Î½")))
       return len - 5;
     
     if (len > 4 && (endsWith(s, len, "Î±Î³Î±Î½") ||
         endsWith(s, len, "Î±Î¼Î±Î¹") ||
         endsWith(s, len, "Î±ÏÎ±Î¹") ||
         endsWith(s, len, "Î±ÏÎ±Î¹") ||
         endsWith(s, len, "ÎµÎ¹ÏÎµ") ||
         endsWith(s, len, "ÎµÏÎ±Î¹") ||
         endsWith(s, len, "ÎµÏÎ±Î¹") ||
         endsWith(s, len, "Î·Î´ÎµÏ") ||
         endsWith(s, len, "Î·Î´ÏÎ½") ||
         endsWith(s, len, "Î·Î¸ÎµÎ¹") ||
         endsWith(s, len, "Î·ÎºÎ±Î½") ||
         endsWith(s, len, "Î·ÏÎ±Î½") ||
         endsWith(s, len, "Î·ÏÎµÎ¹") ||
         endsWith(s, len, "Î·ÏÎµÏ") ||
         endsWith(s, len, "Î¿Î¼Î±Î¹") ||
         endsWith(s, len, "Î¿ÏÎ±Î½")))
       return len - 4;
     
     if (len > 3 && (endsWith(s, len, "Î±ÎµÎ¹") ||
         endsWith(s, len, "ÎµÎ¹Ï") ||
         endsWith(s, len, "Î·Î¸Ï") ||
         endsWith(s, len, "Î·ÏÏ") ||
         endsWith(s, len, "Î¿ÏÎ½") ||
         endsWith(s, len, "Î¿ÏÏ")))
       return len - 3;
     
     if (len > 2 && (endsWith(s, len, "Î±Î½") ||
         endsWith(s, len, "Î±Ï") ||
         endsWith(s, len, "Î±Ï") ||
         endsWith(s, len, "ÎµÎ¹") ||
         endsWith(s, len, "ÎµÏ") ||
         endsWith(s, len, "Î·Ï") ||
         endsWith(s, len, "Î¿Î¹") ||
         endsWith(s, len, "Î¿Ï") ||
         endsWith(s, len, "Î¿Ï") ||
         endsWith(s, len, "ÏÏ") ||
         endsWith(s, len, "ÏÎ½")))
       return len - 2;
     
     if (len > 1 && endsWithVowel(s, len))
       return len - 1;
 
     return len;
   }
   
   private int rule22(char s[], int len) {
     if (endsWith(s, len, "ÎµÏÏÎµÏ") ||
         endsWith(s, len, "ÎµÏÏÎ±Ï"))
       return len - 5;
     
     if (endsWith(s, len, "Î¿ÏÎµÏ") ||
         endsWith(s, len, "Î¿ÏÎ±Ï") ||
         endsWith(s, len, "ÏÏÎµÏ") ||
         endsWith(s, len, "ÏÏÎ±Ï") ||
         endsWith(s, len, "ÏÏÎµÏ") ||
         endsWith(s, len, "ÏÏÎ±Ï"))
       return len - 4;
 
     return len;
   }
 
   private boolean endsWith(char s[], int len, String suffix) {
     final int suffixLen = suffix.length();
     if (suffixLen > len)
       return false;
     for (int i = suffixLen - 1; i >= 0; i--)
       if (s[len -(suffixLen - i)] != suffix.charAt(i))
         return false;
     
     return true;
   }
   
   private boolean endsWithVowel(char s[], int len) {
     if (len == 0)
       return false;
     switch(s[len - 1]) {
       case 'Î±':
       case 'Îµ':
       case 'Î·':
       case 'Î¹':
       case 'Î¿':
       case 'Ï':
       case 'Ï':
         return true;
       default:
         return false;
     }
   }
   
   private boolean endsWithVowelNoY(char s[], int len) {
     if (len == 0)
       return false;
     switch(s[len - 1]) {
       case 'Î±':
       case 'Îµ':
       case 'Î·':
       case 'Î¹':
       case 'Î¿':
       case 'Ï':
         return true;
       default:
         return false;
     }
   }
 }
