 package org.apache.lucene.analysis.ja.util;
 
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
 
 import java.io.IOException;
 import java.util.HashMap;
 
 /**
  * Utility class for english translations of morphological data,
  * used only for debugging.
  */
 public class ToStringUtil {
   // a translation map for parts of speech, only used for reflectWith
   private static final HashMap<String,String> posTranslations = new HashMap<String,String>();
   static {
     posTranslations.put("åè©", "noun");
     posTranslations.put("åè©-ä¸è¬", "noun-common");
     posTranslations.put("åè©-åºæåè©", "noun-proper");
     posTranslations.put("åè©-åºæåè©-ä¸è¬", "noun-proper-misc");
     posTranslations.put("åè©-åºæåè©-äººå", "noun-proper-person");
     posTranslations.put("åè©-åºæåè©-äººå-ä¸è¬", "noun-proper-person-misc");
     posTranslations.put("åè©-åºæåè©-äººå-å§", "noun-proper-person-surname");
     posTranslations.put("åè©-åºæåè©-äººå-å", "noun-proper-person-given_name");
     posTranslations.put("åè©-åºæåè©-çµç¹", "noun-proper-organization");
     posTranslations.put("åè©-åºæåè©-å°å", "noun-proper-place");
     posTranslations.put("åè©-åºæåè©-å°å-ä¸è¬", "noun-proper-place-misc");
     posTranslations.put("åè©-åºæåè©-å°å-å½", "noun-proper-place-country");
     posTranslations.put("åè©-ä»£åè©", "noun-pronoun");
     posTranslations.put("åè©-ä»£åè©-ä¸è¬", "noun-pronoun-misc");
     posTranslations.put("åè©-ä»£åè©-ç¸®ç´", "noun-pronoun-contraction");
     posTranslations.put("åè©-å¯è©å¯è½", "noun-adverbial");
     posTranslations.put("åè©-ãµå¤æ¥ç¶", "noun-verbal");
     posTranslations.put("åè©-å½¢å®¹åè©èªå¹¹", "noun-adjective-base");
     posTranslations.put("åè©-æ°", "noun-numeric");
     posTranslations.put("åè©-éèªç«", "noun-affix");
     posTranslations.put("åè©-éèªç«-ä¸è¬", "noun-affix-misc");
     posTranslations.put("åè©-éèªç«-å¯è©å¯è½", "noun-affix-adverbial");
     posTranslations.put("åè©-éèªç«-å©åè©èªå¹¹", "noun-affix-aux");
     posTranslations.put("åè©-éèªç«-å½¢å®¹åè©èªå¹¹", "noun-affix-adjective-base");
     posTranslations.put("åè©-ç¹æ®", "noun-special");
     posTranslations.put("åè©-ç¹æ®-å©åè©èªå¹¹", "noun-special-aux");
     posTranslations.put("åè©-æ¥å°¾", "noun-suffix");
     posTranslations.put("åè©-æ¥å°¾-ä¸è¬", "noun-suffix-misc");
     posTranslations.put("åè©-æ¥å°¾-äººå", "noun-suffix-person");
     posTranslations.put("åè©-æ¥å°¾-å°å", "noun-suffix-place");
     posTranslations.put("åè©-æ¥å°¾-ãµå¤æ¥ç¶", "noun-suffix-verbal");
     posTranslations.put("åè©-æ¥å°¾-å©åè©èªå¹¹", "noun-suffix-aux");
     posTranslations.put("åè©-æ¥å°¾-å½¢å®¹åè©èªå¹¹", "noun-suffix-adjective-base");
     posTranslations.put("åè©-æ¥å°¾-å¯è©å¯è½", "noun-suffix-adverbial");
     posTranslations.put("åè©-æ¥å°¾-å©æ°è©", "noun-suffix-classifier");
     posTranslations.put("åè©-æ¥å°¾-ç¹æ®", "noun-suffix-special");
     posTranslations.put("åè©-æ¥ç¶è©ç", "noun-suffix-conjunctive");
     posTranslations.put("åè©-åè©éèªç«ç", "noun-verbal_aux");
     posTranslations.put("åè©-å¼ç¨æå­å", "noun-quotation");
     posTranslations.put("åè©-ãã¤å½¢å®¹è©èªå¹¹", "noun-nai_adjective");
     posTranslations.put("æ¥é ­è©", "prefix");
     posTranslations.put("æ¥é ­è©-åè©æ¥ç¶", "prefix-nominal");
     posTranslations.put("æ¥é ­è©-åè©æ¥ç¶", "prefix-verbal");
     posTranslations.put("æ¥é ­è©-å½¢å®¹è©æ¥ç¶", "prefix-adjectival");
     posTranslations.put("æ¥é ­è©-æ°æ¥ç¶", "prefix-numerical");
     posTranslations.put("åè©", "verb");
     posTranslations.put("åè©-èªç«", "verb-main");
     posTranslations.put("åè©-éèªç«", "verb-auxiliary");
     posTranslations.put("åè©-æ¥å°¾", "verb-suffix");
     posTranslations.put("å½¢å®¹è©", "adjective");
     posTranslations.put("å½¢å®¹è©-èªç«", "adjective-main");
     posTranslations.put("å½¢å®¹è©-éèªç«", "adjective-auxiliary");
     posTranslations.put("å½¢å®¹è©-æ¥å°¾", "adjective-suffix");
     posTranslations.put("å¯è©", "adverb");
     posTranslations.put("å¯è©-ä¸è¬", "adverb-misc");
     posTranslations.put("å¯è©-å©è©é¡æ¥ç¶", "adverb-particle_conjunction");
     posTranslations.put("é£ä½è©", "adnominal");
     posTranslations.put("æ¥ç¶è©", "conjunction");
     posTranslations.put("å©è©", "particle");
     posTranslations.put("å©è©-æ ¼å©è©", "particle-case");
     posTranslations.put("å©è©-æ ¼å©è©-ä¸è¬", "particle-case-misc");
     posTranslations.put("å©è©-æ ¼å©è©-å¼ç¨", "particle-case-quote");
     posTranslations.put("å©è©-æ ¼å©è©-é£èª", "particle-case-compound");
     posTranslations.put("å©è©-æ¥ç¶å©è©", "particle-conjunctive");
     posTranslations.put("å©è©-ä¿å©è©", "particle-dependency");
     posTranslations.put("å©è©-å¯å©è©", "particle-adverbial");
     posTranslations.put("å©è©-éæå©è©", "particle-interjective");
     posTranslations.put("å©è©-ä¸¦ç«å©è©", "particle-coordinate");
     posTranslations.put("å©è©-çµå©è©", "particle-final");
     posTranslations.put("å©è©-å¯å©è©ï¼ä¸¦ç«å©è©ï¼çµå©è©", "particle-adverbial/conjunctive/final");
     posTranslations.put("å©è©-é£ä½å", "particle-adnominalizer");
     posTranslations.put("å©è©-å¯è©å", "particle-adnominalizer");
     posTranslations.put("å©è©-ç¹æ®", "particle-special");
     posTranslations.put("å©åè©", "auxiliary-verb");
     posTranslations.put("æåè©", "interjection");
     posTranslations.put("è¨å·", "symbol");
     posTranslations.put("è¨å·-ä¸è¬", "symbol-misc");
     posTranslations.put("è¨å·-å¥ç¹", "symbol-period");
     posTranslations.put("è¨å·-èª­ç¹", "symbol-comma");
     posTranslations.put("è¨å·-ç©ºç½", "symbol-space");
     posTranslations.put("è¨å·-æ¬å¼§é", "symbol-open_bracket");
     posTranslations.put("è¨å·-æ¬å¼§é", "symbol-close_bracket");
     posTranslations.put("è¨å·-ã¢ã«ãã¡ããã", "symbol-alphabetic");
     posTranslations.put("ãã®ä»", "other");
     posTranslations.put("ãã®ä»-éæ", "other-interjection");
     posTranslations.put("ãã£ã©ã¼", "filler");
     posTranslations.put("éè¨èªé³", "non-verbal");
     posTranslations.put("èªæ­ç", "fragment");
     posTranslations.put("æªç¥èª", "unknown");
   }
   
   /**
    * Get the english form of a POS tag
    */
   public static String getPOSTranslation(String s) {
     return posTranslations.get(s);
   }
   
   // a translation map for inflection types, only used for reflectWith
   private static final HashMap<String,String> inflTypeTranslations = new HashMap<String,String>();
   static {
     inflTypeTranslations.put("*", "*");
     inflTypeTranslations.put("å½¢å®¹è©ã»ã¢ã¦ãªæ®µ", "adj-group-a-o-u");
     inflTypeTranslations.put("å½¢å®¹è©ã»ã¤æ®µ", "adj-group-i");
     inflTypeTranslations.put("å½¢å®¹è©ã»ã¤ã¤",  "adj-group-ii");
     inflTypeTranslations.put("ä¸å¤åå", "non-inflectional");
     inflTypeTranslations.put("ç¹æ®ã»ã¿", "special-da");
     inflTypeTranslations.put("ç¹æ®ã»ã", "special-ta");
     inflTypeTranslations.put("æèªã»ã´ãã·", "classical-gotoshi");
     inflTypeTranslations.put("ç¹æ®ã»ã¸ã£", "special-ja");
     inflTypeTranslations.put("ç¹æ®ã»ãã¤", "special-nai");
     inflTypeTranslations.put("äºæ®µã»ã©è¡ç¹æ®", "5-row-cons-r-special");
     inflTypeTranslations.put("ç¹æ®ã»ã", "special-nu");
     inflTypeTranslations.put("æèªã»ã­", "classical-ki");
     inflTypeTranslations.put("ç¹æ®ã»ã¿ã¤", "special-tai");
     inflTypeTranslations.put("æèªã»ãã·", "classical-beshi");
     inflTypeTranslations.put("ç¹æ®ã»ã¤", "special-ya");
     inflTypeTranslations.put("æèªã»ãã¸", "classical-maji");
     inflTypeTranslations.put("ä¸äºã»ã¿è¡", "2-row-lower-cons-t");
     inflTypeTranslations.put("ç¹æ®ã»ãã¹", "special-desu");
     inflTypeTranslations.put("ç¹æ®ã»ãã¹", "special-masu");
     inflTypeTranslations.put("äºæ®µã»ã©è¡ã¢ã«", "5-row-aru");
     inflTypeTranslations.put("æèªã»ããª", "classical-nari");
     inflTypeTranslations.put("æèªã»ãª", "classical-ri");
     inflTypeTranslations.put("æèªã»ã±ãª", "classical-keri");
     inflTypeTranslations.put("æèªã»ã«", "classical-ru");
     inflTypeTranslations.put("äºæ®µã»ã«è¡ã¤é³ä¾¿", "5-row-cons-k-i-onbin");
     inflTypeTranslations.put("äºæ®µã»ãµè¡", "5-row-cons-s");
     inflTypeTranslations.put("ä¸æ®µ", "1-row");
     inflTypeTranslations.put("äºæ®µã»ã¯è¡ä¿é³ä¾¿", "5-row-cons-w-cons-onbin");
     inflTypeTranslations.put("äºæ®µã»ãè¡", "5-row-cons-m");
     inflTypeTranslations.put("äºæ®µã»ã¿è¡", "5-row-cons-t");
     inflTypeTranslations.put("äºæ®µã»ã©è¡", "5-row-cons-r");
     inflTypeTranslations.put("ãµå¤ã»âã¹ã«", "irregular-suffix-suru");
     inflTypeTranslations.put("äºæ®µã»ã¬è¡", "5-row-cons-g");
     inflTypeTranslations.put("ãµå¤ã»âãºã«", "irregular-suffix-zuru");
     inflTypeTranslations.put("äºæ®µã»ãè¡", "5-row-cons-b");
     inflTypeTranslations.put("äºæ®µã»ã¯è¡ã¦é³ä¾¿", "5-row-cons-w-u-onbin");
     inflTypeTranslations.put("ä¸äºã»ãè¡", "2-row-lower-cons-d");
     inflTypeTranslations.put("äºæ®µã»ã«è¡ä¿é³ä¾¿ã¦ã¯", "5-row-cons-k-cons-onbin-yuku");
     inflTypeTranslations.put("ä¸äºã»ãè¡", "2-row-upper-cons-d");
     inflTypeTranslations.put("äºæ®µã»ã«è¡ä¿é³ä¾¿", "5-row-cons-k-cons-onbin");
     inflTypeTranslations.put("ä¸æ®µã»å¾ã«", "1-row-eru");
     inflTypeTranslations.put("åæ®µã»ã¿è¡", "4-row-cons-t");
     inflTypeTranslations.put("äºæ®µã»ãè¡", "5-row-cons-n");
     inflTypeTranslations.put("ä¸äºã»ãè¡", "2-row-lower-cons-h");
     inflTypeTranslations.put("åæ®µã»ãè¡", "4-row-cons-h");
     inflTypeTranslations.put("åæ®µã»ãè¡", "4-row-cons-b");
     inflTypeTranslations.put("ãµå¤ã»ã¹ã«", "irregular-suru");
     inflTypeTranslations.put("ä¸äºã»ãè¡", "2-row-upper-cons-h");
     inflTypeTranslations.put("ä¸äºã»ãè¡", "2-row-lower-cons-m");
     inflTypeTranslations.put("åæ®µã»ãµè¡", "4-row-cons-s");
     inflTypeTranslations.put("ä¸äºã»ã¬è¡", "2-row-lower-cons-g");
     inflTypeTranslations.put("ã«å¤ã»æ¥ã«", "kuru-kanji");
     inflTypeTranslations.put("ä¸æ®µã»ã¯ã¬ã«", "1-row-kureru");
     inflTypeTranslations.put("ä¸äºã»å¾", "2-row-lower-u");
     inflTypeTranslations.put("ã«å¤ã»ã¯ã«", "kuru-kana");
     inflTypeTranslations.put("ã©å¤", "irregular-cons-r");
     inflTypeTranslations.put("ä¸äºã»ã«è¡", "2-row-lower-cons-k");
   }
   
   /**
    * Get the english form of inflection type
    */
   public static String getInflectionTypeTranslation(String s) {
     return inflTypeTranslations.get(s);
   }
 
   // a translation map for inflection forms, only used for reflectWith
   private static final HashMap<String,String> inflFormTranslations = new HashMap<String,String>();
   static {
     inflFormTranslations.put("*", "*");
     inflFormTranslations.put("åºæ¬å½¢", "base");
     inflFormTranslations.put("æèªåºæ¬å½¢", "classical-base");
     inflFormTranslations.put("æªç¶ãæ¥ç¶", "imperfective-nu-connection");
     inflFormTranslations.put("æªç¶ã¦æ¥ç¶", "imperfective-u-connection");
     inflFormTranslations.put("é£ç¨ã¿æ¥ç¶", "conjunctive-ta-connection");
     inflFormTranslations.put("é£ç¨ãæ¥ç¶", "conjunctive-te-connection");
     inflFormTranslations.put("é£ç¨ã´ã¶ã¤æ¥ç¶", "conjunctive-gozai-connection");
     inflFormTranslations.put("ä½è¨æ¥ç¶", "uninflected-connection");
     inflFormTranslations.put("ä»®å®å½¢", "subjunctive");
     inflFormTranslations.put("å½ä»¤ï½", "imperative-e");
     inflFormTranslations.put("ä»®å®ç¸®ç´ï¼", "conditional-contracted-1");
     inflFormTranslations.put("ä»®å®ç¸®ç´ï¼", "conditional-contracted-2");
     inflFormTranslations.put("ã¬ã«æ¥ç¶", "garu-connection");
     inflFormTranslations.put("æªç¶å½¢", "imperfective");
     inflFormTranslations.put("é£ç¨å½¢", "conjunctive");
     inflFormTranslations.put("é³ä¾¿åºæ¬å½¢", "onbin-base");
     inflFormTranslations.put("é£ç¨ãæ¥ç¶", "conjunctive-de-connection");
     inflFormTranslations.put("æªç¶ç¹æ®", "imperfective-special");
     inflFormTranslations.put("å½ä»¤ï½", "imperative-i");
     inflFormTranslations.put("é£ç¨ãæ¥ç¶", "conjunctive-ni-connection");
     inflFormTranslations.put("å½ä»¤ï½ï½", "imperative-yo");
     inflFormTranslations.put("ä½è¨æ¥ç¶ç¹æ®", "adnominal-special");
     inflFormTranslations.put("å½ä»¤ï½ï½", "imperative-ro");
     inflFormTranslations.put("ä½è¨æ¥ç¶ç¹æ®ï¼", "uninflected-special-connection-2");
     inflFormTranslations.put("æªç¶ã¬ã«æ¥ç¶", "imperfective-reru-connection");
     inflFormTranslations.put("ç¾ä»£åºæ¬å½¢", "modern-base");
     inflFormTranslations.put("åºæ¬å½¢-ä¿é³ä¾¿", "base-onbin"); // not sure about this
   }
   
   /**
    * Get the english form of inflected form
    */
   public static String getInflectedFormTranslation(String s) {
     return inflFormTranslations.get(s);
   }
   
   /**
    * Romanize katakana with modified hepburn
    */
   public static String getRomanization(String s) {
     StringBuilder out = new StringBuilder();
     try {
       getRomanization(out, s);
     } catch (IOException bogus) {
       throw new RuntimeException(bogus);
     }
     return out.toString();
   }
   
   /**
    * Romanize katakana with modified hepburn
    */
   public static void getRomanization(Appendable builder, CharSequence s) throws IOException {
     final int len = s.length();
     for (int i = 0; i < len; i++) {
       // maximum lookahead: 3
       char ch = s.charAt(i);
       char ch2 = (i < len - 1) ? s.charAt(i + 1) : 0;
       char ch3 = (i < len - 2) ? s.charAt(i + 2) : 0;
       
       main: switch (ch) {
         case 'ã':
           switch (ch2) {
             case 'ã«':
             case 'ã­':
             case 'ã¯':
             case 'ã±':
             case 'ã³':
               builder.append('k');
               break main;
             case 'ãµ':
             case 'ã·':
             case 'ã¹':
             case 'ã»':
             case 'ã½':
               builder.append('s');
               break main;
             case 'ã¿':
             case 'ã':
             case 'ã':
             case 'ã':
             case 'ã':
               builder.append('t');
               break main;
             case 'ã':
             case 'ã':
             case 'ã':
             case 'ã':
             case 'ã':
               builder.append('p');
               break main;
           }
           break;
         case 'ã¢':
           builder.append('a');
           break;
         case 'ã¤':
           if (ch2 == 'ã£') {
             builder.append("yi");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("ye");
             i++;
           } else {
             builder.append('i');
           }
           break;
         case 'ã¦':
           switch(ch2) {
             case 'ã¡':
               builder.append("wa");
               i++;
               break;
             case 'ã£':
               builder.append("wi");
               i++;
               break;
             case 'ã¥':
               builder.append("wu");
               i++;
               break;
             case 'ã§':
               builder.append("we");
               i++;
               break;
             case 'ã©':
               builder.append("wo");
               i++;
               break;
             case 'ã¥':
               builder.append("wyu");
               i++;
               break;
             default:
               builder.append('u');
               break;
           }
           break;
         case 'ã¨':
           builder.append('e');
           break;
         case 'ãª':
           if (ch2 == 'ã¦') {
             builder.append('Å');
             i++;
           } else {
             builder.append('o');
           }
           break;
         case 'ã«':
           builder.append("ka");
           break;
         case 'ã­':
           if (ch2 == 'ã§' && ch3 == 'ã¦') {
             builder.append("kyÅ");
             i += 2;
           } else if (ch2 == 'ã¥' && ch3 == 'ã¦') {
             builder.append("kyÅ«");
             i += 2;
           } else if (ch2 == 'ã£') {
             builder.append("kya");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("kyo");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("kyu");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("kye");
             i++;
           } else {
             builder.append("ki");
           }
           break;
         case 'ã¯':
           switch(ch2) {
             case 'ã¡':
               builder.append("kwa");
               i++;
               break;
             case 'ã£':
               builder.append("kwi");
               i++;
               break;
             case 'ã§':
               builder.append("kwe");
               i++;
               break;
             case 'ã©':
               builder.append("kwo");
               i++;
               break;
             case 'ã®':
               builder.append("kwa");
               i++;
               break;
             default:
               builder.append("ku");
               break;
           }
           break;
         case 'ã±':
           builder.append("ke");
           break;
         case 'ã³':
           if (ch2 == 'ã¦') {
             builder.append("kÅ");
             i++;
           } else {
             builder.append("ko");
           }
           break;
         case 'ãµ':
           builder.append("sa");
           break;
         case 'ã·':
           if (ch2 == 'ã§' && ch3 == 'ã¦') {
             builder.append("shÅ");
             i += 2;
           } else if (ch2 == 'ã¥' && ch3 == 'ã¦') {
             builder.append("shÅ«");
             i += 2;
           } else if (ch2 == 'ã£') {
             builder.append("sha");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("sho");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("shu");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("she");
             i++;
           } else {
             builder.append("shi");
           }
           break;
         case 'ã¹':
           if (ch2 == 'ã£') {
             builder.append("si");
             i++;
           } else {
             builder.append("su");
           }
           break;
         case 'ã»':
           builder.append("se");
           break;
         case 'ã½':
           if (ch2 == 'ã¦') {
             builder.append("sÅ");
             i++;
           } else {
             builder.append("so");
           }
           break;
         case 'ã¿':
           builder.append("ta");
           break;
         case 'ã':
           if (ch2 == 'ã§' && ch3 == 'ã¦') {
             builder.append("chÅ");
             i += 2;
           } else if (ch2 == 'ã¥' && ch3 == 'ã¦') {
             builder.append("chÅ«");
             i += 2;
           } else if (ch2 == 'ã£') {
             builder.append("cha");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("cho");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("chu");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("che");
             i++;
           } else {
             builder.append("chi");
           }
           break;
         case 'ã':
           if (ch2 == 'ã¡') {
             builder.append("tsa");
             i++;
           } else if (ch2 == 'ã£') {
             builder.append("tsi");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("tse");
             i++;
           } else if (ch2 == 'ã©') {
             builder.append("tso");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("tsyu");
             i++;
           } else {
             builder.append("tsu");
           }
           break;
         case 'ã':
           if (ch2 == 'ã£') {
             builder.append("ti");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("tu");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("tyu");
             i++;
           } else {
             builder.append("te");
           }
           break;
         case 'ã':
           if (ch2 == 'ã¦') {
             builder.append("tÅ");
             i++;
           } else {
             builder.append("to");
           }
           break;
         case 'ã':
           builder.append("na");
           break;
         case 'ã':
           if (ch2 == 'ã§' && ch3 == 'ã¦') {
             builder.append("nyÅ");
             i += 2;
           } else if (ch2 == 'ã¥' && ch3 == 'ã¦') {
             builder.append("nyÅ«");
             i += 2;
           } else if (ch2 == 'ã£') {
             builder.append("nya");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("nyo");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("nyu");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("nye");
             i++;
           } else {
             builder.append("ni");
           }
           break;
         case 'ã':
           builder.append("nu");
           break;
         case 'ã':
           builder.append("ne");
           break;
         case 'ã':
           if (ch2 == 'ã¦') {
             builder.append("nÅ");
             i++;
           } else {
             builder.append("no");
           }
           break;
         case 'ã':
           builder.append("ha");
           break;
         case 'ã':
           if (ch2 == 'ã§' && ch3 == 'ã¦') {
             builder.append("hyÅ");
             i += 2;
           } else if (ch2 == 'ã¥' && ch3 == 'ã¦') {
             builder.append("hyÅ«");
             i += 2;
           } else if (ch2 == 'ã£') {
             builder.append("hya");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("hyo");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("hyu");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("hye");
             i++;
           } else {
             builder.append("hi");
           }
           break;
         case 'ã':
           if (ch2 == 'ã£') {
             builder.append("fya");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("fyu");
             i++;
           } else if (ch2 == 'ã£' && ch3 == 'ã§') {
             builder.append("fye");
             i+=2;
           } else if (ch2 == 'ã§') {
             builder.append("fyo");
             i++;
           } else if (ch2 == 'ã¡') {
             builder.append("fa");
             i++;
           } else if (ch2 == 'ã£') {
             builder.append("fi");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("fe");
             i++;
           } else if (ch2 == 'ã©') {
             builder.append("fo");
             i++;
           } else {
             builder.append("fu");
           }
           break;
         case 'ã':
           builder.append("he");
           break;
         case 'ã':
           if (ch2 == 'ã¦') {
             builder.append("hÅ");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("hu");
             i++;
           } else {
             builder.append("ho");
           }
           break;
         case 'ã':
           builder.append("ma");
           break;
         case 'ã':
           if (ch2 == 'ã§' && ch3 == 'ã¦') {
             builder.append("myÅ");
             i += 2;
           } else if (ch2 == 'ã¥' && ch3 == 'ã¦') {
             builder.append("myÅ«");
             i += 2;
           } else if (ch2 == 'ã£') {
             builder.append("mya");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("myo");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("myu");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("mye");
             i++;
           } else {
             builder.append("mi");
           }
           break;
         case 'ã ':
           builder.append("mu");
           break;
         case 'ã¡':
          builder.append("mi");
           break;
         case 'ã¢':
           if (ch2 == 'ã¦') {
             builder.append("mÅ");
             i++;
           } else {
             builder.append("mo");
           }
           break;
         case 'ã¤':
           builder.append("ya");
           break;
         case 'ã¦':
           builder.append("yu");
           break;
         case 'ã¨':
           if (ch2 == 'ã¦') {
             builder.append("yÅ");
             i++;
           } else {
             builder.append("yo");
           }
           break;
         case 'ã©':
           builder.append("ra");
           break;
         case 'ãª':
           if (ch2 == 'ã§' && ch3 == 'ã¦') {
             builder.append("ryÅ");
             i += 2;
           } else if (ch2 == 'ã¥' && ch3 == 'ã¦') {
             builder.append("ryÅ«");
             i += 2;
           } else if (ch2 == 'ã£') {
             builder.append("rya");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("ryo");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("ryu");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("rye");
             i++;
           } else {
             builder.append("ri");
           }
           break;
         case 'ã«':
           builder.append("ru");
           break;
         case 'ã¬':
           builder.append("re");
           break;
         case 'ã­':
           if (ch2 == 'ã¦') {
             builder.append("rÅ");
             i++;
           } else {
             builder.append("ro");
           }
           break;
         case 'ã¯':
           builder.append("wa");
           break;
         case 'ã°':
           builder.append("i");
           break;
         case 'ã±':
           builder.append("e");
           break;
         case 'ã²':
           builder.append("o");
           break;
         case 'ã³':
           switch (ch2) {
             case 'ã':
             case 'ã':
             case 'ã':
             case 'ã':
             case 'ã':
             case 'ã':
             case 'ã':
             case 'ã':
             case 'ã':
             case 'ã':
             case 'ã':
             case 'ã':
             case 'ã ':
             case 'ã¡':
             case 'ã¢':
               builder.append('m');
               break main;
             case 'ã¤':
             case 'ã¦':
             case 'ã¨':
             case 'ã¢':
             case 'ã¤':
             case 'ã¦':
             case 'ã¨':
             case 'ãª':
               builder.append("n'");
               break main;
             default:
               builder.append("n");
               break main;
           }
         case 'ã¬':
           builder.append("ga");
           break;
         case 'ã®':
           if (ch2 == 'ã§' && ch3 == 'ã¦') {
             builder.append("gyÅ");
             i += 2;
           } else if (ch2 == 'ã¥' && ch3 == 'ã¦') {
             builder.append("gyÅ«");
             i += 2;
           } else if (ch2 == 'ã£') {
             builder.append("gya");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("gyo");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("gyu");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("gye");
             i++;
           } else {
             builder.append("gi");
           }
           break;
         case 'ã°':
           switch(ch2) {
             case 'ã¡':
               builder.append("gwa");
               i++;
               break;
             case 'ã£':
               builder.append("gwi");
               i++;
               break;
             case 'ã§':
               builder.append("gwe");
               i++;
               break;
             case 'ã©':
               builder.append("gwo");
               i++;
               break;
             case 'ã®':
               builder.append("gwa");
               i++;
               break;
             default:
               builder.append("gu");
               break;
           }
           break;
         case 'ã²':
           builder.append("ge");
           break;
         case 'ã´':
           if (ch2 == 'ã¦') {
             builder.append("gÅ");
             i++;
           } else {
             builder.append("go");
           }
           break;
         case 'ã¶':
           builder.append("za");
           break;
         case 'ã¸':
           if (ch2 == 'ã§' && ch3 == 'ã¦') {
             builder.append("jÅ");
             i += 2;
           } else if (ch2 == 'ã¥' && ch3 == 'ã¦') {
             builder.append("jÅ«");
             i += 2;
           } else if (ch2 == 'ã£') {
             builder.append("ja");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("jo");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("ju");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("je");
             i++;
           } else {
             builder.append("ji");
           }
           break;
         case 'ãº':
           if (ch2 == 'ã£') {
             builder.append("zi");
             i++;
           } else {
             builder.append("zu");
           }
           break;
         case 'ã¼':
           builder.append("ze");
           break;
         case 'ã¾':
           if (ch2 == 'ã¦') {
             builder.append("zÅ");
             i++;
           } else {
             builder.append("zo");
           }
           break;
         case 'ã':
           builder.append("da");
           break;
         case 'ã':
           builder.append("ji");
           break;
         case 'ã':
           builder.append("zu");
           break;
         case 'ã':
           if (ch2 == 'ã£') {
             builder.append("di");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("dyu");
             i++;
           } else {
             builder.append("de");
           }
           break;
         case 'ã':
           if (ch2 == 'ã¦') {
             builder.append("dÅ");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("du");
             i++;
           } else {
             builder.append("do");
           }
           break;
         case 'ã':
           builder.append("ba");
           break;
         case 'ã':
           if (ch2 == 'ã§' && ch3 == 'ã¦') {
             builder.append("byÅ");
             i += 2;
           } else if (ch2 == 'ã¥' && ch3 == 'ã¦') {
             builder.append("byÅ«");
             i += 2;
           } else if (ch2 == 'ã£') {
             builder.append("bya");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("byo");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("byu");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("bye");
             i++;
           } else {
             builder.append("bi");
           }
           break;
         case 'ã':
           builder.append("bu");
           break;
         case 'ã':
           builder.append("be");
           break;
         case 'ã':
           if (ch2 == 'ã¦') {
             builder.append("bÅ");
             i++;
           } else {
             builder.append("bo");
           }
           break;
         case 'ã':
           builder.append("pa");
           break;
         case 'ã':
           if (ch2 == 'ã§' && ch3 == 'ã¦') {
             builder.append("pyÅ");
             i += 2;
           } else if (ch2 == 'ã¥' && ch3 == 'ã¦') {
             builder.append("pyÅ«");
             i += 2;
           } else if (ch2 == 'ã£') {
             builder.append("pya");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("pyo");
             i++;
           } else if (ch2 == 'ã¥') {
             builder.append("pyu");
             i++;
           } else if (ch2 == 'ã§') {
             builder.append("pye");
             i++;
           } else {
             builder.append("pi");
           }
           break;
         case 'ã':
           builder.append("pu");
           break;
         case 'ã':
           builder.append("pe");
           break;
         case 'ã':
           if (ch2 == 'ã¦') {
             builder.append("pÅ");
             i++;
           } else {
             builder.append("po");
           }
           break;
         case 'ã´':
           if (ch2 == 'ã£' && ch3 == 'ã§') {
             builder.append("vye");
             i+= 2;
           } else {
             builder.append('v');
           }
           break;
         case 'ã¡':
           builder.append('a');
           break;
         case 'ã£':
           builder.append('i');
           break;
         case 'ã¥':
           builder.append('u');
           break;
         case 'ã§':
           builder.append('e');
           break;
         case 'ã©':
           builder.append('o');
           break;
         case 'ã®':
           builder.append("wa");
           break;
         case 'ã£':
           builder.append("ya");
           break;
         case 'ã¥':
           builder.append("yu");
           break;
         case 'ã§':
           builder.append("yo");
           break;
         case 'ã¼':
           break;
         default:
           builder.append(ch);
       }
     }
   }
 }
