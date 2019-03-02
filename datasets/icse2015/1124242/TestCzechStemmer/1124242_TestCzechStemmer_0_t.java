 package org.apache.lucene.analysis.cz;
 
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
 import org.apache.lucene.analysis.CharArraySet;
 import org.apache.lucene.analysis.KeywordMarkerFilter;
import org.apache.lucene.analysis.MockTokenizer;
 
 /**
  * Test the Czech Stemmer.
  * 
  * Note: its algorithmic, so some stems are nonsense
  *
  */
 public class TestCzechStemmer extends BaseTokenStreamTestCase {
   
   /**
    * Test showing how masculine noun forms conflate
    */
   public void testMasculineNouns() throws IOException {
     CzechAnalyzer cz = new CzechAnalyzer(TEST_VERSION_CURRENT);
     
     /* animate ending with a hard consonant */
     assertAnalyzesTo(cz, "pÃ¡n", new String[] { "pÃ¡n" });
     assertAnalyzesTo(cz, "pÃ¡ni", new String[] { "pÃ¡n" });
     assertAnalyzesTo(cz, "pÃ¡novÃ©", new String[] { "pÃ¡n" });
     assertAnalyzesTo(cz, "pÃ¡na", new String[] { "pÃ¡n" });
     assertAnalyzesTo(cz, "pÃ¡nÅ¯", new String[] { "pÃ¡n" });
     assertAnalyzesTo(cz, "pÃ¡novi", new String[] { "pÃ¡n" });
     assertAnalyzesTo(cz, "pÃ¡nÅ¯m", new String[] { "pÃ¡n" });
     assertAnalyzesTo(cz, "pÃ¡ny", new String[] { "pÃ¡n" });
     assertAnalyzesTo(cz, "pÃ¡ne", new String[] { "pÃ¡n" });
     assertAnalyzesTo(cz, "pÃ¡nech", new String[] { "pÃ¡n" });
     assertAnalyzesTo(cz, "pÃ¡nem", new String[] { "pÃ¡n" });
     
     /* inanimate ending with hard consonant */
     assertAnalyzesTo(cz, "hrad", new String[] { "hrad" });
     assertAnalyzesTo(cz, "hradu", new String[] { "hrad" });
     assertAnalyzesTo(cz, "hrade", new String[] { "hrad" });
     assertAnalyzesTo(cz, "hradem", new String[] { "hrad" });
     assertAnalyzesTo(cz, "hrady", new String[] { "hrad" });
     assertAnalyzesTo(cz, "hradech", new String[] { "hrad" });
     assertAnalyzesTo(cz, "hradÅ¯m", new String[] { "hrad" });
     assertAnalyzesTo(cz, "hradÅ¯", new String[] { "hrad" });
     
     /* animate ending with a soft consonant */
     assertAnalyzesTo(cz, "muÅ¾", new String[] { "muh" });
     assertAnalyzesTo(cz, "muÅ¾i", new String[] { "muh" });
     assertAnalyzesTo(cz, "muÅ¾e", new String[] { "muh" });
     assertAnalyzesTo(cz, "muÅ¾Å¯", new String[] { "muh" });
     assertAnalyzesTo(cz, "muÅ¾Å¯m", new String[] { "muh" });
     assertAnalyzesTo(cz, "muÅ¾Ã­ch", new String[] { "muh" });
     assertAnalyzesTo(cz, "muÅ¾em", new String[] { "muh" });
     
     /* inanimate ending with a soft consonant */
     assertAnalyzesTo(cz, "stroj", new String[] { "stroj" });
     assertAnalyzesTo(cz, "stroje", new String[] { "stroj" });
     assertAnalyzesTo(cz, "strojÅ¯", new String[] { "stroj" });
     assertAnalyzesTo(cz, "stroji", new String[] { "stroj" });
     assertAnalyzesTo(cz, "strojÅ¯m", new String[] { "stroj" });
     assertAnalyzesTo(cz, "strojÃ­ch", new String[] { "stroj" });
     assertAnalyzesTo(cz, "strojem", new String[] { "stroj" });
     
     /* ending with a */
     assertAnalyzesTo(cz, "pÅedseda", new String[] { "pÅedsd" });
     assertAnalyzesTo(cz, "pÅedsedovÃ©", new String[] { "pÅedsd" });
     assertAnalyzesTo(cz, "pÅedsedy", new String[] { "pÅedsd" });
     assertAnalyzesTo(cz, "pÅedsedÅ¯", new String[] { "pÅedsd" });
     assertAnalyzesTo(cz, "pÅedsedovi", new String[] { "pÅedsd" });
     assertAnalyzesTo(cz, "pÅedsedÅ¯m", new String[] { "pÅedsd" });
     assertAnalyzesTo(cz, "pÅedsedu", new String[] { "pÅedsd" });
     assertAnalyzesTo(cz, "pÅedsedo", new String[] { "pÅedsd" });
     assertAnalyzesTo(cz, "pÅedsedech", new String[] { "pÅedsd" });
     assertAnalyzesTo(cz, "pÅedsedou", new String[] { "pÅedsd" });
     
     /* ending with e */
     assertAnalyzesTo(cz, "soudce", new String[] { "soudk" });
     assertAnalyzesTo(cz, "soudci", new String[] { "soudk" });
     assertAnalyzesTo(cz, "soudcÅ¯", new String[] { "soudk" });
     assertAnalyzesTo(cz, "soudcÅ¯m", new String[] { "soudk" });
     assertAnalyzesTo(cz, "soudcÃ­ch", new String[] { "soudk" });
     assertAnalyzesTo(cz, "soudcem", new String[] { "soudk" });
   }
   
   /**
    * Test showing how feminine noun forms conflate
    */
   public void testFeminineNouns() throws IOException {
     CzechAnalyzer cz = new CzechAnalyzer(TEST_VERSION_CURRENT);
     
     /* ending with hard consonant */
     assertAnalyzesTo(cz, "kost", new String[] { "kost" });
     assertAnalyzesTo(cz, "kosti", new String[] { "kost" });
     assertAnalyzesTo(cz, "kostÃ­", new String[] { "kost" });
     assertAnalyzesTo(cz, "kostem", new String[] { "kost" });
     assertAnalyzesTo(cz, "kostech", new String[] { "kost" });
     assertAnalyzesTo(cz, "kostmi", new String[] { "kost" });
     
     /* ending with a soft consonant */
     // note: in this example sing nom. and sing acc. don't conflate w/ the rest
     assertAnalyzesTo(cz, "pÃ­seÅ", new String[] { "pÃ­sÅ" });
     assertAnalyzesTo(cz, "pÃ­snÄ", new String[] { "pÃ­sn" });
     assertAnalyzesTo(cz, "pÃ­sni", new String[] { "pÃ­sn" });
     assertAnalyzesTo(cz, "pÃ­snÄmi", new String[] { "pÃ­sn" });
     assertAnalyzesTo(cz, "pÃ­snÃ­ch", new String[] { "pÃ­sn" });
     assertAnalyzesTo(cz, "pÃ­snÃ­m", new String[] { "pÃ­sn" });
     
     /* ending with e */
     assertAnalyzesTo(cz, "rÅ¯Å¾e", new String[] { "rÅ¯h" });
     assertAnalyzesTo(cz, "rÅ¯Å¾Ã­", new String[] { "rÅ¯h" });
     assertAnalyzesTo(cz, "rÅ¯Å¾Ã­m", new String[] { "rÅ¯h" });
     assertAnalyzesTo(cz, "rÅ¯Å¾Ã­ch", new String[] { "rÅ¯h" });
     assertAnalyzesTo(cz, "rÅ¯Å¾emi", new String[] { "rÅ¯h" });
     assertAnalyzesTo(cz, "rÅ¯Å¾i", new String[] { "rÅ¯h" });
     
     /* ending with a */
     assertAnalyzesTo(cz, "Å¾ena", new String[] { "Å¾n" });
     assertAnalyzesTo(cz, "Å¾eny", new String[] { "Å¾n" });
     assertAnalyzesTo(cz, "Å¾en", new String[] { "Å¾n" });
     assertAnalyzesTo(cz, "Å¾enÄ", new String[] { "Å¾n" });
     assertAnalyzesTo(cz, "Å¾enÃ¡m", new String[] { "Å¾n" });
     assertAnalyzesTo(cz, "Å¾enu", new String[] { "Å¾n" });
     assertAnalyzesTo(cz, "Å¾eno", new String[] { "Å¾n" });
     assertAnalyzesTo(cz, "Å¾enÃ¡ch", new String[] { "Å¾n" });
     assertAnalyzesTo(cz, "Å¾enou", new String[] { "Å¾n" });
     assertAnalyzesTo(cz, "Å¾enami", new String[] { "Å¾n" });
   }
 
   /**
    * Test showing how neuter noun forms conflate
    */
   public void testNeuterNouns() throws IOException {
     CzechAnalyzer cz = new CzechAnalyzer(TEST_VERSION_CURRENT);
     
     /* ending with o */
     assertAnalyzesTo(cz, "mÄsto", new String[] { "mÄst" });
     assertAnalyzesTo(cz, "mÄsta", new String[] { "mÄst" });
     assertAnalyzesTo(cz, "mÄst", new String[] { "mÄst" });
     assertAnalyzesTo(cz, "mÄstu", new String[] { "mÄst" });
     assertAnalyzesTo(cz, "mÄstÅ¯m", new String[] { "mÄst" });
     assertAnalyzesTo(cz, "mÄstÄ", new String[] { "mÄst" });
     assertAnalyzesTo(cz, "mÄstech", new String[] { "mÄst" });
     assertAnalyzesTo(cz, "mÄstem", new String[] { "mÄst" });
     assertAnalyzesTo(cz, "mÄsty", new String[] { "mÄst" });
     
     /* ending with e */
     assertAnalyzesTo(cz, "moÅe", new String[] { "moÅ" });
     assertAnalyzesTo(cz, "moÅÃ­", new String[] { "moÅ" });
     assertAnalyzesTo(cz, "moÅÃ­m", new String[] { "moÅ" });
     assertAnalyzesTo(cz, "moÅi", new String[] { "moÅ" });
     assertAnalyzesTo(cz, "moÅÃ­ch", new String[] { "moÅ" });
     assertAnalyzesTo(cz, "moÅem", new String[] { "moÅ" });
 
     /* ending with Ä */
     assertAnalyzesTo(cz, "kuÅe", new String[] { "kuÅ" });
     assertAnalyzesTo(cz, "kuÅata", new String[] { "kuÅ" });
     assertAnalyzesTo(cz, "kuÅete", new String[] { "kuÅ" });
     assertAnalyzesTo(cz, "kuÅat", new String[] { "kuÅ" });
     assertAnalyzesTo(cz, "kuÅeti", new String[] { "kuÅ" });
     assertAnalyzesTo(cz, "kuÅatÅ¯m", new String[] { "kuÅ" });
     assertAnalyzesTo(cz, "kuÅatech", new String[] { "kuÅ" });
     assertAnalyzesTo(cz, "kuÅetem", new String[] { "kuÅ" });
     assertAnalyzesTo(cz, "kuÅaty", new String[] { "kuÅ" });
     
     /* ending with Ã­ */
     assertAnalyzesTo(cz, "stavenÃ­", new String[] { "stavn" });
     assertAnalyzesTo(cz, "stavenÃ­m", new String[] { "stavn" });
     assertAnalyzesTo(cz, "stavenÃ­ch", new String[] { "stavn" });
     assertAnalyzesTo(cz, "stavenÃ­mi", new String[] { "stavn" });    
   }
   
   /**
    * Test showing how adjectival forms conflate
    */
   public void testAdjectives() throws IOException {
     CzechAnalyzer cz = new CzechAnalyzer(TEST_VERSION_CURRENT);
     
     /* ending with Ã½/Ã¡/Ã© */
     assertAnalyzesTo(cz, "mladÃ½", new String[] { "mlad" });
     assertAnalyzesTo(cz, "mladÃ­", new String[] { "mlad" });
     assertAnalyzesTo(cz, "mladÃ©ho", new String[] { "mlad" });
     assertAnalyzesTo(cz, "mladÃ½ch", new String[] { "mlad" });
     assertAnalyzesTo(cz, "mladÃ©mu", new String[] { "mlad" });
     assertAnalyzesTo(cz, "mladÃ½m", new String[] { "mlad" });
     assertAnalyzesTo(cz, "mladÃ©", new String[] { "mlad" });
     assertAnalyzesTo(cz, "mladÃ©m", new String[] { "mlad" });
     assertAnalyzesTo(cz, "mladÃ½mi", new String[] { "mlad" }); 
     assertAnalyzesTo(cz, "mladÃ¡", new String[] { "mlad" });
     assertAnalyzesTo(cz, "mladou", new String[] { "mlad" });
 
     /* ending with Ã­ */
     assertAnalyzesTo(cz, "jarnÃ­", new String[] { "jarn" });
     assertAnalyzesTo(cz, "jarnÃ­ho", new String[] { "jarn" });
     assertAnalyzesTo(cz, "jarnÃ­ch", new String[] { "jarn" });
     assertAnalyzesTo(cz, "jarnÃ­mu", new String[] { "jarn" });
     assertAnalyzesTo(cz, "jarnÃ­m", new String[] { "jarn" });
     assertAnalyzesTo(cz, "jarnÃ­mi", new String[] { "jarn" });  
   }
   
   /**
    * Test some possessive suffixes
    */
   public void testPossessive() throws IOException {
     CzechAnalyzer cz = new CzechAnalyzer(TEST_VERSION_CURRENT);
     assertAnalyzesTo(cz, "KarlÅ¯v", new String[] { "karl" });
     assertAnalyzesTo(cz, "jazykovÃ½", new String[] { "jazyk" });
   }
   
   /**
    * Test some exceptional rules, implemented as rewrites.
    */
   public void testExceptions() throws IOException {
     CzechAnalyzer cz = new CzechAnalyzer(TEST_VERSION_CURRENT);
     
     /* rewrite of Å¡t -> sk */
     assertAnalyzesTo(cz, "ÄeskÃ½", new String[] { "Äesk" });
     assertAnalyzesTo(cz, "ÄeÅ¡tÃ­", new String[] { "Äesk" });
     
     /* rewrite of Ät -> ck */
     assertAnalyzesTo(cz, "anglickÃ½", new String[] { "anglick" });
     assertAnalyzesTo(cz, "angliÄtÃ­", new String[] { "anglick" });
     
     /* rewrite of z -> h */
     assertAnalyzesTo(cz, "kniha", new String[] { "knih" });
     assertAnalyzesTo(cz, "knize", new String[] { "knih" });
     
     /* rewrite of Å¾ -> h */
     assertAnalyzesTo(cz, "mazat", new String[] { "mah" });
     assertAnalyzesTo(cz, "maÅ¾u", new String[] { "mah" });
     
     /* rewrite of c -> k */
     assertAnalyzesTo(cz, "kluk", new String[] { "kluk" });
     assertAnalyzesTo(cz, "kluci", new String[] { "kluk" });
     assertAnalyzesTo(cz, "klucÃ­ch", new String[] { "kluk" });
     
     /* rewrite of Ä -> k */
     assertAnalyzesTo(cz, "hezkÃ½", new String[] { "hezk" });
     assertAnalyzesTo(cz, "hezÄÃ­", new String[] { "hezk" });
     
     /* rewrite of *Å¯* -> *o* */
     assertAnalyzesTo(cz, "hÅ¯l", new String[] { "hol" });
     assertAnalyzesTo(cz, "hole", new String[] { "hol" });
     
     /* rewrite of e* -> * */
     assertAnalyzesTo(cz, "deska", new String[] { "desk" });
     assertAnalyzesTo(cz, "desek", new String[] { "desk" });
   }
   
   /**
    * Test that very short words are not stemmed.
    */
   public void testDontStem() throws IOException {
     CzechAnalyzer cz = new CzechAnalyzer(TEST_VERSION_CURRENT);
     assertAnalyzesTo(cz, "e", new String[] { "e" });
     assertAnalyzesTo(cz, "zi", new String[] { "zi" });
   }
   
   public void testWithKeywordAttribute() throws IOException {
     CharArraySet set = new CharArraySet(TEST_VERSION_CURRENT, 1, true);
     set.add("hole");
     CzechStemFilter filter = new CzechStemFilter(new KeywordMarkerFilter(
        new MockTokenizer(new StringReader("hole desek"), MockTokenizer.WHITESPACE, false), set));
     assertTokenStreamContents(filter, new String[] { "hole", "desk" });
   }
   
 }
