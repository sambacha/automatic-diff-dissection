 package org.apache.lucene.analysis.in;
 
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
 import org.apache.lucene.analysis.TokenFilter;
 import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
 
 /**
  * Test IndicNormalizer
  */
 public class TestIndicNormalizer extends BaseTokenStreamTestCase {
   /**
    * Test some basic normalization
    */
   public void testBasics() throws IOException {
     check("à¤à¤¾à¥à¤à¤¾à¥", "à¤à¤");
     check("à¤à¤¾à¥à¤à¤¾à¥", "à¤à¤");
     check("à¤à¤¾à¥à¤à¤¾à¥", "à¤à¤");
     check("à¤à¤¾à¥à¤à¤¾à¥", "à¤à¤");
     check("à¤à¤¾à¤à¤¾", "à¤à¤");
     check("à¤à¤¾à¥à¤°", "à¤à¤°");
     // khanda-ta
     check("à¦¤à§â", "à§");
   }
   
   private void check(String input, String output) throws IOException {
    Tokenizer tokenizer = new WhitespaceTokenizer(TEST_VERSION_CURRENT, 
        new StringReader(input));
     TokenFilter tf = new IndicNormalizationFilter(tokenizer);
     assertTokenStreamContents(tf, new String[] { output });
   }
 }