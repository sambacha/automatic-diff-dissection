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
 
 import java.io.IOException;
 import java.io.StringReader;
 
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
 import org.apache.lucene.util.Version;
 
 /**
  * Test the Arabic Normalization Filter
  *
  */
 public class TestArabicNormalizationFilter extends BaseTokenStreamTestCase {
 
   public void testAlifMadda() throws IOException {
     check("Ø¢Ø¬Ù", "Ø§Ø¬Ù");
   }
   
   public void testAlifHamzaAbove() throws IOException {
     check("Ø£Ø­ÙØ¯", "Ø§Ø­ÙØ¯");
   }
   
   public void testAlifHamzaBelow() throws IOException {
     check("Ø¥Ø¹Ø§Ø°", "Ø§Ø¹Ø§Ø°");
   }
   
   public void testAlifMaksura() throws IOException {
     check("Ø¨ÙÙ", "Ø¨ÙÙ");
   }
 
   public void testTehMarbuta() throws IOException {
     check("ÙØ§Ø·ÙØ©", "ÙØ§Ø·ÙÙ");
   }
   
   public void testTatweel() throws IOException {
     check("Ø±ÙØ¨Ø±ÙÙÙÙÙØª", "Ø±ÙØ¨Ø±Øª");
   }
   
   public void testFatha() throws IOException {
     check("ÙÙØ¨ÙØ§", "ÙØ¨ÙØ§");
   }
   
   public void testKasra() throws IOException {
     check("Ø¹ÙÙÙ", "Ø¹ÙÙ");
   }
   
   public void testDamma() throws IOException {
     check("Ø¨ÙÙØ§Øª", "Ø¨ÙØ§Øª");
   }
   
   public void testFathatan() throws IOException {
     check("ÙÙØ¯Ø§Ù", "ÙÙØ¯Ø§");
   }
   
   public void testKasratan() throws IOException {
     check("ÙÙØ¯Ù", "ÙÙØ¯");
   }
   
   public void testDammatan() throws IOException {
     check("ÙÙØ¯Ù", "ÙÙØ¯");
   }  
   
   public void testSukun() throws IOException {
     check("ÙÙÙØ³ÙÙ", "ÙÙØ³ÙÙ");
   }
   
   public void testShaddah() throws IOException {
     check("ÙØªÙÙÙ", "ÙØªÙÙ");
   }  
   
   private void check(final String input, final String expected) throws IOException {
     ArabicLetterTokenizer tokenStream = new ArabicLetterTokenizer(Version.LUCENE_CURRENT, new StringReader(input));
     ArabicNormalizationFilter filter = new ArabicNormalizationFilter(tokenStream);
     assertTokenStreamContents(filter, new String[]{expected});
   }
 
 }
