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
 
 import java.io.Reader;
 import java.io.StringReader;
 
 import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.BaseTokenStreamFactoryTestCase;
 
 /**
  * Simple tests to ensure the CJK bigram factory is working.
  */
public class TestCJKBigramFilterFactory extends BaseTokenStreamFactoryTestCase {
   public void testDefaults() throws Exception {
     Reader reader = new StringReader("å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ãã");
    TokenStream stream = tokenizerFactory("standard").create(reader);
    stream = tokenFilterFactory("CJKBigram").create(stream);
     assertTokenStreamContents(stream,
         new String[] { "å¤ã", "ãã®", "ã®å­¦", "å­¦ç", "çã", "ãè©¦", "è©¦é¨", "é¨ã«", "ã«è½", "è½ã¡", "ã¡ã" });
   }
   
   public void testHanOnly() throws Exception {
     Reader reader = new StringReader("å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ãã");
    TokenStream stream = tokenizerFactory("standard").create(reader);
    stream = tokenFilterFactory("CJKBigram", 
        "hiragana", "false").create(stream);
     assertTokenStreamContents(stream,
         new String[] { "å¤", "ã", "ã®",  "å­¦ç", "ã",  "è©¦é¨", "ã«",  "è½", "ã¡", "ã" });
   }
   
   public void testHanOnlyUnigrams() throws Exception {
     Reader reader = new StringReader("å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ãã");
    TokenStream stream = tokenizerFactory("standard").create(reader);
    stream = tokenFilterFactory("CJKBigram", 
        "hiragana", "false", 
        "outputUnigrams", "true").create(stream);
     assertTokenStreamContents(stream,
         new String[] { "å¤", "ã", "ã®",  "å­¦", "å­¦ç", "ç", "ã",  "è©¦", "è©¦é¨", "é¨", "ã«",  "è½", "ã¡", "ã" });
   }
  
  /** Test that bogus arguments result in exception */
  public void testBogusArguments() throws Exception {
    try {
      tokenFilterFactory("CJKBigram", "bogusArg", "bogusValue");
      fail();
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("Unknown parameters"));
    }
  }
 }
