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
 
 package org.apache.lucene.analysis.reverse;
 
 import java.io.StringReader;
 
 import org.apache.lucene.analysis.TokenStream;
 import org.apache.lucene.analysis.WhitespaceTokenizer;
 import org.apache.lucene.analysis.tokenattributes.TermAttribute;
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.util.Version;
 
 public class TestReverseStringFilter extends BaseTokenStreamTestCase {
   public void testFilter() throws Exception {
     TokenStream stream = new WhitespaceTokenizer(
         new StringReader("Do have a nice day"));     // 1-4 length string
     ReverseStringFilter filter = new ReverseStringFilter(stream);
     TermAttribute text = filter.getAttribute(TermAttribute.class);
     assertTrue(filter.incrementToken());
     assertEquals("oD", text.term());
     assertTrue(filter.incrementToken());
     assertEquals("evah", text.term());
     assertTrue(filter.incrementToken());
     assertEquals("a", text.term());
     assertTrue(filter.incrementToken());
     assertEquals("ecin", text.term());
     assertTrue(filter.incrementToken());
     assertEquals("yad", text.term());
     assertFalse(filter.incrementToken());
   }
   
   public void testFilterWithMark() throws Exception {
     TokenStream stream = new WhitespaceTokenizer(new StringReader(
         "Do have a nice day")); // 1-4 length string
     ReverseStringFilter filter = new ReverseStringFilter(stream, '\u0001');
     TermAttribute text = filter
         .getAttribute(TermAttribute.class);
     assertTrue(filter.incrementToken());
     assertEquals("\u0001oD", text.term());
     assertTrue(filter.incrementToken());
     assertEquals("\u0001evah", text.term());
     assertTrue(filter.incrementToken());
     assertEquals("\u0001a", text.term());
     assertTrue(filter.incrementToken());
     assertEquals("\u0001ecin", text.term());
     assertTrue(filter.incrementToken());
     assertEquals("\u0001yad", text.term());
     assertFalse(filter.incrementToken());
   }
 
   public void testReverseString() throws Exception {
     assertEquals( "A", ReverseStringFilter.reverse( "A" ) );
     assertEquals( "BA", ReverseStringFilter.reverse( "AB" ) );
     assertEquals( "CBA", ReverseStringFilter.reverse( "ABC" ) );
   }
   
   public void testReverseChar() throws Exception {
     char[] buffer = { 'A', 'B', 'C', 'D', 'E', 'F' };
     ReverseStringFilter.reverse( buffer, 2, 3 );
     assertEquals( "ABEDCF", new String( buffer ) );
   }
  
  /**
   * Test the broken 3.0 behavior, for back compat
   */
  public void testBackCompat() throws Exception {
    assertEquals("\uDF05\uD866\uDF05\uD866", ReverseStringFilter.reverse("ð©¬ð©¬"));
  }
  
  public void testReverseSupplementary() throws Exception {
    // supplementary at end
    assertEquals("ð©¬è±éä¹æ¯ç", ReverseStringFilter.reverse(Version.LUCENE_CURRENT, "çæ¯ä¹éè±ð©¬"));
    // supplementary at end - 1
    assertEquals("að©¬è±éä¹æ¯ç", ReverseStringFilter.reverse(Version.LUCENE_CURRENT, "çæ¯ä¹éè±ð©¬a"));
    // supplementary at start
    assertEquals("fedcbað©¬", ReverseStringFilter.reverse(Version.LUCENE_CURRENT, "ð©¬abcdef"));
    // supplementary at start + 1
    assertEquals("fedcbað©¬z", ReverseStringFilter.reverse(Version.LUCENE_CURRENT, "zð©¬abcdef"));
    // supplementary medial
    assertEquals("gfeð©¬dcba", ReverseStringFilter.reverse(Version.LUCENE_CURRENT, "abcdð©¬efg"));
  }

  public void testReverseSupplementaryChar() throws Exception {
    // supplementary at end
    char[] buffer = "abcçæ¯ä¹éè±ð©¬".toCharArray();
    ReverseStringFilter.reverse(Version.LUCENE_CURRENT, buffer, 3, 7);
    assertEquals("abcð©¬è±éä¹æ¯ç", new String(buffer));
    // supplementary at end - 1
    buffer = "abcçæ¯ä¹éè±ð©¬d".toCharArray();
    ReverseStringFilter.reverse(Version.LUCENE_CURRENT, buffer, 3, 8);
    assertEquals("abcdð©¬è±éä¹æ¯ç", new String(buffer));
    // supplementary at start
    buffer = "abcð©¬çæ¯ä¹éè±".toCharArray();
    ReverseStringFilter.reverse(Version.LUCENE_CURRENT, buffer, 3, 7);
    assertEquals("abcè±éä¹æ¯çð©¬", new String(buffer));
    // supplementary at start + 1
    buffer = "abcdð©¬çæ¯ä¹éè±".toCharArray();
    ReverseStringFilter.reverse(Version.LUCENE_CURRENT, buffer, 3, 8);
    assertEquals("abcè±éä¹æ¯çð©¬d", new String(buffer));
    // supplementary medial
    buffer = "abcçæ¯ð©¬def".toCharArray();
    ReverseStringFilter.reverse(Version.LUCENE_CURRENT, buffer, 3, 7);
    assertEquals("abcfedð©¬æ¯ç", new String(buffer));
  }
 }
