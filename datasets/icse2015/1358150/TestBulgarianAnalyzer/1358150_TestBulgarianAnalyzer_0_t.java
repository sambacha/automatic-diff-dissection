 package org.apache.lucene.analysis.bg;
 
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
 
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.util.CharArraySet;
 import org.apache.lucene.util.Version;
 
 /**
  * Test the Bulgarian analyzer
  */
 public class TestBulgarianAnalyzer extends BaseTokenStreamTestCase {
   
   /**
    * This test fails with NPE when the stopwords file is missing in classpath
    */
   public void testResourcesAvailable() {
     new BulgarianAnalyzer(TEST_VERSION_CURRENT);
   }
   
   public void testStopwords() throws IOException {
     Analyzer a = new BulgarianAnalyzer(TEST_VERSION_CURRENT);
     assertAnalyzesTo(a, "ÐÐ°Ðº ÑÐµ ÐºÐ°Ð·Ð²Ð°Ñ?", new String[] {"ÐºÐ°Ð·Ð²Ð°Ñ"});
   }
   
   public void testCustomStopwords() throws IOException {
     Analyzer a = new BulgarianAnalyzer(TEST_VERSION_CURRENT, CharArraySet.EMPTY_SET);
     assertAnalyzesTo(a, "ÐÐ°Ðº ÑÐµ ÐºÐ°Ð·Ð²Ð°Ñ?", 
         new String[] {"ÐºÐ°Ðº", "ÑÐµ", "ÐºÐ°Ð·Ð²Ð°Ñ"});
   }
   
   public void testReusableTokenStream() throws IOException {
     Analyzer a = new BulgarianAnalyzer(TEST_VERSION_CURRENT);
     assertAnalyzesToReuse(a, "Ð´Ð¾ÐºÑÐ¼ÐµÐ½ÑÐ¸", new String[] {"Ð´Ð¾ÐºÑÐ¼ÐµÐ½Ñ"});
     assertAnalyzesToReuse(a, "Ð´Ð¾ÐºÑÐ¼ÐµÐ½Ñ", new String[] {"Ð´Ð¾ÐºÑÐ¼ÐµÐ½Ñ"});
   }
   
   /**
    * Test some examples from the paper
    */
   public void testBasicExamples() throws IOException {
     Analyzer a = new BulgarianAnalyzer(TEST_VERSION_CURRENT);
     assertAnalyzesTo(a, "ÐµÐ½ÐµÑÐ³Ð¸Ð¹Ð½Ð¸ ÐºÑÐ¸Ð·Ð¸", new String[] {"ÐµÐ½ÐµÑÐ³Ð¸Ð¹Ð½", "ÐºÑÐ¸Ð·"});
     assertAnalyzesTo(a, "ÐÑÐ¾Ð¼Ð½Ð°ÑÐ° ÐµÐ½ÐµÑÐ³Ð¸Ñ", new String[] {"Ð°ÑÐ¾Ð¼Ð½", "ÐµÐ½ÐµÑÐ³"});
     
     assertAnalyzesTo(a, "ÐºÐ¾Ð¼Ð¿ÑÑÑÐ¸", new String[] {"ÐºÐ¾Ð¼Ð¿ÑÑÑ"});
     assertAnalyzesTo(a, "ÐºÐ¾Ð¼Ð¿ÑÑÑÑ", new String[] {"ÐºÐ¾Ð¼Ð¿ÑÑÑ"});
     
     assertAnalyzesTo(a, "Ð³ÑÐ°Ð´Ð¾Ð²Ðµ", new String[] {"Ð³ÑÐ°Ð´"});
   }
   
   public void testWithStemExclusionSet() throws IOException {
     CharArraySet set = new CharArraySet(TEST_VERSION_CURRENT, 1, true);
     set.add("ÑÑÑÐ¾ÐµÐ²Ðµ");
     Analyzer a = new BulgarianAnalyzer(TEST_VERSION_CURRENT, CharArraySet.EMPTY_SET, set);
     assertAnalyzesTo(a, "ÑÑÑÐ¾ÐµÐ²ÐµÑÐµ ÑÑÑÐ¾ÐµÐ²Ðµ", new String[] { "ÑÑÑÐ¾Ð¹", "ÑÑÑÐ¾ÐµÐ²Ðµ" });
   }
   
   /** blast some random strings through the analyzer */
   public void testRandomStrings() throws Exception {
    checkRandomData(random(), new BulgarianAnalyzer(TEST_VERSION_CURRENT), 1000*RANDOM_MULTIPLIER);
   }
 }
