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
 
 import java.io.Reader;
 import java.io.StringReader;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
 
 /** basic tests for {@link ICUTransformFilterFactory} */
 public class TestICUTransformFilterFactory extends BaseTokenStreamTestCase {
   
   /** ensure the transform is working */
   public void test() throws Exception {
     Reader reader = new StringReader("ç°¡åå­");
    ICUTransformFilterFactory factory = new ICUTransformFilterFactory();
     Map<String,String> args = new HashMap<String,String>();
     args.put("id", "Traditional-Simplified");
    factory.init(args);
    Tokenizer tokenizer = new WhitespaceTokenizer(TEST_VERSION_CURRENT, reader);
    TokenStream stream = factory.create(tokenizer);
     assertTokenStreamContents(stream, new String[] { "ç®åå­" });
   }
   
   /** test forward and reverse direction */
  public void testDirection() throws Exception {
     // forward
     Reader reader = new StringReader("Ð Ð¾ÑÑÐ¸Ð¹ÑÐºÐ°Ñ Ð¤ÐµÐ´ÐµÑÐ°ÑÐ¸Ñ");
    ICUTransformFilterFactory factory = new ICUTransformFilterFactory();
     Map<String,String> args = new HashMap<String,String>();
     args.put("id", "Cyrillic-Latin");
    factory.init(args);
    Tokenizer tokenizer = new WhitespaceTokenizer(TEST_VERSION_CURRENT, reader);
    TokenStream stream = factory.create(tokenizer);
     assertTokenStreamContents(stream, new String[] { "RossijskaÃ¢",  "FederaciÃ¢" });
     
     // backward (invokes Latin-Cyrillic)
    reader = new StringReader("RossijskaÃ¢ FederaciÃ¢");
     args.put("direction", "reverse");
    factory.init(args);
    tokenizer = new WhitespaceTokenizer(TEST_VERSION_CURRENT, reader);
    stream = factory.create(tokenizer);
     assertTokenStreamContents(stream, new String[] { "Ð Ð¾ÑÑÐ¸Ð¹ÑÐºÐ°Ñ", "Ð¤ÐµÐ´ÐµÑÐ°ÑÐ¸Ñ" });
   }
 }
