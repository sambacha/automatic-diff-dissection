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
 
 import java.io.IOException;
 import java.io.StringReader;
 
 import org.apache.lucene.analysis.BaseTokenStreamTestCase;
 import org.apache.lucene.analysis.ar.ArabicLetterTokenizer;
 
 /**
 * Test the Arabic Normalization Filter
  * 
  */
 public class TestPersianNormalizationFilter extends BaseTokenStreamTestCase {
 
   public void testFarsiYeh() throws IOException {
     check("ÙØ§Û", "ÙØ§Ù");
   }
 
   public void testYehBarree() throws IOException {
     check("ÙØ§Û", "ÙØ§Ù");
   }
 
   public void testKeheh() throws IOException {
     check("Ú©Ø´Ø§ÙØ¯Ù", "ÙØ´Ø§ÙØ¯Ù");
   }
 
   public void testHehYeh() throws IOException {
     check("ÙØªØ§Ø¨Û", "ÙØªØ§Ø¨Ù");
   }
 
   public void testHehHamzaAbove() throws IOException {
     check("ÙØªØ§Ø¨ÙÙ", "ÙØªØ§Ø¨Ù");
   }
 
   public void testHehGoal() throws IOException {
     check("Ø²Ø§Ø¯Û", "Ø²Ø§Ø¯Ù");
   }
 
   private void check(final String input, final String expected) throws IOException {
     ArabicLetterTokenizer tokenStream = new ArabicLetterTokenizer(
         new StringReader(input));
     PersianNormalizationFilter filter = new PersianNormalizationFilter(
         tokenStream);
     assertTokenStreamContents(filter, new String[]{expected});
   }
 
 }
