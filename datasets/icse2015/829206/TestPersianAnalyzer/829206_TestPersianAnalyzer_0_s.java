 package org.apache.lucene.analysis.fa;
 
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
 
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.TokenStream;
 
 /**
  * Test the Persian Analyzer
  * 
  */
 public class TestPersianAnalyzer extends BaseTokenStreamTestCase {
 
   /**
    * This test fails with NPE when the stopwords file is missing in classpath
    */
   public void testResourcesAvailable() {
    new PersianAnalyzer();
   }
 
   /**
    * This test shows how the combination of tokenization (breaking on zero-width
    * non-joiner), normalization (such as treating arabic YEH and farsi YEH the
    * same), and stopwords creates a light-stemming effect for verbs.
    * 
    * These verb forms are from http://en.wikipedia.org/wiki/Persian_grammar
    */
   public void testBehaviorVerbs() throws Exception {
    Analyzer a = new PersianAnalyzer();
     // active present indicative
     assertAnalyzesTo(a, "ÙÛâØ®ÙØ±Ø¯", new String[] { "Ø®ÙØ±Ø¯" });
     // active preterite indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯", new String[] { "Ø®ÙØ±Ø¯" });
     // active imperfective preterite indicative
     assertAnalyzesTo(a, "ÙÛâØ®ÙØ±Ø¯", new String[] { "Ø®ÙØ±Ø¯" });
     // active future indicative
     assertAnalyzesTo(a, "Ø®ÙØ§ÙØ¯ Ø®ÙØ±Ø¯", new String[] { "Ø®ÙØ±Ø¯" });
     // active present progressive indicative
     assertAnalyzesTo(a, "Ø¯Ø§Ø±Ø¯ ÙÛâØ®ÙØ±Ø¯", new String[] { "Ø®ÙØ±Ø¯" });
     // active preterite progressive indicative
     assertAnalyzesTo(a, "Ø¯Ø§Ø´Øª ÙÛâØ®ÙØ±Ø¯", new String[] { "Ø®ÙØ±Ø¯" });
 
     // active perfect indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯ÙâØ§Ø³Øª", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // active imperfective perfect indicative
     assertAnalyzesTo(a, "ÙÛâØ®ÙØ±Ø¯ÙâØ§Ø³Øª", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // active pluperfect indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø¨ÙØ¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // active imperfective pluperfect indicative
     assertAnalyzesTo(a, "ÙÛâØ®ÙØ±Ø¯Ù Ø¨ÙØ¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // active preterite subjunctive
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // active imperfective preterite subjunctive
     assertAnalyzesTo(a, "ÙÛâØ®ÙØ±Ø¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // active pluperfect subjunctive
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // active imperfective pluperfect subjunctive
     assertAnalyzesTo(a, "ÙÛâØ®ÙØ±Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive present indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù ÙÛâØ´ÙØ¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive preterite indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive imperfective preterite indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù ÙÛâØ´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive perfect indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø´Ø¯ÙâØ§Ø³Øª", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive imperfective perfect indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù ÙÛâØ´Ø¯ÙâØ§Ø³Øª", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive pluperfect indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø´Ø¯Ù Ø¨ÙØ¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive imperfective pluperfect indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù ÙÛâØ´Ø¯Ù Ø¨ÙØ¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive future indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø®ÙØ§ÙØ¯ Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive present progressive indicative
     assertAnalyzesTo(a, "Ø¯Ø§Ø±Ø¯ Ø®ÙØ±Ø¯Ù ÙÛâØ´ÙØ¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive preterite progressive indicative
     assertAnalyzesTo(a, "Ø¯Ø§Ø´Øª Ø®ÙØ±Ø¯Ù ÙÛâØ´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive present subjunctive
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø´ÙØ¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive preterite subjunctive
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø´Ø¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive imperfective preterite subjunctive
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù ÙÛâØ´Ø¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive pluperfect subjunctive
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø´Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive imperfective pluperfect subjunctive
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù ÙÛâØ´Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
 
     // active present subjunctive
     assertAnalyzesTo(a, "Ø¨Ø®ÙØ±Ø¯", new String[] { "Ø¨Ø®ÙØ±Ø¯" });
   }
 
   /**
    * This test shows how the combination of tokenization and stopwords creates a
    * light-stemming effect for verbs.
    * 
    * In this case, these forms are presented with alternative orthography, using
    * arabic yeh and whitespace. This yeh phenomenon is common for legacy text
    * due to some previous bugs in Microsoft Windows.
    * 
    * These verb forms are from http://en.wikipedia.org/wiki/Persian_grammar
    */
   public void testBehaviorVerbsDefective() throws Exception {
    Analyzer a = new PersianAnalyzer();
     // active present indicative
     assertAnalyzesTo(a, "ÙÙ Ø®ÙØ±Ø¯", new String[] { "Ø®ÙØ±Ø¯" });
     // active preterite indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯", new String[] { "Ø®ÙØ±Ø¯" });
     // active imperfective preterite indicative
     assertAnalyzesTo(a, "ÙÙ Ø®ÙØ±Ø¯", new String[] { "Ø®ÙØ±Ø¯" });
     // active future indicative
     assertAnalyzesTo(a, "Ø®ÙØ§ÙØ¯ Ø®ÙØ±Ø¯", new String[] { "Ø®ÙØ±Ø¯" });
     // active present progressive indicative
     assertAnalyzesTo(a, "Ø¯Ø§Ø±Ø¯ ÙÙ Ø®ÙØ±Ø¯", new String[] { "Ø®ÙØ±Ø¯" });
     // active preterite progressive indicative
     assertAnalyzesTo(a, "Ø¯Ø§Ø´Øª ÙÙ Ø®ÙØ±Ø¯", new String[] { "Ø®ÙØ±Ø¯" });
 
     // active perfect indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø§Ø³Øª", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // active imperfective perfect indicative
     assertAnalyzesTo(a, "ÙÙ Ø®ÙØ±Ø¯Ù Ø§Ø³Øª", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // active pluperfect indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø¨ÙØ¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // active imperfective pluperfect indicative
     assertAnalyzesTo(a, "ÙÙ Ø®ÙØ±Ø¯Ù Ø¨ÙØ¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // active preterite subjunctive
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // active imperfective preterite subjunctive
     assertAnalyzesTo(a, "ÙÙ Ø®ÙØ±Ø¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // active pluperfect subjunctive
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // active imperfective pluperfect subjunctive
     assertAnalyzesTo(a, "ÙÙ Ø®ÙØ±Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive present indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù ÙÙ Ø´ÙØ¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive preterite indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive imperfective preterite indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù ÙÙ Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive perfect indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø´Ø¯Ù Ø§Ø³Øª", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive imperfective perfect indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù ÙÙ Ø´Ø¯Ù Ø§Ø³Øª", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive pluperfect indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø´Ø¯Ù Ø¨ÙØ¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive imperfective pluperfect indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù ÙÙ Ø´Ø¯Ù Ø¨ÙØ¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive future indicative
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø®ÙØ§ÙØ¯ Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive present progressive indicative
     assertAnalyzesTo(a, "Ø¯Ø§Ø±Ø¯ Ø®ÙØ±Ø¯Ù ÙÙ Ø´ÙØ¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive preterite progressive indicative
     assertAnalyzesTo(a, "Ø¯Ø§Ø´Øª Ø®ÙØ±Ø¯Ù ÙÙ Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive present subjunctive
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø´ÙØ¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive preterite subjunctive
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø´Ø¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive imperfective preterite subjunctive
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù ÙÙ Ø´Ø¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive pluperfect subjunctive
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù Ø´Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     // passive imperfective pluperfect subjunctive
     assertAnalyzesTo(a, "Ø®ÙØ±Ø¯Ù ÙÙ Ø´Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
 
     // active present subjunctive
     assertAnalyzesTo(a, "Ø¨Ø®ÙØ±Ø¯", new String[] { "Ø¨Ø®ÙØ±Ø¯" });
   }
 
   /**
    * This test shows how the combination of tokenization (breaking on zero-width
    * non-joiner or space) and stopwords creates a light-stemming effect for
    * nouns, removing the plural -ha.
    */
   public void testBehaviorNouns() throws Exception {
    Analyzer a = new PersianAnalyzer();
     assertAnalyzesTo(a, "Ø¨Ø±Ú¯ ÙØ§", new String[] { "Ø¨Ø±Ú¯" });
     assertAnalyzesTo(a, "Ø¨Ø±Ú¯âÙØ§", new String[] { "Ø¨Ø±Ú¯" });
   }
 
   /**
    * Test showing that non-persian text is treated very much like SimpleAnalyzer
    * (lowercased, etc)
    */
   public void testBehaviorNonPersian() throws Exception {
    Analyzer a = new PersianAnalyzer();
     assertAnalyzesTo(a, "English test.", new String[] { "english", "test" });
   }
   
   /**
    * Basic test ensuring that reusableTokenStream works correctly.
    */
   public void testReusableTokenStream() throws Exception {
    Analyzer a = new PersianAnalyzer();
     assertAnalyzesToReuse(a, "Ø®ÙØ±Ø¯Ù ÙÙ Ø´Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯", new String[] { "Ø®ÙØ±Ø¯Ù" });
     assertAnalyzesToReuse(a, "Ø¨Ø±Ú¯âÙØ§", new String[] { "Ø¨Ø±Ú¯" });
   }
   
   /**
    * Test that custom stopwords work, and are not case-sensitive.
    */
   public void testCustomStopwords() throws Exception {
    PersianAnalyzer a = new PersianAnalyzer(new String[] { "the", "and", "a" });
     assertAnalyzesTo(a, "The quick brown fox.", new String[] { "quick",
         "brown", "fox" });
   }
 
 }
