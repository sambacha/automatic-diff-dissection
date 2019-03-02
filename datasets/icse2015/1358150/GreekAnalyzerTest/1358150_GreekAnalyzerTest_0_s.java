 package org.apache.lucene.analysis.el;
 
 /**
  * Copyright 2005 The Apache Software Foundation
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.util.Version;
 
 /**
  * A unit test class for verifying the correct operation of the GreekAnalyzer.
  *
  */
 public class GreekAnalyzerTest extends BaseTokenStreamTestCase {
 
   /**
    * Test the analysis of various greek strings.
    *
    * @throws Exception in case an error occurs
    */
   public void testAnalyzer() throws Exception {
     Analyzer a = new GreekAnalyzer(TEST_VERSION_CURRENT);
     // Verify the correct analysis of capitals and small accented letters, and
     // stemming
     assertAnalyzesTo(a, "ÎÎ¯Î± ÎµÎ¾Î±Î¹ÏÎµÏÎ¹ÎºÎ¬ ÎºÎ±Î»Î® ÎºÎ±Î¹ ÏÎ»Î¿ÏÏÎ¹Î± ÏÎµÎ¹ÏÎ¬ ÏÎ±ÏÎ±ÎºÏÎ®ÏÏÎ½ ÏÎ·Ï ÎÎ»Î»Î·Î½Î¹ÎºÎ®Ï Î³Î»ÏÏÏÎ±Ï",
         new String[] { "Î¼Î¹Î±", "ÎµÎ¾Î±Î¹ÏÎµÏ", "ÎºÎ±Î»", "ÏÎ»Î¿ÏÏ", "ÏÎµÎ¹Ï", "ÏÎ±ÏÎ±ÎºÏÎ·Ï",
         "ÎµÎ»Î»Î·Î½Î¹Îº", "Î³Î»ÏÏÏ" });
     // Verify the correct analysis of small letters with diaeresis and the elimination
     // of punctuation marks
     assertAnalyzesTo(a, "Î ÏÎ¿ÏÏÎ½ÏÎ± (ÎºÎ±Î¹)     [ÏÎ¿Î»Î»Î±ÏÎ»Î­Ï] - ÎÎÎÎÎÎÎ£",
         new String[] { "ÏÏÎ¿Î¹Î¿Î½Ï", "ÏÎ¿Î»Î»Î±ÏÎ»", "Î±Î½Î±Î³Îº" });
     // Verify the correct analysis of capital accented letters and capital letters with diaeresis,
     // as well as the elimination of stop words
     assertAnalyzesTo(a, "Î Î¡ÎÎ«Î ÎÎÎÎ£ÎÎÎ£  ÎÏÎ¿Î³Î¿Ï, Î¿ Î¼ÎµÏÏÏÏ ÎºÎ±Î¹ Î¿Î¹ Î¬Î»Î»Î¿Î¹",
         new String[] { "ÏÏÎ¿ÏÏÎ¿Î¸ÎµÏ", "Î±ÏÎ¿Î³", "Î¼ÎµÏÏ", "Î±Î»Î»" });
   }
   
 	/**
 	 * Test the analysis of various greek strings.
 	 *
 	 * @throws Exception in case an error occurs
 	 * @deprecated (3.1) Remove this test when support for 3.0 is no longer needed
 	 */
   @Deprecated
 	public void testAnalyzerBWCompat() throws Exception {
 		Analyzer a = new GreekAnalyzer(Version.LUCENE_30);
 		// Verify the correct analysis of capitals and small accented letters
 		assertAnalyzesTo(a, "ÎÎ¯Î± ÎµÎ¾Î±Î¹ÏÎµÏÎ¹ÎºÎ¬ ÎºÎ±Î»Î® ÎºÎ±Î¹ ÏÎ»Î¿ÏÏÎ¹Î± ÏÎµÎ¹ÏÎ¬ ÏÎ±ÏÎ±ÎºÏÎ®ÏÏÎ½ ÏÎ·Ï ÎÎ»Î»Î·Î½Î¹ÎºÎ®Ï Î³Î»ÏÏÏÎ±Ï",
 				new String[] { "Î¼Î¹Î±", "ÎµÎ¾Î±Î¹ÏÎµÏÎ¹ÎºÎ±", "ÎºÎ±Î»Î·", "ÏÎ»Î¿ÏÏÎ¹Î±", "ÏÎµÎ¹ÏÎ±", "ÏÎ±ÏÎ±ÎºÏÎ·ÏÏÎ½",
 				"ÎµÎ»Î»Î·Î½Î¹ÎºÎ·Ï", "Î³Î»ÏÏÏÎ±Ï" });
 		// Verify the correct analysis of small letters with diaeresis and the elimination
 		// of punctuation marks
 		assertAnalyzesTo(a, "Î ÏÎ¿ÏÏÎ½ÏÎ± (ÎºÎ±Î¹)     [ÏÎ¿Î»Î»Î±ÏÎ»Î­Ï] - ÎÎÎÎÎÎÎ£",
 				new String[] { "ÏÏÎ¿Î¹Î¿Î½ÏÎ±", "ÏÎ¿Î»Î»Î±ÏÎ»ÎµÏ", "Î±Î½Î±Î³ÎºÎµÏ" });
 		// Verify the correct analysis of capital accented letters and capital letters with diaeresis,
 		// as well as the elimination of stop words
 		assertAnalyzesTo(a, "Î Î¡ÎÎ«Î ÎÎÎÎ£ÎÎÎ£  ÎÏÎ¿Î³Î¿Ï, Î¿ Î¼ÎµÏÏÏÏ ÎºÎ±Î¹ Î¿Î¹ Î¬Î»Î»Î¿Î¹",
 				new String[] { "ÏÏÎ¿ÏÏÎ¿Î¸ÎµÏÎµÎ¹Ï", "Î±ÏÎ¿Î³Î¿Ï", "Î¼ÎµÏÏÎ¿Ï", "Î±Î»Î»Î¿Î¹" });
 	}
 	
   public void testReusableTokenStream() throws Exception {
     Analyzer a = new GreekAnalyzer(TEST_VERSION_CURRENT);
     // Verify the correct analysis of capitals and small accented letters, and
     // stemming
     assertAnalyzesToReuse(a, "ÎÎ¯Î± ÎµÎ¾Î±Î¹ÏÎµÏÎ¹ÎºÎ¬ ÎºÎ±Î»Î® ÎºÎ±Î¹ ÏÎ»Î¿ÏÏÎ¹Î± ÏÎµÎ¹ÏÎ¬ ÏÎ±ÏÎ±ÎºÏÎ®ÏÏÎ½ ÏÎ·Ï ÎÎ»Î»Î·Î½Î¹ÎºÎ®Ï Î³Î»ÏÏÏÎ±Ï",
         new String[] { "Î¼Î¹Î±", "ÎµÎ¾Î±Î¹ÏÎµÏ", "ÎºÎ±Î»", "ÏÎ»Î¿ÏÏ", "ÏÎµÎ¹Ï", "ÏÎ±ÏÎ±ÎºÏÎ·Ï",
         "ÎµÎ»Î»Î·Î½Î¹Îº", "Î³Î»ÏÏÏ" });
     // Verify the correct analysis of small letters with diaeresis and the elimination
     // of punctuation marks
     assertAnalyzesToReuse(a, "Î ÏÎ¿ÏÏÎ½ÏÎ± (ÎºÎ±Î¹)     [ÏÎ¿Î»Î»Î±ÏÎ»Î­Ï] - ÎÎÎÎÎÎÎ£",
         new String[] { "ÏÏÎ¿Î¹Î¿Î½Ï", "ÏÎ¿Î»Î»Î±ÏÎ»", "Î±Î½Î±Î³Îº" });
     // Verify the correct analysis of capital accented letters and capital letters with diaeresis,
     // as well as the elimination of stop words
     assertAnalyzesToReuse(a, "Î Î¡ÎÎ«Î ÎÎÎÎ£ÎÎÎ£  ÎÏÎ¿Î³Î¿Ï, Î¿ Î¼ÎµÏÏÏÏ ÎºÎ±Î¹ Î¿Î¹ Î¬Î»Î»Î¿Î¹",
         new String[] { "ÏÏÎ¿ÏÏÎ¿Î¸ÎµÏ", "Î±ÏÎ¿Î³", "Î¼ÎµÏÏ", "Î±Î»Î»" });
   }
   
   /** blast some random strings through the analyzer */
   public void testRandomStrings() throws Exception {
    checkRandomData(random(), new GreekAnalyzer(TEST_VERSION_CURRENT), 10000*RANDOM_MULTIPLIER);
   }
 }
