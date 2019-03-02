 package org.apache.lucene.analysis.ja;
 
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
 import java.io.StringReader;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.TokenStream;
 
 /**
  * Simple tests for {@link JapaneseTokenizerFactory}
  */
 public class TestJapaneseTokenizerFactory extends BaseTokenStreamTestCase {
   public void testSimple() throws IOException {
    JapaneseTokenizerFactory factory = new JapaneseTokenizerFactory(new HashMap<String,String>());
     factory.inform(new StringMockResourceLoader(""));
     TokenStream ts = factory.create(new StringReader("ããã¯æ¬ã§ã¯ãªã"));
     assertTokenStreamContents(ts,
         new String[] { "ãã", "ã¯", "æ¬", "ã§", "ã¯", "ãªã" },
         new int[] { 0, 2, 3, 4, 5, 6 },
         new int[] { 2, 3, 4, 5, 6, 8 }
     );
   }
   
   /**
    * Test that search mode is enabled and working by default
    */
   public void testDefaults() throws IOException {
    JapaneseTokenizerFactory factory = new JapaneseTokenizerFactory(new HashMap<String,String>());
     factory.inform(new StringMockResourceLoader(""));
     TokenStream ts = factory.create(new StringReader("ã·ãã¢ã½ããã¦ã§ã¢ã¨ã³ã¸ãã¢"));
     assertTokenStreamContents(ts,
                               new String[] { "ã·ãã¢", "ã·ãã¢ã½ããã¦ã§ã¢ã¨ã³ã¸ãã¢", "ã½ããã¦ã§ã¢", "ã¨ã³ã¸ãã¢" }
     );
   }
   
   /**
    * Test mode parameter: specifying normal mode
    */
   public void testMode() throws IOException {
     Map<String,String> args = new HashMap<String,String>();
     args.put("mode", "normal");
    JapaneseTokenizerFactory factory = new JapaneseTokenizerFactory(args);
     factory.inform(new StringMockResourceLoader(""));
     TokenStream ts = factory.create(new StringReader("ã·ãã¢ã½ããã¦ã§ã¢ã¨ã³ã¸ãã¢"));
     assertTokenStreamContents(ts,
         new String[] { "ã·ãã¢ã½ããã¦ã§ã¢ã¨ã³ã¸ãã¢" }
     );
   }
 
   /**
    * Test user dictionary
    */
   public void testUserDict() throws IOException {
     String userDict = 
         "# Custom segmentation for long entries\n" +
         "æ¥æ¬çµæ¸æ°è,æ¥æ¬ çµæ¸ æ°è,ããã³ ã±ã¤ã¶ã¤ ã·ã³ãã³,ã«ã¹ã¿ã åè©\n" +
         "é¢è¥¿å½éç©ºæ¸¯,é¢è¥¿ å½é ç©ºæ¸¯,ã«ã³ãµã¤ ã³ã¯ãµã¤ ã¯ã¦ã³ã¦,ãã¹ãåè©\n" +
         "# Custom reading for sumo wrestler\n" +
         "æéé¾,æéé¾,ã¢ãµã·ã§ã¦ãªã¥ã¦,ã«ã¹ã¿ã äººå\n";
     Map<String,String> args = new HashMap<String,String>();
     args.put("userDictionary", "userdict.txt");
    JapaneseTokenizerFactory factory = new JapaneseTokenizerFactory(args);
     factory.inform(new StringMockResourceLoader(userDict));
     TokenStream ts = factory.create(new StringReader("é¢è¥¿å½éç©ºæ¸¯ã«è¡ã£ã"));
     assertTokenStreamContents(ts,
         new String[] { "é¢è¥¿", "å½é", "ç©ºæ¸¯", "ã«",  "è¡ã£",  "ã" }
     );
   }
 
   /**
    * Test preserving punctuation
    */
   public void testPreservePunctuation() throws IOException {
     Map<String,String> args = new HashMap<String,String>();
     args.put("discardPunctuation", "false");
    JapaneseTokenizerFactory factory = new JapaneseTokenizerFactory(args);
     factory.inform(new StringMockResourceLoader(""));
     TokenStream ts = factory.create(
         new StringReader("ä»ãã«ã¦ã§ã¼ã«ãã¾ãããæ¥é±ã®é ­æ¥æ¬ã«æ»ãã¾ããæ¥½ãã¿ã«ãã¦ãã¾ãï¼ãå¯¿å¸ãé£ã¹ãããªããã")
     );
     assertTokenStreamContents(ts,
         new String[] { "ä»", "ãã«ã¦ã§ã¼", "ã«", "ã", "ã¾ã", "ã", "ã",
             "æ¥é±", "ã®", "é ­", "æ¥æ¬", "ã«", "æ»ã", "ã¾ã", "ã",
             "æ¥½ãã¿", "ã«", "ã", "ã¦", "ã", "ã¾ã", "ï¼",
             "ã", "å¯¿å¸", "ã", "é£ã¹", "ãã", "ãª", "ã", "ã", "ã"}
     );
   }
  
  /** Test that bogus arguments result in exception */
  public void testBogusArguments() throws Exception {
    try {
      new JapaneseTokenizerFactory(new HashMap<String,String>() {{
        put("bogusArg", "bogusValue");
      }});
      fail();
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("Unknown parameters"));
    }
  }
 }
