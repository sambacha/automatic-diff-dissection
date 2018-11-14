 package org.apache.lucene.analysis.ja;
 
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
 
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.CharFilter;
 import org.apache.lucene.analysis.MockTokenizer;
 import org.apache.lucene.analysis.Tokenizer;
 
 import java.io.IOException;
 import java.io.Reader;
 import java.io.StringReader;
 
 public class TestJapaneseIterationMarkCharFilter extends BaseTokenStreamTestCase {
 
   private Analyzer keywordAnalyzer = new Analyzer() {
     @Override
     protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
       Tokenizer tokenizer = new MockTokenizer(reader, MockTokenizer.KEYWORD, false);
       return new TokenStreamComponents(tokenizer, tokenizer);
     }
 
     @Override
     protected Reader initReader(String fieldName, Reader reader) {
       return new JapaneseIterationMarkCharFilter(reader);
     }
   };
 
   private Analyzer japaneseAnalyzer = new Analyzer() {
     @Override
     protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
       Tokenizer tokenizer = new JapaneseTokenizer(reader, null, false, JapaneseTokenizer.Mode.SEARCH);
       return new TokenStreamComponents(tokenizer, tokenizer);
     }
 
     @Override
     protected Reader initReader(String fieldName, Reader reader) {
       return new JapaneseIterationMarkCharFilter(reader);
     }
   };
   
   public void testKanji() throws IOException {
     // Test single repetition
     assertAnalyzesTo(keywordAnalyzer, "æã", new String[]{"ææ"});
     assertAnalyzesTo(japaneseAnalyzer, "æã", new String[]{"ææ"});
 
     // Test multiple repetitions
     assertAnalyzesTo(keywordAnalyzer, "é¦¬é¹¿ãããã", new String[]{"é¦¬é¹¿é¦¬é¹¿ãã"});
     assertAnalyzesTo(japaneseAnalyzer, "é¦¬é¹¿ãããã", new String[]{"é¦¬é¹¿é¦¬é¹¿ãã"});
   }
 
   public void testKatakana() throws IOException {
     // Test single repetition
     assertAnalyzesTo(keywordAnalyzer, "ãã¹ã¾", new String[]{"ãã¹ãº"});
     assertAnalyzesTo(japaneseAnalyzer, "ãã¹ã¾", new String[]{"ã", "ã¹ãº"}); // Side effect
   }
 
   public void testHiragana() throws IOException {
     // Test single unvoiced iteration
     assertAnalyzesTo(keywordAnalyzer, "ããã®", new String[]{"ããã®"});
     assertAnalyzesTo(japaneseAnalyzer, "ããã®", new String[]{"ã", "ãã®"}); // Side effect
 
     // Test single voiced iteration
     assertAnalyzesTo(keywordAnalyzer, "ã¿ãã", new String[]{"ã¿ãã"});
     assertAnalyzesTo(japaneseAnalyzer, "ã¿ãã", new String[]{"ã¿ãã"});
 
     // Test single voiced iteration
     assertAnalyzesTo(keywordAnalyzer, "ãã", new String[]{"ãã"});
     assertAnalyzesTo(japaneseAnalyzer, "ãã", new String[]{"ãã"});
 
     // Test single unvoiced iteration with voiced iteration
     assertAnalyzesTo(keywordAnalyzer, "ãã", new String[]{"ãã"});
     assertAnalyzesTo(japaneseAnalyzer, "ãã", new String[]{"ãã"});
 
     // Test multiple repetitions with voiced iteration
     assertAnalyzesTo(keywordAnalyzer, "ã¨ããããã", new String[]{"ã¨ããã©ãã"});
     assertAnalyzesTo(japaneseAnalyzer, "ã¨ããããã", new String[]{"ã¨ããã©ãã"});
   }
 
   public void testMalformed() throws IOException {
     // We can't iterate c here, so emit as it is
     assertAnalyzesTo(keywordAnalyzer, "abcã¨ãããããã", new String[]{"abcã¨ããcã¨ãã"});
 
     // We can't iterate c (with dakuten change) here, so emit it as-is
     assertAnalyzesTo(keywordAnalyzer, "abcã¨ãããããã", new String[]{"abcã¨ããcã¨ãã"});
 
     // We can't iterate before beginning of stream, so emit characters as-is
     assertAnalyzesTo(keywordAnalyzer, "ã¨ãããããããã", new String[]{"ã¨ããã©ããããã"});
 
     // We can't iterate an iteration mark only, so emit as-is
     assertAnalyzesTo(keywordAnalyzer, "ã", new String[]{"ã"});
     assertAnalyzesTo(keywordAnalyzer, "ã", new String[]{"ã"});
     assertAnalyzesTo(keywordAnalyzer, "ãã", new String[]{"ãã"});
 
     // We can't iterate a full stop punctuation mark (because we use it as a flush marker)
     assertAnalyzesTo(keywordAnalyzer, "ãã", new String[]{"ãã"});
     assertAnalyzesTo(keywordAnalyzer, "ãããã", new String[]{"ãããã"});
 
     // We can iterate other punctuation marks
     assertAnalyzesTo(keywordAnalyzer, "ï¼ã", new String[]{"ï¼ï¼"});
 
     // We can not get a dakuten variant of ã½ -- this is also a corner case test for inside()
     assertAnalyzesTo(keywordAnalyzer, "ã­ãã½ãã¤ãã´", new String[]{"ã­ãã½ã½ã¤ãã´"});
     assertAnalyzesTo(keywordAnalyzer, "ã­ãã½ãã¤ãã´", new String[]{"ã­ãã½ã½ã¤ãã´"});
   }
 
   public void testEmpty() throws IOException {
     // Empty input stays empty
     assertAnalyzesTo(keywordAnalyzer, "", new String[0]);
     assertAnalyzesTo(japaneseAnalyzer, "", new String[0]);
   }
 
   public void testFullStop() throws IOException {
     // Test full stops   
     assertAnalyzesTo(keywordAnalyzer, "ã", new String[]{"ã"});
     assertAnalyzesTo(keywordAnalyzer, "ãã", new String[]{"ãã"});
     assertAnalyzesTo(keywordAnalyzer, "ããã", new String[]{"ããã"});
   }
 
   public void testKanjiOnly() throws IOException {
     // Test kanji only repetition marks
     CharFilter filter = new JapaneseIterationMarkCharFilter(
         new StringReader("æããããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ããã§ããabcã¨ãããããã"),
         true, // kanji
         false // no kana
     );
     assertCharFilterEquals(filter, "ææãããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ããã§ããabcã¨ãããããã");
   }
 
   public void testKanaOnly() throws IOException {
     // Test kana only repetition marks
     CharFilter filter = new JapaneseIterationMarkCharFilter(
         new StringReader("æããããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ããã§ããabcã¨ãããããã"),
         false, // no kanji
         true   // kana
     );
     assertCharFilterEquals(filter, "æããããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ããã§ããabcã¨ããã©ããã");
   }
 
   public void testNone() throws IOException {
     // Test no repetition marks
     CharFilter filter = new JapaneseIterationMarkCharFilter(
         new StringReader("æããããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ããã§ããabcã¨ãããããã"),
         false, // no kanji
         false  // no kana
     );
     assertCharFilterEquals(filter, "æããããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ããã§ããabcã¨ãããããã");
   }
 
   public void testCombinations() throws IOException {
     assertAnalyzesTo(keywordAnalyzer, "æããããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ã«è¡ãã¾ãã",
         new String[]{"ææãããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ã«è¡ãã¾ãã"}
     );
   }
   
   public void testHiraganaCoverage() throws IOException {
     // Test all hiragana iteration variants
     String source = "ããããããããããããããããããããããããããããããããããããããããããã ãã¡ãã¢ãã¤ãã¥ãã¦ãã§ãã¨ãã©ãã¯ãã°ãã²ãã³ããµãã¶ãã¸ãã¹ãã»ãã¼ã";
     String target = "ããããããããããããããããããããããããããããããããããããããããããã ãã¡ã¡ã¢ã¡ã¤ã¤ã¥ã¤ã¦ã¦ã§ã¦ã¨ã¨ã©ã¨ã¯ã¯ã°ã¯ã²ã²ã³ã²ãµãµã¶ãµã¸ã¸ã¹ã¸ã»ã»ã¼ã»";
     assertAnalyzesTo(keywordAnalyzer, source, new String[]{target});
 
     // Test all hiragana iteration variants with dakuten
     source = "ããããããããããããããããããããããããããããããããããããããããããã ãã¡ãã¢ãã¤ãã¥ãã¦ãã§ãã¨ãã©ãã¯ãã°ãã²ãã³ããµãã¶ãã¸ãã¹ãã»ãã¼ã";
     target = "ãããããããããããããããããããããããããããããããããããããããããã ã ã ã¡ã¢ã¢ã¢ã¤ã¥ã¥ã¥ã¦ã§ã§ã§ã¨ã©ã©ã©ã¯ã°ã°ã°ã²ã³ã³ã³ãµã¶ã¶ã¶ã¸ã¹ã¹ã¹ã»ã¼ã¼ã¼";
     assertAnalyzesTo(keywordAnalyzer, source, new String[]{target});
   }
 
   public void testKatakanaCoverage() throws IOException {
     // Test all katakana iteration variants
     String source = "ã«ã½ã¬ã½ã­ã½ã®ã½ã¯ã½ã°ã½ã±ã½ã²ã½ã³ã½ã´ã½ãµã½ã¶ã½ã·ã½ã¸ã½ã¹ã½ãºã½ã»ã½ã¼ã½ã½ã½ã¾ã½ã¿ã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½";
     String target = "ã«ã«ã¬ã«ã­ã­ã®ã­ã¯ã¯ã°ã¯ã±ã±ã²ã±ã³ã³ã´ã³ãµãµã¶ãµã·ã·ã¸ã·ã¹ã¹ãºã¹ã»ã»ã¼ã»ã½ã½ã¾ã½ã¿ã¿ãã¿ãããããããããããããããããããããããããããããããããããã";
     assertAnalyzesTo(keywordAnalyzer, source, new String[]{target});
 
     // Test all katakana iteration variants with dakuten
     source = "ã«ã¾ã¬ã¾ã­ã¾ã®ã¾ã¯ã¾ã°ã¾ã±ã¾ã²ã¾ã³ã¾ã´ã¾ãµã¾ã¶ã¾ã·ã¾ã¸ã¾ã¹ã¾ãºã¾ã»ã¾ã¼ã¾ã½ã¾ã¾ã¾ã¿ã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾";
     target = "ã«ã¬ã¬ã¬ã­ã®ã®ã®ã¯ã°ã°ã°ã±ã²ã²ã²ã³ã´ã´ã´ãµã¶ã¶ã¶ã·ã¸ã¸ã¸ã¹ãºãºãºã»ã¼ã¼ã¼ã½ã¾ã¾ã¾ã¿ããããããããããããããããããããããããããããããããããããããã";
     assertAnalyzesTo(keywordAnalyzer, source, new String[]{target});
   }
     
   public void testRandomStrings() throws Exception {
     // Blast some random strings through
     checkRandomData(random(), keywordAnalyzer, 1000 * RANDOM_MULTIPLIER);
   }
   
   public void testRandomHugeStrings() throws Exception {
     // Blast some random strings through
     checkRandomData(random(), keywordAnalyzer, 100 * RANDOM_MULTIPLIER, 8192);
   }
 
   private void assertCharFilterEquals(CharFilter filter, String expected) throws IOException {
     String actual = readFully(filter);
     assertEquals(expected, actual);
   }
 
   private String readFully(Reader stream) throws IOException {
    StringBuilder buffer = new StringBuilder();
     int ch;
     while ((ch = stream.read()) != -1) {
       buffer.append((char) ch);
     }
     return buffer.toString();
   }
 }
 
