 package org.apache.lucene.analysis.kuromoji;
 
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
 
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.kuromoji.KuromojiTokenizer.Mode;
 
 /**
  * Test Kuromoji Japanese morphological analyzer
  */
 public class TestKuromojiAnalyzer extends BaseTokenStreamTestCase {
   /** This test fails with NPE when the 
    * stopwords file is missing in classpath */
   public void testResourcesAvailable() {
     new KuromojiAnalyzer(TEST_VERSION_CURRENT);
   }
   
   /**
    * An example sentence, test removal of particles, etc by POS,
    * lemmatization with the basic form, and that position increments
    * and offsets are correct.
    */
   public void testBasics() throws IOException {
     assertAnalyzesTo(new KuromojiAnalyzer(TEST_VERSION_CURRENT), "å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ãã",
         new String[] { "å¤ã", "å­¦ç", "è©¦é¨", "è½ã¡ã" },
         new int[] { 0, 3, 6,  9 },
         new int[] { 2, 5, 8, 11 },
         new int[] { 1, 2, 2,  2 }
       );
   }
 
   /**
    * Test that search mode is enabled and working by default
    */
   public void testDecomposition() throws IOException {
 
     final Analyzer a = new KuromojiAnalyzer(TEST_VERSION_CURRENT, null, Mode.SEARCH,
                                             KuromojiAnalyzer.getDefaultStopSet(),
                                             KuromojiAnalyzer.getDefaultStopTags());
 
     // Senior software engineer:
     assertAnalyzesToPositions(a, "ã·ãã¢ã½ããã¦ã§ã¢ã¨ã³ã¸ãã¢",
                               new String[] { "ã·ãã¢",
                                              "ã·ãã¢ã½ããã¦ã§ã¢ã¨ã³ã¸ãã¢", // zero pos inc
                                              "ã½ããã¦ã§ã¢",
                                              "ã¨ã³ã¸ãã¢" },
                               new int[] { 1, 0, 1, 1},
                               new int[] { 1, 3, 1, 1}
                               );
 
     // Senior project manager: also tests katakana spelling variation stemming
     assertAnalyzesToPositions(a, "ã·ãã¢ãã­ã¸ã§ã¯ãããã¼ã¸ã£ã¼",
                               new String[] { "ã·ãã¢",
                                               "ã·ãã¢ãã­ã¸ã§ã¯ãããã¼ã¸ã£", // trailing ã¼ removed by stemming, zero pos inc
                                               "ãã­ã¸ã§ã¯ã",
                                               "ããã¼ã¸ã£"}, // trailing ã¼ removed by stemming
                               new int[]{1, 0, 1, 1},
                               new int[]{1, 3, 1, 1}
                               );
 
     // Kansai International Airport:
     assertAnalyzesToPositions(a, "é¢è¥¿å½éç©ºæ¸¯",
                               new String[] { "é¢è¥¿",
                                              "é¢è¥¿å½éç©ºæ¸¯", // zero pos inc
                                              "å½é",
                                              "ç©ºæ¸¯" },
                               new int[] {1, 0, 1, 1},
                               new int[] {1, 3, 1, 1}
                               );
 
     // Konika Minolta Holdings; not quite the right
     // segmentation (see LUCENE-3726):
     assertAnalyzesToPositions(a, "ã³ãã«ããã«ã¿ãã¼ã«ãã£ã³ã°ã¹",
                               new String[] { "ã³ãã«",
                                              "ã³ãã«ããã«ã¿ãã¼ã«ãã£ã³ã°ã¹", // zero pos inc
                                              "ããã«ã¿", 
                                              "ãã¼ã«ãã£ã³ã°ã¹"},
                               new int[] {1, 0, 1, 1},
                               new int[] {1, 3, 1, 1}
                               );
 
     // Narita Airport
     assertAnalyzesToPositions(a, "æç°ç©ºæ¸¯",
                               new String[] { "æç°",
                                              "æç°ç©ºæ¸¯",
                                              "ç©ºæ¸¯" },
                               new int[] {1, 0, 1},
                               new int[] {1, 2, 1}
                               );
 
     // Kyoto University Baseball Club
     assertAnalyzesToPositions(new KuromojiAnalyzer(TEST_VERSION_CURRENT), "äº¬é½å¤§å­¦ç¡¬å¼éçé¨",
                      new String[] { "äº¬é½å¤§",
                                     "å­¦",
                                     "ç¡¬å¼",
                                     "éç",
                                     "é¨" },
                               new int[] {1, 1, 1, 1, 1},
                               new int[] {1, 1, 1, 1, 1});
     // toDotFile(a, "æç°ç©ºæ¸¯", "/mnt/scratch/out.dot");
   }
 
   
   /**
    * blast random strings against the analyzer
    */
   public void testRandom() throws IOException {
     final Analyzer a = new KuromojiAnalyzer(TEST_VERSION_CURRENT, null, Mode.SEARCH,
                                             KuromojiAnalyzer.getDefaultStopSet(),
                                             KuromojiAnalyzer.getDefaultStopTags());
     checkRandomData(random, a, atLeast(10000));
   }
   
   /** blast some random large strings through the analyzer */
   public void testRandomHugeStrings() throws Exception {
     final Analyzer a = new KuromojiAnalyzer(TEST_VERSION_CURRENT, null, Mode.SEARCH,
         KuromojiAnalyzer.getDefaultStopSet(),
         KuromojiAnalyzer.getDefaultStopTags());
     checkRandomData(random, a, 200*RANDOM_MULTIPLIER, 8192);
   }
 
   // Copied from TestKuromojiTokenizer, to make sure passing
   // user dict to analyzer works:
   public void testUserDict3() throws Exception {
     // Test entry that breaks into multiple tokens:
     final Analyzer a = new KuromojiAnalyzer(TEST_VERSION_CURRENT, TestKuromojiTokenizer.readDict(),
                                             Mode.SEARCH,
                                             KuromojiAnalyzer.getDefaultStopSet(),
                                             KuromojiAnalyzer.getDefaultStopTags());
     assertTokenStreamContents(a.tokenStream("foo", new StringReader("abcd")),
                               new String[] { "a", "b", "cd"  },
                               new int[] { 0, 1, 2 },
                               new int[] { 1, 2, 4 },
                               new Integer(4)
     );
   }
 }
