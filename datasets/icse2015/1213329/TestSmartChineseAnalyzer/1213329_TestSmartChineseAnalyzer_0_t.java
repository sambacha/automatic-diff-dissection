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
 
 package org.apache.lucene.analysis.cn.smart;
 
import java.io.Reader;
 import java.io.StringReader;
 
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.TokenFilter;
 import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
 import org.apache.lucene.util.Version;
 
 public class TestSmartChineseAnalyzer extends BaseTokenStreamTestCase {
   
   public void testChineseStopWordsDefault() throws Exception {
     Analyzer ca = new SmartChineseAnalyzer(Version.LUCENE_CURRENT); /* will load stopwords */
     String sentence = "æè´­ä¹°äºéå·åæè£ã";
     String result[] = { "æ", "è´­ä¹°", "äº", "éå·", "å", "æè£" };
     assertAnalyzesTo(ca, sentence, result);
     // set stop-words from the outer world - must yield same behavior
     ca = new SmartChineseAnalyzer(Version.LUCENE_CURRENT, SmartChineseAnalyzer.getDefaultStopSet());
     assertAnalyzesTo(ca, sentence, result);
   }
   
   /*
    * This test is the same as the above, except with two phrases.
    * This tests to ensure the SentenceTokenizer->WordTokenFilter chain works correctly.
    */
   public void testChineseStopWordsDefaultTwoPhrases() throws Exception {
     Analyzer ca = new SmartChineseAnalyzer(Version.LUCENE_CURRENT); /* will load stopwords */
     String sentence = "æè´­ä¹°äºéå·åæè£ã æè´­ä¹°äºéå·åæè£ã";
     String result[] = { "æ", "è´­ä¹°", "äº", "éå·", "å", "æè£", "æ", "è´­ä¹°", "äº", "éå·", "å", "æè£" };
     assertAnalyzesTo(ca, sentence, result);
   }
   
   /*
    * This test is the same as the above, except using an ideographic space as a separator.
    * This tests to ensure the stopwords are working correctly.
    */
   public void testChineseStopWordsDefaultTwoPhrasesIdeoSpace() throws Exception {
     Analyzer ca = new SmartChineseAnalyzer(Version.LUCENE_CURRENT); /* will load stopwords */
     String sentence = "æè´­ä¹°äºéå·åæè£ãæè´­ä¹°äºéå·åæè£ã";
     String result[] = { "æ", "è´­ä¹°", "äº", "éå·", "å", "æè£", "æ", "è´­ä¹°", "äº", "éå·", "å", "æè£" };
     assertAnalyzesTo(ca, sentence, result);
   }
   
   /*
    * Punctuation is handled in a strange way if you disable stopwords
    * In this example the IDEOGRAPHIC FULL STOP is converted into a comma.
    * if you don't supply (true) to the constructor, or use a different stopwords list,
    * then punctuation is indexed.
    */
   public void testChineseStopWordsOff() throws Exception {
     Analyzer[] analyzers = new Analyzer[] {
       new SmartChineseAnalyzer(Version.LUCENE_CURRENT, false),/* doesn't load stopwords */
       new SmartChineseAnalyzer(Version.LUCENE_CURRENT, null) /* sets stopwords to empty set */};
     String sentence = "æè´­ä¹°äºéå·åæè£ã";
     String result[] = { "æ", "è´­ä¹°", "äº", "éå·", "å", "æè£", "," };
     for (Analyzer analyzer : analyzers) {
       assertAnalyzesTo(analyzer, sentence, result);
       assertAnalyzesToReuse(analyzer, sentence, result);
     }
   }
   
   /*
    * Check that position increments after stopwords are correct,
    * when stopfilter is configured with enablePositionIncrements
    */
   public void testChineseStopWords2() throws Exception {
     Analyzer ca = new SmartChineseAnalyzer(Version.LUCENE_CURRENT); /* will load stopwords */
     String sentence = "Title:San"; // : is a stopword
     String result[] = { "titl", "san"};
     int startOffsets[] = { 0, 6 };
     int endOffsets[] = { 5, 9 };
     int posIncr[] = { 1, 2 };
     assertAnalyzesTo(ca, sentence, result, startOffsets, endOffsets, posIncr);
   }
   
   public void testChineseAnalyzer() throws Exception {
     Analyzer ca = new SmartChineseAnalyzer(Version.LUCENE_CURRENT, true);
     String sentence = "æè´­ä¹°äºéå·åæè£ã";
     String[] result = { "æ", "è´­ä¹°", "äº", "éå·", "å", "æè£" };
     assertAnalyzesTo(ca, sentence, result);
   }
   
   /*
    * English words are lowercased and porter-stemmed.
    */
   public void testMixedLatinChinese() throws Exception {
     assertAnalyzesTo(new SmartChineseAnalyzer(Version.LUCENE_CURRENT, true), "æè´­ä¹° Tests äºéå·åæè£", 
         new String[] { "æ", "è´­ä¹°", "test", "äº", "éå·", "å", "æè£"});
   }
   
   /*
    * Numerics are parsed as their own tokens
    */
   public void testNumerics() throws Exception {
     assertAnalyzesTo(new SmartChineseAnalyzer(Version.LUCENE_CURRENT, true), "æè´­ä¹° Tests äºéå·åæè£1234",
       new String[] { "æ", "è´­ä¹°", "test", "äº", "éå·", "å", "æè£", "1234"});
   }
   
   /*
    * Full width alphas and numerics are folded to half-width
    */
   public void testFullWidth() throws Exception {
     assertAnalyzesTo(new SmartChineseAnalyzer(Version.LUCENE_CURRENT, true), "æè´­ä¹° ï¼´ï½ï½ï½ï½ äºéå·åæè£ï¼ï¼ï¼ï¼",
         new String[] { "æ", "è´­ä¹°", "test", "äº", "éå·", "å", "æè£", "1234"});
   }
   
   /*
    * Presentation form delimiters are removed
    */
   public void testDelimiters() throws Exception {
     assertAnalyzesTo(new SmartChineseAnalyzer(Version.LUCENE_CURRENT, true), "æè´­ä¹°ï¸± Tests äºéå·åæè£", 
         new String[] { "æ", "è´­ä¹°", "test", "äº", "éå·", "å", "æè£"});
   }
   
   /*
    * Text from writing systems other than Chinese and Latin are parsed as individual characters.
    * (regardless of Unicode category)
    */
   public void testNonChinese() throws Exception {
     assertAnalyzesTo(new SmartChineseAnalyzer(Version.LUCENE_CURRENT, true), "æè´­ä¹° Ø±ÙØ¨Ø±ØªTests äºéå·åæè£", 
         new String[] { "æ", "è´­ä¹°", "Ø±", "Ù", "Ø¨", "Ø±", "Øª", "test", "äº", "éå·", "å", "æè£"});
   }
   
   /*
    * Test what the analyzer does with out-of-vocabulary words.
    * In this case the name is Yousaf Raza Gillani.
    * Currently it is being analyzed into single characters...
    */
   public void testOOV() throws Exception {
     assertAnalyzesTo(new SmartChineseAnalyzer(Version.LUCENE_CURRENT, true), "ä¼ç´ ç¦Â·ææÂ·åæå°¼",
       new String[] { "ä¼", "ç´ ", "ç¦", "æ", "æ", "å", "æ", "å°¼" });
     
     assertAnalyzesTo(new SmartChineseAnalyzer(Version.LUCENE_CURRENT, true), "ä¼ç´ ç¦ææåæå°¼",
       new String[] { "ä¼", "ç´ ", "ç¦", "æ", "æ", "å", "æ", "å°¼" });
   }
   
   public void testOffsets() throws Exception {
     assertAnalyzesTo(new SmartChineseAnalyzer(Version.LUCENE_CURRENT, true), "æè´­ä¹°äºéå·åæè£",
         new String[] { "æ", "è´­ä¹°", "äº", "éå·", "å", "æè£" },
         new int[] { 0, 1, 3, 4, 6, 7 },
         new int[] { 1, 3, 4, 6, 7, 9 });
   }
   
   public void testReusableTokenStream() throws Exception {
     Analyzer a = new SmartChineseAnalyzer(Version.LUCENE_CURRENT);
     assertAnalyzesToReuse(a, "æè´­ä¹° Tests äºéå·åæè£", 
         new String[] { "æ", "è´­ä¹°", "test", "äº", "éå·", "å", "æè£"},
         new int[] { 0, 1, 4, 10, 11, 13, 14 },
         new int[] { 1, 3, 9, 11, 13, 14, 16 });
     assertAnalyzesToReuse(a, "æè´­ä¹°äºéå·åæè£ã",
         new String[] { "æ", "è´­ä¹°", "äº", "éå·", "å", "æè£" },
         new int[] { 0, 1, 3, 4, 6, 7 },
         new int[] { 1, 3, 4, 6, 7, 9 });
   }
   
   // LUCENE-3026
   public void testLargeDocument() throws Exception {
     StringBuilder sb = new StringBuilder();
     for (int i = 0; i < 5000; i++) {
       sb.append("æè´­ä¹°äºéå·åæè£ã");
     }
     Analyzer analyzer = new SmartChineseAnalyzer(TEST_VERSION_CURRENT);
     TokenStream stream = analyzer.tokenStream("", new StringReader(sb.toString()));
     stream.reset();
     while (stream.incrementToken()) {
     }
   }
   
   // LUCENE-3026
   public void testLargeSentence() throws Exception {
     StringBuilder sb = new StringBuilder();
     for (int i = 0; i < 5000; i++) {
       sb.append("æè´­ä¹°äºéå·åæè£");
     }
     Analyzer analyzer = new SmartChineseAnalyzer(TEST_VERSION_CURRENT);
     TokenStream stream = analyzer.tokenStream("", new StringReader(sb.toString()));
     stream.reset();
     while (stream.incrementToken()) {
     }
   }
   
  // LUCENE-3642
  public void testInvalidOffset() throws Exception {
    Analyzer analyzer = new Analyzer() {
      @Override
      protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer tokenizer = new MockTokenizer(reader, MockTokenizer.WHITESPACE, false);
        TokenFilter filters = new ASCIIFoldingFilter(tokenizer);
        filters = new WordTokenFilter(filters);
        return new TokenStreamComponents(tokenizer, filters);
      }
    };
    
    assertAnalyzesTo(analyzer, "mosfellsbÃ¦r", 
        new String[] { "mosfellsbaer" },
        new int[]    { 0 },
        new int[]    { 11 });
  }
  
   /** blast some random strings through the analyzer */
   public void testRandomStrings() throws Exception {
     checkRandomData(random, new SmartChineseAnalyzer(TEST_VERSION_CURRENT), 10000*RANDOM_MULTIPLIER);
   }
 }
