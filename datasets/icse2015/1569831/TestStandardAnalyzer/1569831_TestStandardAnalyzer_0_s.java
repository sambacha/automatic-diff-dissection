 package org.apache.lucene.analysis.core;
 
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
 import java.io.StringReader;
 import java.util.Arrays;
 import java.util.Random;
 
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.MockGraphTokenFilter;
 import org.apache.lucene.analysis.TokenStream;
 import org.apache.lucene.analysis.Tokenizer;
 import org.apache.lucene.analysis.standard.StandardAnalyzer;
 import org.apache.lucene.analysis.standard.StandardTokenizer;
 import org.apache.lucene.util.Version;
 
 public class TestStandardAnalyzer extends BaseTokenStreamTestCase {
   
   public void testHugeDoc() throws IOException {
     StringBuilder sb = new StringBuilder();
     char whitespace[] = new char[4094];
     Arrays.fill(whitespace, ' ');
     sb.append(whitespace);
     sb.append("testing 1234");
     String input = sb.toString();
     StandardTokenizer tokenizer = new StandardTokenizer(TEST_VERSION_CURRENT);
     tokenizer.setReader(new StringReader(input));
     BaseTokenStreamTestCase.assertTokenStreamContents(tokenizer, new String[] { "testing", "1234" });
   }
 
   private Analyzer a = new Analyzer() {
     @Override
     protected TokenStreamComponents createComponents(String fieldName) {
 
       Tokenizer tokenizer = new StandardTokenizer(TEST_VERSION_CURRENT);
       return new TokenStreamComponents(tokenizer);
     }
   };
 
   public void testArmenian() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "ÕÕ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ« 13 Õ´Õ«Õ¬Õ«Õ¸Õ¶ Õ°Õ¸Õ¤Õ¾Õ¡Õ®Õ¶Õ¥ÖÕ¨ (4,600` Õ°Õ¡ÕµÕ¥ÖÕ¥Õ¶ Õ¾Õ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ¸ÖÕ´) Õ£ÖÕ¾Õ¥Õ¬ Õ¥Õ¶ Õ¯Õ¡Õ´Õ¡Õ¾Õ¸ÖÕ¶Õ¥ÖÕ« Õ¯Õ¸Õ²Õ´Õ«Ö Õ¸Ö Õ°Õ¡Õ´Õ¡ÖÕµÕ¡ Õ¢Õ¸Õ¬Õ¸Ö Õ°Õ¸Õ¤Õ¾Õ¡Õ®Õ¶Õ¥ÖÕ¨ Õ¯Õ¡ÖÕ¸Õ² Õ§ Õ­Õ´Õ¢Õ¡Õ£ÖÕ¥Õ¬ ÖÕ¡Õ¶Õ¯Õ¡Ö Õ´Õ¡ÖÕ¤ Õ¸Õ¾ Õ¯Õ¡ÖÕ¸Õ² Õ§ Õ¢Õ¡ÖÕ¥Õ¬ ÕÕ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ« Õ¯Õ¡ÕµÖÕ¨Ö",
         new String[] { "ÕÕ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ«", "13", "Õ´Õ«Õ¬Õ«Õ¸Õ¶", "Õ°Õ¸Õ¤Õ¾Õ¡Õ®Õ¶Õ¥ÖÕ¨", "4,600", "Õ°Õ¡ÕµÕ¥ÖÕ¥Õ¶", "Õ¾Õ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ¸ÖÕ´", "Õ£ÖÕ¾Õ¥Õ¬", "Õ¥Õ¶", "Õ¯Õ¡Õ´Õ¡Õ¾Õ¸ÖÕ¶Õ¥ÖÕ«", "Õ¯Õ¸Õ²Õ´Õ«Ö", 
         "Õ¸Ö", "Õ°Õ¡Õ´Õ¡ÖÕµÕ¡", "Õ¢Õ¸Õ¬Õ¸Ö", "Õ°Õ¸Õ¤Õ¾Õ¡Õ®Õ¶Õ¥ÖÕ¨", "Õ¯Õ¡ÖÕ¸Õ²", "Õ§", "Õ­Õ´Õ¢Õ¡Õ£ÖÕ¥Õ¬", "ÖÕ¡Õ¶Õ¯Õ¡Ö", "Õ´Õ¡ÖÕ¤", "Õ¸Õ¾", "Õ¯Õ¡ÖÕ¸Õ²", "Õ§", "Õ¢Õ¡ÖÕ¥Õ¬", "ÕÕ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ«", "Õ¯Õ¡ÕµÖÕ¨" } );
   }
   
   public void testAmharic() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "ááªááµá« á¨á£á á¥á ááá á¨á°áá áµá­á­ááá áá» áááá  ááááµ (á¢áá³á­á­ááá²á«) ááá¢ ááááá",
         new String[] { "ááªááµá«", "á¨á£á", "á¥á", "ááá", "á¨á°áá", "áµá­á­ááá", "áá»", "áááá ", "ááááµ", "á¢áá³á­á­ááá²á«", "áá", "ááááá" } );
   }
   
   public void testArabic() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "Ø§ÙÙÙÙÙ Ø§ÙÙØ«Ø§Ø¦ÙÙ Ø§ÙØ£ÙÙ Ø¹Ù ÙÙÙÙØ¨ÙØ¯ÙØ§ ÙØ³ÙÙ \"Ø§ÙØ­ÙÙÙØ© Ø¨Ø§ÙØ£Ø±ÙØ§Ù: ÙØµØ© ÙÙÙÙØ¨ÙØ¯ÙØ§\" (Ø¨Ø§ÙØ¥ÙØ¬ÙÙØ²ÙØ©: Truth in Numbers: The Wikipedia Story)Ø Ø³ÙØªÙ Ø¥Ø·ÙØ§ÙÙ ÙÙ 2008.",
         new String[] { "Ø§ÙÙÙÙÙ", "Ø§ÙÙØ«Ø§Ø¦ÙÙ", "Ø§ÙØ£ÙÙ", "Ø¹Ù", "ÙÙÙÙØ¨ÙØ¯ÙØ§", "ÙØ³ÙÙ", "Ø§ÙØ­ÙÙÙØ©", "Ø¨Ø§ÙØ£Ø±ÙØ§Ù", "ÙØµØ©", "ÙÙÙÙØ¨ÙØ¯ÙØ§",
         "Ø¨Ø§ÙØ¥ÙØ¬ÙÙØ²ÙØ©", "Truth", "in", "Numbers", "The", "Wikipedia", "Story", "Ø³ÙØªÙ", "Ø¥Ø·ÙØ§ÙÙ", "ÙÙ", "2008" } ); 
   }
   
   public void testAramaic() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "ÜÜÜ©ÜÜ¦ÜÜÜ (ÜÜ¢ÜÜ ÜÜ: Wikipedia) ÜÜ ÜÜÜ¢Ü£Ü©Ü ÜÜ¦ÜÜÜ ÜÜÜªÜ¬Ü ÜÜÜ¢ÜÜªÜ¢Ü ÜÜ Ü«Ü¢ÌÜ Ü£ÜÜÜÌÜÜ Ü«Ü¡Ü ÜÜ¬Ü Ü¡Ü¢ Ü¡ÌÜ Ü¬Ü Ü\"ÜÜÜ©Ü\" Ü\"ÜÜÜ¢Ü£Ü©Ü ÜÜ¦ÜÜÜ\"Ü",
         new String[] { "ÜÜÜ©ÜÜ¦ÜÜÜ", "ÜÜ¢ÜÜ ÜÜ", "Wikipedia", "ÜÜ", "ÜÜÜ¢Ü£Ü©Ü ÜÜ¦ÜÜÜ", "ÜÜÜªÜ¬Ü", "ÜÜÜ¢ÜÜªÜ¢Ü", "ÜÜ Ü«Ü¢ÌÜ", "Ü£ÜÜÜÌÜ", "Ü«Ü¡Ü",
         "ÜÜ¬Ü", "Ü¡Ü¢", "Ü¡ÌÜ Ü¬Ü", "Ü", "ÜÜÜ©Ü", "Ü", "ÜÜÜ¢Ü£Ü©Ü ÜÜ¦ÜÜÜ"});
   }
   
   public void testBengali() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "à¦à¦ à¦¬à¦¿à¦¶à§à¦¬à¦à§à¦· à¦ªà¦°à¦¿à¦à¦¾à¦²à¦¨à¦¾ à¦à¦°à§ à¦à¦à¦à¦¿à¦®à¦¿à¦¡à¦¿à¦¯à¦¼à¦¾ à¦«à¦¾à¦à¦¨à§à¦¡à§à¦¶à¦¨ (à¦à¦à¦à¦¿ à¦à¦²à¦¾à¦­à¦à¦¨à¦ à¦¸à¦à¦¸à§à¦¥à¦¾)à¥¤ à¦à¦à¦à¦¿à¦ªà¦¿à¦¡à¦¿à¦¯à¦¼à¦¾à¦° à¦¶à§à¦°à§ à§§à§« à¦à¦¾à¦¨à§à¦¯à¦¼à¦¾à¦°à¦¿, à§¨à§¦à§¦à§§ à¦¸à¦¾à¦²à§à¥¤ à¦à¦à¦¨ à¦ªà¦°à§à¦¯à¦¨à§à¦¤ à§¨à§¦à§¦à¦à¦¿à¦°à¦ à¦¬à§à¦¶à§ à¦­à¦¾à¦·à¦¾à¦¯à¦¼ à¦à¦à¦à¦¿à¦ªà¦¿à¦¡à¦¿à¦¯à¦¼à¦¾ à¦°à¦¯à¦¼à§à¦à§à¥¤",
         new String[] { "à¦à¦", "à¦¬à¦¿à¦¶à§à¦¬à¦à§à¦·", "à¦ªà¦°à¦¿à¦à¦¾à¦²à¦¨à¦¾", "à¦à¦°à§", "à¦à¦à¦à¦¿à¦®à¦¿à¦¡à¦¿à¦¯à¦¼à¦¾", "à¦«à¦¾à¦à¦¨à§à¦¡à§à¦¶à¦¨", "à¦à¦à¦à¦¿", "à¦à¦²à¦¾à¦­à¦à¦¨à¦", "à¦¸à¦à¦¸à§à¦¥à¦¾", "à¦à¦à¦à¦¿à¦ªà¦¿à¦¡à¦¿à¦¯à¦¼à¦¾à¦°",
         "à¦¶à§à¦°à§", "à§§à§«", "à¦à¦¾à¦¨à§à¦¯à¦¼à¦¾à¦°à¦¿", "à§¨à§¦à§¦à§§", "à¦¸à¦¾à¦²à§", "à¦à¦à¦¨", "à¦ªà¦°à§à¦¯à¦¨à§à¦¤", "à§¨à§¦à§¦à¦à¦¿à¦°à¦", "à¦¬à§à¦¶à§", "à¦­à¦¾à¦·à¦¾à¦¯à¦¼", "à¦à¦à¦à¦¿à¦ªà¦¿à¦¡à¦¿à¦¯à¦¼à¦¾", "à¦°à¦¯à¦¼à§à¦à§" });
   }
   
   public void testFarsi() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "ÙÛÚ©Û Ù¾Ø¯ÛØ§Û Ø§ÙÚ¯ÙÛØ³Û Ø¯Ø± ØªØ§Ø±ÛØ® Û²Ûµ Ø¯Û Û±Û³Û·Û¹ Ø¨Ù ØµÙØ±Øª ÙÚ©ÙÙÛ Ø¨Ø±Ø§Û Ø¯Ø§ÙØ´ÙØ§ÙÙÙ ØªØ®ØµØµÛ ÙÙÙ¾Ø¯ÛØ§ ÙÙØ´ØªÙ Ø´Ø¯.",
         new String[] { "ÙÛÚ©Û", "Ù¾Ø¯ÛØ§Û", "Ø§ÙÚ¯ÙÛØ³Û", "Ø¯Ø±", "ØªØ§Ø±ÛØ®", "Û²Ûµ", "Ø¯Û", "Û±Û³Û·Û¹", "Ø¨Ù", "ØµÙØ±Øª", "ÙÚ©ÙÙÛ",
         "Ø¨Ø±Ø§Û", "Ø¯Ø§ÙØ´ÙØ§ÙÙÙ", "ØªØ®ØµØµÛ", "ÙÙÙ¾Ø¯ÛØ§", "ÙÙØ´ØªÙ", "Ø´Ø¯" });
   }
   
   public void testGreek() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "ÎÏÎ¬ÏÎµÏÎ±Î¹ ÏÎµ ÏÏÎ½ÎµÏÎ³Î±ÏÎ¯Î± Î±ÏÏ ÎµÎ¸ÎµÎ»Î¿Î½ÏÎ­Ï Î¼Îµ ÏÎ¿ Î»Î¿Î³Î¹ÏÎ¼Î¹ÎºÏ wiki, ÎºÎ¬ÏÎ¹ ÏÎ¿Ï ÏÎ·Î¼Î±Î¯Î½ÎµÎ¹ ÏÏÎ¹ Î¬ÏÎ¸ÏÎ± Î¼ÏÎ¿ÏÎµÎ¯ Î½Î± ÏÏÎ¿ÏÏÎµÎ¸Î¿ÏÎ½ Î® Î½Î± Î±Î»Î»Î¬Î¾Î¿ÏÎ½ Î±ÏÏ ÏÎ¿Î½ ÎºÎ±Î¸Î­Î½Î±.",
         new String[] { "ÎÏÎ¬ÏÎµÏÎ±Î¹", "ÏÎµ", "ÏÏÎ½ÎµÏÎ³Î±ÏÎ¯Î±", "Î±ÏÏ", "ÎµÎ¸ÎµÎ»Î¿Î½ÏÎ­Ï", "Î¼Îµ", "ÏÎ¿", "Î»Î¿Î³Î¹ÏÎ¼Î¹ÎºÏ", "wiki", "ÎºÎ¬ÏÎ¹", "ÏÎ¿Ï",
         "ÏÎ·Î¼Î±Î¯Î½ÎµÎ¹", "ÏÏÎ¹", "Î¬ÏÎ¸ÏÎ±", "Î¼ÏÎ¿ÏÎµÎ¯", "Î½Î±", "ÏÏÎ¿ÏÏÎµÎ¸Î¿ÏÎ½", "Î®", "Î½Î±", "Î±Î»Î»Î¬Î¾Î¿ÏÎ½", "Î±ÏÏ", "ÏÎ¿Î½", "ÎºÎ±Î¸Î­Î½Î±" });
   }
 
   public void testThai() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ. à¹à¸¥à¹à¸§à¹à¸à¸­à¸à¸°à¹à¸à¹à¸«à¸? à¹à¹à¹à¹",
         new String[] { "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ", "à¹à¸¥à¹à¸§à¹à¸à¸­à¸à¸°à¹à¸à¹à¸«à¸", "à¹à¹à¹à¹" });
   }
   
   public void testLao() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "àºªàº²àºàº²àº¥àº°àºàº°àº¥àº±àº àºàº°àºàº²àºàº´àºàº°à»àº àºàº°àºàº²àºàº»àºàº¥àº²àº§", 
         new String[] { "àºªàº²àºàº²àº¥àº°àºàº°àº¥àº±àº", "àºàº°àºàº²àºàº´àºàº°à»àº", "àºàº°àºàº²àºàº»àºàº¥àº²àº§" });
   }
   
   public void testTibetan() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "à½¦à¾£à½¼à½à¼à½à½à½¼à½à¼à½à½à¼à½£à½¦à¼à½ à½à½²à½¦à¼à½à½¼à½à¼à½¡à½²à½à¼à½à½²à¼à½à½à½¦à¼à½à½¼à½à¼à½ à½à½ºà½£à¼à½à½´à¼à½à½à½¼à½à¼à½à½¢à¼à½§à¼à½à½à¼à½à½à½ºà¼à½à½à½à¼à½à½à½²à½¦à¼à½¦à½¼à¼ à¼",
                      new String[] { "à½¦à¾£à½¼à½", "à½à½à½¼à½", "à½à½", "à½£à½¦", "à½ à½à½²à½¦", "à½à½¼à½", "à½¡à½²à½", 
                                     "à½à½²", "à½à½à½¦", "à½à½¼à½", "à½ à½à½ºà½£", "à½à½´", "à½à½à½¼à½", "à½à½¢", 
                                     "à½§", "à½à½", "à½à½à½º", "à½à½à½", "à½à½à½²à½¦", "à½¦à½¼" });
   }
   
   /*
    * For chinese, tokenize as char (these can later form bigrams or whatever)
    */
   public void testChinese() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "ææ¯ä¸­å½äººã ï¼ï¼ï¼ï¼ ï¼´ï½ï½ï½ï½ ",
         new String[] { "æ", "æ¯", "ä¸­", "å½", "äºº", "ï¼ï¼ï¼ï¼", "ï¼´ï½ï½ï½ï½"});
   }
   
   public void testEmpty() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "", new String[] {});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, ".", new String[] {});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, " ", new String[] {});
   }
   
   /* test various jira issues this analyzer is related to */
   
   public void testLUCENE1545() throws Exception {
     /*
      * Standard analyzer does not correctly tokenize combining character U+0364 COMBINING LATIN SMALL LETTRE E.
      * The word "moÍ¤chte" is incorrectly tokenized into "mo" "chte", the combining character is lost.
      * Expected result is only on token "moÍ¤chte".
      */
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "moÍ¤chte", new String[] { "moÍ¤chte" }); 
   }
   
   /* Tests from StandardAnalyzer, just to show behavior is similar */
   public void testAlphanumericSA() throws Exception {
     // alphanumeric tokens
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "B2B", new String[]{"B2B"});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "2B", new String[]{"2B"});
   }
 
   public void testDelimitersSA() throws Exception {
     // other delimiters: "-", "/", ","
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "some-dashed-phrase", new String[]{"some", "dashed", "phrase"});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "dogs,chase,cats", new String[]{"dogs", "chase", "cats"});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "ac/dc", new String[]{"ac", "dc"});
   }
 
   public void testApostrophesSA() throws Exception {
     // internal apostrophes: O'Reilly, you're, O'Reilly's
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "O'Reilly", new String[]{"O'Reilly"});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "you're", new String[]{"you're"});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "she's", new String[]{"she's"});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "Jim's", new String[]{"Jim's"});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "don't", new String[]{"don't"});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "O'Reilly's", new String[]{"O'Reilly's"});
   }
 
   public void testNumericSA() throws Exception {
     // floating point, serial, model numbers, ip addresses, etc.
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "21.35", new String[]{"21.35"});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "R2D2 C3PO", new String[]{"R2D2", "C3PO"});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "216.239.63.104", new String[]{"216.239.63.104"});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "216.239.63.104", new String[]{"216.239.63.104"});
   }
 
   public void testTextWithNumbersSA() throws Exception {
     // numbers
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "David has 5000 bones", new String[]{"David", "has", "5000", "bones"});
   }
 
   public void testVariousTextSA() throws Exception {
     // various
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "C embedded developers wanted", new String[]{"C", "embedded", "developers", "wanted"});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "foo bar FOO BAR", new String[]{"foo", "bar", "FOO", "BAR"});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "foo      bar .  FOO <> BAR", new String[]{"foo", "bar", "FOO", "BAR"});
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "\"QUOTED\" word", new String[]{"QUOTED", "word"});
   }
 
   public void testKoreanSA() throws Exception {
     // Korean words
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "ìëíì¸ì íê¸ìëë¤", new String[]{"ìëíì¸ì", "íê¸ìëë¤"});
   }
   
   public void testOffsets() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "David has 5000 bones", 
         new String[] {"David", "has", "5000", "bones"},
         new int[] {0, 6, 10, 15},
         new int[] {5, 9, 14, 20});
   }
   
   public void testTypes() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "David has 5000 bones", 
         new String[] {"David", "has", "5000", "bones"},
         new String[] { "<ALPHANUM>", "<ALPHANUM>", "<NUM>", "<ALPHANUM>" });
   }
   
   public void testUnicodeWordBreaks() throws Exception {
     WordBreakTestUnicode_6_3_0 wordBreakTest = new WordBreakTestUnicode_6_3_0();
     wordBreakTest.test(a);
   }
   
   public void testSupplementary() throws Exception {
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "ð©¬è±éä¹æ¯ç", 
         new String[] {"ð©¬", "è±", "é", "ä¹", "æ¯", "ç"},
         new String[] { "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>", "<IDEOGRAPHIC>" });
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
   
   public void testCombiningMarks() throws Exception {
     checkOneTerm(a, "ãã", "ãã"); // hiragana
     checkOneTerm(a, "ãµã", "ãµã"); // katakana
     checkOneTerm(a, "å£¹ã", "å£¹ã"); // ideographic
     checkOneTerm(a, "ìã",  "ìã"); // hangul
   }
 
   /**
    * Multiple consecutive chars in \p{WB:MidLetter}, \p{WB:MidNumLet},
    * and/or \p{MidNum} should trigger a token split.
    */
   public void testMid() throws Exception {
     // ':' is in \p{WB:MidLetter}, which should trigger a split unless there is a Letter char on both sides
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "A:B", new String[] { "A:B" });
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "A::B", new String[] { "A", "B" });
 
     // '.' is in \p{WB:MidNumLet}, which should trigger a split unless there is a Letter or Numeric char on both sides
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "1.2", new String[] { "1.2" });
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "A.B", new String[] { "A.B" });
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "1..2", new String[] { "1", "2" });
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "A..B", new String[] { "A", "B" });
 
     // ',' is in \p{WB:MidNum}, which should trigger a split unless there is a Numeric char on both sides
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "1,2", new String[] { "1,2" });
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "1,,2", new String[] { "1", "2" });
 
     // Mixed consecutive \p{WB:MidLetter} and \p{WB:MidNumLet} should trigger a split
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "A.:B", new String[] { "A", "B" });
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "A:.B", new String[] { "A", "B" });
 
     // Mixed consecutive \p{WB:MidNum} and \p{WB:MidNumLet} should trigger a split
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "1,.2", new String[] { "1", "2" });
     BaseTokenStreamTestCase.assertAnalyzesTo(a, "1.,2", new String[] { "1", "2" });
   }
 
 
 
   /** blast some random strings through the analyzer */
   public void testRandomStrings() throws Exception {
     checkRandomData(random(), new StandardAnalyzer(TEST_VERSION_CURRENT), 1000*RANDOM_MULTIPLIER);
   }
   
   /** blast some random large strings through the analyzer */
   public void testRandomHugeStrings() throws Exception {
     Random random = random();
     checkRandomData(random, new StandardAnalyzer(TEST_VERSION_CURRENT), 100*RANDOM_MULTIPLIER, 8192);
   }
 
   // Adds random graph after:
   public void testRandomHugeStringsGraphAfter() throws Exception {
     Random random = random();
     checkRandomData(random,
                     new Analyzer() {
                       @Override
                       protected TokenStreamComponents createComponents(String fieldName) {
                         Tokenizer tokenizer = new StandardTokenizer(TEST_VERSION_CURRENT);
                         TokenStream tokenStream = new MockGraphTokenFilter(random(), tokenizer);
                         return new TokenStreamComponents(tokenizer, tokenStream);
                       }
                     },
                     100*RANDOM_MULTIPLIER, 8192);
   }
 }
