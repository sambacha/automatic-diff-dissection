 package org.apache.lucene.analysis.lv;
 
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
 import java.io.Reader;
 
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.MockTokenizer;
 import org.apache.lucene.analysis.Tokenizer;
 import org.apache.lucene.analysis.util.ReusableAnalyzerBase;
 
 /**
  * Basic tests for {@link LatvianStemmer}
  */
 public class TestLatvianStemmer extends BaseTokenStreamTestCase {
   private Analyzer a = new ReusableAnalyzerBase() {
     @Override
     protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
      Tokenizer tokenizer = new MockTokenizer(reader, MockTokenizer.WHITESPACE, false);
       return new TokenStreamComponents(tokenizer, new LatvianStemFilter(tokenizer));
     }
   };
   
   public void testNouns1() throws IOException {
     // decl. I
     checkOneTerm(a, "tÄvs",   "tÄv"); // nom. sing.
     checkOneTerm(a, "tÄvi",   "tÄv"); // nom. pl.
     checkOneTerm(a, "tÄva",   "tÄv"); // gen. sing.
     checkOneTerm(a, "tÄvu",   "tÄv"); // gen. pl.
     checkOneTerm(a, "tÄvam",  "tÄv"); // dat. sing.
     checkOneTerm(a, "tÄviem", "tÄv"); // dat. pl.
     checkOneTerm(a, "tÄvu",   "tÄv"); // acc. sing.
     checkOneTerm(a, "tÄvus",  "tÄv"); // acc. pl.
     checkOneTerm(a, "tÄvÄ",   "tÄv"); // loc. sing.
     checkOneTerm(a, "tÄvos",  "tÄv"); // loc. pl.
     checkOneTerm(a, "tÄvs",   "tÄv"); // voc. sing.
     checkOneTerm(a, "tÄvi",   "tÄv"); // voc. pl.
   }
   
   /**
    * decl II nouns with (s,t) -> Å¡ and (d,z) -> Å¾
    * palatalization will generally conflate to two stems
    * due to the ambiguity (plural and singular).
    */
   public void testNouns2() throws IOException {
     // decl. II
     
     // c -> Ä palatalization
     checkOneTerm(a, "lÄcis",  "lÄc"); // nom. sing.
     checkOneTerm(a, "lÄÄi",   "lÄc"); // nom. pl.
     checkOneTerm(a, "lÄÄa",   "lÄc"); // gen. sing.
     checkOneTerm(a, "lÄÄu",   "lÄc"); // gen. pl.
     checkOneTerm(a, "lÄcim",  "lÄc"); // dat. sing.
     checkOneTerm(a, "lÄÄiem", "lÄc"); // dat. pl.
     checkOneTerm(a, "lÄci",   "lÄc"); // acc. sing.
     checkOneTerm(a, "lÄÄus",  "lÄc"); // acc. pl.
     checkOneTerm(a, "lÄcÄ«",   "lÄc"); // loc. sing.
     checkOneTerm(a, "lÄÄos",  "lÄc"); // loc. pl.
     checkOneTerm(a, "lÄci",   "lÄc"); // voc. sing.
     checkOneTerm(a, "lÄÄi",   "lÄc"); // voc. pl.
     
     // n -> Å palatalization
     checkOneTerm(a, "akmens",   "akmen"); // nom. sing.
     checkOneTerm(a, "akmeÅi",   "akmen"); // nom. pl.
     checkOneTerm(a, "akmens",   "akmen"); // gen. sing.
     checkOneTerm(a, "akmeÅu",   "akmen"); // gen. pl.
     checkOneTerm(a, "akmenim",  "akmen"); // dat. sing.
     checkOneTerm(a, "akmeÅiem", "akmen"); // dat. pl.
     checkOneTerm(a, "akmeni",   "akmen"); // acc. sing.
     checkOneTerm(a, "akmeÅus",  "akmen"); // acc. pl.
     checkOneTerm(a, "akmenÄ«",   "akmen"); // loc. sing.
     checkOneTerm(a, "akmeÅos",  "akmen"); // loc. pl.
     checkOneTerm(a, "akmens",   "akmen"); // voc. sing.
     checkOneTerm(a, "akmeÅi",   "akmen"); // voc. pl.
     
     // no palatalization
     checkOneTerm(a, "kurmis",   "kurm"); // nom. sing.
     checkOneTerm(a, "kurmji",   "kurm"); // nom. pl.
     checkOneTerm(a, "kurmja",   "kurm"); // gen. sing.
     checkOneTerm(a, "kurmju",   "kurm"); // gen. pl.
     checkOneTerm(a, "kurmim",   "kurm"); // dat. sing.
     checkOneTerm(a, "kurmjiem", "kurm"); // dat. pl.
     checkOneTerm(a, "kurmi",    "kurm"); // acc. sing.
     checkOneTerm(a, "kurmjus",  "kurm"); // acc. pl.
     checkOneTerm(a, "kurmÄ«",    "kurm"); // loc. sing.
     checkOneTerm(a, "kurmjos",  "kurm"); // loc. pl.
     checkOneTerm(a, "kurmi",    "kurm"); // voc. sing.
     checkOneTerm(a, "kurmji",   "kurm"); // voc. pl.
   }
   
   public void testNouns3() throws IOException {
     // decl III
     checkOneTerm(a, "lietus",  "liet"); // nom. sing.
     checkOneTerm(a, "lieti",   "liet"); // nom. pl.
     checkOneTerm(a, "lietus",  "liet"); // gen. sing.
     checkOneTerm(a, "lietu",   "liet"); // gen. pl.
     checkOneTerm(a, "lietum",  "liet"); // dat. sing.
     checkOneTerm(a, "lietiem", "liet"); // dat. pl.
     checkOneTerm(a, "lietu",   "liet"); // acc. sing.
     checkOneTerm(a, "lietus",  "liet"); // acc. pl.
     checkOneTerm(a, "lietÅ«",   "liet"); // loc. sing.
     checkOneTerm(a, "lietos",  "liet"); // loc. pl.
     checkOneTerm(a, "lietus",  "liet"); // voc. sing.
     checkOneTerm(a, "lieti",   "liet"); // voc. pl.
   }
   
   public void testNouns4() throws IOException {
     // decl IV
     checkOneTerm(a, "lapa",  "lap"); // nom. sing.
     checkOneTerm(a, "lapas", "lap"); // nom. pl.
     checkOneTerm(a, "lapas", "lap"); // gen. sing.
     checkOneTerm(a, "lapu",  "lap"); // gen. pl.
     checkOneTerm(a, "lapai", "lap"); // dat. sing.
     checkOneTerm(a, "lapÄm", "lap"); // dat. pl.
     checkOneTerm(a, "lapu",  "lap"); // acc. sing.
     checkOneTerm(a, "lapas", "lap"); // acc. pl.
     checkOneTerm(a, "lapÄ",  "lap"); // loc. sing.
     checkOneTerm(a, "lapÄs", "lap"); // loc. pl.
     checkOneTerm(a, "lapa",  "lap"); // voc. sing.
     checkOneTerm(a, "lapas", "lap"); // voc. pl.
     
     checkOneTerm(a, "puika",  "puik"); // nom. sing.
     checkOneTerm(a, "puikas", "puik"); // nom. pl.
     checkOneTerm(a, "puikas", "puik"); // gen. sing.
     checkOneTerm(a, "puiku",  "puik"); // gen. pl.
     checkOneTerm(a, "puikam", "puik"); // dat. sing.
     checkOneTerm(a, "puikÄm", "puik"); // dat. pl.
     checkOneTerm(a, "puiku",  "puik"); // acc. sing.
     checkOneTerm(a, "puikas", "puik"); // acc. pl.
     checkOneTerm(a, "puikÄ",  "puik"); // loc. sing.
     checkOneTerm(a, "puikÄs", "puik"); // loc. pl.
     checkOneTerm(a, "puika",  "puik"); // voc. sing.
     checkOneTerm(a, "puikas", "puik"); // voc. pl.
   }
   
   /**
    * Genitive plural forms with (s,t) -> Å¡ and (d,z) -> Å¾
    * will not conflate due to ambiguity.
    */
   public void testNouns5() throws IOException {
     // decl V
     // l -> Ä¼ palatalization
     checkOneTerm(a, "egle",  "egl"); // nom. sing.
     checkOneTerm(a, "egles", "egl"); // nom. pl.
     checkOneTerm(a, "egles", "egl"); // gen. sing.
     checkOneTerm(a, "egÄ¼u",  "egl"); // gen. pl.
     checkOneTerm(a, "eglei", "egl"); // dat. sing.
     checkOneTerm(a, "eglÄm", "egl"); // dat. pl.
     checkOneTerm(a, "egli",  "egl"); // acc. sing.
     checkOneTerm(a, "egles", "egl"); // acc. pl.
     checkOneTerm(a, "eglÄ",  "egl"); // loc. sing.
     checkOneTerm(a, "eglÄs", "egl"); // loc. pl.
     checkOneTerm(a, "egle",  "egl"); // voc. sing.
     checkOneTerm(a, "egles", "egl"); // voc. pl.
   }
   
   public void testNouns6() throws IOException {
     // decl VI
     
     // no palatalization
     checkOneTerm(a, "govs",  "gov"); // nom. sing.
     checkOneTerm(a, "govis", "gov"); // nom. pl.
     checkOneTerm(a, "govs",  "gov"); // gen. sing.
     checkOneTerm(a, "govju", "gov"); // gen. pl.
     checkOneTerm(a, "govij", "gov"); // dat. sing.
     checkOneTerm(a, "govÄ«m", "gov"); // dat. pl.
     checkOneTerm(a, "govi ", "gov"); // acc. sing.
     checkOneTerm(a, "govis", "gov"); // acc. pl.
     checkOneTerm(a, "govi ", "gov"); // inst. sing.
     checkOneTerm(a, "govÄ«m", "gov"); // inst. pl.
     checkOneTerm(a, "govÄ«",  "gov"); // loc. sing.
     checkOneTerm(a, "govÄ«s", "gov"); // loc. pl.
     checkOneTerm(a, "govs",  "gov"); // voc. sing.
     checkOneTerm(a, "govis", "gov"); // voc. pl.
   }
   
   public void testAdjectives() throws IOException {
     checkOneTerm(a, "zils",     "zil"); // indef. nom. masc. sing.
     checkOneTerm(a, "zilais",   "zil"); // def. nom. masc. sing.
     checkOneTerm(a, "zili",     "zil"); // indef. nom. masc. pl.
     checkOneTerm(a, "zilie",    "zil"); // def. nom. masc. pl.
     checkOneTerm(a, "zila",     "zil"); // indef. nom. fem. sing.
     checkOneTerm(a, "zilÄ",     "zil"); // def. nom. fem. sing.
     checkOneTerm(a, "zilas",    "zil"); // indef. nom. fem. pl.
     checkOneTerm(a, "zilÄs",    "zil"); // def. nom. fem. pl.
     checkOneTerm(a, "zila",     "zil"); // indef. gen. masc. sing.
     checkOneTerm(a, "zilÄ",     "zil"); // def. gen. masc. sing.
     checkOneTerm(a, "zilu",     "zil"); // indef. gen. masc. pl.
     checkOneTerm(a, "zilo",     "zil"); // def. gen. masc. pl.
     checkOneTerm(a, "zilas",    "zil"); // indef. gen. fem. sing.
     checkOneTerm(a, "zilÄs",    "zil"); // def. gen. fem. sing.
     checkOneTerm(a, "zilu",     "zil"); // indef. gen. fem. pl.
     checkOneTerm(a, "zilo",     "zil"); // def. gen. fem. pl.
     checkOneTerm(a, "zilam",    "zil"); // indef. dat. masc. sing.
     checkOneTerm(a, "zilajam",  "zil"); // def. dat. masc. sing.
     checkOneTerm(a, "ziliem",   "zil"); // indef. dat. masc. pl.
     checkOneTerm(a, "zilajiem", "zil"); // def. dat. masc. pl.
     checkOneTerm(a, "zilai",    "zil"); // indef. dat. fem. sing.
     checkOneTerm(a, "zilajai",  "zil"); // def. dat. fem. sing.
     checkOneTerm(a, "zilÄm",    "zil"); // indef. dat. fem. pl.
     checkOneTerm(a, "zilajÄm",  "zil"); // def. dat. fem. pl.
     checkOneTerm(a, "zilu",     "zil"); // indef. acc. masc. sing.
     checkOneTerm(a, "zilo",     "zil"); // def. acc. masc. sing.
     checkOneTerm(a, "zilus",    "zil"); // indef. acc. masc. pl.
     checkOneTerm(a, "zilos",    "zil"); // def. acc. masc. pl.
     checkOneTerm(a, "zilu",     "zil"); // indef. acc. fem. sing.
     checkOneTerm(a, "zilo",     "zil"); // def. acc. fem. sing.
     checkOneTerm(a, "zilÄs",    "zil"); // indef. acc. fem. pl.
     checkOneTerm(a, "zilÄs",    "zil"); // def. acc. fem. pl.
     checkOneTerm(a, "zilÄ",     "zil"); // indef. loc. masc. sing.
     checkOneTerm(a, "zilajÄ",   "zil"); // def. loc. masc. sing.
     checkOneTerm(a, "zilos",    "zil"); // indef. loc. masc. pl.
     checkOneTerm(a, "zilajos",  "zil"); // def. loc. masc. pl.
     checkOneTerm(a, "zilÄ",     "zil"); // indef. loc. fem. sing.
     checkOneTerm(a, "zilajÄ",   "zil"); // def. loc. fem. sing.
     checkOneTerm(a, "zilÄs",    "zil"); // indef. loc. fem. pl.
     checkOneTerm(a, "zilajÄs",  "zil"); // def. loc. fem. pl.
     checkOneTerm(a, "zilais",   "zil"); // voc. masc. sing.
     checkOneTerm(a, "zilie",    "zil"); // voc. masc. pl.
     checkOneTerm(a, "zilÄ",     "zil"); // voc. fem. sing.
     checkOneTerm(a, "zilÄs",    "zil"); // voc. fem. pl.
   }
   
   /**
    * Note: we intentionally don't handle the ambiguous
    * (s,t) -> Å¡ and (d,z) -> Å¾
    */
   public void testPalatalization() throws IOException {
     checkOneTerm(a, "krÄsns", "krÄsn"); // nom. sing.
     checkOneTerm(a, "krÄÅ¡Åu", "krÄsn"); // gen. pl.
     checkOneTerm(a, "zvaigzne", "zvaigzn"); // nom. sing.
     checkOneTerm(a, "zvaigÅ¾Åu", "zvaigzn"); // gen. pl.
     checkOneTerm(a, "kÄpslis", "kÄpsl"); // nom. sing.
     checkOneTerm(a, "kÄpÅ¡Ä¼u",  "kÄpsl"); // gen. pl.
     checkOneTerm(a, "zizlis", "zizl"); // nom. sing.
     checkOneTerm(a, "ziÅ¾Ä¼u",  "zizl"); // gen. pl.
     checkOneTerm(a, "vilnis", "viln"); // nom. sing.
     checkOneTerm(a, "viÄ¼Åu",  "viln"); // gen. pl.
     checkOneTerm(a, "lelle", "lell"); // nom. sing.
     checkOneTerm(a, "leÄ¼Ä¼u", "lell"); // gen. pl.
     checkOneTerm(a, "pinne", "pinn"); // nom. sing.
     checkOneTerm(a, "piÅÅu", "pinn"); // gen. pl.
     checkOneTerm(a, "rÄ«kste", "rÄ«kst"); // nom. sing.
     checkOneTerm(a, "rÄ«kÅ¡u",  "rÄ«kst"); // gen. pl.
   }
   
   /**
    * Test some length restrictions, we require a 3+ char stem,
    * with at least one vowel.
    */
   public void testLength() throws IOException {
     checkOneTerm(a, "usa", "usa"); // length
     checkOneTerm(a, "60ms", "60ms"); // vowel count
   }
 }
