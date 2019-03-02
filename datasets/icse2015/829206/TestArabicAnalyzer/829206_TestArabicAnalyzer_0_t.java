 package org.apache.lucene.analysis.ar;
 
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
 
 import java.io.StringReader;
 
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.TokenStream;
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.util.Version;
 
 /**
  * Test the Arabic Analyzer
  *
  */
 public class TestArabicAnalyzer extends BaseTokenStreamTestCase {
   
   /** This test fails with NPE when the 
    * stopwords file is missing in classpath */
   public void testResourcesAvailable() {
    new ArabicAnalyzer(Version.LUCENE_CURRENT);
   }
   
   /**
    * Some simple tests showing some features of the analyzer, how some regular forms will conflate
    */
   public void testBasicFeatures() throws Exception {
    ArabicAnalyzer a = new ArabicAnalyzer(Version.LUCENE_CURRENT);
     assertAnalyzesTo(a, "ÙØ¨ÙØ±", new String[] { "ÙØ¨ÙØ±" });
     assertAnalyzesTo(a, "ÙØ¨ÙØ±Ø©", new String[] { "ÙØ¨ÙØ±" }); // feminine marker
     
     assertAnalyzesTo(a, "ÙØ´Ø±ÙØ¨", new String[] { "ÙØ´Ø±ÙØ¨" });
     assertAnalyzesTo(a, "ÙØ´Ø±ÙØ¨Ø§Øª", new String[] { "ÙØ´Ø±ÙØ¨" }); // plural -at
     
     assertAnalyzesTo(a, "Ø£ÙØ±ÙÙÙÙÙ", new String[] { "Ø§ÙØ±ÙÙ" }); // plural -in
     assertAnalyzesTo(a, "Ø§ÙØ±ÙÙÙ", new String[] { "Ø§ÙØ±ÙÙ" }); // singular with bare alif
     
     assertAnalyzesTo(a, "ÙØªØ§Ø¨", new String[] { "ÙØªØ§Ø¨" }); 
     assertAnalyzesTo(a, "Ø§ÙÙØªØ§Ø¨", new String[] { "ÙØªØ§Ø¨" }); // definite article
     
     assertAnalyzesTo(a, "ÙØ§ ÙÙÙØª Ø£ÙÙØ§ÙÙÙ", new String[] { "ÙÙÙØª", "Ø§ÙÙØ§ÙÙÙ"});
     assertAnalyzesTo(a, "Ø§ÙØ°ÙÙ ÙÙÙØª Ø£ÙÙØ§ÙÙÙ", new String[] { "ÙÙÙØª", "Ø§ÙÙØ§ÙÙÙ" }); // stopwords
   }
   
   /**
    * Simple tests to show things are getting reset correctly, etc.
    */
   public void testReusableTokenStream() throws Exception {
    ArabicAnalyzer a = new ArabicAnalyzer(Version.LUCENE_CURRENT);
     assertAnalyzesToReuse(a, "ÙØ¨ÙØ±", new String[] { "ÙØ¨ÙØ±" });
     assertAnalyzesToReuse(a, "ÙØ¨ÙØ±Ø©", new String[] { "ÙØ¨ÙØ±" }); // feminine marker
   }
 
   /**
    * Non-arabic text gets treated in a similar way as SimpleAnalyzer.
    */
   public void testEnglishInput() throws Exception {
    assertAnalyzesTo(new ArabicAnalyzer(Version.LUCENE_CURRENT), "English text.", new String[] {
         "english", "text" });
   }
   
   /**
    * Test that custom stopwords work, and are not case-sensitive.
    */
   public void testCustomStopwords() throws Exception {
    ArabicAnalyzer a = new ArabicAnalyzer(Version.LUCENE_CURRENT, new String[] { "the", "and", "a" });
     assertAnalyzesTo(a, "The quick brown fox.", new String[] { "quick",
         "brown", "fox" });
   }
 }
