 package org.apache.lucene.analysis.th;
 
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
 
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.junit.Assume;
 
 /**
  * Test case for ThaiAnalyzer, modified from TestFrenchAnalyzer
  *
  * @version   0.1
  */
 
 public class TestThaiAnalyzer extends BaseTokenStreamTestCase {
 	
 	/* 
 	 * testcase for offsets
 	 */
 	public void testOffsets() throws Exception {
	  Assume.assumeTrue(ThaiWordFilter.DBBI_AVAILABLE);
 		assertAnalyzesTo(new ThaiAnalyzer(TEST_VERSION_CURRENT), "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ", 
 		    new String[] { "à¸à¸²à¸£", "à¸à¸µà¹", "à¹à¸à¹", "à¸à¹à¸­à¸", "à¹à¸ªà¸à¸", "à¸§à¹à¸²", "à¸à¸²à¸", "à¸à¸µ" },
 				new int[] { 0, 3, 6, 9, 13, 17, 20, 23 },
 				new int[] { 3, 6, 9, 13, 17, 20, 23, 25 });
 	}
 	
 	
 	/*
 	 * Thai numeric tokens are typed as <ALPHANUM> instead of <NUM>.
 	 * This is really a problem with the interaction w/ StandardTokenizer, which is used by ThaiAnalyzer.
 	 * 
 	 * The issue is this: in StandardTokenizer the entire [:Thai:] block is specified in ALPHANUM (including punctuation, digits, etc)
 	 * Fix is easy: refine this spec to exclude thai punctuation and digits.
 	 * 
 	 * A better fix, that would also fix quite a few other languages would be to remove the thai hack.
 	 * Instead, allow the definition of alphanum to include relevant categories like nonspacing marks!
 	 */
 	public void testBuggyTokenType() throws Exception {
	  Assume.assumeTrue(ThaiWordFilter.DBBI_AVAILABLE);
 		assertAnalyzesTo(new ThaiAnalyzer(TEST_VERSION_CURRENT), "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ à¹à¹à¹", 
 		    new String[] { "à¸à¸²à¸£", "à¸à¸µà¹", "à¹à¸à¹", "à¸à¹à¸­à¸", "à¹à¸ªà¸à¸", "à¸§à¹à¸²", "à¸à¸²à¸", "à¸à¸µ", "à¹à¹à¹" },
 				new String[] { "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", 
 		     "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>" });
 	}
 	
 	/* correct testcase
 	public void testTokenType() throws Exception {
     assertAnalyzesTo(new ThaiAnalyzer(TEST_VERSION_CURRENT), "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ à¹à¹à¹", 
         new String[] { "à¸à¸²à¸£", "à¸à¸µà¹", "à¹à¸à¹", "à¸à¹à¸­à¸", "à¹à¸ªà¸à¸", "à¸§à¹à¸²", "à¸à¸²à¸", "à¸à¸µ", "à¹à¹à¹" },
         new String[] { "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", 
          "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<NUM>" });
 	}
 	*/
 
 	public void testAnalyzer() throws Exception {
	  Assume.assumeTrue(ThaiWordFilter.DBBI_AVAILABLE);
 		ThaiAnalyzer analyzer = new ThaiAnalyzer(TEST_VERSION_CURRENT);
 	
 		assertAnalyzesTo(analyzer, "", new String[] {});
 
 		assertAnalyzesTo(
 			analyzer,
 			"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ",
 			new String[] { "à¸à¸²à¸£", "à¸à¸µà¹", "à¹à¸à¹", "à¸à¹à¸­à¸", "à¹à¸ªà¸à¸", "à¸§à¹à¸²", "à¸à¸²à¸", "à¸à¸µ"});
 
 		assertAnalyzesTo(
 			analyzer,
 			"à¸à¸£à¸´à¸©à¸±à¸à¸à¸·à¹à¸­ XY&Z - à¸à¸¸à¸¢à¸à¸±à¸ xyz@demo.com",
 			new String[] { "à¸à¸£à¸´à¸©à¸±à¸", "à¸à¸·à¹à¸­", "xy&z", "à¸à¸¸à¸¢", "à¸à¸±à¸", "xyz@demo.com" });
 
     // English stop words
 		assertAnalyzesTo(
 			analyzer,
 			"à¸à¸£à¸°à¹à¸¢à¸à¸§à¹à¸² The quick brown fox jumped over the lazy dogs",
 			new String[] { "à¸à¸£à¸°à¹à¸¢à¸", "à¸§à¹à¸²", "quick", "brown", "fox", "jumped", "over", "lazy", "dogs" });
 	}
 	
 	/*
 	 * Test that position increments are adjusted correctly for stopwords.
 	 */
 	public void testPositionIncrements() throws Exception {
	  Assume.assumeTrue(ThaiWordFilter.DBBI_AVAILABLE);
 	  ThaiAnalyzer analyzer = new ThaiAnalyzer(TEST_VERSION_CURRENT);
 
     assertAnalyzesTo(new ThaiAnalyzer(TEST_VERSION_CURRENT), "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸ the à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ", 
         new String[] { "à¸à¸²à¸£", "à¸à¸µà¹", "à¹à¸à¹", "à¸à¹à¸­à¸", "à¹à¸ªà¸à¸", "à¸§à¹à¸²", "à¸à¸²à¸", "à¸à¸µ" },
         new int[] { 0, 3, 6, 9, 18, 22, 25, 28 },
         new int[] { 3, 6, 9, 13, 22, 25, 28, 30 },
         new int[] { 1, 1, 1, 1, 2, 1, 1, 1 });
 	 
 	  // case that a stopword is adjacent to thai text, with no whitespace
     assertAnalyzesTo(new ThaiAnalyzer(TEST_VERSION_CURRENT), "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸the à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ", 
         new String[] { "à¸à¸²à¸£", "à¸à¸µà¹", "à¹à¸à¹", "à¸à¹à¸­à¸", "à¹à¸ªà¸à¸", "à¸§à¹à¸²", "à¸à¸²à¸", "à¸à¸µ" },
         new int[] { 0, 3, 6, 9, 17, 21, 24, 27 },
         new int[] { 3, 6, 9, 13, 21, 24, 27, 29 },
         new int[] { 1, 1, 1, 1, 2, 1, 1, 1 });
 	}
 	
 	public void testReusableTokenStream() throws Exception {
	  Assume.assumeTrue(ThaiWordFilter.DBBI_AVAILABLE);
 	  ThaiAnalyzer analyzer = new ThaiAnalyzer(TEST_VERSION_CURRENT);
 	  assertAnalyzesToReuse(analyzer, "", new String[] {});
 
       assertAnalyzesToReuse(
           analyzer,
           "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ",
           new String[] { "à¸à¸²à¸£", "à¸à¸µà¹", "à¹à¸à¹", "à¸à¹à¸­à¸", "à¹à¸ªà¸à¸", "à¸§à¹à¸²", "à¸à¸²à¸", "à¸à¸µ"});
 
       assertAnalyzesToReuse(
           analyzer,
           "à¸à¸£à¸´à¸©à¸±à¸à¸à¸·à¹à¸­ XY&Z - à¸à¸¸à¸¢à¸à¸±à¸ xyz@demo.com",
           new String[] { "à¸à¸£à¸´à¸©à¸±à¸", "à¸à¸·à¹à¸­", "xy&z", "à¸à¸¸à¸¢", "à¸à¸±à¸", "xyz@demo.com" });
 	}
 }
