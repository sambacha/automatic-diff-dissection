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
 
import java.util.HashMap;
import java.util.Map;

 import org.apache.lucene.util.LuceneTestCase;
 
 public class TestToStringUtil extends LuceneTestCase {
   public void testPOS() {
     assertEquals("noun-suffix-verbal", ToStringUtil.getPOSTranslation("åè©-æ¥å°¾-ãµå¤æ¥ç¶"));
   }
   
   public void testHepburn() {
     assertEquals("majan", ToStringUtil.getRomanization("ãã¼ã¸ã£ã³"));
     assertEquals("uroncha", ToStringUtil.getRomanization("ã¦ã¼ã­ã³ãã£"));
     assertEquals("chahan", ToStringUtil.getRomanization("ãã£ã¼ãã³"));
     assertEquals("chashu", ToStringUtil.getRomanization("ãã£ã¼ã·ã¥ã¼"));
     assertEquals("shumai", ToStringUtil.getRomanization("ã·ã¥ã¼ãã¤"));
   }
  
  // see http://en.wikipedia.org/wiki/Hepburn_romanization,
  // but this isnt even thorough or really probably what we want!
  public void testHepburnTable() {
    Map<String,String> table = new HashMap<String,String>() {{
      put("ã¢", "a");   put("ã¤", "i");   put("ã¦", "u");   put("ã¨", "e");   put("ãª", "o");
      put("ã«", "ka");  put("ã­", "ki");  put("ã¯", "ku");  put("ã±", "ke");  put("ã³", "ko");
      put("ãµ", "sa");  put("ã·", "shi"); put("ã¹", "su");  put("ã»", "se");  put("ã½", "so");
      put("ã¿", "ta");  put("ã", "chi"); put("ã", "tsu"); put("ã", "te");  put("ã", "to");
      put("ã", "na");  put("ã", "ni");  put("ã", "nu");  put("ã", "ne");  put("ã", "no");
      put("ã", "ha");  put("ã", "hi");  put("ã", "fu");  put("ã", "he");  put("ã", "ho");
      put("ã", "ma");  put("ã", "mi");  put("ã ", "mu");  put("ã¡", "me");  put("ã¢", "mo");
      put("ã¤", "ya");                  put("ã¦", "yu");                 put("ã¨", "yo");
      put("ã©", "ra");  put("ãª", "ri");  put("ã«", "ru");  put("ã¬", "re");  put("ã­", "ro");
      put("ã¯", "wa");  put("ã°", "i");                   put("ã±", "e");   put("ã²", "o");
                                                                     put("ã³", "n");
      put("ã¬", "ga");  put("ã®", "gi");  put("ã°", "gu");  put("ã²", "ge");  put("ã´", "go");
      put("ã¶", "za");  put("ã¸", "ji");  put("ãº", "zu");  put("ã¼", "ze");  put("ã¾", "zo");
      put("ã", "da");  put("ã", "ji");  put("ã", "zu");  put("ã", "de");  put("ã", "do");
      put("ã", "ba");  put("ã", "bi");  put("ã", "bu");  put("ã", "be");  put("ã", "bo");
      put("ã", "pa");  put("ã", "pi");  put("ã", "pu");  put("ã", "pe");  put("ã", "po");
      
                   put("ã­ã£", "kya");   put("ã­ã¥", "kyu");   put("ã­ã§", "kyo");
                   put("ã·ã£", "sha");   put("ã·ã¥", "shu");   put("ã·ã§", "sho");
                   put("ãã£", "cha");   put("ãã¥", "chu");   put("ãã§", "cho");
                   put("ãã£", "nya");   put("ãã¥", "nyu");   put("ãã§", "nyo");
                   put("ãã£", "hya");   put("ãã¥", "hyu");   put("ãã§", "hyo");
                   put("ãã£", "mya");   put("ãã¥", "myu");   put("ãã§", "myo");
                   put("ãªã£", "rya");   put("ãªã¥", "ryu");   put("ãªã§", "ryo");
                   put("ã®ã£", "gya");   put("ã®ã¥", "gyu");   put("ã®ã§", "gyo");
                   put("ã¸ã£", "ja");    put("ã¸ã¥", "ju");    put("ã¸ã§", "jo");
                   put("ãã£", "ja");    put("ãã¥", "ju");    put("ãã§", "jo");
                   put("ãã£", "bya");   put("ãã¥", "byu");   put("ãã§", "byo");
                   put("ãã£", "pya");   put("ãã¥", "pyu");   put("ãã§", "pyo");
      
                      put("ã¤ã£", "yi");                 put("ã¤ã§", "ye");
      put("ã¦ã¡", "wa"); put("ã¦ã£", "wi"); put("ã¦ã¥", "wu"); put("ã¦ã§", "we"); put("ã¦ã©", "wo");
                                     put("ã¦ã¥", "wyu");
                                   // TODO: really should be vu
      put("ã´ã¡", "va"); put("ã´ã£", "vi"); put("ã´", "v");  put("ã´ã§", "ve"); put("ã´ã©", "vo");
      put("ã´ã£", "vya");              put("ã´ã¥", "vyu"); put("ã´ã£ã§", "vye"); put("ã´ã§", "vyo");
                                                     put("ã­ã§", "kye");
                                                     put("ã®ã§", "gye");
      put("ã¯ã¡", "kwa"); put("ã¯ã£", "kwi");              put("ã¯ã§", "kwe"); put("ã¯ã©", "kwo");
      put("ã¯ã®", "kwa");
      put("ã°ã¡", "gwa"); put("ã°ã£", "gwi");              put("ã°ã§", "gwe"); put("ã°ã©", "gwo");
      put("ã°ã®", "gwa");
                                                     put("ã·ã§", "she");
                                                     put("ã¸ã§", "je");
                       put("ã¹ã£", "si");
                       put("ãºã£", "zi");
                                                     put("ãã§", "che");
      put("ãã¡", "tsa"); put("ãã£", "tsi");              put("ãã§", "tse"); put("ãã©", "tso");
                                     put("ãã¥", "tsyu");
                      put("ãã£", "ti"); put("ãã¥", "tu");
                                     put("ãã¥", "tyu");
                      put("ãã£", "di"); put("ãã¥", "du");
                                     put("ãã¥", "dyu");
                                                     put("ãã§", "nye");
                                                     put("ãã§", "hye");
                                                     put("ãã§", "bye");
                                                     put("ãã§", "pye");
      put("ãã¡", "fa");  put("ãã£", "fi");               put("ãã§", "fe");  put("ãã©", "fo");
      put("ãã£", "fya");              put("ãã¥", "fyu"); put("ãã£ã§", "fye"); put("ãã§", "fyo");
                                    put("ãã¥", "hu");
                                                     put("ãã§", "mye");
                                                     put("ãªã§", "rye");
      put("ã©ã", "la");  put("ãªã", "li");  put("ã«ã", "lu");  put("ã¬ã", "le");  put("ã­ã", "lo");
      put("ã·", "va");  put("ã¸", "vi");                  put("ã¹", "ve");  put("ãº", "vo");
    }};
    
    for (String s : table.keySet()) {
      assertEquals(s, table.get(s), ToStringUtil.getRomanization(s));
    }
  }
 }
