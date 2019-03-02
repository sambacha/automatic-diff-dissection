 package org.apache.lucene.analysis.icu.segmentation;
 
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
 
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.TokenFilter;
 import org.apache.lucene.analysis.TokenStream;
 import org.apache.lucene.analysis.Tokenizer;
 import org.apache.lucene.analysis.icu.ICUNormalizer2Filter;
 import org.apache.lucene.analysis.icu.tokenattributes.ScriptAttribute;
import org.apache.lucene.util.IOUtils;
 
 import com.ibm.icu.lang.UScript;
 
 import java.io.IOException;
 import java.io.Reader;
 import java.io.StringReader;
 import java.util.Arrays;
 import java.util.Random;
 
 public class TestICUTokenizer extends BaseTokenStreamTestCase {
   
   public void testHugeDoc() throws IOException {
     StringBuilder sb = new StringBuilder();
     char whitespace[] = new char[4094];
     Arrays.fill(whitespace, ' ');
     sb.append(whitespace);
     sb.append("testing 1234");
     String input = sb.toString();
     ICUTokenizer tokenizer = new ICUTokenizer(new StringReader(input));
     assertTokenStreamContents(tokenizer, new String[] { "testing", "1234" });
   }
   
   public void testHugeTerm2() throws IOException {
     StringBuilder sb = new StringBuilder();
     for (int i = 0; i < 40960; i++) {
       sb.append('a');
     }
     String input = sb.toString();
     ICUTokenizer tokenizer = new ICUTokenizer(new StringReader(input));
     char token[] = new char[4096];
     Arrays.fill(token, 'a');
     String expectedToken = new String(token);
     String expected[] = { 
         expectedToken, expectedToken, expectedToken, 
         expectedToken, expectedToken, expectedToken,
         expectedToken, expectedToken, expectedToken,
         expectedToken
     };
     assertTokenStreamContents(tokenizer, expected);
   }
   
   private Analyzer a = new Analyzer() {
     @Override
     protected TokenStreamComponents createComponents(String fieldName,
         Reader reader) {
       Tokenizer tokenizer = new ICUTokenizer(reader);
       TokenFilter filter = new ICUNormalizer2Filter(tokenizer);
       return new TokenStreamComponents(tokenizer, filter);
     }
   };
 
   public void testArmenian() throws Exception {
     assertAnalyzesTo(a, "ÕÕ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ« 13 Õ´Õ«Õ¬Õ«Õ¸Õ¶ Õ°Õ¸Õ¤Õ¾Õ¡Õ®Õ¶Õ¥ÖÕ¨ (4,600` Õ°Õ¡ÕµÕ¥ÖÕ¥Õ¶ Õ¾Õ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ¸ÖÕ´) Õ£ÖÕ¾Õ¥Õ¬ Õ¥Õ¶ Õ¯Õ¡Õ´Õ¡Õ¾Õ¸ÖÕ¶Õ¥ÖÕ« Õ¯Õ¸Õ²Õ´Õ«Ö Õ¸Ö Õ°Õ¡Õ´Õ¡ÖÕµÕ¡ Õ¢Õ¸Õ¬Õ¸Ö Õ°Õ¸Õ¤Õ¾Õ¡Õ®Õ¶Õ¥ÖÕ¨ Õ¯Õ¡ÖÕ¸Õ² Õ§ Õ­Õ´Õ¢Õ¡Õ£ÖÕ¥Õ¬ ÖÕ¡Õ¶Õ¯Õ¡Ö Õ´Õ¡ÖÕ¤ Õ¸Õ¾ Õ¯Õ¡ÖÕ¸Õ² Õ§ Õ¢Õ¡ÖÕ¥Õ¬ ÕÕ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ« Õ¯Õ¡ÕµÖÕ¨Ö",
         new String[] { "Õ¾Õ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ«", "13", "Õ´Õ«Õ¬Õ«Õ¸Õ¶", "Õ°Õ¸Õ¤Õ¾Õ¡Õ®Õ¶Õ¥ÖÕ¨", "4,600", "Õ°Õ¡ÕµÕ¥ÖÕ¥Õ¶", "Õ¾Õ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ¸ÖÕ´", "Õ£ÖÕ¾Õ¥Õ¬", "Õ¥Õ¶", "Õ¯Õ¡Õ´Õ¡Õ¾Õ¸ÖÕ¶Õ¥ÖÕ«", "Õ¯Õ¸Õ²Õ´Õ«Ö", 
         "Õ¸Ö", "Õ°Õ¡Õ´Õ¡ÖÕµÕ¡", "Õ¢Õ¸Õ¬Õ¸Ö", "Õ°Õ¸Õ¤Õ¾Õ¡Õ®Õ¶Õ¥ÖÕ¨", "Õ¯Õ¡ÖÕ¸Õ²", "Õ§", "Õ­Õ´Õ¢Õ¡Õ£ÖÕ¥Õ¬", "ÖÕ¡Õ¶Õ¯Õ¡Ö", "Õ´Õ¡ÖÕ¤", "Õ¸Õ¾", "Õ¯Õ¡ÖÕ¸Õ²", "Õ§", "Õ¢Õ¡ÖÕ¥Õ¬", "Õ¾Õ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ«", "Õ¯Õ¡ÕµÖÕ¨" } );
   }
   
   public void testAmharic() throws Exception {
     assertAnalyzesTo(a, "ááªááµá« á¨á£á á¥á ááá á¨á°áá áµá­á­ááá áá» áááá  ááááµ (á¢áá³á­á­ááá²á«) ááá¢ ááááá",
         new String[] { "ááªááµá«", "á¨á£á", "á¥á", "ááá", "á¨á°áá", "áµá­á­ááá", "áá»", "áááá ", "ááááµ", "á¢áá³á­á­ááá²á«", "áá", "ááááá" } );
   }
   
   public void testArabic() throws Exception {
     assertAnalyzesTo(a, "Ø§ÙÙÙÙÙ Ø§ÙÙØ«Ø§Ø¦ÙÙ Ø§ÙØ£ÙÙ Ø¹Ù ÙÙÙÙØ¨ÙØ¯ÙØ§ ÙØ³ÙÙ \"Ø§ÙØ­ÙÙÙØ© Ø¨Ø§ÙØ£Ø±ÙØ§Ù: ÙØµØ© ÙÙÙÙØ¨ÙØ¯ÙØ§\" (Ø¨Ø§ÙØ¥ÙØ¬ÙÙØ²ÙØ©: Truth in Numbers: The Wikipedia Story)Ø Ø³ÙØªÙ Ø¥Ø·ÙØ§ÙÙ ÙÙ 2008.",
         new String[] { "Ø§ÙÙÙÙÙ", "Ø§ÙÙØ«Ø§Ø¦ÙÙ", "Ø§ÙØ£ÙÙ", "Ø¹Ù", "ÙÙÙÙØ¨ÙØ¯ÙØ§", "ÙØ³ÙÙ", "Ø§ÙØ­ÙÙÙØ©", "Ø¨Ø§ÙØ£Ø±ÙØ§Ù", "ÙØµØ©", "ÙÙÙÙØ¨ÙØ¯ÙØ§",
         "Ø¨Ø§ÙØ¥ÙØ¬ÙÙØ²ÙØ©", "truth", "in", "numbers", "the", "wikipedia", "story", "Ø³ÙØªÙ", "Ø¥Ø·ÙØ§ÙÙ", "ÙÙ", "2008" } ); 
   }
   
   public void testAramaic() throws Exception {
     assertAnalyzesTo(a, "ÜÜÜ©ÜÜ¦ÜÜÜ (ÜÜ¢ÜÜ ÜÜ: Wikipedia) ÜÜ ÜÜÜ¢Ü£Ü©Ü ÜÜ¦ÜÜÜ ÜÜÜªÜ¬Ü ÜÜÜ¢ÜÜªÜ¢Ü ÜÜ Ü«Ü¢ÌÜ Ü£ÜÜÜÌÜÜ Ü«Ü¡Ü ÜÜ¬Ü Ü¡Ü¢ Ü¡ÌÜ Ü¬Ü Ü\"ÜÜÜ©Ü\" Ü\"ÜÜÜ¢Ü£Ü©Ü ÜÜ¦ÜÜÜ\"Ü",
         new String[] { "ÜÜÜ©ÜÜ¦ÜÜÜ", "ÜÜ¢ÜÜ ÜÜ", "wikipedia", "ÜÜ", "ÜÜÜ¢Ü£Ü©Ü ÜÜ¦ÜÜÜ", "ÜÜÜªÜ¬Ü", "ÜÜÜ¢ÜÜªÜ¢Ü", "ÜÜ Ü«Ü¢ÌÜ", "Ü£ÜÜÜÌÜ", "Ü«Ü¡Ü",
         "ÜÜ¬Ü", "Ü¡Ü¢", "Ü¡ÌÜ Ü¬Ü", "Ü", "ÜÜÜ©Ü", "Ü", "ÜÜÜ¢Ü£Ü©Ü ÜÜ¦ÜÜÜ"});
   }
   
   public void testBengali() throws Exception {
     assertAnalyzesTo(a, "à¦à¦ à¦¬à¦¿à¦¶à§à¦¬à¦à§à¦· à¦ªà¦°à¦¿à¦à¦¾à¦²à¦¨à¦¾ à¦à¦°à§ à¦à¦à¦à¦¿à¦®à¦¿à¦¡à¦¿à¦¯à¦¼à¦¾ à¦«à¦¾à¦à¦¨à§à¦¡à§à¦¶à¦¨ (à¦à¦à¦à¦¿ à¦à¦²à¦¾à¦­à¦à¦¨à¦ à¦¸à¦à¦¸à§à¦¥à¦¾)à¥¤ à¦à¦à¦à¦¿à¦ªà¦¿à¦¡à¦¿à¦¯à¦¼à¦¾à¦° à¦¶à§à¦°à§ à§§à§« à¦à¦¾à¦¨à§à¦¯à¦¼à¦¾à¦°à¦¿, à§¨à§¦à§¦à§§ à¦¸à¦¾à¦²à§à¥¤ à¦à¦à¦¨ à¦ªà¦°à§à¦¯à¦¨à§à¦¤ à§¨à§¦à§¦à¦à¦¿à¦°à¦ à¦¬à§à¦¶à§ à¦­à¦¾à¦·à¦¾à¦¯à¦¼ à¦à¦à¦à¦¿à¦ªà¦¿à¦¡à¦¿à¦¯à¦¼à¦¾ à¦°à¦¯à¦¼à§à¦à§à¥¤",
         new String[] { "à¦à¦", "à¦¬à¦¿à¦¶à§à¦¬à¦à§à¦·", "à¦ªà¦°à¦¿à¦à¦¾à¦²à¦¨à¦¾", "à¦à¦°à§", "à¦à¦à¦à¦¿à¦®à¦¿à¦¡à¦¿à¦¯à¦¼à¦¾", "à¦«à¦¾à¦à¦¨à§à¦¡à§à¦¶à¦¨", "à¦à¦à¦à¦¿", "à¦à¦²à¦¾à¦­à¦à¦¨à¦", "à¦¸à¦à¦¸à§à¦¥à¦¾", "à¦à¦à¦à¦¿à¦ªà¦¿à¦¡à¦¿à¦¯à¦¼à¦¾à¦°",
         "à¦¶à§à¦°à§", "à§§à§«", "à¦à¦¾à¦¨à§à¦¯à¦¼à¦¾à¦°à¦¿", "à§¨à§¦à§¦à§§", "à¦¸à¦¾à¦²à§", "à¦à¦à¦¨", "à¦ªà¦°à§à¦¯à¦¨à§à¦¤", "à§¨à§¦à§¦à¦à¦¿à¦°à¦", "à¦¬à§à¦¶à§", "à¦­à¦¾à¦·à¦¾à¦¯à¦¼", "à¦à¦à¦à¦¿à¦ªà¦¿à¦¡à¦¿à¦¯à¦¼à¦¾", "à¦°à¦¯à¦¼à§à¦à§" });
   }
   
   public void testFarsi() throws Exception {
     assertAnalyzesTo(a, "ÙÛÚ©Û Ù¾Ø¯ÛØ§Û Ø§ÙÚ¯ÙÛØ³Û Ø¯Ø± ØªØ§Ø±ÛØ® Û²Ûµ Ø¯Û Û±Û³Û·Û¹ Ø¨Ù ØµÙØ±Øª ÙÚ©ÙÙÛ Ø¨Ø±Ø§Û Ø¯Ø§ÙØ´ÙØ§ÙÙÙ ØªØ®ØµØµÛ ÙÙÙ¾Ø¯ÛØ§ ÙÙØ´ØªÙ Ø´Ø¯.",
         new String[] { "ÙÛÚ©Û", "Ù¾Ø¯ÛØ§Û", "Ø§ÙÚ¯ÙÛØ³Û", "Ø¯Ø±", "ØªØ§Ø±ÛØ®", "Û²Ûµ", "Ø¯Û", "Û±Û³Û·Û¹", "Ø¨Ù", "ØµÙØ±Øª", "ÙÚ©ÙÙÛ",
         "Ø¨Ø±Ø§Û", "Ø¯Ø§ÙØ´ÙØ§ÙÙÙ", "ØªØ®ØµØµÛ", "ÙÙÙ¾Ø¯ÛØ§", "ÙÙØ´ØªÙ", "Ø´Ø¯" });
   }
   
   public void testGreek() throws Exception {
     assertAnalyzesTo(a, "ÎÏÎ¬ÏÎµÏÎ±Î¹ ÏÎµ ÏÏÎ½ÎµÏÎ³Î±ÏÎ¯Î± Î±ÏÏ ÎµÎ¸ÎµÎ»Î¿Î½ÏÎ­Ï Î¼Îµ ÏÎ¿ Î»Î¿Î³Î¹ÏÎ¼Î¹ÎºÏ wiki, ÎºÎ¬ÏÎ¹ ÏÎ¿Ï ÏÎ·Î¼Î±Î¯Î½ÎµÎ¹ ÏÏÎ¹ Î¬ÏÎ¸ÏÎ± Î¼ÏÎ¿ÏÎµÎ¯ Î½Î± ÏÏÎ¿ÏÏÎµÎ¸Î¿ÏÎ½ Î® Î½Î± Î±Î»Î»Î¬Î¾Î¿ÏÎ½ Î±ÏÏ ÏÎ¿Î½ ÎºÎ±Î¸Î­Î½Î±.",
         new String[] { "Î³ÏÎ¬ÏÎµÏÎ±Î¹", "ÏÎµ", "ÏÏÎ½ÎµÏÎ³Î±ÏÎ¯Î±", "Î±ÏÏ", "ÎµÎ¸ÎµÎ»Î¿Î½ÏÎ­Ï", "Î¼Îµ", "ÏÎ¿", "Î»Î¿Î³Î¹ÏÎ¼Î¹ÎºÏ", "wiki", "ÎºÎ¬ÏÎ¹", "ÏÎ¿Ï",
         "ÏÎ·Î¼Î±Î¯Î½ÎµÎ¹", "ÏÏÎ¹", "Î¬ÏÎ¸ÏÎ±", "Î¼ÏÎ¿ÏÎµÎ¯", "Î½Î±", "ÏÏÎ¿ÏÏÎµÎ¸Î¿ÏÎ½", "Î®", "Î½Î±", "Î±Î»Î»Î¬Î¾Î¿ÏÎ½", "Î±ÏÏ", "ÏÎ¿Î½", "ÎºÎ±Î¸Î­Î½Î±" });
   }
   
   public void testLao() throws Exception {
     assertAnalyzesTo(a, "àºàº§à»àº²àºàº­àº", new String[] { "àºàº§à»àº²", "àºàº­àº" });
   }
   
   public void testThai() throws Exception {
     assertAnalyzesTo(a, "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ. à¹à¸¥à¹à¸§à¹à¸à¸­à¸à¸°à¹à¸à¹à¸«à¸? à¹à¹à¹à¹",
         new String[] { "à¸à¸²à¸£", "à¸à¸µà¹", "à¹à¸à¹", "à¸à¹à¸­à¸", "à¹à¸ªà¸à¸", "à¸§à¹à¸²", "à¸à¸²à¸", "à¸à¸µ", "à¹à¸¥à¹à¸§", "à¹à¸à¸­", "à¸à¸°", "à¹à¸", "à¹à¸«à¸", "à¹à¹à¹à¹"});
   }
   
   public void testTibetan() throws Exception {
     assertAnalyzesTo(a, "à½¦à¾£à½¼à½à¼à½à½à½¼à½à¼à½à½à¼à½£à½¦à¼à½ à½à½²à½¦à¼à½à½¼à½à¼à½¡à½²à½à¼à½à½²à¼à½à½à½¦à¼à½à½¼à½à¼à½ à½à½ºà½£à¼à½à½´à¼à½à½à½¼à½à¼à½à½¢à¼à½§à¼à½à½à¼à½à½à½ºà¼à½à½à½à¼à½à½à½²à½¦à¼à½¦à½¼à¼ à¼",
         new String[] { "à½¦à¾£à½¼à½", "à½à½à½¼à½", "à½à½", "à½£à½¦", "à½ à½à½²à½¦", "à½à½¼à½", "à½¡à½²à½", "à½à½²", "à½à½à½¦", "à½à½¼à½", "à½ à½à½ºà½£", "à½à½´", "à½à½à½¼à½", "à½à½¢", "à½§", "à½à½", "à½à½à½º", "à½à½à½", "à½à½à½²à½¦", "à½¦à½¼" });
   }
   
   /*
    * For chinese, tokenize as char (these can later form bigrams or whatever)
    */
   public void testChinese() throws Exception {
     assertAnalyzesTo(a, "ææ¯ä¸­å½äººã ï¼ï¼ï¼ï¼ ï¼´ï½ï½ï½ï½ ",
         new String[] { "æ", "æ¯", "ä¸­", "å½", "äºº", "1234", "tests"});
   }
   
   public void testEmpty() throws Exception {
     assertAnalyzesTo(a, "", new String[] {});
     assertAnalyzesTo(a, ".", new String[] {});
     assertAnalyzesTo(a, " ", new String[] {});
   }
   
   /* test various jira issues this analyzer is related to */
   
   public void testLUCENE1545() throws Exception {
     /*
      * Standard analyzer does not correctly tokenize combining character U+0364 COMBINING LATIN SMALL LETTRE E.
      * The word "moÍ¤chte" is incorrectly tokenized into "mo" "chte", the combining character is lost.
      * Expected result is only on token "moÍ¤chte".
      */
     assertAnalyzesTo(a, "moÍ¤chte", new String[] { "moÍ¤chte" }); 
   }
   
   /* Tests from StandardAnalyzer, just to show behavior is similar */
   public void testAlphanumericSA() throws Exception {
     // alphanumeric tokens
     assertAnalyzesTo(a, "B2B", new String[]{"b2b"});
     assertAnalyzesTo(a, "2B", new String[]{"2b"});
   }
 
   public void testDelimitersSA() throws Exception {
     // other delimiters: "-", "/", ","
     assertAnalyzesTo(a, "some-dashed-phrase", new String[]{"some", "dashed", "phrase"});
     assertAnalyzesTo(a, "dogs,chase,cats", new String[]{"dogs", "chase", "cats"});
     assertAnalyzesTo(a, "ac/dc", new String[]{"ac", "dc"});
   }
 
   public void testApostrophesSA() throws Exception {
     // internal apostrophes: O'Reilly, you're, O'Reilly's
     assertAnalyzesTo(a, "O'Reilly", new String[]{"o'reilly"});
     assertAnalyzesTo(a, "you're", new String[]{"you're"});
     assertAnalyzesTo(a, "she's", new String[]{"she's"});
     assertAnalyzesTo(a, "Jim's", new String[]{"jim's"});
     assertAnalyzesTo(a, "don't", new String[]{"don't"});
     assertAnalyzesTo(a, "O'Reilly's", new String[]{"o'reilly's"});
   }
 
   public void testNumericSA() throws Exception {
     // floating point, serial, model numbers, ip addresses, etc.
     // every other segment must have at least one digit
     assertAnalyzesTo(a, "21.35", new String[]{"21.35"});
     assertAnalyzesTo(a, "R2D2 C3PO", new String[]{"r2d2", "c3po"});
     assertAnalyzesTo(a, "216.239.63.104", new String[]{"216.239.63.104"});
     assertAnalyzesTo(a, "216.239.63.104", new String[]{"216.239.63.104"});
   }
 
   public void testTextWithNumbersSA() throws Exception {
     // numbers
     assertAnalyzesTo(a, "David has 5000 bones", new String[]{"david", "has", "5000", "bones"});
   }
 
   public void testVariousTextSA() throws Exception {
     // various
     assertAnalyzesTo(a, "C embedded developers wanted", new String[]{"c", "embedded", "developers", "wanted"});
     assertAnalyzesTo(a, "foo bar FOO BAR", new String[]{"foo", "bar", "foo", "bar"});
     assertAnalyzesTo(a, "foo      bar .  FOO <> BAR", new String[]{"foo", "bar", "foo", "bar"});
     assertAnalyzesTo(a, "\"QUOTED\" word", new String[]{"quoted", "word"});
   }
 
   public void testKoreanSA() throws Exception {
     // Korean words
     assertAnalyzesTo(a, "ìëíì¸ì íê¸ìëë¤", new String[]{"ìëíì¸ì", "íê¸ìëë¤"});
   }
   
   public void testReusableTokenStream() throws Exception {
     assertAnalyzesTo(a, "à½¦à¾£à½¼à½à¼à½à½à½¼à½à¼à½à½à¼à½£à½¦à¼à½ à½à½²à½¦à¼à½à½¼à½à¼à½¡à½²à½à¼à½à½²à¼à½à½à½¦à¼à½à½¼à½à¼à½ à½à½ºà½£à¼à½à½´à¼à½à½à½¼à½à¼à½à½¢à¼à½§à¼à½à½à¼à½à½à½ºà¼à½à½à½à¼à½à½à½²à½¦à¼à½¦à½¼à¼ à¼",
         new String[] { "à½¦à¾£à½¼à½", "à½à½à½¼à½", "à½à½", "à½£à½¦", "à½ à½à½²à½¦", "à½à½¼à½", "à½¡à½²à½", "à½à½²", "à½à½à½¦", "à½à½¼à½", 
                       "à½ à½à½ºà½£", "à½à½´", "à½à½à½¼à½", "à½à½¢", "à½§", "à½à½", "à½à½à½º", "à½à½à½", "à½à½à½²à½¦", "à½¦à½¼" });
   }
   
   public void testOffsets() throws Exception {
     assertAnalyzesTo(a, "David has 5000 bones", 
         new String[] {"david", "has", "5000", "bones"},
         new int[] {0, 6, 10, 15},
         new int[] {5, 9, 14, 20});
   }
   
   public void testTypes() throws Exception {
     assertAnalyzesTo(a, "David has 5000 bones", 
         new String[] {"david", "has", "5000", "bones"},
         new String[] { "<ALPHANUM>", "<ALPHANUM>", "<NUM>", "<ALPHANUM>" });
   }
   
   public void testKorean() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "íë¯¼ì ì",
         new String[] { "íë¯¼ì ì" },
         new String[] { "<HANGUL>" });
   }
   
   public void testJapanese() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "ä»®åé£ã ã«ã¿ã«ã",
         new String[] { "ä»®", "å", "é£", "ã", "ã«ã¿ã«ã" },
         new String[] { "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<HIRAGANA>", "<KATAKANA>" });
   }
   
   /** blast some random strings through the analyzer */
   public void testRandomStrings() throws Exception {
     checkRandomData(random(), a, 1000*RANDOM_MULTIPLIER);
   }
   
   /** blast some random large strings through the analyzer */
   public void testRandomHugeStrings() throws Exception {
     Random random = random();
     checkRandomData(random, a, 100*RANDOM_MULTIPLIER, 8192);
   }
   
   public void testTokenAttributes() throws Exception {
     TokenStream ts = a.tokenStream("dummy", "This is a test");
    try {
     ScriptAttribute scriptAtt = ts.addAttribute(ScriptAttribute.class);
     ts.reset();
     while (ts.incrementToken()) {
       assertEquals(UScript.LATIN, scriptAtt.getCode());
       assertEquals(UScript.getName(UScript.LATIN), scriptAtt.getName());
       assertEquals(UScript.getShortName(UScript.LATIN), scriptAtt.getShortName());
       assertTrue(ts.reflectAsString(false).contains("script=Latin"));
     }
     ts.end();
    } finally {
      IOUtils.closeWhileHandlingException(ts);
    }
   }
 }
