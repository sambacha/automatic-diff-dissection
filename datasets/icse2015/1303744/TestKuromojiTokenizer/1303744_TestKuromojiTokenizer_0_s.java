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
 
 import java.io.BufferedReader;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.LineNumberReader;
 import java.io.PrintWriter;
 import java.io.Reader;
 import java.io.StringReader;
 
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.ReusableAnalyzerBase;
 import org.apache.lucene.analysis.TokenStream;
 import org.apache.lucene.analysis.Tokenizer;
 import org.apache.lucene.analysis.kuromoji.KuromojiTokenizer.Mode;
 import org.apache.lucene.analysis.kuromoji.dict.ConnectionCosts;
 import org.apache.lucene.analysis.kuromoji.dict.UserDictionary;
 import org.apache.lucene.analysis.kuromoji.tokenattributes.*;
 import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
 import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
 import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
 import org.apache.lucene.util.IOUtils;
 import org.apache.lucene.util.UnicodeUtil;
 import org.apache.lucene.util._TestUtil;
 import org.junit.Ignore;
 
 public class TestKuromojiTokenizer extends BaseTokenStreamTestCase {
 
   public static UserDictionary readDict() {
     InputStream is = TestKuromojiTokenizer.class.getResourceAsStream("userdict.txt");
     if (is == null) {
       throw new RuntimeException("Cannot find userdict.txt in test classpath!");
     }
     try {
       try {
         Reader reader = new InputStreamReader(is, IOUtils.CHARSET_UTF_8);
         return new UserDictionary(reader);
       } finally {
         is.close();
       }
     } catch (IOException ioe) {
       throw new RuntimeException(ioe);
     }
   }
 
   private Analyzer analyzer = new ReusableAnalyzerBase() {
     @Override
     protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
       Tokenizer tokenizer = new KuromojiTokenizer(reader, readDict(), false, Mode.SEARCH);
       return new TokenStreamComponents(tokenizer, tokenizer);
     }
   };
 
   private Analyzer analyzerNormal = new ReusableAnalyzerBase() {
     @Override
     protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
       Tokenizer tokenizer = new KuromojiTokenizer(reader, readDict(), false, Mode.NORMAL);
       return new TokenStreamComponents(tokenizer, tokenizer);
     }
   };
 
   private Analyzer analyzerNoPunct = new ReusableAnalyzerBase() {
     @Override
     protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
       Tokenizer tokenizer = new KuromojiTokenizer(reader, readDict(), true, Mode.SEARCH);
       return new TokenStreamComponents(tokenizer, tokenizer);
     }
   };
 
   private Analyzer extendedModeAnalyzerNoPunct = new ReusableAnalyzerBase() {
     @Override
     protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
       Tokenizer tokenizer = new KuromojiTokenizer(reader, readDict(), true, Mode.EXTENDED);
       return new TokenStreamComponents(tokenizer, tokenizer);
     }
   };
 
   public void testNormalMode() throws Exception {
     assertAnalyzesTo(analyzerNormal,
                      "ã·ãã¢ã½ããã¦ã§ã¢ã¨ã³ã¸ãã¢",
                      new String[] {"ã·ãã¢ã½ããã¦ã§ã¢ã¨ã³ã¸ãã¢"});
   }
 
   public void testDecomposition1() throws Exception {
     assertAnalyzesTo(analyzerNoPunct, "æ¬æ¥ã¯ãè²§å°å±¤ã®å¥³æ§ãå­ä¾ã«å»çä¿è­·ãæä¾ããããã«åµè¨­ãããå¶åº¦ã§ããã" +
                          "ã¢ã¡ãªã«ä½æå¾èå»çæ´å©å¶åº¦ããä»æ¥ã§ã¯ããã®äºç®ã®ç´ï¼åã®ï¼ãèäººã«è²»ããã¦ããã",
      new String[] { "æ¬æ¥", "ã¯",  "è²§å°", "å±¤", "ã®", "å¥³æ§", "ã", "å­ä¾", "ã«", "å»ç", "ä¿è­·", "ã",      
                     "æä¾", "ãã", "ãã", "ã«", "åµè¨­", "ã", "ã", "ã", "å¶åº¦", "ã§", "ãã",  "ã¢ã¡ãªã«", 
                     "ä½", "æå¾", "è", "å»ç", "æ´å©", "å¶åº¦", "ã",  "ä»æ¥", "ã§", "ã¯",  "ãã®",
                     "äºç®", "ã®", "ç´", "ï¼", "åã®", "ï¼", "ã", "èäºº", "ã«", "è²»ãã", "ã¦", "ãã" },
      new int[] { 0, 2, 4, 6, 7,  8, 10, 11, 13, 14, 16, 18, 19, 21, 23, 25, 26, 28, 29, 30, 
                  31, 33, 34, 37, 41, 42, 44, 45, 47, 49, 51, 53, 55, 56, 58, 60,
                  62, 63, 64, 65, 67, 68, 69, 71, 72, 75, 76 },
      new int[] { 2, 3, 6, 7, 8, 10, 11, 13, 14, 16, 18, 19, 21, 23, 25, 26, 28, 29, 30, 31,
                  33, 34, 36, 41, 42, 44, 45, 47, 49, 51, 52, 55, 56, 57, 60, 62,
                  63, 64, 65, 67, 68, 69, 71, 72, 75, 76, 78 }
     );
   }
   
   public void testDecomposition2() throws Exception {
     assertAnalyzesTo(analyzerNoPunct, "éº»è¬ã®å¯å£²ã¯æ ¹ãããçµ¶ãããªããã°ãªããªã",
       new String[] { "éº»è¬", "ã®", "å¯å£²", "ã¯", "æ ¹ããã", "çµ¶ãã", "ãªãã", "ã°", "ãªã", "ãªã" },
       new int[] { 0, 2, 3, 5, 6,  10, 13, 16, 17, 19 },
       new int[] { 2, 3, 5, 6, 10, 13, 16, 17, 19, 21 }
     );
   }
   
   public void testDecomposition3() throws Exception {
     assertAnalyzesTo(analyzerNoPunct, "é­å¥³ç©å¤§å°ãã·ã¥ã¼ã»ããã­ã³ã¹ã",
       new String[] { "é­å¥³", "ç©", "å¤§å°", "ãã·ã¥ã¼",  "ããã­ã³ã¹" },
       new int[] { 0, 2, 3, 5, 10 },
       new int[] { 2, 3, 5, 9, 15 }
     );
   }
 
   public void testDecomposition4() throws Exception {
     assertAnalyzesTo(analyzer, "ããã¯æ¬ã§ã¯ãªã",
       new String[] { "ãã", "ã¯", "æ¬", "ã§", "ã¯", "ãªã" },
       new int[] { 0, 2, 3, 4, 5, 6 },
       new int[] { 2, 3, 4, 5, 6, 8 }
     );
   }
 
   /* Note this is really a stupid test just to see if things arent horribly slow.
    * ideally the test would actually fail instead of hanging...
    */
   public void testDecomposition5() throws Exception {
     TokenStream ts = analyzer.tokenStream("bogus", new StringReader("ãããããããããããããããããããããããããããããããããããããããã"));
     ts.reset();
     while (ts.incrementToken()) {
       
     }
     ts.end();
     ts.close();
   }
 
   /*
     // NOTE: intentionally fails!  Just trying to debug this
     // one input...
   public void testDecomposition6() throws Exception {
     assertAnalyzesTo(analyzer, "å¥è¯åç«¯ç§å­¦æè¡å¤§å­¦é¢å¤§å­¦",
       new String[] { "ãã", "ã¯", "æ¬", "ã§", "ã¯", "ãªã" },
       new int[] { 0, 2, 3, 4, 5, 6 },
       new int[] { 2, 3, 4, 5, 6, 8 }
                      );
   }
   */
 
   /** Tests that sentence offset is incorporated into the resulting offsets */
   public void testTwoSentences() throws Exception {
     /*
     //TokenStream ts = a.tokenStream("foo", new StringReader("å¦¹ã®å²å­ã§ããä¿ºã¨å¹´å­ã§ãä»åé¨çã§ãã"));
     TokenStream ts = analyzer.tokenStream("foo", new StringReader("&#x250cdf66<!--\"<!--#<!--;?><!--#<!--#><!---->?>-->;"));
     ts.reset();
     CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
     while(ts.incrementToken()) {
       System.out.println("  " + termAtt.toString());
     }
     System.out.println("DONE PARSE\n\n");
     */
 
     assertAnalyzesTo(analyzerNoPunct, "é­å¥³ç©å¤§å°ãã·ã¥ã¼ã»ããã­ã³ã¹ã é­å¥³ç©å¤§å°ãã·ã¥ã¼ã»ããã­ã³ã¹ã",
       new String[] { "é­å¥³", "ç©", "å¤§å°", "ãã·ã¥ã¼", "ããã­ã³ã¹",  "é­å¥³", "ç©", "å¤§å°", "ãã·ã¥ã¼",  "ããã­ã³ã¹"  },
       new int[] { 0, 2, 3, 5, 10, 17, 19, 20, 22, 27 },
       new int[] { 2, 3, 5, 9, 15, 19, 20, 22, 26, 32 }
     );
   }
 
   /** blast some random strings through the analyzer */
   public void testRandomStrings() throws Exception {
     checkRandomData(random, analyzer, 10000*RANDOM_MULTIPLIER);
     checkRandomData(random, analyzerNoPunct, 10000*RANDOM_MULTIPLIER);
   }
   
   /** blast some random large strings through the analyzer */
  @Ignore("FIXME: see LUCENE-3897")
   public void testRandomHugeStrings() throws Exception {
     checkRandomData(random, analyzer, 200*RANDOM_MULTIPLIER, 8192);
     checkRandomData(random, analyzerNoPunct, 200*RANDOM_MULTIPLIER, 8192);
   }
   
   public void testLargeDocReliability() throws Exception {
     for (int i = 0; i < 100; i++) {
       String s = _TestUtil.randomUnicodeString(random, 10000);
       TokenStream ts = analyzer.tokenStream("foo", new StringReader(s));
       ts.reset();
       while (ts.incrementToken()) {
       }
     }
   }
   
   /** simple test for supplementary characters */
   public void testSurrogates() throws IOException {
     assertAnalyzesTo(analyzer, "ð©¬è±éä¹æ¯ç",
       new String[] { "ð©¬", "è±", "é", "ä¹", "æ¯", "ç" });
   }
   
   /** random test ensuring we don't ever split supplementaries */
   public void testSurrogates2() throws IOException {
     int numIterations = atLeast(10000);
     for (int i = 0; i < numIterations; i++) {
       if (VERBOSE) {
         System.out.println("\nTEST: iter=" + i);
       }
       String s = _TestUtil.randomUnicodeString(random, 100);
       TokenStream ts = analyzer.tokenStream("foo", new StringReader(s));
       CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
       ts.reset();
       while (ts.incrementToken()) {
         assertTrue(UnicodeUtil.validUTF16String(termAtt));
       }
     }
   }
 
   public void testOnlyPunctuation() throws IOException {
     TokenStream ts = analyzerNoPunct.tokenStream("foo", new StringReader("ãããã"));
     ts.reset();
     assertFalse(ts.incrementToken());
     ts.end();
   }
 
   public void testOnlyPunctuationExtended() throws IOException {
     TokenStream ts = extendedModeAnalyzerNoPunct.tokenStream("foo", new StringReader("......"));
     ts.reset();
     assertFalse(ts.incrementToken());
     ts.end();
   }
   
   // note: test is kinda silly since kuromoji emits punctuation tokens.
   // but, when/if we filter these out it will be useful.
   public void testEnd() throws Exception {
     assertTokenStreamContents(analyzerNoPunct.tokenStream("foo", new StringReader("ããã¯æ¬ã§ã¯ãªã")),
         new String[] { "ãã", "ã¯", "æ¬", "ã§", "ã¯", "ãªã" },
         new int[] { 0, 2, 3, 4, 5, 6 },
         new int[] { 2, 3, 4, 5, 6, 8 },
         new Integer(8)
     );
 
     assertTokenStreamContents(analyzerNoPunct.tokenStream("foo", new StringReader("ããã¯æ¬ã§ã¯ãªã    ")),
         new String[] { "ãã", "ã¯", "æ¬", "ã§", "ã¯", "ãªã"  },
         new int[] { 0, 2, 3, 4, 5, 6, 8 },
         new int[] { 2, 3, 4, 5, 6, 8, 9 },
         new Integer(12)
     );
   }
 
   public void testUserDict() throws Exception {
     // Not a great test because w/o userdict.txt the
     // segmentation is the same:
     assertTokenStreamContents(analyzer.tokenStream("foo", new StringReader("é¢è¥¿å½éç©ºæ¸¯ã«è¡ã£ã")),
                               new String[] { "é¢è¥¿", "å½é", "ç©ºæ¸¯", "ã«", "è¡ã£", "ã"  },
                               new int[] { 0, 2, 4, 6, 7, 9 },
                               new int[] { 2, 4, 6, 7, 9, 10 },
                               new Integer(10)
     );
   }
 
   public void testUserDict2() throws Exception {
     // Better test: w/o userdict the segmentation is different:
     assertTokenStreamContents(analyzer.tokenStream("foo", new StringReader("æéé¾")),
                               new String[] { "æéé¾"  },
                               new int[] { 0 },
                               new int[] { 3 },
                               new Integer(3)
     );
   }
 
   public void testUserDict3() throws Exception {
     // Test entry that breaks into multiple tokens:
     assertTokenStreamContents(analyzer.tokenStream("foo", new StringReader("abcd")),
                               new String[] { "a", "b", "cd"  },
                               new int[] { 0, 1, 2 },
                               new int[] { 1, 2, 4 },
                               new Integer(4)
     );
   }
 
   // HMM: fails (segments as a/b/cd/efghij)... because the
   // two paths have exactly equal paths (1 KNOWN + 1
   // UNKNOWN) and we don't seem to favor longer KNOWN /
   // shorter UNKNOWN matches:
 
   /*
   public void testUserDict4() throws Exception {
     // Test entry that has another entry as prefix
     assertTokenStreamContents(analyzer.tokenStream("foo", new StringReader("abcdefghij")),
                               new String[] { "ab", "cd", "efg", "hij"  },
                               new int[] { 0, 2, 4, 7 },
                               new int[] { 2, 4, 7, 10 },
                               new Integer(10)
     );
   }
   */
   
   public void testSegmentation() throws Exception {
     // Skip tests for Michelle Kwan -- UniDic segments Kwan as ã¯ ã¯ã³
     //		String input = "ãã·ã§ã«ã»ã¯ã¯ã³ãåªåãã¾ãããã¹ãã¼ã¹ã¹ãã¼ã·ã§ã³ã«è¡ãã¾ããããããããã";
     //		String[] surfaceForms = {
     //				"ãã·ã§ã«", "ã»", "ã¯ã¯ã³", "ã", "åªå", "ã", "ã¾ã", "ã", "ã",
     //				"ã¹ãã¼ã¹", "ã¹ãã¼ã·ã§ã³", "ã«", "è¡ã", "ã¾ã", "ã",
     //				"ãããããã", "ã"
     //		};
     String input = "ã¹ãã¼ã¹ã¹ãã¼ã·ã§ã³ã«è¡ãã¾ããããããããã";
     String[] surfaceForms = {
         "ã¹ãã¼ã¹", "ã¹ãã¼ã·ã§ã³", "ã«", "è¡ã", "ã¾ã", "ã",
         "ãããããã", "ã"
     };
     assertAnalyzesTo(analyzer,
                      input,
                      surfaceForms);
   }
 
   public void testLatticeToDot() throws Exception {
     final GraphvizFormatter gv2 = new GraphvizFormatter(ConnectionCosts.getInstance());
     final Analyzer analyzer = new ReusableAnalyzerBase() {
       @Override
       protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
         KuromojiTokenizer tokenizer = new KuromojiTokenizer(reader, readDict(), false, Mode.SEARCH);
         tokenizer.setGraphvizFormatter(gv2);
         return new TokenStreamComponents(tokenizer, tokenizer);
       }
     };
 
     String input = "ã¹ãã¼ã¹ã¹ãã¼ã·ã§ã³ã«è¡ãã¾ããããããããã";
     String[] surfaceForms = {
         "ã¹ãã¼ã¹", "ã¹ãã¼ã·ã§ã³", "ã«", "è¡ã", "ã¾ã", "ã",
         "ãããããã", "ã"
     };
     assertAnalyzesTo(analyzer,
                      input,
                      surfaceForms);
     
     assertTrue(gv2.finish().indexOf("22.0") != -1);
   }
 
   private void assertReadings(String input, String... readings) throws IOException {
     TokenStream ts = analyzer.tokenStream("ignored", new StringReader(input));
     ReadingAttribute readingAtt = ts.addAttribute(ReadingAttribute.class);
     ts.reset();
     for(String reading : readings) {
       assertTrue(ts.incrementToken());
       assertEquals(reading, readingAtt.getReading());
     }
     assertFalse(ts.incrementToken());
     ts.end();
   }
 
   private void assertPronunciations(String input, String... pronunciations) throws IOException {
     TokenStream ts = analyzer.tokenStream("ignored", new StringReader(input));
     ReadingAttribute readingAtt = ts.addAttribute(ReadingAttribute.class);
     ts.reset();
     for(String pronunciation : pronunciations) {
       assertTrue(ts.incrementToken());
       assertEquals(pronunciation, readingAtt.getPronunciation());
     }
     assertFalse(ts.incrementToken());
     ts.end();
   }
   
   private void assertBaseForms(String input, String... baseForms) throws IOException {
     TokenStream ts = analyzer.tokenStream("ignored", new StringReader(input));
     BaseFormAttribute baseFormAtt = ts.addAttribute(BaseFormAttribute.class);
     ts.reset();
     for(String baseForm : baseForms) {
       assertTrue(ts.incrementToken());
       assertEquals(baseForm, baseFormAtt.getBaseForm());
     }
     assertFalse(ts.incrementToken());
     ts.end();
   }
 
   private void assertInflectionTypes(String input, String... inflectionTypes) throws IOException {
     TokenStream ts = analyzer.tokenStream("ignored", new StringReader(input));
     InflectionAttribute inflectionAtt = ts.addAttribute(InflectionAttribute.class);
     ts.reset();
     for(String inflectionType : inflectionTypes) {
       assertTrue(ts.incrementToken());
       assertEquals(inflectionType, inflectionAtt.getInflectionType());
     }
     assertFalse(ts.incrementToken());
     ts.end();
   }
 
   private void assertInflectionForms(String input, String... inflectionForms) throws IOException {
     TokenStream ts = analyzer.tokenStream("ignored", new StringReader(input));
     InflectionAttribute inflectionAtt = ts.addAttribute(InflectionAttribute.class);
     ts.reset();
     for(String inflectionForm : inflectionForms) {
       assertTrue(ts.incrementToken());
       assertEquals(inflectionForm, inflectionAtt.getInflectionForm());
     }
     assertFalse(ts.incrementToken());
     ts.end();
   }
   
   private void assertPartsOfSpeech(String input, String... partsOfSpeech) throws IOException {
     TokenStream ts = analyzer.tokenStream("ignored", new StringReader(input));
     PartOfSpeechAttribute partOfSpeechAtt = ts.addAttribute(PartOfSpeechAttribute.class);
     ts.reset();
     for(String partOfSpeech : partsOfSpeech) {
       assertTrue(ts.incrementToken());
       assertEquals(partOfSpeech, partOfSpeechAtt.getPartOfSpeech());
     }
     assertFalse(ts.incrementToken());
     ts.end();
   }
   
   public void testReadings() throws Exception {
     assertReadings("å¯¿å¸ãé£ã¹ããã§ãã",
                    "ã¹ã·",
                    "ã¬",
                    "ã¿ã",
                    "ã¿ã¤",
                    "ãã¹",
                    "ã");
   }
   
   public void testReadings2() throws Exception {
     assertReadings("å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ãã",
                    "ãªãªã¯",
                    "ã",
                    "ã¬ã¯ã»ã¤",
                    "ã¬",
                    "ã·ã±ã³",
                    "ã",
                    "ãªã",
                    "ã¿",
                    "ã");
   }
   
   public void testPronunciations() throws Exception {
     assertPronunciations("å¯¿å¸ãé£ã¹ããã§ãã",
                          "ã¹ã·",
                          "ã¬",
                          "ã¿ã",
                          "ã¿ã¤",
                          "ãã¹",
                          "ã");
   }
   
   public void testPronunciations2() throws Exception {
     // pronunciation differs from reading here
     assertPronunciations("å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ãã",
                          "ãªã¼ã¯",
                          "ã",
                          "ã¬ã¯ã»ã¤",
                          "ã¬",
                          "ã·ã±ã³",
                          "ã",
                          "ãªã",
                          "ã¿",
                          "ã");
   }
   
   public void testBasicForms() throws Exception {
     assertBaseForms("ããã¯ã¾ã å®é¨æ®µéã«ããã¾ãã",
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     "ãã",
                     null,
                     null);
   }
   
   public void testInflectionTypes() throws Exception {
     assertInflectionTypes("ããã¯ã¾ã å®é¨æ®µéã«ããã¾ãã",
                           null,
                           null,
                           null,
                           null,
                           null,
                           null,
                           "äºæ®µã»ã©è¡",
                           "ç¹æ®ã»ãã¹",
                           null);
   }
   
   public void testInflectionForms() throws Exception {
     assertInflectionForms("ããã¯ã¾ã å®é¨æ®µéã«ããã¾ãã",
                           null,
                           null,
                           null,
                           null,
                           null,
                           null,
                           "é£ç¨å½¢",
                           "åºæ¬å½¢",
                           null);
   }
   
   public void testPartOfSpeech() throws Exception {
     assertPartsOfSpeech("ããã¯ã¾ã å®é¨æ®µéã«ããã¾ãã",
                         "åè©-ä»£åè©-ä¸è¬",
                         "å©è©-ä¿å©è©",
                         "å¯è©-å©è©é¡æ¥ç¶",
                         "åè©-ãµå¤æ¥ç¶",
                         "åè©-ä¸è¬",
                         "å©è©-æ ¼å©è©-ä¸è¬",
                         "åè©-èªç«",
                         "å©åè©",
                         "è¨å·-å¥ç¹");
   }
 
   // TODO: the next 2 tests are no longer using the first/last word ids, maybe lookup the words and fix?
   // do we have a possibility to actually lookup the first and last word from dictionary?
   public void testYabottai() throws Exception {
     assertAnalyzesTo(analyzer, "ãã¼ã£ãã",
                      new String[] {"ãã¼ã£ãã"});
   }
 
   public void testTsukitosha() throws Exception {
     assertAnalyzesTo(analyzer, "çªãéãã",
                      new String[] {"çªãéãã"});
   }
 
   public void testBocchan() throws Exception {
     doTestBocchan(1);
   }
 
   @Nightly
   public void testBocchanBig() throws Exception {
     doTestBocchan(100);
   }
 
   /*
   public void testWikipedia() throws Exception {
     final FileInputStream fis = new FileInputStream("/q/lucene/jawiki-20120220-pages-articles.xml");
     final Reader r = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
 
     final long startTimeNS = System.nanoTime();
     boolean done = false;
     long compoundCount = 0;
     long nonCompoundCount = 0;
     long netOffset = 0;
     while (!done) {
       final TokenStream ts = analyzer.tokenStream("ignored", r);
       ts.reset();
       final PositionIncrementAttribute posIncAtt = ts.addAttribute(PositionIncrementAttribute.class);
       final OffsetAttribute offsetAtt = ts.addAttribute(OffsetAttribute.class);
       int count = 0;
       while (true) {
         if (!ts.incrementToken()) {
           done = true;
           break;
         }
         count++;
         if (posIncAtt.getPositionIncrement() == 0) {
           compoundCount++;
         } else {
           nonCompoundCount++;
           if (nonCompoundCount % 1000000 == 0) {
             System.out.println(String.format("%.2f msec [pos=%d, %d, %d]",
                                              (System.nanoTime()-startTimeNS)/1000000.0,
                                              netOffset + offsetAtt.startOffset(),
                                              nonCompoundCount,
                                              compoundCount));
           }
         }
         if (count == 100000000) {
           System.out.println("  again...");
           break;
         }
       }
       ts.end();
       netOffset += offsetAtt.endOffset();
     }
     System.out.println("compoundCount=" + compoundCount + " nonCompoundCount=" + nonCompoundCount);
     r.close();
   }
   */
 
   
   private void doTestBocchan(int numIterations) throws Exception {
     LineNumberReader reader = new LineNumberReader(new InputStreamReader(
         this.getClass().getResourceAsStream("bocchan.utf-8")));
     String line = reader.readLine();
     reader.close();
     
     if (VERBOSE) {
       System.out.println("Test for Bocchan without pre-splitting sentences");
     }
 
     /*
     if (numIterations > 1) {
       // warmup
       for (int i = 0; i < numIterations; i++) {
         final TokenStream ts = analyzer.tokenStream("ignored", new StringReader(line));
         ts.reset();
         while(ts.incrementToken());
       }
     }
     */
 
     long totalStart = System.currentTimeMillis();
     for (int i = 0; i < numIterations; i++) {
       final TokenStream ts = analyzer.tokenStream("ignored", new StringReader(line));
       ts.reset();
       while(ts.incrementToken());
     }
     String[] sentences = line.split("ã|ã");
     if (VERBOSE) {
       System.out.println("Total time : " + (System.currentTimeMillis() - totalStart));
       System.out.println("Test for Bocchan with pre-splitting sentences (" + sentences.length + " sentences)");
     }
     totalStart = System.currentTimeMillis();
     for (int i = 0; i < numIterations; i++) {
       for (String sentence: sentences) {
         final TokenStream ts = analyzer.tokenStream("ignored", new StringReader(sentence));
         ts.reset();
         while(ts.incrementToken());
       }
     }
     if (VERBOSE) {
       System.out.println("Total time : " + (System.currentTimeMillis() - totalStart));
     }
   }
 }
