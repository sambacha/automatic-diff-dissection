 package org.apache.lucene.analysis.ru;
 
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
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.Reader;
 import java.io.StringReader;
 
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.TokenStream;
 import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;
 
 /**
  * Test case for RussianAnalyzer.
  */
 
 public class TestRussianAnalyzer extends BaseTokenStreamTestCase
 {
     private InputStreamReader inWords;
 
     private InputStreamReader sampleUnicode;
 
     private File dataDir;
 
     protected void setUp() throws Exception
     {
       super.setUp();
       dataDir = new File(System.getProperty("dataDir", "./bin"));
     }
 
     public void testUnicode() throws IOException
     {
        RussianAnalyzer ra = new RussianAnalyzer(Version.LUCENE_CURRENT);
         inWords =
             new InputStreamReader(
                 new FileInputStream(new File(dataDir, "/org/apache/lucene/analysis/ru/testUTF8.txt")),
                 "UTF-8");
 
         sampleUnicode =
             new InputStreamReader(
                 new FileInputStream(new File(dataDir, "/org/apache/lucene/analysis/ru/resUTF8.htm")),
                 "UTF-8");
 
         TokenStream in = ra.tokenStream("all", inWords);
 
         RussianLetterTokenizer sample =
             new RussianLetterTokenizer(
                 sampleUnicode);
 
         TermAttribute text = in.getAttribute(TermAttribute.class);
         TermAttribute sampleText = sample.getAttribute(TermAttribute.class);
 
         for (;;)
         {
           if (in.incrementToken() == false)
             break;
 
             boolean nextSampleToken = sample.incrementToken();
             assertEquals(
                 "Unicode",
                 text.term(),
                 nextSampleToken == false
                 ? null
                 : sampleText.term());
         }
 
         inWords.close();
         sampleUnicode.close();
     }
     
     public void testDigitsInRussianCharset() 
     {
         Reader reader = new StringReader("text 1000");
        RussianAnalyzer ra = new RussianAnalyzer(Version.LUCENE_CURRENT);
         TokenStream stream = ra.tokenStream("", reader);
 
         TermAttribute termText = stream.getAttribute(TermAttribute.class);
         try {
             assertTrue(stream.incrementToken());
             assertEquals("text", termText.term());
             assertTrue(stream.incrementToken());
             assertEquals("RussianAnalyzer's tokenizer skips numbers from input text", "1000", termText.term());
             assertFalse(stream.incrementToken());
         }
         catch (IOException e)
         {
             fail("unexpected IOException");
         }
     }
     
     public void testReusableTokenStream() throws Exception {
      Analyzer a = new RussianAnalyzer(Version.LUCENE_CURRENT);
       assertAnalyzesToReuse(a, "ÐÐ¼ÐµÑÑÐµ Ñ ÑÐµÐ¼ Ð¾ ÑÐ¸Ð»Ðµ ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½Ð¾Ð¹ ÑÐ½ÐµÑÐ³Ð¸Ð¸ Ð¸Ð¼ÐµÐ»Ð¸ Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½Ð¸Ðµ ÐµÑÐµ",
           new String[] { "Ð²Ð¼ÐµÑÑ", "ÑÐ¸Ð»", "ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½", "ÑÐ½ÐµÑÐ³", "Ð¸Ð¼ÐµÐ»", "Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½" });
       assertAnalyzesToReuse(a, "ÐÐ¾ Ð·Ð½Ð°Ð½Ð¸Ðµ ÑÑÐ¾ ÑÑÐ°Ð½Ð¸Ð»Ð¾ÑÑ Ð² ÑÐ°Ð¹Ð½Ðµ",
           new String[] { "Ð·Ð½Ð°Ð½", "ÑÑÐ°Ð½", "ÑÐ°Ð¹Ð½" });
     }
 }
