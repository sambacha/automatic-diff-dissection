 package org.apache.solr.analysis;
 
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
 
 import java.io.Reader;
 import java.io.StringReader;
 
 import org.apache.lucene.analysis.TokenStream;
 import org.apache.lucene.analysis.Tokenizer;
 import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.th.ThaiWordFilter;
import org.junit.Assume;
 
 /**
  * Simple tests to ensure the Thai word filter factory is working.
  */
 public class TestThaiWordFilterFactory extends BaseTokenTestCase {
   /**
    * Ensure the filter actually decomposes text.
    */
   public void testWordBreak() throws Exception {
    Assume.assumeTrue(ThaiWordFilter.DBBI_AVAILABLE);
     Reader reader = new StringReader("à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ");
     Tokenizer tokenizer = new WhitespaceTokenizer(DEFAULT_VERSION, reader);
     ThaiWordFilterFactory factory = new ThaiWordFilterFactory();
     factory.init(DEFAULT_VERSION_PARAM);
     TokenStream stream = factory.create(tokenizer);
     assertTokenStreamContents(stream, new String[] {"à¸à¸²à¸£", "à¸à¸µà¹", "à¹à¸à¹",
         "à¸à¹à¸­à¸", "à¹à¸ªà¸à¸", "à¸§à¹à¸²", "à¸à¸²à¸", "à¸à¸µ"});
   }
 }