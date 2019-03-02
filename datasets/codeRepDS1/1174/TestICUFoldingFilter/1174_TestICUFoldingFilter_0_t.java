 package org.apache.lucene.analysis.icu;
 
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
 
 import org.apache.lucene.analysis.*;
 import org.apache.lucene.analysis.core.KeywordTokenizer;
 
 /**
  * Tests ICUFoldingFilter
  */
 public class TestICUFoldingFilter extends BaseTokenStreamTestCase {
   Analyzer a = new Analyzer() {
     @Override
     public TokenStreamComponents createComponents(String fieldName, Reader reader) {
       Tokenizer tokenizer = new MockTokenizer(reader, MockTokenizer.WHITESPACE, false);
       return new TokenStreamComponents(tokenizer, new ICUFoldingFilter(tokenizer));
     }
   };
   public void testDefaults() throws IOException {
     // case folding
     assertAnalyzesTo(a, "This is a test", new String[] { "this", "is", "a", "test" });
 
     // case folding
     assertAnalyzesTo(a, "RuÃ", new String[] { "russ" });
     
     // case folding with accent removal
     assertAnalyzesTo(a, "ÎÎÎªÎÎ£", new String[] { "Î¼Î±Î¹Î¿Ï" });
     assertAnalyzesTo(a, "ÎÎ¬ÏÎ¿Ï", new String[] { "Î¼Î±Î¹Î¿Ï" });
 
     // supplementary case folding
     assertAnalyzesTo(a, "ð", new String[] { "ð¾" });
     
     // normalization
     assertAnalyzesTo(a, "ï´³ï´ºï°§", new String[] { "Ø·ÙØ·ÙØ·Ù" });
 
     // removal of default ignorables
     assertAnalyzesTo(a, "à¤à¥âà¤·", new String[] { "à¤à¤·" });
     
     // removal of latin accents (composed)
     assertAnalyzesTo(a, "rÃ©sumÃ©", new String[] { "resume" });
     
     // removal of latin accents (decomposed)
     assertAnalyzesTo(a, "re\u0301sume\u0301", new String[] { "resume" });
     
     // fold native digits
     assertAnalyzesTo(a, "à§­à§¦à§¬", new String[] { "706" });
     
     // ascii-folding-filter type stuff
     assertAnalyzesTo(a, "Äis is crÃ¦zy", new String[] { "dis", "is", "craezy" });
 
     // proper downcasing of Turkish dotted-capital I
     // (according to default case folding rules)
     assertAnalyzesTo(a, "ELÄ°F", new String[] { "elif" });
     
     // handling of decomposed combining-dot-above
     assertAnalyzesTo(a, "eli\u0307f", new String[] { "elif" });
   }
   
   /** blast some random strings through the analyzer */
   public void testRandomStrings() throws Exception {
    checkRandomData(random(), a, 1000*RANDOM_MULTIPLIER);
   }
   
   public void testEmptyTerm() throws IOException {
     Analyzer a = new Analyzer() {
       @Override
       protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
         Tokenizer tokenizer = new KeywordTokenizer(reader);
         return new TokenStreamComponents(tokenizer, new ICUFoldingFilter(tokenizer));
       }
     };
     checkOneTermReuse(a, "", "");
   }
 }
