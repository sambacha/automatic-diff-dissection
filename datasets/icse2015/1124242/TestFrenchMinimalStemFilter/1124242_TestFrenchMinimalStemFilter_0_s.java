 package org.apache.lucene.analysis.fr;
 
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
 import java.io.Reader;
 
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WhitespaceTokenizer;
 import org.apache.lucene.analysis.ReusableAnalyzerBase;
 
 import static org.apache.lucene.analysis.VocabularyAssert.*;
 
 /**
  * Simple tests for {@link FrenchMinimalStemFilter}
  */
 public class TestFrenchMinimalStemFilter extends BaseTokenStreamTestCase {
   private Analyzer analyzer = new ReusableAnalyzerBase() {
     @Override
     protected TokenStreamComponents createComponents(String fieldName,
         Reader reader) {
      Tokenizer source = new WhitespaceTokenizer(TEST_VERSION_CURRENT, reader);
       return new TokenStreamComponents(source, new FrenchMinimalStemFilter(source));
     }
   };
   
   /** Test some examples from the paper */
   public void testExamples() throws IOException {
     checkOneTerm(analyzer, "chevaux", "cheval");
     checkOneTerm(analyzer, "hiboux", "hibou");
     
     checkOneTerm(analyzer, "chantÃ©s", "chant");
     checkOneTerm(analyzer, "chanter", "chant");
     checkOneTerm(analyzer, "chante", "chant");
     
     checkOneTerm(analyzer, "baronnes", "baron");
     checkOneTerm(analyzer, "barons", "baron");
     checkOneTerm(analyzer, "baron", "baron");
   }
   
   /** Test against a vocabulary from the reference impl */
   public void testVocabulary() throws IOException {
     assertVocabulary(analyzer, getDataFile("frminimaltestdata.zip"), "frminimal.txt");
   }
   
   /** blast some random strings through the analyzer */
   public void testRandomStrings() throws Exception {
     checkRandomData(random, analyzer, 10000*RANDOM_MULTIPLIER);
   }
 }
