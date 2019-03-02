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
 
 import java.io.Reader;
 
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.TokenStream;
 import org.apache.lucene.analysis.WhitespaceTokenizer;
 
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
		assertAnalyzesTo(new ThaiAnalyzer(), "à¹à¸à¸­à¸°à¸à¸´à¸§à¸¢à¸­à¸£à¹à¸à¹à¸à¸¡à¸ªà¹", 
 				new String[] { "à¹à¸", "à¸­à¸°à¸à¸´à¸§", "à¸¢à¸­", "à¸£à¹à¸", "à¹à¸à¸¡à¸ªà¹"},
 				new int[] { 0, 2, 7, 9, 12 },
 				new int[] { 2, 7, 9, 12, 17});
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
		assertAnalyzesTo(new ThaiAnalyzer(), "à¹à¸à¸­à¸°à¸à¸´à¸§à¸¢à¸­à¸£à¹à¸à¹à¸à¸¡à¸ªà¹ à¹à¹à¹", 
 				new String[] { "à¹à¸", "à¸­à¸°à¸à¸´à¸§", "à¸¢à¸­", "à¸£à¹à¸", "à¹à¸à¸¡à¸ªà¹", "à¹à¹à¹" },
 				new String[] { "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>" });
 	}
 	
 	/* correct testcase
 	public void testTokenType() throws Exception {
 		assertAnalyzesTo(new ThaiAnalyzer(), "à¹à¸à¸­à¸°à¸à¸´à¸§à¸¢à¸­à¸£à¹à¸à¹à¸à¸¡à¸ªà¹ à¹à¹à¹", 
 				new String[] { "à¹à¸", "à¸­à¸°à¸à¸´à¸§", "à¸¢à¸­", "à¸£à¹à¸", "à¹à¸à¸¡à¸ªà¹", "à¹à¹à¹" },
 				new String[] { "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<NUM>" });
 	}
 	*/
 
 	public void testAnalyzer() throws Exception {
		ThaiAnalyzer analyzer = new ThaiAnalyzer();
 	
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
 	
 	public void testReusableTokenStream() throws Exception {
	  ThaiAnalyzer analyzer = new ThaiAnalyzer();
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
 	
 	/**
 	 * subclass that acts just like whitespace analyzer for testing
 	 */
 	private class ThaiSubclassAnalyzer extends ThaiAnalyzer {
 	  public TokenStream tokenStream(String fieldName, Reader reader) {
 	    return new WhitespaceTokenizer(reader);
 	  }
 	}
 	
 	public void testLUCENE1678BWComp() throws Exception {
	  ThaiSubclassAnalyzer a = new ThaiSubclassAnalyzer();
 	  assertAnalyzesToReuse(a, "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ", new String[] { "à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ" });
 	}
 }
