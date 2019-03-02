 package org.apache.lucene.analysis.kr.utils;
 
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
 
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.apache.lucene.analysis.kr.morph.MorphException;
 
 public class SyllableUtil {
 
   public static int IDX_JOSA1 = 0; // ì¡°ì¬ì ì²«ìì ë¡ ì¬ì©ëë ìì  48ê°
   public static int IDX_JOSA2 = 1; // ì¡°ì¬ì ë ë²ì§¸ ì´ìì ìì ë¡ ì¬ì©ëë ìì  58ê°
   public static int IDX_EOMI1 = 2; // ì´ë¯¸ì ì²«ìì ë¡ ì¬ì©ëë ìì  72ê°
   public static int IDX_EOMI2 = 3; // ì´ë¯¸ì ë ë²ì§¸ ì´ìì ìì ë¡ ì¬ì©ëë ìì  105ê°
   public static int IDX_YONG1 = 4; // 1ìì  ì©ì¸ì ì¬ì©ëë ìì  362ê°
   public static int IDX_YONG2 = 5; // 2ìì  ì©ì¸ì ë§ì§ë§ ìì ë¡ ì¬ì©ëë ìì  316ê°
   public static int IDX_YONG3 = 6; // 3ìì  ì´ì ì©ì¸ì ë§ì§ë§ ìì ë¡ ì¬ì©ëë ìì  195ê°
   public static int IDX_CHEON1 = 7; // 1ìì  ì²´ì¸ì ì¬ì©ëë ìì  680ê°
   public static int IDX_CHEON2 = 8; // 2ìì  ì²´ì¸ì ë§ì§ë§ ìì ë¡ ì¬ì©ëë ìì  916ê°
   public static int IDX_CHEON3 = 9; // 3ìì  ì²´ì¸ì ë§ì§ë§ ìì ë¡ ì¬ì©ëë ìì  800ê°
   public static int IDX_CHEON4 = 10; // 4ìì  ì²´ì¸ì ë§ì§ë§ ìì ë¡ ì¬ì©ëë ìì  610ê°
   public static int IDX_CHEON5 = 11; // 5ìì  ì´ì ì²´ì¸ì ë§ì§ë§ ìì ë¡ ì¬ì©ëë ìì  330ê°
   public static int IDX_BUSA1 = 12; // 1ìì  ë¶ì¬ì ë§ì§ë§ ìì ë¡ ì¬ì©ëë ìì  191ê°
   public static int IDX_BUSA2 = 13; // 2ìì  ë¶ì¬ì ë§ì§ë§ ìì ë¡ ì¬ì©ëë ìì  519ê°
   public static int IDX_BUSA3 = 14; // 3ìì  ë¶ì¬ì ë§ì§ë§ ìì ë¡ ì¬ì©ëë ìì  139ê°
   public static int IDX_BUSA4 = 15; // 4ìì  ë¶ì¬ì ë§ì§ë§ ìì ë¡ ì¬ì©ëë ìì  366ê°
   public static int IDX_BUSA5 = 16; // 5ìì  ë¶ì¬ì ë§ì§ë§ ìì ë¡ ì¬ì©ëë ìì  79ê°
   public static int IDX_PRONOUN = 17; // ëëªì¬ì ë§ì§ë§ ìì ë¡ ì¬ì©ëë ìì  77ê°
   public static int IDX_EXCLAM = 18; // ê´íì¬ì ê°íì¬ì ë§ì§ë§ ìì ë¡ ì¬ì©ëë ìì  241ê°
   
   public static int IDX_YNPNA = 19; // (ì©ì¸+'-ã´')ì ìíì¬ ìì±ëë ìì  129ê°
   public static int IDX_YNPLA = 20; // (ì©ì¸+'-ã¹')ì ìí´ ìì±ëë ìì  129ê°
   public static int IDX_YNPMA = 21; // (ì©ì¸+'-ã')ì ìí´ ìì±ëë ìì  129ê°
   public static int IDX_YNPBA = 22; // (ì©ì¸+'-ã')ì ìí´ ìì±ëë ìì  129ê°
   public static int IDX_YNPAH = 23; // ëª¨ìì¼ë¡ ëëë ìì  129ê°ì¤ 'ã/ã/ã/ã/ã'ë¡ ëëë ê²ì´ ì ì´ë§ ì´ë¯¸ '-ì-'ê³¼ ê²°í©í  ë ìì±ëë ìì 
   public static int IDX_YNPOU = 24; // ëª¨ì 'ã/ã'ë¡ ëëë ìì ì´ 'ì/ì´'ë¡ ììëë ì´ë¯¸ë ì ì´ë§ ì´ë¯¸ '-ì-'ê³¼ ê²°í©í  ë ìì±ëë ìì 
   public static int IDX_YNPEI = 25; // ëª¨ì 'ã£'ë¡ ëëë ì©ì¸ì´ 'ì/ì´'ë¡ ììëë ì´ë¯¸ë ì ì´ë§ ì´ë¯¸ '-ì-'ê³¼ ê²°í©í  ë ìì±ëë ìì 
   public static int IDX_YNPOI = 26; // ëª¨ì 'ã'ë¡ ëëë ì©ì¸ì´ 'ì/ì´'ë¡ ììëë ì´ë¯¸ë ì ì´ë§ ì´ë¯¸ '-ì-'ê³¼ ê²°í©í  ë ìì±ëë ìì 
   public static int IDX_YNPLN = 27; // ë°ì¹¨ 'ã¹'ë¡ ëëë ì©ì¸ì´ ì´ë¯¸ '-ã´'ê³¼ ê²°í©í  ë ìì±ëë ìì 
   public static int IDX_IRRLO = 28; // 'ë¬' ë¶ê·ì¹(8ê°)ì ìíì¬ ìì±ëë ìì  : ë¬, ë 
   public static int IDX_IRRPLE = 29; // 'ë¥´' ë¶ê·ì¹(193ê°)ì ìíì¬ ìì±ëë ìì  
   public static int IDX_IRROO = 30; // 'ì°' ë¶ê·ì¹ì ìíì¬ ìì±ëë ìì  : í¼, í
   public static int IDX_IRROU = 31; // 'ì´' ë¶ê·ì¹ì ìíì¬ ìì±ëë ìì  : í´, í
   public static int IDX_IRRDA = 32; // 'ã·' ë¶ê·ì¹(37ê°)ì ìíì¬ ìì±ëë ìì 
   public static int IDX_IRRBA = 33; // 'ã' ë¶ê·ì¹(446ê°)ì ìíì¬ ìì±ëë ìì 
   public static int IDX_IRRSA = 34; // 'ã' ë¶ê·ì¹(39ê°)ì ìíì¬ ìì±ëë ìì 
   public static int IDX_IRRHA = 35; // 'ã' ë¶ê·ì¹(96ê°)ì ìíì¬ ìì±ëë ìì  
   public static int IDX_PEND = 36; // ì ì´ë§ ì´ë¯¸ : ì ì¨ ì ì ì ê² 
   
   public static int IDX_YNPEOMI = 37; // ì©ì¸ì´ ì´ë¯¸ì ê²°í©í  ë ìì±ëë ìì ì ì 734ê°
   
   /**   ì©ì¸ì íì¸µ ííë¡ë§ ì¬ì©ëë ìì  */
   public static int IDX_WDSURF = 38; 
   
   public static int IDX_EOGAN = 39; // ì´ë¯¸ ëë ì´ë¯¸ì ë³íì¼ë¡ ì¡´ì¬í  ì ìë ì (ì¦ IDX_EOMI ì´ê±°ë IDX_YNPNA ì´íì 1ì´ ìë ìì )
   
   private static List Syllables;  // ìì í¹ì± ì ë³´
   
   /**
    * ì¸ë±ì¤ ê°ì í´ë¹íë ìì ì í¹ì±ì ë°ííë¤.
    * ìì ëë ì«ìì¼ ê²½ì°ë ëª¨ë í´ë¹ì´ ìëë¯ë¡ ê°ì¥ ë§ì§ë§ ê¸ìì¸ 'í£' ì ìì í¹ì±ì ë°ííë¤.
    * 
    * @param idx 'ê°'(0xAC00)ì´ 0ë¶í° ì ëì½ëì ìí´ íê¸ìì ì ìì°¨ì ì¼ë¡ ëì´í ê°
    * @throws Exception 
    */
   public static char[] getFeature(int idx)  throws MorphException {
     
     if(Syllables==null) Syllables = getSyllableFeature();
   
     if(idx<0||idx>=Syllables.size()) 
       return (char[])Syllables.get(Syllables.size()-1);
     else 
       return (char[])Syllables.get(idx);
     
   }
   
   /**
    * ê° ìì ì í¹ì±ì ë°ííë¤.
    * @param syl  ìì  íë
    * @throws Exception 
    */
   public static char[] getFeature(char syl) throws MorphException {
     
     int idx = syl - 0xAC00;
     return getFeature(idx);
     
   }
   
   /**
    * ìì ì ë³´í¹ì±ì íì¼ìì ì½ëë¤.
    * 
    * @throws Exception
    */  
   private static List getSyllableFeature() throws MorphException {
   
     try{
       Syllables = new ArrayList<char[]>();
 
       List<String> line = FileUtil.readLines(KoreanEnv.getInstance().getValue(KoreanEnv.FILE_SYLLABLE_FEATURE),"UTF-8");  
       for(int i=0;i<line.size();i++) {        
         if(i!=0)
           Syllables.add(line.get(i).toCharArray());
       }
     }catch(IOException e) {
       throw new MorphException(e.getMessage());
     } 
 
     return Syllables;
   }  
   
   public static boolean isAlpanumeric(char ch) {
     return (ch>='0'&&ch<='z');
   }
 }
