 package org.apache.lucene.analysis.bg;
 
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
 import java.io.StringReader;
 
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.CharArraySet;
 import org.apache.lucene.analysis.KeywordMarkerFilter;
import org.apache.lucene.analysis.WhitespaceTokenizer;
 import org.apache.lucene.util.Version;
 
 /**
  * Test the Bulgarian Stemmer
  */
 public class TestBulgarianStemmer extends BaseTokenStreamTestCase {
   /**
    * Test showing how masculine noun forms conflate. An example noun for each
    * common (and some rare) plural pattern is listed.
    */
   public void testMasculineNouns() throws IOException {
     BulgarianAnalyzer a = new BulgarianAnalyzer(TEST_VERSION_CURRENT);
     
     // -Ð¸ pattern
     assertAnalyzesTo(a, "Ð³ÑÐ°Ð´", new String[] {"Ð³ÑÐ°Ð´"});
     assertAnalyzesTo(a, "Ð³ÑÐ°Ð´Ð°", new String[] {"Ð³ÑÐ°Ð´"});
     assertAnalyzesTo(a, "Ð³ÑÐ°Ð´ÑÑ", new String[] {"Ð³ÑÐ°Ð´"});
     assertAnalyzesTo(a, "Ð³ÑÐ°Ð´Ð¾Ð²Ðµ", new String[] {"Ð³ÑÐ°Ð´"});
     assertAnalyzesTo(a, "Ð³ÑÐ°Ð´Ð¾Ð²ÐµÑÐµ", new String[] {"Ð³ÑÐ°Ð´"});
     
     // -Ð¾Ð²Ðµ pattern
     assertAnalyzesTo(a, "Ð½Ð°ÑÐ¾Ð´", new String[] {"Ð½Ð°ÑÐ¾Ð´"});
     assertAnalyzesTo(a, "Ð½Ð°ÑÐ¾Ð´Ð°", new String[] {"Ð½Ð°ÑÐ¾Ð´"});
     assertAnalyzesTo(a, "Ð½Ð°ÑÐ¾Ð´ÑÑ", new String[] {"Ð½Ð°ÑÐ¾Ð´"});
     assertAnalyzesTo(a, "Ð½Ð°ÑÐ¾Ð´Ð¸", new String[] {"Ð½Ð°ÑÐ¾Ð´"});
     assertAnalyzesTo(a, "Ð½Ð°ÑÐ¾Ð´Ð¸ÑÐµ", new String[] {"Ð½Ð°ÑÐ¾Ð´"});
     assertAnalyzesTo(a, "Ð½Ð°ÑÐ¾Ð´Ðµ", new String[] {"Ð½Ð°ÑÐ¾Ð´"});
     
     // -Ð¸ÑÐ° pattern
     assertAnalyzesTo(a, "Ð¿ÑÑ", new String[] {"Ð¿ÑÑ"});
     assertAnalyzesTo(a, "Ð¿ÑÑÑ", new String[] {"Ð¿ÑÑ"});
     assertAnalyzesTo(a, "Ð¿ÑÑÑÑ", new String[] {"Ð¿ÑÑ"});
     assertAnalyzesTo(a, "Ð¿ÑÑÐ¸ÑÐ°", new String[] {"Ð¿ÑÑ"});
     assertAnalyzesTo(a, "Ð¿ÑÑÐ¸ÑÐ°ÑÐ°", new String[] {"Ð¿ÑÑ"});
     
     // -ÑÐµÑÐ° pattern
     assertAnalyzesTo(a, "Ð³ÑÐ°Ð´ÐµÑ", new String[] {"Ð³ÑÐ°Ð´ÐµÑ"});
     assertAnalyzesTo(a, "Ð³ÑÐ°Ð´ÐµÑÐ°", new String[] {"Ð³ÑÐ°Ð´ÐµÑ"});
     assertAnalyzesTo(a, "Ð³ÑÐ°Ð´ÐµÑÑÑ", new String[] {"Ð³ÑÐ°Ð´ÐµÑ"});
     /* note the below forms conflate with each other, but not the rest */
     assertAnalyzesTo(a, "Ð³ÑÐ°Ð´Ð¾Ð²ÑÐµ", new String[] {"Ð³ÑÐ°Ð´Ð¾Ð²Ñ"});
     assertAnalyzesTo(a, "Ð³ÑÐ°Ð´Ð¾Ð²ÑÐµÑÐµ", new String[] {"Ð³ÑÐ°Ð´Ð¾Ð²Ñ"});
     
     // -Ð¾Ð²ÑÐ¸ pattern
     assertAnalyzesTo(a, "Ð´ÑÐ´Ð¾", new String[] {"Ð´ÑÐ´"});
     assertAnalyzesTo(a, "Ð´ÑÐ´Ð¾ÑÐ¾", new String[] {"Ð´ÑÐ´"});
     assertAnalyzesTo(a, "Ð´ÑÐ´Ð¾Ð²ÑÐ¸", new String[] {"Ð´ÑÐ´"});
     assertAnalyzesTo(a, "Ð´ÑÐ´Ð¾Ð²ÑÐ¸ÑÐµ", new String[] {"Ð´ÑÐ´"});
     
     // -Ðµ pattern
     assertAnalyzesTo(a, "Ð¼ÑÐ¶", new String[] {"Ð¼ÑÐ¶"});
     assertAnalyzesTo(a, "Ð¼ÑÐ¶Ð°", new String[] {"Ð¼ÑÐ¶"});
     assertAnalyzesTo(a, "Ð¼ÑÐ¶Ðµ", new String[] {"Ð¼ÑÐ¶"});
     assertAnalyzesTo(a, "Ð¼ÑÐ¶ÐµÑÐµ", new String[] {"Ð¼ÑÐ¶"});
     assertAnalyzesTo(a, "Ð¼ÑÐ¶Ð¾", new String[] {"Ð¼ÑÐ¶"});
     /* word is too short, will not remove -ÑÑ */
     assertAnalyzesTo(a, "Ð¼ÑÐ¶ÑÑ", new String[] {"Ð¼ÑÐ¶ÑÑ"});
     
     // -Ð° pattern
     assertAnalyzesTo(a, "ÐºÑÐ°Ðº", new String[] {"ÐºÑÐ°Ðº"});
     assertAnalyzesTo(a, "ÐºÑÐ°ÐºÐ°", new String[] {"ÐºÑÐ°Ðº"});
     assertAnalyzesTo(a, "ÐºÑÐ°ÐºÑÑ", new String[] {"ÐºÑÐ°Ðº"});
     assertAnalyzesTo(a, "ÐºÑÐ°ÐºÐ°ÑÐ°", new String[] {"ÐºÑÐ°Ðº"});
     
     // Ð±ÑÐ°Ñ
     assertAnalyzesTo(a, "Ð±ÑÐ°Ñ", new String[] {"Ð±ÑÐ°Ñ"});
     assertAnalyzesTo(a, "Ð±ÑÐ°ÑÐ°", new String[] {"Ð±ÑÐ°Ñ"});
     assertAnalyzesTo(a, "Ð±ÑÐ°ÑÑÑ", new String[] {"Ð±ÑÐ°Ñ"});
     assertAnalyzesTo(a, "Ð±ÑÐ°ÑÑ", new String[] {"Ð±ÑÐ°Ñ"});
     assertAnalyzesTo(a, "Ð±ÑÐ°ÑÑÑÐ°", new String[] {"Ð±ÑÐ°Ñ"});
     assertAnalyzesTo(a, "Ð±ÑÐ°ÑÐµ", new String[] {"Ð±ÑÐ°Ñ"});
   }
   
   /**
    * Test showing how feminine noun forms conflate
    */
   public void testFeminineNouns() throws IOException {
     BulgarianAnalyzer a = new BulgarianAnalyzer(TEST_VERSION_CURRENT);
     
     assertAnalyzesTo(a, "Ð²ÐµÑÑ", new String[] {"Ð²ÐµÑÑ"});
     assertAnalyzesTo(a, "Ð²ÐµÑÑÑÐ°", new String[] {"Ð²ÐµÑÑ"});
     assertAnalyzesTo(a, "Ð²ÐµÑÑÐ¸", new String[] {"Ð²ÐµÑÑ"});
     assertAnalyzesTo(a, "Ð²ÐµÑÑÐ¸ÑÐµ", new String[] {"Ð²ÐµÑÑ"});
   }
   
   /**
    * Test showing how neuter noun forms conflate an example noun for each common
    * plural pattern is listed
    */
   public void testNeuterNouns() throws IOException {
     BulgarianAnalyzer a = new BulgarianAnalyzer(TEST_VERSION_CURRENT);
     
     // -Ð° pattern
     assertAnalyzesTo(a, "Ð´ÑÑÐ²Ð¾", new String[] {"Ð´ÑÑÐ²"});
     assertAnalyzesTo(a, "Ð´ÑÑÐ²Ð¾ÑÐ¾", new String[] {"Ð´ÑÑÐ²"});
     assertAnalyzesTo(a, "Ð´ÑÑÐ²Ð°", new String[] {"Ð´ÑÑÐ²"});
     assertAnalyzesTo(a, "Ð´ÑÑÐ²ÐµÑÐ°", new String[] {"Ð´ÑÑÐ²"});
     assertAnalyzesTo(a, "Ð´ÑÑÐ²Ð°ÑÐ°", new String[] {"Ð´ÑÑÐ²"});
     assertAnalyzesTo(a, "Ð´ÑÑÐ²ÐµÑÐ°ÑÐ°", new String[] {"Ð´ÑÑÐ²"});
     
     // -ÑÐ° pattern
     assertAnalyzesTo(a, "Ð¼Ð¾ÑÐµ", new String[] {"Ð¼Ð¾Ñ"});
     assertAnalyzesTo(a, "Ð¼Ð¾ÑÐµÑÐ¾", new String[] {"Ð¼Ð¾Ñ"});
     assertAnalyzesTo(a, "Ð¼Ð¾ÑÐµÑÐ°", new String[] {"Ð¼Ð¾Ñ"});
     assertAnalyzesTo(a, "Ð¼Ð¾ÑÐµÑÐ°ÑÐ°", new String[] {"Ð¼Ð¾Ñ"});
     
     // -Ñ pattern
     assertAnalyzesTo(a, "Ð¸Ð·ÐºÐ»ÑÑÐµÐ½Ð¸Ðµ", new String[] {"Ð¸Ð·ÐºÐ»ÑÑÐµÐ½Ð¸"});
     assertAnalyzesTo(a, "Ð¸Ð·ÐºÐ»ÑÑÐµÐ½Ð¸ÐµÑÐ¾", new String[] {"Ð¸Ð·ÐºÐ»ÑÑÐµÐ½Ð¸"});
     assertAnalyzesTo(a, "Ð¸Ð·ÐºÐ»ÑÑÐµÐ½Ð¸ÑÑÐ°", new String[] {"Ð¸Ð·ÐºÐ»ÑÑÐµÐ½Ð¸"});
     /* note the below form in this example does not conflate with the rest */
     assertAnalyzesTo(a, "Ð¸Ð·ÐºÐ»ÑÑÐµÐ½Ð¸Ñ", new String[] {"Ð¸Ð·ÐºÐ»ÑÑÐ½"});
   }
   
   /**
    * Test showing how adjectival forms conflate
    */
   public void testAdjectives() throws IOException {
     BulgarianAnalyzer a = new BulgarianAnalyzer(TEST_VERSION_CURRENT);
     assertAnalyzesTo(a, "ÐºÑÐ°ÑÐ¸Ð²", new String[] {"ÐºÑÐ°ÑÐ¸Ð²"});
     assertAnalyzesTo(a, "ÐºÑÐ°ÑÐ¸Ð²Ð¸Ñ", new String[] {"ÐºÑÐ°ÑÐ¸Ð²"});
     assertAnalyzesTo(a, "ÐºÑÐ°ÑÐ¸Ð²Ð¸ÑÑ", new String[] {"ÐºÑÐ°ÑÐ¸Ð²"});
     assertAnalyzesTo(a, "ÐºÑÐ°ÑÐ¸Ð²Ð°", new String[] {"ÐºÑÐ°ÑÐ¸Ð²"});
     assertAnalyzesTo(a, "ÐºÑÐ°ÑÐ¸Ð²Ð°ÑÐ°", new String[] {"ÐºÑÐ°ÑÐ¸Ð²"});
     assertAnalyzesTo(a, "ÐºÑÐ°ÑÐ¸Ð²Ð¾", new String[] {"ÐºÑÐ°ÑÐ¸Ð²"});
     assertAnalyzesTo(a, "ÐºÑÐ°ÑÐ¸Ð²Ð¾ÑÐ¾", new String[] {"ÐºÑÐ°ÑÐ¸Ð²"});
     assertAnalyzesTo(a, "ÐºÑÐ°ÑÐ¸Ð²Ð¸", new String[] {"ÐºÑÐ°ÑÐ¸Ð²"});
     assertAnalyzesTo(a, "ÐºÑÐ°ÑÐ¸Ð²Ð¸ÑÐµ", new String[] {"ÐºÑÐ°ÑÐ¸Ð²"});
   }
   
   /**
    * Test some exceptional rules, implemented as rewrites.
    */
   public void testExceptions() throws IOException {
     BulgarianAnalyzer a = new BulgarianAnalyzer(TEST_VERSION_CURRENT);
     
     // ÑÐ¸ -> Ðº
     assertAnalyzesTo(a, "ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸Ðº", new String[] {"ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸Ðº"});
     assertAnalyzesTo(a, "ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸ÐºÐ°", new String[] {"ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸Ðº"});
     assertAnalyzesTo(a, "ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸ÐºÑÑ", new String[] {"ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸Ðº"});
     assertAnalyzesTo(a, "ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸ÑÐ¸", new String[] {"ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸Ðº"});
     assertAnalyzesTo(a, "ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸ÑÐ¸ÑÐµ", new String[] {"ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸Ðº"});
     
     // Ð·Ð¸ -> Ð³
     assertAnalyzesTo(a, "Ð¿Ð¾Ð´Ð»Ð¾Ð³", new String[] {"Ð¿Ð¾Ð´Ð»Ð¾Ð³"});
     assertAnalyzesTo(a, "Ð¿Ð¾Ð´Ð»Ð¾Ð³Ð°", new String[] {"Ð¿Ð¾Ð´Ð»Ð¾Ð³"});
     assertAnalyzesTo(a, "Ð¿Ð¾Ð´Ð»Ð¾Ð³ÑÑ", new String[] {"Ð¿Ð¾Ð´Ð»Ð¾Ð³"});
     assertAnalyzesTo(a, "Ð¿Ð¾Ð´Ð»Ð¾Ð·Ð¸", new String[] {"Ð¿Ð¾Ð´Ð»Ð¾Ð³"});
     assertAnalyzesTo(a, "Ð¿Ð¾Ð´Ð»Ð¾Ð·Ð¸ÑÐµ", new String[] {"Ð¿Ð¾Ð´Ð»Ð¾Ð³"});
     
     // ÑÐ¸ -> Ñ
     assertAnalyzesTo(a, "ÐºÐ¾Ð¶ÑÑ", new String[] {"ÐºÐ¾Ð¶ÑÑ"});
     assertAnalyzesTo(a, "ÐºÐ¾Ð¶ÑÑÐ°", new String[] {"ÐºÐ¾Ð¶ÑÑ"});
     assertAnalyzesTo(a, "ÐºÐ¾Ð¶ÑÑÑÑ", new String[] {"ÐºÐ¾Ð¶ÑÑ"});
     assertAnalyzesTo(a, "ÐºÐ¾Ð¶ÑÑÐ¸", new String[] {"ÐºÐ¾Ð¶ÑÑ"});
     assertAnalyzesTo(a, "ÐºÐ¾Ð¶ÑÑÐ¸ÑÐµ", new String[] {"ÐºÐ¾Ð¶ÑÑ"});
     
     // Ñ deletion
     assertAnalyzesTo(a, "ÑÐµÐ½ÑÑÑ", new String[] {"ÑÐµÐ½ÑÑ"});
     assertAnalyzesTo(a, "ÑÐµÐ½ÑÑÑÐ°", new String[] {"ÑÐµÐ½ÑÑ"});
     assertAnalyzesTo(a, "ÑÐµÐ½ÑÑÑÑÑ", new String[] {"ÑÐµÐ½ÑÑ"});
     assertAnalyzesTo(a, "ÑÐµÐ½ÑÑÐ¾Ð²Ðµ", new String[] {"ÑÐµÐ½ÑÑ"});
     assertAnalyzesTo(a, "ÑÐµÐ½ÑÑÐ¾Ð²ÐµÑÐµ", new String[] {"ÑÐµÐ½ÑÑ"});
     
     // Ðµ*Ð¸ -> Ñ*
     assertAnalyzesTo(a, "Ð¿ÑÐ¾Ð¼ÑÐ½Ð°", new String[] {"Ð¿ÑÐ¾Ð¼ÑÐ½"});
     assertAnalyzesTo(a, "Ð¿ÑÐ¾Ð¼ÑÐ½Ð°ÑÐ°", new String[] {"Ð¿ÑÐ¾Ð¼ÑÐ½"});
     assertAnalyzesTo(a, "Ð¿ÑÐ¾Ð¼ÐµÐ½Ð¸", new String[] {"Ð¿ÑÐ¾Ð¼ÑÐ½"});
     assertAnalyzesTo(a, "Ð¿ÑÐ¾Ð¼ÐµÐ½Ð¸ÑÐµ", new String[] {"Ð¿ÑÐ¾Ð¼ÑÐ½"});
     
     // ÐµÐ½ -> Ð½
     assertAnalyzesTo(a, "Ð¿ÐµÑÐµÐ½", new String[] {"Ð¿ÐµÑÐ½"});
     assertAnalyzesTo(a, "Ð¿ÐµÑÐµÐ½ÑÐ°", new String[] {"Ð¿ÐµÑÐ½"});
     assertAnalyzesTo(a, "Ð¿ÐµÑÐ½Ð¸", new String[] {"Ð¿ÐµÑÐ½"});
     assertAnalyzesTo(a, "Ð¿ÐµÑÐ½Ð¸ÑÐµ", new String[] {"Ð¿ÐµÑÐ½"});
     
     // -ÐµÐ²Ðµ -> Ð¹
     // note: this is the only word i think this rule works for.
     // most -ÐµÐ²Ðµ pluralized nouns are monosyllabic,
     // and the stemmer requires length > 6...
     assertAnalyzesTo(a, "ÑÑÑÐ¾Ð¹", new String[] {"ÑÑÑÐ¾Ð¹"});
     assertAnalyzesTo(a, "ÑÑÑÐ¾ÐµÐ²Ðµ", new String[] {"ÑÑÑÐ¾Ð¹"});
     assertAnalyzesTo(a, "ÑÑÑÐ¾ÐµÐ²ÐµÑÐµ", new String[] {"ÑÑÑÐ¾Ð¹"});
     /* note the below forms conflate with each other, but not the rest */
     assertAnalyzesTo(a, "ÑÑÑÐ¾Ñ", new String[] {"ÑÑÑ"});
     assertAnalyzesTo(a, "ÑÑÑÐ¾ÑÑ", new String[] {"ÑÑÑ"});
   }
 
   public void testWithKeywordAttribute() throws IOException {
     CharArraySet set = new CharArraySet(Version.LUCENE_31, 1, true);
     set.add("ÑÑÑÐ¾ÐµÐ²Ðµ");
    WhitespaceTokenizer tokenStream = new WhitespaceTokenizer(TEST_VERSION_CURRENT, 
        new StringReader("ÑÑÑÐ¾ÐµÐ²ÐµÑÐµ ÑÑÑÐ¾ÐµÐ²Ðµ"));
 
     BulgarianStemFilter filter = new BulgarianStemFilter(
         new KeywordMarkerFilter(tokenStream, set));
     assertTokenStreamContents(filter, new String[] { "ÑÑÑÐ¾Ð¹", "ÑÑÑÐ¾ÐµÐ²Ðµ" });
   }
 }
