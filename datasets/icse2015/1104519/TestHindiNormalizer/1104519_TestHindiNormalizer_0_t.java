 package org.apache.lucene.analysis.hi;
 
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
 
 import java.io.IOException;
 import java.io.StringReader;
 
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.MockTokenizer;
 import org.apache.lucene.analysis.TokenFilter;
 import org.apache.lucene.analysis.Tokenizer;
 
 /**
  * Test HindiNormalizer
  */
 public class TestHindiNormalizer extends BaseTokenStreamTestCase {
   /**
    * Test some basic normalization, with an example from the paper.
    */
   public void testBasics() throws IOException {
     check("à¤à¤à¤à¤°à¥à¤à¤¼à¥", "à¤à¤à¤à¤°à¥à¤à¤¿");
     check("à¤à¤à¤à¤°à¥à¤à¥", "à¤à¤à¤à¤°à¥à¤à¤¿");
     check("à¤à¤à¤à¥à¤°à¥à¤à¤¼à¥", "à¤à¤à¤à¤°à¥à¤à¤¿");
     check("à¤à¤à¤à¥à¤°à¥à¤à¥", "à¤à¤à¤à¤°à¥à¤à¤¿");
     check("à¤à¤à¤à¤°à¥à¤à¤¼à¥", "à¤à¤à¤à¤°à¥à¤à¤¿");
     check("à¤à¤à¤à¤°à¥à¤à¥", "à¤à¤à¤à¤°à¥à¤à¤¿");
     check("à¤à¤à¤à¥à¤°à¥à¤à¤¼à¥", "à¤à¤à¤à¤°à¥à¤à¤¿");
     check("à¤à¤à¤à¥à¤°à¥à¤à¥", "à¤à¤à¤à¤°à¥à¤à¤¿");
   }
   
   public void testDecompositions() throws IOException {
     // removing nukta dot
     check("à¥à¤¿à¤¤à¤¾à¤¬", "à¤à¤¿à¤¤à¤¾à¤¬");
     check("à¥à¤°à¥à¥", "à¤«à¤°à¤");
     check("à¥à¤°à¥à¥", "à¤à¤°à¤");
     // some other composed nukta forms
     check("à¤±à¤´à¥à¥à¥à¥à¥", "à¤°à¤³à¤à¤à¤¡à¤¢à¤¯");
     // removal of format (ZWJ/ZWNJ)
     check("à¤¶à¤¾à¤°à¥âà¤®à¤¾", "à¤¶à¤¾à¤°à¤®à¤¾");
     check("à¤¶à¤¾à¤°à¥âà¤®à¤¾", "à¤¶à¤¾à¤°à¤®à¤¾");
     // removal of chandra
     check("à¥à¥à¥à¥à¤à¤à¤à¤\u0972", "à¥à¥à¥à¥à¤à¤à¤à¤à¤");
     // vowel shortening
     check("à¤à¤à¤à¥ à¥¡à¤à¤à¥à¥à¥à¥£à¥à¥", "à¤à¤à¤à¤à¤à¤à¤à¤¿à¥à¥à¥¢à¥à¥");
   }
   private void check(String input, String output) throws IOException {
    Tokenizer tokenizer = new MockTokenizer(new StringReader(input), MockTokenizer.WHITESPACE, false);
     TokenFilter tf = new HindiNormalizationFilter(tokenizer);
     assertTokenStreamContents(tf, new String[] { output });
   }
 }
