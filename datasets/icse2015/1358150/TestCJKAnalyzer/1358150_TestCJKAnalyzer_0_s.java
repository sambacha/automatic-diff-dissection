 package org.apache.lucene.analysis.cjk;
 
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
 import org.apache.lucene.analysis.CharReader;
 import org.apache.lucene.analysis.MockTokenizer;
 import org.apache.lucene.analysis.TokenFilter;
 import org.apache.lucene.analysis.TokenStream;
 import org.apache.lucene.analysis.Tokenizer;
 import org.apache.lucene.analysis.charfilter.MappingCharFilter;
 import org.apache.lucene.analysis.charfilter.NormalizeCharMap;
 import org.apache.lucene.analysis.core.KeywordTokenizer;
 import org.apache.lucene.analysis.core.StopFilter;
 import org.apache.lucene.analysis.standard.StandardTokenizer;
 import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
 import org.apache.lucene.analysis.util.CharArraySet;
 
 /**
  * Most tests adopted from TestCJKTokenizer
  */
 public class TestCJKAnalyzer extends BaseTokenStreamTestCase {
   private Analyzer analyzer = new CJKAnalyzer(TEST_VERSION_CURRENT);
   
   public void testJa1() throws IOException {
     assertAnalyzesTo(analyzer, "ä¸äºä¸åäºå­ä¸å«ä¹å",
       new String[] { "ä¸äº", "äºä¸", "ä¸å", "åäº", "äºå­", "å­ä¸", "ä¸å«", "å«ä¹", "ä¹å" },
       new int[] { 0, 1, 2, 3, 4, 5, 6, 7,  8 },
       new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10 },
       new String[] { "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>" },
       new int[] { 1, 1, 1, 1, 1, 1, 1, 1,  1 });
   }
   
   public void testJa2() throws IOException {
     assertAnalyzesTo(analyzer, "ä¸ äºä¸å äºå­ä¸å«ä¹ å",
       new String[] { "ä¸", "äºä¸", "ä¸å", "äºå­", "å­ä¸", "ä¸å«", "å«ä¹", "å" },
       new int[] { 0, 2, 3, 6, 7,  8,  9, 12 },
       new int[] { 1, 4, 5, 8, 9, 10, 11, 13 },
       new String[] { "<SINGLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<SINGLE>" },
       new int[] { 1, 1, 1, 1, 1,  1,  1,  1 });
   }
   
   public void testC() throws IOException {
     assertAnalyzesTo(analyzer, "abc defgh ijklmn opqrstu vwxy z",
       new String[] { "abc", "defgh", "ijklmn", "opqrstu", "vwxy", "z" },
       new int[] { 0, 4, 10, 17, 25, 30 },
       new int[] { 3, 9, 16, 24, 29, 31 },
       new String[] { "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>" },
       new int[] { 1, 1,  1,  1,  1,  1 });
   }
   
   /**
    * LUCENE-2207: wrong offset calculated by end() 
    */
   public void testFinalOffset() throws IOException {
     assertAnalyzesTo(analyzer, "ãã",
       new String[] { "ãã" },
       new int[] { 0 },
       new int[] { 2 },
       new String[] { "<DOUBLE>" },
       new int[] { 1 });
     
     assertAnalyzesTo(analyzer, "ãã   ",
       new String[] { "ãã" },
       new int[] { 0 },
       new int[] { 2 },
       new String[] { "<DOUBLE>" },
       new int[] { 1 });
 
     assertAnalyzesTo(analyzer, "test",
       new String[] { "test" },
       new int[] { 0 },
       new int[] { 4 },
       new String[] { "<ALPHANUM>" },
       new int[] { 1 });
     
     assertAnalyzesTo(analyzer, "test   ",
       new String[] { "test" },
       new int[] { 0 },
       new int[] { 4 },
       new String[] { "<ALPHANUM>" },
       new int[] { 1 });
     
     assertAnalyzesTo(analyzer, "ããtest",
       new String[] { "ãã", "test" },
       new int[] { 0, 2 },
       new int[] { 2, 6 },
       new String[] { "<DOUBLE>", "<ALPHANUM>" },
       new int[] { 1, 1 });
     
     assertAnalyzesTo(analyzer, "testãã    ",
       new String[] { "test", "ãã" },
       new int[] { 0, 4 },
       new int[] { 4, 6 },
       new String[] { "<ALPHANUM>", "<DOUBLE>" },
       new int[] { 1, 1 });
   }
   
   public void testMix() throws IOException {
     assertAnalyzesTo(analyzer, "ãããããabcããããã",
       new String[] { "ãã", "ãã", "ãã", "ãã", "abc", "ãã", "ãã", "ãã", "ãã" },
       new int[] { 0, 1, 2, 3, 5,  8,  9, 10, 11 },
       new int[] { 2, 3, 4, 5, 8, 10, 11, 12, 13 },
       new String[] { "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<ALPHANUM>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>" },
       new int[] { 1, 1, 1, 1, 1,  1,  1,  1,  1});
   }
   
   public void testMix2() throws IOException {
     assertAnalyzesTo(analyzer, "ãããããabãcãããã ã",
       new String[] { "ãã", "ãã", "ãã", "ãã", "ab", "ã", "c", "ãã", "ãã", "ãã", "ã" },
       new int[] { 0, 1, 2, 3, 5, 7, 8,  9, 10, 11, 14 },
       new int[] { 2, 3, 4, 5, 7, 8, 9, 11, 12, 13, 15 },
       new String[] { "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<ALPHANUM>", "<SINGLE>", "<ALPHANUM>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<SINGLE>" },
       new int[] { 1, 1, 1, 1, 1, 1, 1,  1,  1,  1,  1 });
   }
   
   /**
    * Non-english text (outside of CJK) is treated normally, according to unicode rules 
    */
   public void testNonIdeographic() throws IOException {
     assertAnalyzesTo(analyzer, "ä¸ Ø±ÙØ¨Ø±Øª ÙÙÙØ±",
       new String[] { "ä¸", "Ø±ÙØ¨Ø±Øª", "ÙÙÙØ±" },
       new int[] { 0, 2, 8 },
       new int[] { 1, 7, 12 },
       new String[] { "<SINGLE>", "<ALPHANUM>", "<ALPHANUM>" },
       new int[] { 1, 1, 1 });
   }
   
   /**
    * Same as the above, except with a nonspacing mark to show correctness.
    */
   public void testNonIdeographicNonLetter() throws IOException {
     assertAnalyzesTo(analyzer, "ä¸ Ø±ÙÙØ¨Ø±Øª ÙÙÙØ±",
       new String[] { "ä¸", "Ø±ÙÙØ¨Ø±Øª", "ÙÙÙØ±" },
       new int[] { 0, 2, 9 },
       new int[] { 1, 8, 13 },
       new String[] { "<SINGLE>", "<ALPHANUM>", "<ALPHANUM>" },
       new int[] { 1, 1, 1 });
   }
   
   public void testSurrogates() throws IOException {
     assertAnalyzesTo(analyzer, "ð©¬è±éä¹æ¯ç",
       new String[] { "ð©¬è±", "è±é", "éä¹", "ä¹æ¯", "æ¯ç" },
       new int[] { 0, 2, 3, 4, 5 },
       new int[] { 3, 4, 5, 6, 7 },
       new String[] { "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>" },
       new int[] { 1, 1, 1, 1, 1 });
   }
   
   public void testReusableTokenStream() throws IOException {
     assertAnalyzesToReuse(analyzer, "ãããããabcããããã",
         new String[] { "ãã", "ãã", "ãã", "ãã", "abc", "ãã", "ãã", "ãã", "ãã" },
         new int[] { 0, 1, 2, 3, 5,  8,  9, 10, 11 },
         new int[] { 2, 3, 4, 5, 8, 10, 11, 12, 13 },
         new String[] { "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<ALPHANUM>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>" },
         new int[] { 1, 1, 1, 1, 1,  1,  1,  1,  1});
     
     assertAnalyzesToReuse(analyzer, "ãããããabãcãããã ã",
         new String[] { "ãã", "ãã", "ãã", "ãã", "ab", "ã", "c", "ãã", "ãã", "ãã", "ã" },
         new int[] { 0, 1, 2, 3, 5, 7, 8,  9, 10, 11, 14 },
         new int[] { 2, 3, 4, 5, 7, 8, 9, 11, 12, 13, 15 },
         new String[] { "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<ALPHANUM>", "<SINGLE>", "<ALPHANUM>", "<DOUBLE>", "<DOUBLE>", "<DOUBLE>", "<SINGLE>" },
         new int[] { 1, 1, 1, 1, 1, 1, 1,  1,  1,  1,  1 });
   }
   
   public void testSingleChar() throws IOException {
     assertAnalyzesTo(analyzer, "ä¸",
       new String[] { "ä¸" },
       new int[] { 0 },
       new int[] { 1 },
       new String[] { "<SINGLE>" },
       new int[] { 1 });
   }
   
   public void testTokenStream() throws IOException {
     assertAnalyzesTo(analyzer, "ä¸ä¸ä¸", 
       new String[] { "ä¸ä¸", "ä¸ä¸"},
       new int[] { 0, 1 },
       new int[] { 2, 3 },
       new String[] { "<DOUBLE>", "<DOUBLE>" },
       new int[] { 1, 1 });
   }
   
   /** test that offsets are correct when mappingcharfilter is previously applied */
   public void testChangedOffsets() throws IOException {
     final NormalizeCharMap.Builder builder = new NormalizeCharMap.Builder();
     builder.add("a", "ä¸äº");
     builder.add("b", "äºä¸");
     final NormalizeCharMap norm = builder.build();
     Analyzer analyzer = new Analyzer() {
       @Override
       protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
         Tokenizer tokenizer = new StandardTokenizer(TEST_VERSION_CURRENT, reader);
         return new TokenStreamComponents(tokenizer, new CJKBigramFilter(tokenizer));
       }
 
       @Override
       protected Reader initReader(String fieldName, Reader reader) {
         return new MappingCharFilter(norm, CharReader.get(reader));
       }
     };
     
     assertAnalyzesTo(analyzer, "ab",
         new String[] { "ä¸äº", "äºäº", "äºä¸" },
         new int[] { 0, 0, 1 },
         new int[] { 1, 1, 2 });
     
     // note: offsets are strange since this is how the charfilter maps them... 
     // before bigramming, the 4 tokens look like:
     //   { 0, 0, 1, 1 },
     //   { 0, 1, 1, 2 }
   }
 
   private static class FakeStandardTokenizer extends TokenFilter {
     final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
     
     public FakeStandardTokenizer(TokenStream input) {
       super(input);
     }
 
     @Override
     public boolean incrementToken() throws IOException {
       if (input.incrementToken()) {
         typeAtt.setType(StandardTokenizer.TOKEN_TYPES[StandardTokenizer.IDEOGRAPHIC]);
         return true;
       } else {
         return false;
       }
     }
   }
 
   public void testSingleChar2() throws Exception {
     Analyzer analyzer = new Analyzer() {
 
       @Override
       protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
         Tokenizer tokenizer = new MockTokenizer(reader, MockTokenizer.WHITESPACE, false);
         TokenFilter filter = new FakeStandardTokenizer(tokenizer);
         filter = new StopFilter(TEST_VERSION_CURRENT, filter, CharArraySet.EMPTY_SET);
         filter = new CJKBigramFilter(filter);
         return new TokenStreamComponents(tokenizer, filter);
       }
     };
     
     assertAnalyzesTo(analyzer, "ä¸",
         new String[] { "ä¸" },
         new int[] { 0 },
         new int[] { 1 },
         new String[] { "<SINGLE>" },
         new int[] { 1 });
   }
   
   /** blast some random strings through the analyzer */
   public void testRandomStrings() throws Exception {
    checkRandomData(random(), new CJKAnalyzer(TEST_VERSION_CURRENT), 10000*RANDOM_MULTIPLIER);
   }
   
   /** blast some random strings through the analyzer */
   public void testRandomHugeStrings() throws Exception {
     Random random = random();
    checkRandomData(random, new CJKAnalyzer(TEST_VERSION_CURRENT), 200*RANDOM_MULTIPLIER, 8192);
   }
   
   public void testEmptyTerm() throws IOException {
     Analyzer a = new Analyzer() {
       @Override
       protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
         Tokenizer tokenizer = new KeywordTokenizer(reader);
         return new TokenStreamComponents(tokenizer, new CJKBigramFilter(tokenizer));
       }
     };
     checkOneTermReuse(a, "", "");
   }
 }
