 package org.apache.lucene.analysis.ru;
 
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
 
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.util.CharArraySet;
 import org.apache.lucene.util.Version;
 
 /**
  * Test case for RussianAnalyzer.
  */
 
 public class TestRussianAnalyzer extends BaseTokenStreamTestCase {
 
      /** Check that RussianAnalyzer doesnt discard any numbers */
     public void testDigitsInRussianCharset() throws IOException
     {
       RussianAnalyzer ra = new RussianAnalyzer(TEST_VERSION_CURRENT);
       assertAnalyzesTo(ra, "text 1000", new String[] { "text", "1000" });
     }
     
     public void testReusableTokenStream() throws Exception {
       Analyzer a = new RussianAnalyzer(TEST_VERSION_CURRENT);
       assertAnalyzesToReuse(a, "ÐÐ¼ÐµÑÑÐµ Ñ ÑÐµÐ¼ Ð¾ ÑÐ¸Ð»Ðµ ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½Ð¾Ð¹ ÑÐ½ÐµÑÐ³Ð¸Ð¸ Ð¸Ð¼ÐµÐ»Ð¸ Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½Ð¸Ðµ ÐµÑÐµ",
           new String[] { "Ð²Ð¼ÐµÑÑ", "ÑÐ¸Ð»", "ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½", "ÑÐ½ÐµÑÐ³", "Ð¸Ð¼ÐµÐ»", "Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½" });
       assertAnalyzesToReuse(a, "ÐÐ¾ Ð·Ð½Ð°Ð½Ð¸Ðµ ÑÑÐ¾ ÑÑÐ°Ð½Ð¸Ð»Ð¾ÑÑ Ð² ÑÐ°Ð¹Ð½Ðµ",
           new String[] { "Ð·Ð½Ð°Ð½", "ÑÑ", "ÑÑÐ°Ð½", "ÑÐ°Ð¹Ð½" });
     }
     
     
     public void testWithStemExclusionSet() throws Exception {
       CharArraySet set = new CharArraySet(TEST_VERSION_CURRENT, 1, true);
       set.add("Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½Ð¸Ðµ");
       Analyzer a = new RussianAnalyzer(TEST_VERSION_CURRENT, RussianAnalyzer.getDefaultStopSet() , set);
       assertAnalyzesToReuse(a, "ÐÐ¼ÐµÑÑÐµ Ñ ÑÐµÐ¼ Ð¾ ÑÐ¸Ð»Ðµ ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½Ð¾Ð¹ ÑÐ½ÐµÑÐ³Ð¸Ð¸ Ð¸Ð¼ÐµÐ»Ð¸ Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½Ð¸Ðµ ÐµÑÐµ",
           new String[] { "Ð²Ð¼ÐµÑÑ", "ÑÐ¸Ð»", "ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½", "ÑÐ½ÐµÑÐ³", "Ð¸Ð¼ÐµÐ»", "Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½Ð¸Ðµ" });
      
     }
     
     /** blast some random strings through the analyzer */
     public void testRandomStrings() throws Exception {
      checkRandomData(random(), new RussianAnalyzer(TEST_VERSION_CURRENT), 10000*RANDOM_MULTIPLIER);
     }
 }
