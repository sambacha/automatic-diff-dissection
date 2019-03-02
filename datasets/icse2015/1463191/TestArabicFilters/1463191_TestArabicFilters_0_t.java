 package org.apache.lucene.analysis.ar;
 
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
 
 import java.io.Reader;
 import java.io.StringReader;
 
 import org.apache.lucene.analysis.TokenStream;
 import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.BaseTokenStreamFactoryTestCase;
 
 /**
  * Simple tests to ensure the Arabic filter Factories are working.
  */
public class TestArabicFilters extends BaseTokenStreamFactoryTestCase {
   
   /**
    * Test ArabicNormalizationFilterFactory
    */
   public void testNormalizer() throws Exception {
     Reader reader = new StringReader("Ø§ÙØ°ÙÙ ÙÙÙÙØª Ø£ÙÙØ§ÙÙÙ");
    Tokenizer tokenizer = tokenizerFactory("Standard").create(reader);
    TokenStream stream = tokenFilterFactory("ArabicNormalization").create(tokenizer);
     assertTokenStreamContents(stream, new String[] {"Ø§ÙØ°ÙÙ", "ÙÙÙØª", "Ø§ÙÙØ§ÙÙÙ"});
   }
   
   /**
    * Test ArabicStemFilterFactory
    */
   public void testStemmer() throws Exception {
     Reader reader = new StringReader("Ø§ÙØ°ÙÙ ÙÙÙÙØª Ø£ÙÙØ§ÙÙÙ");
    Tokenizer tokenizer = tokenizerFactory("Standard").create(reader);
    TokenStream stream = tokenFilterFactory("ArabicNormalization").create(tokenizer);
    stream = tokenFilterFactory("ArabicStem").create(stream);
     assertTokenStreamContents(stream, new String[] {"Ø°ÙÙ", "ÙÙÙØª", "Ø§ÙÙØ§ÙÙÙ"});
   }
   
   /**
    * Test PersianCharFilterFactory
    */
   public void testPersianCharFilter() throws Exception {
    Reader reader = charFilterFactory("Persian").create(new StringReader("ÙÛâØ®ÙØ±Ø¯"));
    Tokenizer tokenizer = tokenizerFactory("Standard").create(reader);
    assertTokenStreamContents(tokenizer, new String[] { "ÙÛ", "Ø®ÙØ±Ø¯" });
  }
  
  /** Test that bogus arguments result in exception */
  public void testBogusArguments() throws Exception {
    try {
      tokenFilterFactory("ArabicNormalization", "bogusArg", "bogusValue");
      fail();
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("Unknown parameters"));
    }
    
    try {
      tokenFilterFactory("Arabicstem", "bogusArg", "bogusValue");
      fail();
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("Unknown parameters"));
    }
    
    try {
      charFilterFactory("Persian", "bogusArg", "bogusValue");
      fail();
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("Unknown parameters"));
    }
   }
 }
