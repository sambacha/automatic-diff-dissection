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
  * Test HindiStemmer
  */
 public class TestHindiStemmer extends BaseTokenStreamTestCase {
   /**
    * Test masc noun inflections
    */
   public void testMasculineNouns() throws IOException {
     check("à¤²à¤¡à¤à¤¾", "à¤²à¤¡à¤");
     check("à¤²à¤¡à¤à¥", "à¤²à¤¡à¤");
     check("à¤²à¤¡à¤à¥à¤", "à¤²à¤¡à¤");
     
     check("à¤à¥à¤°à¥", "à¤à¥à¤°");
     check("à¤à¥à¤°à¥à¤à¤", "à¤à¥à¤°");
     
     check("à¤¦à¥à¤¸à¥à¤¤", "à¤¦à¥à¤¸à¥à¤¤");
     check("à¤¦à¥à¤¸à¥à¤¤à¥à¤", "à¤¦à¥à¤¸à¥à¤¤");
   }
   
   /**
    * Test feminine noun inflections
    */
   public void testFeminineNouns() throws IOException {
     check("à¤²à¤¡à¤à¥", "à¤²à¤¡à¤");
     check("à¤²à¤¡à¤à¤¿à¤¯à¥à¤", "à¤²à¤¡à¤");
     
     check("à¤à¤¿à¤¤à¤¾à¤¬", "à¤à¤¿à¤¤à¤¾à¤¬");
     check("à¤à¤¿à¤¤à¤¾à¤¬à¥à¤", "à¤à¤¿à¤¤à¤¾à¤¬");
     check("à¤à¤¿à¤¤à¤¾à¤¬à¥à¤", "à¤à¤¿à¤¤à¤¾à¤¬");
     
     check("à¤à¤§à¥à¤¯à¤¾à¤ªà¥à¤à¤¾", "à¤à¤§à¥à¤¯à¤¾à¤ªà¥à¤");
     check("à¤à¤§à¥à¤¯à¤¾à¤ªà¥à¤à¤¾à¤à¤", "à¤à¤§à¥à¤¯à¤¾à¤ªà¥à¤");
     check("à¤à¤§à¥à¤¯à¤¾à¤ªà¥à¤à¤¾à¤à¤", "à¤à¤§à¥à¤¯à¤¾à¤ªà¥à¤");
   }
   
   /**
    * Test some verb forms
    */
   public void testVerbs() throws IOException {
     check("à¤à¤¾à¤¨à¤¾", "à¤à¤¾");
     check("à¤à¤¾à¤¤à¤¾", "à¤à¤¾");
     check("à¤à¤¾à¤¤à¥", "à¤à¤¾");
     check("à¤à¤¾", "à¤à¤¾");
   }
   
   /**
    * From the paper: since the suffix list for verbs includes AI, awA and anI,
    * additional suffixes had to be added to the list for noun/adjectives
    * ending with these endings.
    */
   public void testExceptions() throws IOException {
     check("à¤à¤ à¤¿à¤¨à¤¾à¤à¤¯à¤¾à¤", "à¤à¤ à¤¿à¤¨");
     check("à¤à¤ à¤¿à¤¨", "à¤à¤ à¤¿à¤¨");
   }
   
   private void check(String input, String output) throws IOException {
    Tokenizer tokenizer = new MockTokenizer(new StringReader(input), MockTokenizer.WHITESPACE, false);
     TokenFilter tf = new HindiStemFilter(tokenizer);
     assertTokenStreamContents(tf, new String[] { output });
   }
 }
