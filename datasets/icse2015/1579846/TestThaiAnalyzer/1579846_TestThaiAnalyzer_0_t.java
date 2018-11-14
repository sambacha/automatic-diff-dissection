 package org.apache.lucene.analysis.th;
 
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
 import java.io.Reader;
 import java.util.Random;
 
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.TokenStream;
 import org.apache.lucene.analysis.Tokenizer;
 import org.apache.lucene.analysis.core.KeywordTokenizer;
 import org.apache.lucene.analysis.core.StopAnalyzer;
 import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
 import org.apache.lucene.analysis.util.CharArraySet;
 
 /**
  * Test case for ThaiAnalyzer, modified from TestFrenchAnalyzer
  *
  */
 
 public class TestThaiAnalyzer extends BaseTokenStreamTestCase {
   
   @Override
   public void setUp() throws Exception {
     super.setUp();
    assumeTrue("JRE does not support Thai dictionary-based BreakIterator", ThaiTokenizer.DBBI_AVAILABLE);
   }
   /* 
    * testcase for offsets
    */
   public void testOffsets() throws Exception {
     assertAnalyzesTo(new ThaiAnalyzer(TEST_VERSION_CURRENT, CharArraySet.EMPTY_SET), "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ", 
         new String[] { "à¸à¸²à¸£", "à¸à¸µà¹", "à¹à¸à¹", "à¸à¹à¸­à¸", "à¹à¸ªà¸à¸", "à¸§à¹à¸²", "à¸à¸²à¸", "à¸à¸µ" },
         new int[] { 0, 3, 6, 9, 13, 17, 20, 23 },
         new int[] { 3, 6, 9, 13, 17, 20, 23, 25 });
   }
   
   public void testStopWords() throws Exception {
     assertAnalyzesTo(new ThaiAnalyzer(TEST_VERSION_CURRENT), "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ", 
         new String[] { "à¹à¸ªà¸à¸", "à¸à¸²à¸", "à¸à¸µ" },
         new int[] { 13, 20, 23 },
         new int[] { 17, 23, 25 },
         new int[] { 5, 2, 1 });
   }
   
   /*
    * Test that position increments are adjusted correctly for stopwords.
    */
   // note this test uses stopfilter's stopset
   public void testPositionIncrements() throws Exception {
     final ThaiAnalyzer analyzer = new ThaiAnalyzer(TEST_VERSION_CURRENT, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
     assertAnalyzesTo(analyzer, "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸ the à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ", 
         new String[] { "à¸à¸²à¸£", "à¸à¸µà¹", "à¹à¸à¹", "à¸à¹à¸­à¸", "à¹à¸ªà¸à¸", "à¸§à¹à¸²", "à¸à¸²à¸", "à¸à¸µ" },
         new int[] { 0, 3, 6, 9, 18, 22, 25, 28 },
         new int[] { 3, 6, 9, 13, 22, 25, 28, 30 },
         new int[] { 1, 1, 1, 1, 2, 1, 1, 1 });
    
     // case that a stopword is adjacent to thai text, with no whitespace
     assertAnalyzesTo(analyzer, "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸the à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ", 
         new String[] { "à¸à¸²à¸£", "à¸à¸µà¹", "à¹à¸à¹", "à¸à¹à¸­à¸", "à¹à¸ªà¸à¸", "à¸§à¹à¸²", "à¸à¸²à¸", "à¸à¸µ" },
         new int[] { 0, 3, 6, 9, 17, 21, 24, 27 },
         new int[] { 3, 6, 9, 13, 21, 24, 27, 29 },
         new int[] { 1, 1, 1, 1, 2, 1, 1, 1 });
   }
   
   public void testReusableTokenStream() throws Exception {
     ThaiAnalyzer analyzer = new ThaiAnalyzer(TEST_VERSION_CURRENT, CharArraySet.EMPTY_SET);
     assertAnalyzesTo(analyzer, "", new String[] {});
 
       assertAnalyzesTo(
           analyzer,
           "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ",
           new String[] { "à¸à¸²à¸£", "à¸à¸µà¹", "à¹à¸à¹", "à¸à¹à¸­à¸", "à¹à¸ªà¸à¸", "à¸§à¹à¸²", "à¸à¸²à¸", "à¸à¸µ"});
 
       assertAnalyzesTo(
           analyzer,
           "à¸à¸£à¸´à¸©à¸±à¸à¸à¸·à¹à¸­ XY&Z - à¸à¸¸à¸¢à¸à¸±à¸ xyz@demo.com",
           new String[] { "à¸à¸£à¸´à¸©à¸±à¸", "à¸à¸·à¹à¸­", "xy", "z", "à¸à¸¸à¸¢", "à¸à¸±à¸", "xyz", "demo.com" });
   }
   
   /** blast some random strings through the analyzer */
   public void testRandomStrings() throws Exception {
     checkRandomData(random(), new ThaiAnalyzer(TEST_VERSION_CURRENT), 1000*RANDOM_MULTIPLIER);
   }
   
   /** blast some random large strings through the analyzer */
   public void testRandomHugeStrings() throws Exception {
     Random random = random();
     checkRandomData(random, new ThaiAnalyzer(TEST_VERSION_CURRENT), 100*RANDOM_MULTIPLIER, 8192);
   }
   
   // LUCENE-3044
   public void testAttributeReuse() throws Exception {
     ThaiAnalyzer analyzer = new ThaiAnalyzer(TEST_VERSION_CURRENT);
     // just consume
     TokenStream ts = analyzer.tokenStream("dummy", "à¸ à¸²à¸©à¸²à¹à¸à¸¢");
     assertTokenStreamContents(ts, new String[] { "à¸ à¸²à¸©à¸²", "à¹à¸à¸¢" });
     // this consumer adds flagsAtt, which this analyzer does not use. 
     ts = analyzer.tokenStream("dummy", "à¸ à¸²à¸©à¸²à¹à¸à¸¢");
     ts.addAttribute(FlagsAttribute.class);
     assertTokenStreamContents(ts, new String[] { "à¸ à¸²à¸©à¸²", "à¹à¸à¸¢" });
   }
 }
