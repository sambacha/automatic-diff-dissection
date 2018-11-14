 package org.apache.lucene.analysis.miscellaneous;
 
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
import org.apache.lucene.analysis.MockTokenizer;
 import org.apache.lucene.analysis.TokenStream;
 import org.apache.lucene.analysis.core.WhitespaceTokenizer;
 import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
 import java.io.StringReader;
 import java.util.List;
 import java.util.ArrayList;
 import java.util.Iterator;
 
 public class TestASCIIFoldingFilter extends BaseTokenStreamTestCase {
 
   // testLain1Accents() is a copy of TestLatin1AccentFilter.testU().
   public void testLatin1Accents() throws Exception {
    TokenStream stream = new MockTokenizer(new StringReader
       ("Des mot clÃ©s Ã LA CHAÃNE Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ä² Ã Ã"
       +" Ã Ã Ã Ã Ã Ã Å Ã Ã Ã Ã Ã Ã Å¸ Ã  Ã¡ Ã¢ Ã£ Ã¤ Ã¥ Ã¦ Ã§ Ã¨ Ã© Ãª Ã« Ã¬ Ã­ Ã® Ã¯ Ä³"
      +" Ã° Ã± Ã² Ã³ Ã´ Ãµ Ã¶ Ã¸ Å Ã Ã¾ Ã¹ Ãº Ã» Ã¼ Ã½ Ã¿ ï¬ ï¬"), MockTokenizer.WHITESPACE, false);
     ASCIIFoldingFilter filter = new ASCIIFoldingFilter(stream);
 
     CharTermAttribute termAtt = filter.getAttribute(CharTermAttribute.class);
    filter.reset();
     assertTermEquals("Des", filter, termAtt);
     assertTermEquals("mot", filter, termAtt);
     assertTermEquals("cles", filter, termAtt);
     assertTermEquals("A", filter, termAtt);
     assertTermEquals("LA", filter, termAtt);
     assertTermEquals("CHAINE", filter, termAtt);
     assertTermEquals("A", filter, termAtt);
     assertTermEquals("A", filter, termAtt);
     assertTermEquals("A", filter, termAtt);
     assertTermEquals("A", filter, termAtt);
     assertTermEquals("A", filter, termAtt);
     assertTermEquals("A", filter, termAtt);
     assertTermEquals("AE", filter, termAtt);
     assertTermEquals("C", filter, termAtt);
     assertTermEquals("E", filter, termAtt);
     assertTermEquals("E", filter, termAtt);
     assertTermEquals("E", filter, termAtt);
     assertTermEquals("E", filter, termAtt);
     assertTermEquals("I", filter, termAtt);
     assertTermEquals("I", filter, termAtt);
     assertTermEquals("I", filter, termAtt);
     assertTermEquals("I", filter, termAtt);
     assertTermEquals("IJ", filter, termAtt);
     assertTermEquals("D", filter, termAtt);
     assertTermEquals("N", filter, termAtt);
     assertTermEquals("O", filter, termAtt);
     assertTermEquals("O", filter, termAtt);
     assertTermEquals("O", filter, termAtt);
     assertTermEquals("O", filter, termAtt);
     assertTermEquals("O", filter, termAtt);
     assertTermEquals("O", filter, termAtt);
     assertTermEquals("OE", filter, termAtt);
     assertTermEquals("TH", filter, termAtt);
     assertTermEquals("U", filter, termAtt);
     assertTermEquals("U", filter, termAtt);
     assertTermEquals("U", filter, termAtt);
     assertTermEquals("U", filter, termAtt);
     assertTermEquals("Y", filter, termAtt);
     assertTermEquals("Y", filter, termAtt);
     assertTermEquals("a", filter, termAtt);
     assertTermEquals("a", filter, termAtt);
     assertTermEquals("a", filter, termAtt);
     assertTermEquals("a", filter, termAtt);
     assertTermEquals("a", filter, termAtt);
     assertTermEquals("a", filter, termAtt);
     assertTermEquals("ae", filter, termAtt);
     assertTermEquals("c", filter, termAtt);
     assertTermEquals("e", filter, termAtt);
     assertTermEquals("e", filter, termAtt);
     assertTermEquals("e", filter, termAtt);
     assertTermEquals("e", filter, termAtt);
     assertTermEquals("i", filter, termAtt);
     assertTermEquals("i", filter, termAtt);
     assertTermEquals("i", filter, termAtt);
     assertTermEquals("i", filter, termAtt);
     assertTermEquals("ij", filter, termAtt);
     assertTermEquals("d", filter, termAtt);
     assertTermEquals("n", filter, termAtt);
     assertTermEquals("o", filter, termAtt);
     assertTermEquals("o", filter, termAtt);
     assertTermEquals("o", filter, termAtt);
     assertTermEquals("o", filter, termAtt);
     assertTermEquals("o", filter, termAtt);
     assertTermEquals("o", filter, termAtt);
     assertTermEquals("oe", filter, termAtt);
     assertTermEquals("ss", filter, termAtt);
     assertTermEquals("th", filter, termAtt);
     assertTermEquals("u", filter, termAtt);
     assertTermEquals("u", filter, termAtt);
     assertTermEquals("u", filter, termAtt);
     assertTermEquals("u", filter, termAtt);
     assertTermEquals("y", filter, termAtt);
     assertTermEquals("y", filter, termAtt);
     assertTermEquals("fi", filter, termAtt);
     assertTermEquals("fl", filter, termAtt);
     assertFalse(filter.incrementToken());
   }
 
 
   // The following Perl script generated the foldings[] array automatically
   // from ASCIIFoldingFilter.java:
   //
   //    ============== begin get.test.cases.pl ==============
   //
   //    use strict;
   //    use warnings;
   //
   //    my $file = "ASCIIFoldingFilter.java";
   //    my $output = "testcases.txt";
   //    my %codes = ();
   //    my $folded = '';
   //
   //    open IN, "<:utf8", $file || die "Error opening input file '$file': $!";
   //    open OUT, ">:utf8", $output || die "Error opening output file '$output': $!";
   //
   //    while (my $line = <IN>) {
   //      chomp($line);
   //      # case '\u0133': // <char> <maybe URL> [ description ]
   //      if ($line =~ /case\s+'\\u(....)':.*\[([^\]]+)\]/) {
   //        my $code = $1;
   //        my $desc = $2;
   //        $codes{$code} = $desc;
   //      }
   //      # output[outputPos++] = 'A';
   //      elsif ($line =~ /output\[outputPos\+\+\] = '(.+)';/) {
   //        my $output_char = $1;
   //        $folded .= $output_char;
   //      }
   //      elsif ($line =~ /break;/ && length($folded) > 0) {
   //        my $first = 1;
   //        for my $code (sort { hex($a) <=> hex($b) } keys %codes) {
   //          my $desc = $codes{$code};
   //          print OUT '      ';
   //          print OUT '+ ' if (not $first);
   //          $first = 0;
   //          print OUT '"', chr(hex($code)), qq!"  // U+$code: $desc\n!;
   //        }
   //        print OUT qq!      ,"$folded", // Folded result\n\n!;
   //        %codes = ();
   //        $folded = '';
   //      }
   //    }
   //    close OUT;
   //
   //    ============== end get.test.cases.pl ==============
   //
   public void testAllFoldings() throws Exception {
     // Alternating strings of:
     //   1. All non-ASCII characters to be folded, concatenated together as a
     //      single string.
     //   2. The string of ASCII characters to which each of the above
     //      characters should be folded.
     String[] foldings = {
       "Ã"  // U+00C0: LATIN CAPITAL LETTER A WITH GRAVE
       + "Ã"  // U+00C1: LATIN CAPITAL LETTER A WITH ACUTE
       + "Ã"  // U+00C2: LATIN CAPITAL LETTER A WITH CIRCUMFLEX
       + "Ã"  // U+00C3: LATIN CAPITAL LETTER A WITH TILDE
       + "Ã"  // U+00C4: LATIN CAPITAL LETTER A WITH DIAERESIS
       + "Ã"  // U+00C5: LATIN CAPITAL LETTER A WITH RING ABOVE
       + "Ä"  // U+0100: LATIN CAPITAL LETTER A WITH MACRON
       + "Ä"  // U+0102: LATIN CAPITAL LETTER A WITH BREVE
       + "Ä"  // U+0104: LATIN CAPITAL LETTER A WITH OGONEK
       + "Æ"  // U+018F: LATIN CAPITAL LETTER SCHWA
       + "Ç"  // U+01CD: LATIN CAPITAL LETTER A WITH CARON
       + "Ç"  // U+01DE: LATIN CAPITAL LETTER A WITH DIAERESIS AND MACRON
       + "Ç "  // U+01E0: LATIN CAPITAL LETTER A WITH DOT ABOVE AND MACRON
       + "Çº"  // U+01FA: LATIN CAPITAL LETTER A WITH RING ABOVE AND ACUTE
       + "È"  // U+0200: LATIN CAPITAL LETTER A WITH DOUBLE GRAVE
       + "È"  // U+0202: LATIN CAPITAL LETTER A WITH INVERTED BREVE
       + "È¦"  // U+0226: LATIN CAPITAL LETTER A WITH DOT ABOVE
       + "Èº"  // U+023A: LATIN CAPITAL LETTER A WITH STROKE
       + "á´"  // U+1D00: LATIN LETTER SMALL CAPITAL A
       + "á¸"  // U+1E00: LATIN CAPITAL LETTER A WITH RING BELOW
       + "áº "  // U+1EA0: LATIN CAPITAL LETTER A WITH DOT BELOW
       + "áº¢"  // U+1EA2: LATIN CAPITAL LETTER A WITH HOOK ABOVE
       + "áº¤"  // U+1EA4: LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND ACUTE
       + "áº¦"  // U+1EA6: LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND GRAVE
       + "áº¨"  // U+1EA8: LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE
       + "áºª"  // U+1EAA: LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND TILDE
       + "áº¬"  // U+1EAC: LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND DOT BELOW
       + "áº®"  // U+1EAE: LATIN CAPITAL LETTER A WITH BREVE AND ACUTE
       + "áº°"  // U+1EB0: LATIN CAPITAL LETTER A WITH BREVE AND GRAVE
       + "áº²"  // U+1EB2: LATIN CAPITAL LETTER A WITH BREVE AND HOOK ABOVE
       + "áº´"  // U+1EB4: LATIN CAPITAL LETTER A WITH BREVE AND TILDE
       + "áº¶"  // U+1EB6: LATIN CAPITAL LETTER A WITH BREVE AND DOT BELOW
       + "â¶"  // U+24B6: CIRCLED LATIN CAPITAL LETTER A
       + "ï¼¡"  // U+FF21: FULLWIDTH LATIN CAPITAL LETTER A
       ,"A", // Folded result
 
        "Ã "  // U+00E0: LATIN SMALL LETTER A WITH GRAVE
        + "Ã¡"  // U+00E1: LATIN SMALL LETTER A WITH ACUTE
        + "Ã¢"  // U+00E2: LATIN SMALL LETTER A WITH CIRCUMFLEX
        + "Ã£"  // U+00E3: LATIN SMALL LETTER A WITH TILDE
        + "Ã¤"  // U+00E4: LATIN SMALL LETTER A WITH DIAERESIS
        + "Ã¥"  // U+00E5: LATIN SMALL LETTER A WITH RING ABOVE
        + "Ä"  // U+0101: LATIN SMALL LETTER A WITH MACRON
        + "Ä"  // U+0103: LATIN SMALL LETTER A WITH BREVE
        + "Ä"  // U+0105: LATIN SMALL LETTER A WITH OGONEK
        + "Ç"  // U+01CE: LATIN SMALL LETTER A WITH CARON
        + "Ç"  // U+01DF: LATIN SMALL LETTER A WITH DIAERESIS AND MACRON
        + "Ç¡"  // U+01E1: LATIN SMALL LETTER A WITH DOT ABOVE AND MACRON
        + "Ç»"  // U+01FB: LATIN SMALL LETTER A WITH RING ABOVE AND ACUTE
        + "È"  // U+0201: LATIN SMALL LETTER A WITH DOUBLE GRAVE
        + "È"  // U+0203: LATIN SMALL LETTER A WITH INVERTED BREVE
        + "È§"  // U+0227: LATIN SMALL LETTER A WITH DOT ABOVE
        + "É"  // U+0250: LATIN SMALL LETTER TURNED A
        + "É"  // U+0259: LATIN SMALL LETTER SCHWA
        + "É"  // U+025A: LATIN SMALL LETTER SCHWA WITH HOOK
        + "á¶"  // U+1D8F: LATIN SMALL LETTER A WITH RETROFLEX HOOK
        + "á¸"  // U+1E01: LATIN SMALL LETTER A WITH RING BELOW
        + "á¶"  // U+1D95: LATIN SMALL LETTER SCHWA WITH RETROFLEX HOOK
        + "áº"  // U+1E9A: LATIN SMALL LETTER A WITH RIGHT HALF RING
        + "áº¡"  // U+1EA1: LATIN SMALL LETTER A WITH DOT BELOW
        + "áº£"  // U+1EA3: LATIN SMALL LETTER A WITH HOOK ABOVE
        + "áº¥"  // U+1EA5: LATIN SMALL LETTER A WITH CIRCUMFLEX AND ACUTE
        + "áº§"  // U+1EA7: LATIN SMALL LETTER A WITH CIRCUMFLEX AND GRAVE
        + "áº©"  // U+1EA9: LATIN SMALL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE
        + "áº«"  // U+1EAB: LATIN SMALL LETTER A WITH CIRCUMFLEX AND TILDE
        + "áº­"  // U+1EAD: LATIN SMALL LETTER A WITH CIRCUMFLEX AND DOT BELOW
        + "áº¯"  // U+1EAF: LATIN SMALL LETTER A WITH BREVE AND ACUTE
        + "áº±"  // U+1EB1: LATIN SMALL LETTER A WITH BREVE AND GRAVE
        + "áº³"  // U+1EB3: LATIN SMALL LETTER A WITH BREVE AND HOOK ABOVE
        + "áºµ"  // U+1EB5: LATIN SMALL LETTER A WITH BREVE AND TILDE
        + "áº·"  // U+1EB7: LATIN SMALL LETTER A WITH BREVE AND DOT BELOW
        + "â"  // U+2090: LATIN SUBSCRIPT SMALL LETTER A
        + "â"  // U+2094: LATIN SUBSCRIPT SMALL LETTER SCHWA
        + "â"  // U+24D0: CIRCLED LATIN SMALL LETTER A
        + "â±¥"  // U+2C65: LATIN SMALL LETTER A WITH STROKE
        + "â±¯"  // U+2C6F: LATIN CAPITAL LETTER TURNED A
        + "ï½"  // U+FF41: FULLWIDTH LATIN SMALL LETTER A
       ,"a", // Folded result
 
        "ê²"  // U+A732: LATIN CAPITAL LETTER AA
       ,"AA", // Folded result
 
        "Ã"  // U+00C6: LATIN CAPITAL LETTER AE
        + "Ç¢"  // U+01E2: LATIN CAPITAL LETTER AE WITH MACRON
        + "Ç¼"  // U+01FC: LATIN CAPITAL LETTER AE WITH ACUTE
        + "á´"  // U+1D01: LATIN LETTER SMALL CAPITAL AE
       ,"AE", // Folded result
 
        "ê´"  // U+A734: LATIN CAPITAL LETTER AO
       ,"AO", // Folded result
 
        "ê¶"  // U+A736: LATIN CAPITAL LETTER AU
       ,"AU", // Folded result
 
        "ê¸"  // U+A738: LATIN CAPITAL LETTER AV
        + "êº"  // U+A73A: LATIN CAPITAL LETTER AV WITH HORIZONTAL BAR
       ,"AV", // Folded result
 
        "ê¼"  // U+A73C: LATIN CAPITAL LETTER AY
       ,"AY", // Folded result
 
        "â"  // U+249C: PARENTHESIZED LATIN SMALL LETTER A
       ,"(a)", // Folded result
 
        "ê³"  // U+A733: LATIN SMALL LETTER AA
       ,"aa", // Folded result
 
        "Ã¦"  // U+00E6: LATIN SMALL LETTER AE
        + "Ç£"  // U+01E3: LATIN SMALL LETTER AE WITH MACRON
        + "Ç½"  // U+01FD: LATIN SMALL LETTER AE WITH ACUTE
        + "á´"  // U+1D02: LATIN SMALL LETTER TURNED AE
       ,"ae", // Folded result
 
        "êµ"  // U+A735: LATIN SMALL LETTER AO
       ,"ao", // Folded result
 
        "ê·"  // U+A737: LATIN SMALL LETTER AU
       ,"au", // Folded result
 
        "ê¹"  // U+A739: LATIN SMALL LETTER AV
        + "ê»"  // U+A73B: LATIN SMALL LETTER AV WITH HORIZONTAL BAR
       ,"av", // Folded result
 
        "ê½"  // U+A73D: LATIN SMALL LETTER AY
       ,"ay", // Folded result
 
        "Æ"  // U+0181: LATIN CAPITAL LETTER B WITH HOOK
        + "Æ"  // U+0182: LATIN CAPITAL LETTER B WITH TOPBAR
        + "É"  // U+0243: LATIN CAPITAL LETTER B WITH STROKE
        + "Ê"  // U+0299: LATIN LETTER SMALL CAPITAL B
        + "á´"  // U+1D03: LATIN LETTER SMALL CAPITAL BARRED B
        + "á¸"  // U+1E02: LATIN CAPITAL LETTER B WITH DOT ABOVE
        + "á¸"  // U+1E04: LATIN CAPITAL LETTER B WITH DOT BELOW
        + "á¸"  // U+1E06: LATIN CAPITAL LETTER B WITH LINE BELOW
        + "â·"  // U+24B7: CIRCLED LATIN CAPITAL LETTER B
        + "ï¼¢"  // U+FF22: FULLWIDTH LATIN CAPITAL LETTER B
       ,"B", // Folded result
 
        "Æ"  // U+0180: LATIN SMALL LETTER B WITH STROKE
        + "Æ"  // U+0183: LATIN SMALL LETTER B WITH TOPBAR
        + "É"  // U+0253: LATIN SMALL LETTER B WITH HOOK
        + "áµ¬"  // U+1D6C: LATIN SMALL LETTER B WITH MIDDLE TILDE
        + "á¶"  // U+1D80: LATIN SMALL LETTER B WITH PALATAL HOOK
        + "á¸"  // U+1E03: LATIN SMALL LETTER B WITH DOT ABOVE
        + "á¸"  // U+1E05: LATIN SMALL LETTER B WITH DOT BELOW
        + "á¸"  // U+1E07: LATIN SMALL LETTER B WITH LINE BELOW
        + "â"  // U+24D1: CIRCLED LATIN SMALL LETTER B
        + "ï½"  // U+FF42: FULLWIDTH LATIN SMALL LETTER B
       ,"b", // Folded result
 
        "â"  // U+249D: PARENTHESIZED LATIN SMALL LETTER B
       ,"(b)", // Folded result
 
        "Ã"  // U+00C7: LATIN CAPITAL LETTER C WITH CEDILLA
        + "Ä"  // U+0106: LATIN CAPITAL LETTER C WITH ACUTE
        + "Ä"  // U+0108: LATIN CAPITAL LETTER C WITH CIRCUMFLEX
        + "Ä"  // U+010A: LATIN CAPITAL LETTER C WITH DOT ABOVE
        + "Ä"  // U+010C: LATIN CAPITAL LETTER C WITH CARON
        + "Æ"  // U+0187: LATIN CAPITAL LETTER C WITH HOOK
        + "È»"  // U+023B: LATIN CAPITAL LETTER C WITH STROKE
        + "Ê"  // U+0297: LATIN LETTER STRETCHED C
        + "á´"  // U+1D04: LATIN LETTER SMALL CAPITAL C
        + "á¸"  // U+1E08: LATIN CAPITAL LETTER C WITH CEDILLA AND ACUTE
        + "â¸"  // U+24B8: CIRCLED LATIN CAPITAL LETTER C
        + "ï¼£"  // U+FF23: FULLWIDTH LATIN CAPITAL LETTER C
       ,"C", // Folded result
 
        "Ã§"  // U+00E7: LATIN SMALL LETTER C WITH CEDILLA
        + "Ä"  // U+0107: LATIN SMALL LETTER C WITH ACUTE
        + "Ä"  // U+0109: LATIN SMALL LETTER C WITH CIRCUMFLEX
        + "Ä"  // U+010B: LATIN SMALL LETTER C WITH DOT ABOVE
        + "Ä"  // U+010D: LATIN SMALL LETTER C WITH CARON
        + "Æ"  // U+0188: LATIN SMALL LETTER C WITH HOOK
        + "È¼"  // U+023C: LATIN SMALL LETTER C WITH STROKE
        + "É"  // U+0255: LATIN SMALL LETTER C WITH CURL
        + "á¸"  // U+1E09: LATIN SMALL LETTER C WITH CEDILLA AND ACUTE
        + "â"  // U+2184: LATIN SMALL LETTER REVERSED C
        + "â"  // U+24D2: CIRCLED LATIN SMALL LETTER C
        + "ê¾"  // U+A73E: LATIN CAPITAL LETTER REVERSED C WITH DOT
        + "ê¿"  // U+A73F: LATIN SMALL LETTER REVERSED C WITH DOT
        + "ï½"  // U+FF43: FULLWIDTH LATIN SMALL LETTER C
       ,"c", // Folded result
 
        "â"  // U+249E: PARENTHESIZED LATIN SMALL LETTER C
       ,"(c)", // Folded result
 
        "Ã"  // U+00D0: LATIN CAPITAL LETTER ETH
        + "Ä"  // U+010E: LATIN CAPITAL LETTER D WITH CARON
        + "Ä"  // U+0110: LATIN CAPITAL LETTER D WITH STROKE
        + "Æ"  // U+0189: LATIN CAPITAL LETTER AFRICAN D
        + "Æ"  // U+018A: LATIN CAPITAL LETTER D WITH HOOK
        + "Æ"  // U+018B: LATIN CAPITAL LETTER D WITH TOPBAR
        + "á´"  // U+1D05: LATIN LETTER SMALL CAPITAL D
        + "á´"  // U+1D06: LATIN LETTER SMALL CAPITAL ETH
        + "á¸"  // U+1E0A: LATIN CAPITAL LETTER D WITH DOT ABOVE
        + "á¸"  // U+1E0C: LATIN CAPITAL LETTER D WITH DOT BELOW
        + "á¸"  // U+1E0E: LATIN CAPITAL LETTER D WITH LINE BELOW
        + "á¸"  // U+1E10: LATIN CAPITAL LETTER D WITH CEDILLA
        + "á¸"  // U+1E12: LATIN CAPITAL LETTER D WITH CIRCUMFLEX BELOW
        + "â¹"  // U+24B9: CIRCLED LATIN CAPITAL LETTER D
        + "ê¹"  // U+A779: LATIN CAPITAL LETTER INSULAR D
        + "ï¼¤"  // U+FF24: FULLWIDTH LATIN CAPITAL LETTER D
       ,"D", // Folded result
 
        "Ã°"  // U+00F0: LATIN SMALL LETTER ETH
        + "Ä"  // U+010F: LATIN SMALL LETTER D WITH CARON
        + "Ä"  // U+0111: LATIN SMALL LETTER D WITH STROKE
        + "Æ"  // U+018C: LATIN SMALL LETTER D WITH TOPBAR
        + "È¡"  // U+0221: LATIN SMALL LETTER D WITH CURL
        + "É"  // U+0256: LATIN SMALL LETTER D WITH TAIL
        + "É"  // U+0257: LATIN SMALL LETTER D WITH HOOK
        + "áµ­"  // U+1D6D: LATIN SMALL LETTER D WITH MIDDLE TILDE
        + "á¶"  // U+1D81: LATIN SMALL LETTER D WITH PALATAL HOOK
        + "á¶"  // U+1D91: LATIN SMALL LETTER D WITH HOOK AND TAIL
        + "á¸"  // U+1E0B: LATIN SMALL LETTER D WITH DOT ABOVE
        + "á¸"  // U+1E0D: LATIN SMALL LETTER D WITH DOT BELOW
        + "á¸"  // U+1E0F: LATIN SMALL LETTER D WITH LINE BELOW
        + "á¸"  // U+1E11: LATIN SMALL LETTER D WITH CEDILLA
        + "á¸"  // U+1E13: LATIN SMALL LETTER D WITH CIRCUMFLEX BELOW
        + "â"  // U+24D3: CIRCLED LATIN SMALL LETTER D
        + "êº"  // U+A77A: LATIN SMALL LETTER INSULAR D
        + "ï½"  // U+FF44: FULLWIDTH LATIN SMALL LETTER D
       ,"d", // Folded result
 
        "Ç"  // U+01C4: LATIN CAPITAL LETTER DZ WITH CARON
        + "Ç±"  // U+01F1: LATIN CAPITAL LETTER DZ
       ,"DZ", // Folded result
 
        "Ç"  // U+01C5: LATIN CAPITAL LETTER D WITH SMALL LETTER Z WITH CARON
        + "Ç²"  // U+01F2: LATIN CAPITAL LETTER D WITH SMALL LETTER Z
       ,"Dz", // Folded result
 
        "â"  // U+249F: PARENTHESIZED LATIN SMALL LETTER D
       ,"(d)", // Folded result
 
        "È¸"  // U+0238: LATIN SMALL LETTER DB DIGRAPH
       ,"db", // Folded result
 
        "Ç"  // U+01C6: LATIN SMALL LETTER DZ WITH CARON
        + "Ç³"  // U+01F3: LATIN SMALL LETTER DZ
        + "Ê£"  // U+02A3: LATIN SMALL LETTER DZ DIGRAPH
        + "Ê¥"  // U+02A5: LATIN SMALL LETTER DZ DIGRAPH WITH CURL
       ,"dz", // Folded result
 
        "Ã"  // U+00C8: LATIN CAPITAL LETTER E WITH GRAVE
        + "Ã"  // U+00C9: LATIN CAPITAL LETTER E WITH ACUTE
        + "Ã"  // U+00CA: LATIN CAPITAL LETTER E WITH CIRCUMFLEX
        + "Ã"  // U+00CB: LATIN CAPITAL LETTER E WITH DIAERESIS
        + "Ä"  // U+0112: LATIN CAPITAL LETTER E WITH MACRON
        + "Ä"  // U+0114: LATIN CAPITAL LETTER E WITH BREVE
        + "Ä"  // U+0116: LATIN CAPITAL LETTER E WITH DOT ABOVE
        + "Ä"  // U+0118: LATIN CAPITAL LETTER E WITH OGONEK
        + "Ä"  // U+011A: LATIN CAPITAL LETTER E WITH CARON
        + "Æ"  // U+018E: LATIN CAPITAL LETTER REVERSED E
        + "Æ"  // U+0190: LATIN CAPITAL LETTER OPEN E
        + "È"  // U+0204: LATIN CAPITAL LETTER E WITH DOUBLE GRAVE
        + "È"  // U+0206: LATIN CAPITAL LETTER E WITH INVERTED BREVE
        + "È¨"  // U+0228: LATIN CAPITAL LETTER E WITH CEDILLA
        + "É"  // U+0246: LATIN CAPITAL LETTER E WITH STROKE
        + "á´"  // U+1D07: LATIN LETTER SMALL CAPITAL E
        + "á¸"  // U+1E14: LATIN CAPITAL LETTER E WITH MACRON AND GRAVE
        + "á¸"  // U+1E16: LATIN CAPITAL LETTER E WITH MACRON AND ACUTE
        + "á¸"  // U+1E18: LATIN CAPITAL LETTER E WITH CIRCUMFLEX BELOW
        + "á¸"  // U+1E1A: LATIN CAPITAL LETTER E WITH TILDE BELOW
        + "á¸"  // U+1E1C: LATIN CAPITAL LETTER E WITH CEDILLA AND BREVE
        + "áº¸"  // U+1EB8: LATIN CAPITAL LETTER E WITH DOT BELOW
        + "áºº"  // U+1EBA: LATIN CAPITAL LETTER E WITH HOOK ABOVE
        + "áº¼"  // U+1EBC: LATIN CAPITAL LETTER E WITH TILDE
        + "áº¾"  // U+1EBE: LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND ACUTE
        + "á»"  // U+1EC0: LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND GRAVE
        + "á»"  // U+1EC2: LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE
        + "á»"  // U+1EC4: LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND TILDE
        + "á»"  // U+1EC6: LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND DOT BELOW
        + "âº"  // U+24BA: CIRCLED LATIN CAPITAL LETTER E
        + "â±»"  // U+2C7B: LATIN LETTER SMALL CAPITAL TURNED E
        + "ï¼¥"  // U+FF25: FULLWIDTH LATIN CAPITAL LETTER E
       ,"E", // Folded result
 
        "Ã¨"  // U+00E8: LATIN SMALL LETTER E WITH GRAVE
        + "Ã©"  // U+00E9: LATIN SMALL LETTER E WITH ACUTE
        + "Ãª"  // U+00EA: LATIN SMALL LETTER E WITH CIRCUMFLEX
        + "Ã«"  // U+00EB: LATIN SMALL LETTER E WITH DIAERESIS
        + "Ä"  // U+0113: LATIN SMALL LETTER E WITH MACRON
        + "Ä"  // U+0115: LATIN SMALL LETTER E WITH BREVE
        + "Ä"  // U+0117: LATIN SMALL LETTER E WITH DOT ABOVE
        + "Ä"  // U+0119: LATIN SMALL LETTER E WITH OGONEK
        + "Ä"  // U+011B: LATIN SMALL LETTER E WITH CARON
        + "Ç"  // U+01DD: LATIN SMALL LETTER TURNED E
        + "È"  // U+0205: LATIN SMALL LETTER E WITH DOUBLE GRAVE
        + "È"  // U+0207: LATIN SMALL LETTER E WITH INVERTED BREVE
        + "È©"  // U+0229: LATIN SMALL LETTER E WITH CEDILLA
        + "É"  // U+0247: LATIN SMALL LETTER E WITH STROKE
        + "É"  // U+0258: LATIN SMALL LETTER REVERSED E
        + "É"  // U+025B: LATIN SMALL LETTER OPEN E
        + "É"  // U+025C: LATIN SMALL LETTER REVERSED OPEN E
        + "É"  // U+025D: LATIN SMALL LETTER REVERSED OPEN E WITH HOOK
        + "É"  // U+025E: LATIN SMALL LETTER CLOSED REVERSED OPEN E
        + "Ê"  // U+029A: LATIN SMALL LETTER CLOSED OPEN E
        + "á´"  // U+1D08: LATIN SMALL LETTER TURNED OPEN E
        + "á¶"  // U+1D92: LATIN SMALL LETTER E WITH RETROFLEX HOOK
        + "á¶"  // U+1D93: LATIN SMALL LETTER OPEN E WITH RETROFLEX HOOK
        + "á¶"  // U+1D94: LATIN SMALL LETTER REVERSED OPEN E WITH RETROFLEX HOOK
        + "á¸"  // U+1E15: LATIN SMALL LETTER E WITH MACRON AND GRAVE
        + "á¸"  // U+1E17: LATIN SMALL LETTER E WITH MACRON AND ACUTE
        + "á¸"  // U+1E19: LATIN SMALL LETTER E WITH CIRCUMFLEX BELOW
        + "á¸"  // U+1E1B: LATIN SMALL LETTER E WITH TILDE BELOW
        + "á¸"  // U+1E1D: LATIN SMALL LETTER E WITH CEDILLA AND BREVE
        + "áº¹"  // U+1EB9: LATIN SMALL LETTER E WITH DOT BELOW
        + "áº»"  // U+1EBB: LATIN SMALL LETTER E WITH HOOK ABOVE
        + "áº½"  // U+1EBD: LATIN SMALL LETTER E WITH TILDE
        + "áº¿"  // U+1EBF: LATIN SMALL LETTER E WITH CIRCUMFLEX AND ACUTE
        + "á»"  // U+1EC1: LATIN SMALL LETTER E WITH CIRCUMFLEX AND GRAVE
        + "á»"  // U+1EC3: LATIN SMALL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE
        + "á»"  // U+1EC5: LATIN SMALL LETTER E WITH CIRCUMFLEX AND TILDE
        + "á»"  // U+1EC7: LATIN SMALL LETTER E WITH CIRCUMFLEX AND DOT BELOW
        + "â"  // U+2091: LATIN SUBSCRIPT SMALL LETTER E
        + "â"  // U+24D4: CIRCLED LATIN SMALL LETTER E
        + "â±¸"  // U+2C78: LATIN SMALL LETTER E WITH NOTCH
        + "ï½"  // U+FF45: FULLWIDTH LATIN SMALL LETTER E
       ,"e", // Folded result
 
        "â "  // U+24A0: PARENTHESIZED LATIN SMALL LETTER E
       ,"(e)", // Folded result
 
        "Æ"  // U+0191: LATIN CAPITAL LETTER F WITH HOOK
        + "á¸"  // U+1E1E: LATIN CAPITAL LETTER F WITH DOT ABOVE
        + "â»"  // U+24BB: CIRCLED LATIN CAPITAL LETTER F
        + "ê°"  // U+A730: LATIN LETTER SMALL CAPITAL F
        + "ê»"  // U+A77B: LATIN CAPITAL LETTER INSULAR F
        + "ê»"  // U+A7FB: LATIN EPIGRAPHIC LETTER REVERSED F
        + "ï¼¦"  // U+FF26: FULLWIDTH LATIN CAPITAL LETTER F
       ,"F", // Folded result
 
        "Æ"  // U+0192: LATIN SMALL LETTER F WITH HOOK
        + "áµ®"  // U+1D6E: LATIN SMALL LETTER F WITH MIDDLE TILDE
        + "á¶"  // U+1D82: LATIN SMALL LETTER F WITH PALATAL HOOK
        + "á¸"  // U+1E1F: LATIN SMALL LETTER F WITH DOT ABOVE
        + "áº"  // U+1E9B: LATIN SMALL LETTER LONG S WITH DOT ABOVE
        + "â"  // U+24D5: CIRCLED LATIN SMALL LETTER F
        + "ê¼"  // U+A77C: LATIN SMALL LETTER INSULAR F
        + "ï½"  // U+FF46: FULLWIDTH LATIN SMALL LETTER F
       ,"f", // Folded result
 
        "â¡"  // U+24A1: PARENTHESIZED LATIN SMALL LETTER F
       ,"(f)", // Folded result
 
        "ï¬"  // U+FB00: LATIN SMALL LIGATURE FF
       ,"ff", // Folded result
 
        "ï¬"  // U+FB03: LATIN SMALL LIGATURE FFI
       ,"ffi", // Folded result
 
        "ï¬"  // U+FB04: LATIN SMALL LIGATURE FFL
       ,"ffl", // Folded result
 
        "ï¬"  // U+FB01: LATIN SMALL LIGATURE FI
       ,"fi", // Folded result
 
        "ï¬"  // U+FB02: LATIN SMALL LIGATURE FL
       ,"fl", // Folded result
 
        "Ä"  // U+011C: LATIN CAPITAL LETTER G WITH CIRCUMFLEX
        + "Ä"  // U+011E: LATIN CAPITAL LETTER G WITH BREVE
        + "Ä "  // U+0120: LATIN CAPITAL LETTER G WITH DOT ABOVE
        + "Ä¢"  // U+0122: LATIN CAPITAL LETTER G WITH CEDILLA
        + "Æ"  // U+0193: LATIN CAPITAL LETTER G WITH HOOK
        + "Ç¤"  // U+01E4: LATIN CAPITAL LETTER G WITH STROKE
        + "Ç¥"  // U+01E5: LATIN SMALL LETTER G WITH STROKE
        + "Ç¦"  // U+01E6: LATIN CAPITAL LETTER G WITH CARON
        + "Ç§"  // U+01E7: LATIN SMALL LETTER G WITH CARON
        + "Ç´"  // U+01F4: LATIN CAPITAL LETTER G WITH ACUTE
        + "É¢"  // U+0262: LATIN LETTER SMALL CAPITAL G
        + "Ê"  // U+029B: LATIN LETTER SMALL CAPITAL G WITH HOOK
        + "á¸ "  // U+1E20: LATIN CAPITAL LETTER G WITH MACRON
        + "â¼"  // U+24BC: CIRCLED LATIN CAPITAL LETTER G
        + "ê½"  // U+A77D: LATIN CAPITAL LETTER INSULAR G
        + "ê¾"  // U+A77E: LATIN CAPITAL LETTER TURNED INSULAR G
        + "ï¼§"  // U+FF27: FULLWIDTH LATIN CAPITAL LETTER G
       ,"G", // Folded result
 
        "Ä"  // U+011D: LATIN SMALL LETTER G WITH CIRCUMFLEX
        + "Ä"  // U+011F: LATIN SMALL LETTER G WITH BREVE
        + "Ä¡"  // U+0121: LATIN SMALL LETTER G WITH DOT ABOVE
        + "Ä£"  // U+0123: LATIN SMALL LETTER G WITH CEDILLA
        + "Çµ"  // U+01F5: LATIN SMALL LETTER G WITH ACUTE
        + "É "  // U+0260: LATIN SMALL LETTER G WITH HOOK
        + "É¡"  // U+0261: LATIN SMALL LETTER SCRIPT G
        + "áµ·"  // U+1D77: LATIN SMALL LETTER TURNED G
        + "áµ¹"  // U+1D79: LATIN SMALL LETTER INSULAR G
        + "á¶"  // U+1D83: LATIN SMALL LETTER G WITH PALATAL HOOK
        + "á¸¡"  // U+1E21: LATIN SMALL LETTER G WITH MACRON
        + "â"  // U+24D6: CIRCLED LATIN SMALL LETTER G
        + "ê¿"  // U+A77F: LATIN SMALL LETTER TURNED INSULAR G
        + "ï½"  // U+FF47: FULLWIDTH LATIN SMALL LETTER G
       ,"g", // Folded result
 
        "â¢"  // U+24A2: PARENTHESIZED LATIN SMALL LETTER G
       ,"(g)", // Folded result
 
        "Ä¤"  // U+0124: LATIN CAPITAL LETTER H WITH CIRCUMFLEX
        + "Ä¦"  // U+0126: LATIN CAPITAL LETTER H WITH STROKE
        + "È"  // U+021E: LATIN CAPITAL LETTER H WITH CARON
        + "Ê"  // U+029C: LATIN LETTER SMALL CAPITAL H
        + "á¸¢"  // U+1E22: LATIN CAPITAL LETTER H WITH DOT ABOVE
        + "á¸¤"  // U+1E24: LATIN CAPITAL LETTER H WITH DOT BELOW
        + "á¸¦"  // U+1E26: LATIN CAPITAL LETTER H WITH DIAERESIS
        + "á¸¨"  // U+1E28: LATIN CAPITAL LETTER H WITH CEDILLA
        + "á¸ª"  // U+1E2A: LATIN CAPITAL LETTER H WITH BREVE BELOW
        + "â½"  // U+24BD: CIRCLED LATIN CAPITAL LETTER H
        + "â±§"  // U+2C67: LATIN CAPITAL LETTER H WITH DESCENDER
        + "â±µ"  // U+2C75: LATIN CAPITAL LETTER HALF H
        + "ï¼¨"  // U+FF28: FULLWIDTH LATIN CAPITAL LETTER H
       ,"H", // Folded result
 
        "Ä¥"  // U+0125: LATIN SMALL LETTER H WITH CIRCUMFLEX
        + "Ä§"  // U+0127: LATIN SMALL LETTER H WITH STROKE
        + "È"  // U+021F: LATIN SMALL LETTER H WITH CARON
        + "É¥"  // U+0265: LATIN SMALL LETTER TURNED H
        + "É¦"  // U+0266: LATIN SMALL LETTER H WITH HOOK
        + "Ê®"  // U+02AE: LATIN SMALL LETTER TURNED H WITH FISHHOOK
        + "Ê¯"  // U+02AF: LATIN SMALL LETTER TURNED H WITH FISHHOOK AND TAIL
        + "á¸£"  // U+1E23: LATIN SMALL LETTER H WITH DOT ABOVE
        + "á¸¥"  // U+1E25: LATIN SMALL LETTER H WITH DOT BELOW
        + "á¸§"  // U+1E27: LATIN SMALL LETTER H WITH DIAERESIS
        + "á¸©"  // U+1E29: LATIN SMALL LETTER H WITH CEDILLA
        + "á¸«"  // U+1E2B: LATIN SMALL LETTER H WITH BREVE BELOW
        + "áº"  // U+1E96: LATIN SMALL LETTER H WITH LINE BELOW
        + "â"  // U+24D7: CIRCLED LATIN SMALL LETTER H
        + "â±¨"  // U+2C68: LATIN SMALL LETTER H WITH DESCENDER
        + "â±¶"  // U+2C76: LATIN SMALL LETTER HALF H
        + "ï½"  // U+FF48: FULLWIDTH LATIN SMALL LETTER H
       ,"h", // Folded result
 
        "Ç¶"  // U+01F6: LATIN CAPITAL LETTER HWAIR
       ,"HV", // Folded result
 
        "â£"  // U+24A3: PARENTHESIZED LATIN SMALL LETTER H
       ,"(h)", // Folded result
 
        "Æ"  // U+0195: LATIN SMALL LETTER HV
       ,"hv", // Folded result
 
        "Ã"  // U+00CC: LATIN CAPITAL LETTER I WITH GRAVE
        + "Ã"  // U+00CD: LATIN CAPITAL LETTER I WITH ACUTE
        + "Ã"  // U+00CE: LATIN CAPITAL LETTER I WITH CIRCUMFLEX
        + "Ã"  // U+00CF: LATIN CAPITAL LETTER I WITH DIAERESIS
        + "Ä¨"  // U+0128: LATIN CAPITAL LETTER I WITH TILDE
        + "Äª"  // U+012A: LATIN CAPITAL LETTER I WITH MACRON
        + "Ä¬"  // U+012C: LATIN CAPITAL LETTER I WITH BREVE
        + "Ä®"  // U+012E: LATIN CAPITAL LETTER I WITH OGONEK
        + "Ä°"  // U+0130: LATIN CAPITAL LETTER I WITH DOT ABOVE
        + "Æ"  // U+0196: LATIN CAPITAL LETTER IOTA
        + "Æ"  // U+0197: LATIN CAPITAL LETTER I WITH STROKE
        + "Ç"  // U+01CF: LATIN CAPITAL LETTER I WITH CARON
        + "È"  // U+0208: LATIN CAPITAL LETTER I WITH DOUBLE GRAVE
        + "È"  // U+020A: LATIN CAPITAL LETTER I WITH INVERTED BREVE
        + "Éª"  // U+026A: LATIN LETTER SMALL CAPITAL I
        + "áµ»"  // U+1D7B: LATIN SMALL CAPITAL LETTER I WITH STROKE
        + "á¸¬"  // U+1E2C: LATIN CAPITAL LETTER I WITH TILDE BELOW
        + "á¸®"  // U+1E2E: LATIN CAPITAL LETTER I WITH DIAERESIS AND ACUTE
        + "á»"  // U+1EC8: LATIN CAPITAL LETTER I WITH HOOK ABOVE
        + "á»"  // U+1ECA: LATIN CAPITAL LETTER I WITH DOT BELOW
        + "â¾"  // U+24BE: CIRCLED LATIN CAPITAL LETTER I
        + "ê¾"  // U+A7FE: LATIN EPIGRAPHIC LETTER I LONGA
        + "ï¼©"  // U+FF29: FULLWIDTH LATIN CAPITAL LETTER I
       ,"I", // Folded result
 
        "Ã¬"  // U+00EC: LATIN SMALL LETTER I WITH GRAVE
        + "Ã­"  // U+00ED: LATIN SMALL LETTER I WITH ACUTE
        + "Ã®"  // U+00EE: LATIN SMALL LETTER I WITH CIRCUMFLEX
        + "Ã¯"  // U+00EF: LATIN SMALL LETTER I WITH DIAERESIS
        + "Ä©"  // U+0129: LATIN SMALL LETTER I WITH TILDE
        + "Ä«"  // U+012B: LATIN SMALL LETTER I WITH MACRON
        + "Ä­"  // U+012D: LATIN SMALL LETTER I WITH BREVE
        + "Ä¯"  // U+012F: LATIN SMALL LETTER I WITH OGONEK
        + "Ä±"  // U+0131: LATIN SMALL LETTER DOTLESS I
        + "Ç"  // U+01D0: LATIN SMALL LETTER I WITH CARON
        + "È"  // U+0209: LATIN SMALL LETTER I WITH DOUBLE GRAVE
        + "È"  // U+020B: LATIN SMALL LETTER I WITH INVERTED BREVE
        + "É¨"  // U+0268: LATIN SMALL LETTER I WITH STROKE
        + "á´"  // U+1D09: LATIN SMALL LETTER TURNED I
        + "áµ¢"  // U+1D62: LATIN SUBSCRIPT SMALL LETTER I
        + "áµ¼"  // U+1D7C: LATIN SMALL LETTER IOTA WITH STROKE
        + "á¶"  // U+1D96: LATIN SMALL LETTER I WITH RETROFLEX HOOK
        + "á¸­"  // U+1E2D: LATIN SMALL LETTER I WITH TILDE BELOW
        + "á¸¯"  // U+1E2F: LATIN SMALL LETTER I WITH DIAERESIS AND ACUTE
        + "á»"  // U+1EC9: LATIN SMALL LETTER I WITH HOOK ABOVE
        + "á»"  // U+1ECB: LATIN SMALL LETTER I WITH DOT BELOW
        + "â±"  // U+2071: SUPERSCRIPT LATIN SMALL LETTER I
        + "â"  // U+24D8: CIRCLED LATIN SMALL LETTER I
        + "ï½"  // U+FF49: FULLWIDTH LATIN SMALL LETTER I
       ,"i", // Folded result
 
        "Ä²"  // U+0132: LATIN CAPITAL LIGATURE IJ
       ,"IJ", // Folded result
 
        "â¤"  // U+24A4: PARENTHESIZED LATIN SMALL LETTER I
       ,"(i)", // Folded result
 
        "Ä³"  // U+0133: LATIN SMALL LIGATURE IJ
       ,"ij", // Folded result
 
        "Ä´"  // U+0134: LATIN CAPITAL LETTER J WITH CIRCUMFLEX
        + "É"  // U+0248: LATIN CAPITAL LETTER J WITH STROKE
        + "á´"  // U+1D0A: LATIN LETTER SMALL CAPITAL J
        + "â¿"  // U+24BF: CIRCLED LATIN CAPITAL LETTER J
        + "ï¼ª"  // U+FF2A: FULLWIDTH LATIN CAPITAL LETTER J
       ,"J", // Folded result
 
        "Äµ"  // U+0135: LATIN SMALL LETTER J WITH CIRCUMFLEX
        + "Ç°"  // U+01F0: LATIN SMALL LETTER J WITH CARON
        + "È·"  // U+0237: LATIN SMALL LETTER DOTLESS J
        + "É"  // U+0249: LATIN SMALL LETTER J WITH STROKE
        + "É"  // U+025F: LATIN SMALL LETTER DOTLESS J WITH STROKE
        + "Ê"  // U+0284: LATIN SMALL LETTER DOTLESS J WITH STROKE AND HOOK
        + "Ê"  // U+029D: LATIN SMALL LETTER J WITH CROSSED-TAIL
        + "â"  // U+24D9: CIRCLED LATIN SMALL LETTER J
        + "â±¼"  // U+2C7C: LATIN SUBSCRIPT SMALL LETTER J
        + "ï½"  // U+FF4A: FULLWIDTH LATIN SMALL LETTER J
       ,"j", // Folded result
 
        "â¥"  // U+24A5: PARENTHESIZED LATIN SMALL LETTER J
       ,"(j)", // Folded result
 
        "Ä¶"  // U+0136: LATIN CAPITAL LETTER K WITH CEDILLA
        + "Æ"  // U+0198: LATIN CAPITAL LETTER K WITH HOOK
        + "Ç¨"  // U+01E8: LATIN CAPITAL LETTER K WITH CARON
        + "á´"  // U+1D0B: LATIN LETTER SMALL CAPITAL K
        + "á¸°"  // U+1E30: LATIN CAPITAL LETTER K WITH ACUTE
        + "á¸²"  // U+1E32: LATIN CAPITAL LETTER K WITH DOT BELOW
        + "á¸´"  // U+1E34: LATIN CAPITAL LETTER K WITH LINE BELOW
        + "â"  // U+24C0: CIRCLED LATIN CAPITAL LETTER K
        + "â±©"  // U+2C69: LATIN CAPITAL LETTER K WITH DESCENDER
        + "ê"  // U+A740: LATIN CAPITAL LETTER K WITH STROKE
        + "ê"  // U+A742: LATIN CAPITAL LETTER K WITH DIAGONAL STROKE
        + "ê"  // U+A744: LATIN CAPITAL LETTER K WITH STROKE AND DIAGONAL STROKE
        + "ï¼«"  // U+FF2B: FULLWIDTH LATIN CAPITAL LETTER K
       ,"K", // Folded result
 
        "Ä·"  // U+0137: LATIN SMALL LETTER K WITH CEDILLA
        + "Æ"  // U+0199: LATIN SMALL LETTER K WITH HOOK
        + "Ç©"  // U+01E9: LATIN SMALL LETTER K WITH CARON
        + "Ê"  // U+029E: LATIN SMALL LETTER TURNED K
        + "á¶"  // U+1D84: LATIN SMALL LETTER K WITH PALATAL HOOK
        + "á¸±"  // U+1E31: LATIN SMALL LETTER K WITH ACUTE
        + "á¸³"  // U+1E33: LATIN SMALL LETTER K WITH DOT BELOW
        + "á¸µ"  // U+1E35: LATIN SMALL LETTER K WITH LINE BELOW
        + "â"  // U+24DA: CIRCLED LATIN SMALL LETTER K
        + "â±ª"  // U+2C6A: LATIN SMALL LETTER K WITH DESCENDER
        + "ê"  // U+A741: LATIN SMALL LETTER K WITH STROKE
        + "ê"  // U+A743: LATIN SMALL LETTER K WITH DIAGONAL STROKE
        + "ê"  // U+A745: LATIN SMALL LETTER K WITH STROKE AND DIAGONAL STROKE
        + "ï½"  // U+FF4B: FULLWIDTH LATIN SMALL LETTER K
       ,"k", // Folded result
 
        "â¦"  // U+24A6: PARENTHESIZED LATIN SMALL LETTER K
       ,"(k)", // Folded result
 
        "Ä¹"  // U+0139: LATIN CAPITAL LETTER L WITH ACUTE
        + "Ä»"  // U+013B: LATIN CAPITAL LETTER L WITH CEDILLA
        + "Ä½"  // U+013D: LATIN CAPITAL LETTER L WITH CARON
        + "Ä¿"  // U+013F: LATIN CAPITAL LETTER L WITH MIDDLE DOT
        + "Å"  // U+0141: LATIN CAPITAL LETTER L WITH STROKE
        + "È½"  // U+023D: LATIN CAPITAL LETTER L WITH BAR
        + "Ê"  // U+029F: LATIN LETTER SMALL CAPITAL L
        + "á´"  // U+1D0C: LATIN LETTER SMALL CAPITAL L WITH STROKE
        + "á¸¶"  // U+1E36: LATIN CAPITAL LETTER L WITH DOT BELOW
        + "á¸¸"  // U+1E38: LATIN CAPITAL LETTER L WITH DOT BELOW AND MACRON
        + "á¸º"  // U+1E3A: LATIN CAPITAL LETTER L WITH LINE BELOW
        + "á¸¼"  // U+1E3C: LATIN CAPITAL LETTER L WITH CIRCUMFLEX BELOW
        + "â"  // U+24C1: CIRCLED LATIN CAPITAL LETTER L
        + "â± "  // U+2C60: LATIN CAPITAL LETTER L WITH DOUBLE BAR
        + "â±¢"  // U+2C62: LATIN CAPITAL LETTER L WITH MIDDLE TILDE
        + "ê"  // U+A746: LATIN CAPITAL LETTER BROKEN L
        + "ê"  // U+A748: LATIN CAPITAL LETTER L WITH HIGH STROKE
        + "ê"  // U+A780: LATIN CAPITAL LETTER TURNED L
        + "ï¼¬"  // U+FF2C: FULLWIDTH LATIN CAPITAL LETTER L
       ,"L", // Folded result
 
        "Äº"  // U+013A: LATIN SMALL LETTER L WITH ACUTE
        + "Ä¼"  // U+013C: LATIN SMALL LETTER L WITH CEDILLA
        + "Ä¾"  // U+013E: LATIN SMALL LETTER L WITH CARON
        + "Å"  // U+0140: LATIN SMALL LETTER L WITH MIDDLE DOT
        + "Å"  // U+0142: LATIN SMALL LETTER L WITH STROKE
        + "Æ"  // U+019A: LATIN SMALL LETTER L WITH BAR
        + "È´"  // U+0234: LATIN SMALL LETTER L WITH CURL
        + "É«"  // U+026B: LATIN SMALL LETTER L WITH MIDDLE TILDE
        + "É¬"  // U+026C: LATIN SMALL LETTER L WITH BELT
        + "É­"  // U+026D: LATIN SMALL LETTER L WITH RETROFLEX HOOK
        + "á¶"  // U+1D85: LATIN SMALL LETTER L WITH PALATAL HOOK
        + "á¸·"  // U+1E37: LATIN SMALL LETTER L WITH DOT BELOW
        + "á¸¹"  // U+1E39: LATIN SMALL LETTER L WITH DOT BELOW AND MACRON
        + "á¸»"  // U+1E3B: LATIN SMALL LETTER L WITH LINE BELOW
        + "á¸½"  // U+1E3D: LATIN SMALL LETTER L WITH CIRCUMFLEX BELOW
        + "â"  // U+24DB: CIRCLED LATIN SMALL LETTER L
        + "â±¡"  // U+2C61: LATIN SMALL LETTER L WITH DOUBLE BAR
        + "ê"  // U+A747: LATIN SMALL LETTER BROKEN L
        + "ê"  // U+A749: LATIN SMALL LETTER L WITH HIGH STROKE
        + "ê"  // U+A781: LATIN SMALL LETTER TURNED L
        + "ï½"  // U+FF4C: FULLWIDTH LATIN SMALL LETTER L
       ,"l", // Folded result
 
        "Ç"  // U+01C7: LATIN CAPITAL LETTER LJ
       ,"LJ", // Folded result
 
        "á»º"  // U+1EFA: LATIN CAPITAL LETTER MIDDLE-WELSH LL
       ,"LL", // Folded result
 
        "Ç"  // U+01C8: LATIN CAPITAL LETTER L WITH SMALL LETTER J
       ,"Lj", // Folded result
 
        "â§"  // U+24A7: PARENTHESIZED LATIN SMALL LETTER L
       ,"(l)", // Folded result
 
        "Ç"  // U+01C9: LATIN SMALL LETTER LJ
       ,"lj", // Folded result
 
        "á»»"  // U+1EFB: LATIN SMALL LETTER MIDDLE-WELSH LL
       ,"ll", // Folded result
 
        "Êª"  // U+02AA: LATIN SMALL LETTER LS DIGRAPH
       ,"ls", // Folded result
 
        "Ê«"  // U+02AB: LATIN SMALL LETTER LZ DIGRAPH
       ,"lz", // Folded result
 
        "Æ"  // U+019C: LATIN CAPITAL LETTER TURNED M
        + "á´"  // U+1D0D: LATIN LETTER SMALL CAPITAL M
        + "á¸¾"  // U+1E3E: LATIN CAPITAL LETTER M WITH ACUTE
        + "á¹"  // U+1E40: LATIN CAPITAL LETTER M WITH DOT ABOVE
        + "á¹"  // U+1E42: LATIN CAPITAL LETTER M WITH DOT BELOW
        + "â"  // U+24C2: CIRCLED LATIN CAPITAL LETTER M
        + "â±®"  // U+2C6E: LATIN CAPITAL LETTER M WITH HOOK
        + "ê½"  // U+A7FD: LATIN EPIGRAPHIC LETTER INVERTED M
        + "ê¿"  // U+A7FF: LATIN EPIGRAPHIC LETTER ARCHAIC M
        + "ï¼­"  // U+FF2D: FULLWIDTH LATIN CAPITAL LETTER M
       ,"M", // Folded result
 
        "É¯"  // U+026F: LATIN SMALL LETTER TURNED M
        + "É°"  // U+0270: LATIN SMALL LETTER TURNED M WITH LONG LEG
        + "É±"  // U+0271: LATIN SMALL LETTER M WITH HOOK
        + "áµ¯"  // U+1D6F: LATIN SMALL LETTER M WITH MIDDLE TILDE
        + "á¶"  // U+1D86: LATIN SMALL LETTER M WITH PALATAL HOOK
        + "á¸¿"  // U+1E3F: LATIN SMALL LETTER M WITH ACUTE
        + "á¹"  // U+1E41: LATIN SMALL LETTER M WITH DOT ABOVE
        + "á¹"  // U+1E43: LATIN SMALL LETTER M WITH DOT BELOW
        + "â"  // U+24DC: CIRCLED LATIN SMALL LETTER M
        + "ï½"  // U+FF4D: FULLWIDTH LATIN SMALL LETTER M
       ,"m", // Folded result
 
        "â¨"  // U+24A8: PARENTHESIZED LATIN SMALL LETTER M
       ,"(m)", // Folded result
 
        "Ã"  // U+00D1: LATIN CAPITAL LETTER N WITH TILDE
        + "Å"  // U+0143: LATIN CAPITAL LETTER N WITH ACUTE
        + "Å"  // U+0145: LATIN CAPITAL LETTER N WITH CEDILLA
        + "Å"  // U+0147: LATIN CAPITAL LETTER N WITH CARON
        + "Å"  // U+014A: LATIN CAPITAL LETTER ENG
        + "Æ"  // U+019D: LATIN CAPITAL LETTER N WITH LEFT HOOK
        + "Ç¸"  // U+01F8: LATIN CAPITAL LETTER N WITH GRAVE
        + "È "  // U+0220: LATIN CAPITAL LETTER N WITH LONG RIGHT LEG
        + "É´"  // U+0274: LATIN LETTER SMALL CAPITAL N
        + "á´"  // U+1D0E: LATIN LETTER SMALL CAPITAL REVERSED N
        + "á¹"  // U+1E44: LATIN CAPITAL LETTER N WITH DOT ABOVE
        + "á¹"  // U+1E46: LATIN CAPITAL LETTER N WITH DOT BELOW
        + "á¹"  // U+1E48: LATIN CAPITAL LETTER N WITH LINE BELOW
        + "á¹"  // U+1E4A: LATIN CAPITAL LETTER N WITH CIRCUMFLEX BELOW
        + "â"  // U+24C3: CIRCLED LATIN CAPITAL LETTER N
        + "ï¼®"  // U+FF2E: FULLWIDTH LATIN CAPITAL LETTER N
       ,"N", // Folded result
 
        "Ã±"  // U+00F1: LATIN SMALL LETTER N WITH TILDE
        + "Å"  // U+0144: LATIN SMALL LETTER N WITH ACUTE
        + "Å"  // U+0146: LATIN SMALL LETTER N WITH CEDILLA
        + "Å"  // U+0148: LATIN SMALL LETTER N WITH CARON
        + "Å"  // U+0149: LATIN SMALL LETTER N PRECEDED BY APOSTROPHE
        + "Å"  // U+014B: LATIN SMALL LETTER ENG
        + "Æ"  // U+019E: LATIN SMALL LETTER N WITH LONG RIGHT LEG
        + "Ç¹"  // U+01F9: LATIN SMALL LETTER N WITH GRAVE
        + "Èµ"  // U+0235: LATIN SMALL LETTER N WITH CURL
        + "É²"  // U+0272: LATIN SMALL LETTER N WITH LEFT HOOK
        + "É³"  // U+0273: LATIN SMALL LETTER N WITH RETROFLEX HOOK
        + "áµ°"  // U+1D70: LATIN SMALL LETTER N WITH MIDDLE TILDE
        + "á¶"  // U+1D87: LATIN SMALL LETTER N WITH PALATAL HOOK
        + "á¹"  // U+1E45: LATIN SMALL LETTER N WITH DOT ABOVE
        + "á¹"  // U+1E47: LATIN SMALL LETTER N WITH DOT BELOW
        + "á¹"  // U+1E49: LATIN SMALL LETTER N WITH LINE BELOW
        + "á¹"  // U+1E4B: LATIN SMALL LETTER N WITH CIRCUMFLEX BELOW
        + "â¿"  // U+207F: SUPERSCRIPT LATIN SMALL LETTER N
        + "â"  // U+24DD: CIRCLED LATIN SMALL LETTER N
        + "ï½"  // U+FF4E: FULLWIDTH LATIN SMALL LETTER N
       ,"n", // Folded result
 
        "Ç"  // U+01CA: LATIN CAPITAL LETTER NJ
       ,"NJ", // Folded result
 
        "Ç"  // U+01CB: LATIN CAPITAL LETTER N WITH SMALL LETTER J
       ,"Nj", // Folded result
 
        "â©"  // U+24A9: PARENTHESIZED LATIN SMALL LETTER N
       ,"(n)", // Folded result
 
        "Ç"  // U+01CC: LATIN SMALL LETTER NJ
       ,"nj", // Folded result
 
        "Ã"  // U+00D2: LATIN CAPITAL LETTER O WITH GRAVE
        + "Ã"  // U+00D3: LATIN CAPITAL LETTER O WITH ACUTE
        + "Ã"  // U+00D4: LATIN CAPITAL LETTER O WITH CIRCUMFLEX
        + "Ã"  // U+00D5: LATIN CAPITAL LETTER O WITH TILDE
        + "Ã"  // U+00D6: LATIN CAPITAL LETTER O WITH DIAERESIS
        + "Ã"  // U+00D8: LATIN CAPITAL LETTER O WITH STROKE
        + "Å"  // U+014C: LATIN CAPITAL LETTER O WITH MACRON
        + "Å"  // U+014E: LATIN CAPITAL LETTER O WITH BREVE
        + "Å"  // U+0150: LATIN CAPITAL LETTER O WITH DOUBLE ACUTE
        + "Æ"  // U+0186: LATIN CAPITAL LETTER OPEN O
        + "Æ"  // U+019F: LATIN CAPITAL LETTER O WITH MIDDLE TILDE
        + "Æ "  // U+01A0: LATIN CAPITAL LETTER O WITH HORN
        + "Ç"  // U+01D1: LATIN CAPITAL LETTER O WITH CARON
        + "Çª"  // U+01EA: LATIN CAPITAL LETTER O WITH OGONEK
        + "Ç¬"  // U+01EC: LATIN CAPITAL LETTER O WITH OGONEK AND MACRON
        + "Ç¾"  // U+01FE: LATIN CAPITAL LETTER O WITH STROKE AND ACUTE
        + "È"  // U+020C: LATIN CAPITAL LETTER O WITH DOUBLE GRAVE
        + "È"  // U+020E: LATIN CAPITAL LETTER O WITH INVERTED BREVE
        + "Èª"  // U+022A: LATIN CAPITAL LETTER O WITH DIAERESIS AND MACRON
        + "È¬"  // U+022C: LATIN CAPITAL LETTER O WITH TILDE AND MACRON
        + "È®"  // U+022E: LATIN CAPITAL LETTER O WITH DOT ABOVE
        + "È°"  // U+0230: LATIN CAPITAL LETTER O WITH DOT ABOVE AND MACRON
        + "á´"  // U+1D0F: LATIN LETTER SMALL CAPITAL O
        + "á´"  // U+1D10: LATIN LETTER SMALL CAPITAL OPEN O
        + "á¹"  // U+1E4C: LATIN CAPITAL LETTER O WITH TILDE AND ACUTE
        + "á¹"  // U+1E4E: LATIN CAPITAL LETTER O WITH TILDE AND DIAERESIS
        + "á¹"  // U+1E50: LATIN CAPITAL LETTER O WITH MACRON AND GRAVE
        + "á¹"  // U+1E52: LATIN CAPITAL LETTER O WITH MACRON AND ACUTE
        + "á»"  // U+1ECC: LATIN CAPITAL LETTER O WITH DOT BELOW
        + "á»"  // U+1ECE: LATIN CAPITAL LETTER O WITH HOOK ABOVE
        + "á»"  // U+1ED0: LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND ACUTE
        + "á»"  // U+1ED2: LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND GRAVE
        + "á»"  // U+1ED4: LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE
        + "á»"  // U+1ED6: LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND TILDE
        + "á»"  // U+1ED8: LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND DOT BELOW
        + "á»"  // U+1EDA: LATIN CAPITAL LETTER O WITH HORN AND ACUTE
        + "á»"  // U+1EDC: LATIN CAPITAL LETTER O WITH HORN AND GRAVE
        + "á»"  // U+1EDE: LATIN CAPITAL LETTER O WITH HORN AND HOOK ABOVE
        + "á» "  // U+1EE0: LATIN CAPITAL LETTER O WITH HORN AND TILDE
        + "á»¢"  // U+1EE2: LATIN CAPITAL LETTER O WITH HORN AND DOT BELOW
        + "â"  // U+24C4: CIRCLED LATIN CAPITAL LETTER O
        + "ê"  // U+A74A: LATIN CAPITAL LETTER O WITH LONG STROKE OVERLAY
        + "ê"  // U+A74C: LATIN CAPITAL LETTER O WITH LOOP
        + "ï¼¯"  // U+FF2F: FULLWIDTH LATIN CAPITAL LETTER O
       ,"O", // Folded result
 
        "Ã²"  // U+00F2: LATIN SMALL LETTER O WITH GRAVE
        + "Ã³"  // U+00F3: LATIN SMALL LETTER O WITH ACUTE
        + "Ã´"  // U+00F4: LATIN SMALL LETTER O WITH CIRCUMFLEX
        + "Ãµ"  // U+00F5: LATIN SMALL LETTER O WITH TILDE
        + "Ã¶"  // U+00F6: LATIN SMALL LETTER O WITH DIAERESIS
        + "Ã¸"  // U+00F8: LATIN SMALL LETTER O WITH STROKE
        + "Å"  // U+014D: LATIN SMALL LETTER O WITH MACRON
        + "Å"  // U+014F: LATIN SMALL LETTER O WITH BREVE
        + "Å"  // U+0151: LATIN SMALL LETTER O WITH DOUBLE ACUTE
        + "Æ¡"  // U+01A1: LATIN SMALL LETTER O WITH HORN
        + "Ç"  // U+01D2: LATIN SMALL LETTER O WITH CARON
        + "Ç«"  // U+01EB: LATIN SMALL LETTER O WITH OGONEK
        + "Ç­"  // U+01ED: LATIN SMALL LETTER O WITH OGONEK AND MACRON
        + "Ç¿"  // U+01FF: LATIN SMALL LETTER O WITH STROKE AND ACUTE
        + "È"  // U+020D: LATIN SMALL LETTER O WITH DOUBLE GRAVE
        + "È"  // U+020F: LATIN SMALL LETTER O WITH INVERTED BREVE
        + "È«"  // U+022B: LATIN SMALL LETTER O WITH DIAERESIS AND MACRON
        + "È­"  // U+022D: LATIN SMALL LETTER O WITH TILDE AND MACRON
        + "È¯"  // U+022F: LATIN SMALL LETTER O WITH DOT ABOVE
        + "È±"  // U+0231: LATIN SMALL LETTER O WITH DOT ABOVE AND MACRON
        + "É"  // U+0254: LATIN SMALL LETTER OPEN O
        + "Éµ"  // U+0275: LATIN SMALL LETTER BARRED O
        + "á´"  // U+1D16: LATIN SMALL LETTER TOP HALF O
        + "á´"  // U+1D17: LATIN SMALL LETTER BOTTOM HALF O
        + "á¶"  // U+1D97: LATIN SMALL LETTER OPEN O WITH RETROFLEX HOOK
        + "á¹"  // U+1E4D: LATIN SMALL LETTER O WITH TILDE AND ACUTE
        + "á¹"  // U+1E4F: LATIN SMALL LETTER O WITH TILDE AND DIAERESIS
        + "á¹"  // U+1E51: LATIN SMALL LETTER O WITH MACRON AND GRAVE
        + "á¹"  // U+1E53: LATIN SMALL LETTER O WITH MACRON AND ACUTE
        + "á»"  // U+1ECD: LATIN SMALL LETTER O WITH DOT BELOW
        + "á»"  // U+1ECF: LATIN SMALL LETTER O WITH HOOK ABOVE
        + "á»"  // U+1ED1: LATIN SMALL LETTER O WITH CIRCUMFLEX AND ACUTE
        + "á»"  // U+1ED3: LATIN SMALL LETTER O WITH CIRCUMFLEX AND GRAVE
        + "á»"  // U+1ED5: LATIN SMALL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE
        + "á»"  // U+1ED7: LATIN SMALL LETTER O WITH CIRCUMFLEX AND TILDE
        + "á»"  // U+1ED9: LATIN SMALL LETTER O WITH CIRCUMFLEX AND DOT BELOW
        + "á»"  // U+1EDB: LATIN SMALL LETTER O WITH HORN AND ACUTE
        + "á»"  // U+1EDD: LATIN SMALL LETTER O WITH HORN AND GRAVE
        + "á»"  // U+1EDF: LATIN SMALL LETTER O WITH HORN AND HOOK ABOVE
        + "á»¡"  // U+1EE1: LATIN SMALL LETTER O WITH HORN AND TILDE
        + "á»£"  // U+1EE3: LATIN SMALL LETTER O WITH HORN AND DOT BELOW
        + "â"  // U+2092: LATIN SUBSCRIPT SMALL LETTER O
        + "â"  // U+24DE: CIRCLED LATIN SMALL LETTER O
        + "â±º"  // U+2C7A: LATIN SMALL LETTER O WITH LOW RING INSIDE
        + "ê"  // U+A74B: LATIN SMALL LETTER O WITH LONG STROKE OVERLAY
        + "ê"  // U+A74D: LATIN SMALL LETTER O WITH LOOP
        + "ï½"  // U+FF4F: FULLWIDTH LATIN SMALL LETTER O
       ,"o", // Folded result
 
        "Å"  // U+0152: LATIN CAPITAL LIGATURE OE
        + "É¶"  // U+0276: LATIN LETTER SMALL CAPITAL OE
       ,"OE", // Folded result
 
        "ê"  // U+A74E: LATIN CAPITAL LETTER OO
       ,"OO", // Folded result
 
        "È¢"  // U+0222: LATIN CAPITAL LETTER OU
        + "á´"  // U+1D15: LATIN LETTER SMALL CAPITAL OU
       ,"OU", // Folded result
 
        "âª"  // U+24AA: PARENTHESIZED LATIN SMALL LETTER O
       ,"(o)", // Folded result
 
        "Å"  // U+0153: LATIN SMALL LIGATURE OE
        + "á´"  // U+1D14: LATIN SMALL LETTER TURNED OE
       ,"oe", // Folded result
 
        "ê"  // U+A74F: LATIN SMALL LETTER OO
       ,"oo", // Folded result
 
        "È£"  // U+0223: LATIN SMALL LETTER OU
       ,"ou", // Folded result
 
        "Æ¤"  // U+01A4: LATIN CAPITAL LETTER P WITH HOOK
        + "á´"  // U+1D18: LATIN LETTER SMALL CAPITAL P
        + "á¹"  // U+1E54: LATIN CAPITAL LETTER P WITH ACUTE
        + "á¹"  // U+1E56: LATIN CAPITAL LETTER P WITH DOT ABOVE
        + "â"  // U+24C5: CIRCLED LATIN CAPITAL LETTER P
        + "â±£"  // U+2C63: LATIN CAPITAL LETTER P WITH STROKE
        + "ê"  // U+A750: LATIN CAPITAL LETTER P WITH STROKE THROUGH DESCENDER
        + "ê"  // U+A752: LATIN CAPITAL LETTER P WITH FLOURISH
        + "ê"  // U+A754: LATIN CAPITAL LETTER P WITH SQUIRREL TAIL
        + "ï¼°"  // U+FF30: FULLWIDTH LATIN CAPITAL LETTER P
       ,"P", // Folded result
 
        "Æ¥"  // U+01A5: LATIN SMALL LETTER P WITH HOOK
        + "áµ±"  // U+1D71: LATIN SMALL LETTER P WITH MIDDLE TILDE
        + "áµ½"  // U+1D7D: LATIN SMALL LETTER P WITH STROKE
        + "á¶"  // U+1D88: LATIN SMALL LETTER P WITH PALATAL HOOK
        + "á¹"  // U+1E55: LATIN SMALL LETTER P WITH ACUTE
        + "á¹"  // U+1E57: LATIN SMALL LETTER P WITH DOT ABOVE
        + "â"  // U+24DF: CIRCLED LATIN SMALL LETTER P
        + "ê"  // U+A751: LATIN SMALL LETTER P WITH STROKE THROUGH DESCENDER
        + "ê"  // U+A753: LATIN SMALL LETTER P WITH FLOURISH
        + "ê"  // U+A755: LATIN SMALL LETTER P WITH SQUIRREL TAIL
        + "ê¼"  // U+A7FC: LATIN EPIGRAPHIC LETTER REVERSED P
        + "ï½"  // U+FF50: FULLWIDTH LATIN SMALL LETTER P
       ,"p", // Folded result
 
        "â«"  // U+24AB: PARENTHESIZED LATIN SMALL LETTER P
       ,"(p)", // Folded result
 
        "É"  // U+024A: LATIN CAPITAL LETTER SMALL Q WITH HOOK TAIL
        + "â"  // U+24C6: CIRCLED LATIN CAPITAL LETTER Q
        + "ê"  // U+A756: LATIN CAPITAL LETTER Q WITH STROKE THROUGH DESCENDER
        + "ê"  // U+A758: LATIN CAPITAL LETTER Q WITH DIAGONAL STROKE
        + "ï¼±"  // U+FF31: FULLWIDTH LATIN CAPITAL LETTER Q
       ,"Q", // Folded result
 
        "Ä¸"  // U+0138: LATIN SMALL LETTER KRA
        + "É"  // U+024B: LATIN SMALL LETTER Q WITH HOOK TAIL
        + "Ê "  // U+02A0: LATIN SMALL LETTER Q WITH HOOK
        + "â "  // U+24E0: CIRCLED LATIN SMALL LETTER Q
        + "ê"  // U+A757: LATIN SMALL LETTER Q WITH STROKE THROUGH DESCENDER
        + "ê"  // U+A759: LATIN SMALL LETTER Q WITH DIAGONAL STROKE
        + "ï½"  // U+FF51: FULLWIDTH LATIN SMALL LETTER Q
       ,"q", // Folded result
 
        "â¬"  // U+24AC: PARENTHESIZED LATIN SMALL LETTER Q
       ,"(q)", // Folded result
 
        "È¹"  // U+0239: LATIN SMALL LETTER QP DIGRAPH
       ,"qp", // Folded result
 
        "Å"  // U+0154: LATIN CAPITAL LETTER R WITH ACUTE
        + "Å"  // U+0156: LATIN CAPITAL LETTER R WITH CEDILLA
        + "Å"  // U+0158: LATIN CAPITAL LETTER R WITH CARON
        + "È"  // U+0210: LATIN CAPITAL LETTER R WITH DOUBLE GRAVE
        + "È"  // U+0212: LATIN CAPITAL LETTER R WITH INVERTED BREVE
        + "É"  // U+024C: LATIN CAPITAL LETTER R WITH STROKE
        + "Ê"  // U+0280: LATIN LETTER SMALL CAPITAL R
        + "Ê"  // U+0281: LATIN LETTER SMALL CAPITAL INVERTED R
        + "á´"  // U+1D19: LATIN LETTER SMALL CAPITAL REVERSED R
        + "á´"  // U+1D1A: LATIN LETTER SMALL CAPITAL TURNED R
        + "á¹"  // U+1E58: LATIN CAPITAL LETTER R WITH DOT ABOVE
        + "á¹"  // U+1E5A: LATIN CAPITAL LETTER R WITH DOT BELOW
        + "á¹"  // U+1E5C: LATIN CAPITAL LETTER R WITH DOT BELOW AND MACRON
        + "á¹"  // U+1E5E: LATIN CAPITAL LETTER R WITH LINE BELOW
        + "â"  // U+24C7: CIRCLED LATIN CAPITAL LETTER R
        + "â±¤"  // U+2C64: LATIN CAPITAL LETTER R WITH TAIL
        + "ê"  // U+A75A: LATIN CAPITAL LETTER R ROTUNDA
        + "ê"  // U+A782: LATIN CAPITAL LETTER INSULAR R
        + "ï¼²"  // U+FF32: FULLWIDTH LATIN CAPITAL LETTER R
       ,"R", // Folded result
 
        "Å"  // U+0155: LATIN SMALL LETTER R WITH ACUTE
        + "Å"  // U+0157: LATIN SMALL LETTER R WITH CEDILLA
        + "Å"  // U+0159: LATIN SMALL LETTER R WITH CARON
        + "È"  // U+0211: LATIN SMALL LETTER R WITH DOUBLE GRAVE
        + "È"  // U+0213: LATIN SMALL LETTER R WITH INVERTED BREVE
        + "É"  // U+024D: LATIN SMALL LETTER R WITH STROKE
        + "É¼"  // U+027C: LATIN SMALL LETTER R WITH LONG LEG
        + "É½"  // U+027D: LATIN SMALL LETTER R WITH TAIL
        + "É¾"  // U+027E: LATIN SMALL LETTER R WITH FISHHOOK
        + "É¿"  // U+027F: LATIN SMALL LETTER REVERSED R WITH FISHHOOK
        + "áµ£"  // U+1D63: LATIN SUBSCRIPT SMALL LETTER R
        + "áµ²"  // U+1D72: LATIN SMALL LETTER R WITH MIDDLE TILDE
        + "áµ³"  // U+1D73: LATIN SMALL LETTER R WITH FISHHOOK AND MIDDLE TILDE
        + "á¶"  // U+1D89: LATIN SMALL LETTER R WITH PALATAL HOOK
        + "á¹"  // U+1E59: LATIN SMALL LETTER R WITH DOT ABOVE
        + "á¹"  // U+1E5B: LATIN SMALL LETTER R WITH DOT BELOW
        + "á¹"  // U+1E5D: LATIN SMALL LETTER R WITH DOT BELOW AND MACRON
        + "á¹"  // U+1E5F: LATIN SMALL LETTER R WITH LINE BELOW
        + "â¡"  // U+24E1: CIRCLED LATIN SMALL LETTER R
        + "ê"  // U+A75B: LATIN SMALL LETTER R ROTUNDA
        + "ê"  // U+A783: LATIN SMALL LETTER INSULAR R
        + "ï½"  // U+FF52: FULLWIDTH LATIN SMALL LETTER R
       ,"r", // Folded result
 
        "â­"  // U+24AD: PARENTHESIZED LATIN SMALL LETTER R
       ,"(r)", // Folded result
 
        "Å"  // U+015A: LATIN CAPITAL LETTER S WITH ACUTE
        + "Å"  // U+015C: LATIN CAPITAL LETTER S WITH CIRCUMFLEX
        + "Å"  // U+015E: LATIN CAPITAL LETTER S WITH CEDILLA
        + "Å "  // U+0160: LATIN CAPITAL LETTER S WITH CARON
        + "È"  // U+0218: LATIN CAPITAL LETTER S WITH COMMA BELOW
        + "á¹ "  // U+1E60: LATIN CAPITAL LETTER S WITH DOT ABOVE
        + "á¹¢"  // U+1E62: LATIN CAPITAL LETTER S WITH DOT BELOW
        + "á¹¤"  // U+1E64: LATIN CAPITAL LETTER S WITH ACUTE AND DOT ABOVE
        + "á¹¦"  // U+1E66: LATIN CAPITAL LETTER S WITH CARON AND DOT ABOVE
        + "á¹¨"  // U+1E68: LATIN CAPITAL LETTER S WITH DOT BELOW AND DOT ABOVE
        + "â"  // U+24C8: CIRCLED LATIN CAPITAL LETTER S
        + "ê±"  // U+A731: LATIN LETTER SMALL CAPITAL S
        + "ê"  // U+A785: LATIN SMALL LETTER INSULAR S
        + "ï¼³"  // U+FF33: FULLWIDTH LATIN CAPITAL LETTER S
       ,"S", // Folded result
 
        "Å"  // U+015B: LATIN SMALL LETTER S WITH ACUTE
        + "Å"  // U+015D: LATIN SMALL LETTER S WITH CIRCUMFLEX
        + "Å"  // U+015F: LATIN SMALL LETTER S WITH CEDILLA
        + "Å¡"  // U+0161: LATIN SMALL LETTER S WITH CARON
        + "Å¿"  // U+017F: LATIN SMALL LETTER LONG S
        + "È"  // U+0219: LATIN SMALL LETTER S WITH COMMA BELOW
        + "È¿"  // U+023F: LATIN SMALL LETTER S WITH SWASH TAIL
        + "Ê"  // U+0282: LATIN SMALL LETTER S WITH HOOK
        + "áµ´"  // U+1D74: LATIN SMALL LETTER S WITH MIDDLE TILDE
        + "á¶"  // U+1D8A: LATIN SMALL LETTER S WITH PALATAL HOOK
        + "á¹¡"  // U+1E61: LATIN SMALL LETTER S WITH DOT ABOVE
        + "á¹£"  // U+1E63: LATIN SMALL LETTER S WITH DOT BELOW
        + "á¹¥"  // U+1E65: LATIN SMALL LETTER S WITH ACUTE AND DOT ABOVE
        + "á¹§"  // U+1E67: LATIN SMALL LETTER S WITH CARON AND DOT ABOVE
        + "á¹©"  // U+1E69: LATIN SMALL LETTER S WITH DOT BELOW AND DOT ABOVE
        + "áº"  // U+1E9C: LATIN SMALL LETTER LONG S WITH DIAGONAL STROKE
        + "áº"  // U+1E9D: LATIN SMALL LETTER LONG S WITH HIGH STROKE
        + "â¢"  // U+24E2: CIRCLED LATIN SMALL LETTER S
        + "ê"  // U+A784: LATIN CAPITAL LETTER INSULAR S
        + "ï½"  // U+FF53: FULLWIDTH LATIN SMALL LETTER S
       ,"s", // Folded result
 
        "áº"  // U+1E9E: LATIN CAPITAL LETTER SHARP S
       ,"SS", // Folded result
 
        "â®"  // U+24AE: PARENTHESIZED LATIN SMALL LETTER S
       ,"(s)", // Folded result
 
        "Ã"  // U+00DF: LATIN SMALL LETTER SHARP S
       ,"ss", // Folded result
 
        "ï¬"  // U+FB06: LATIN SMALL LIGATURE ST
       ,"st", // Folded result
 
        "Å¢"  // U+0162: LATIN CAPITAL LETTER T WITH CEDILLA
        + "Å¤"  // U+0164: LATIN CAPITAL LETTER T WITH CARON
        + "Å¦"  // U+0166: LATIN CAPITAL LETTER T WITH STROKE
        + "Æ¬"  // U+01AC: LATIN CAPITAL LETTER T WITH HOOK
        + "Æ®"  // U+01AE: LATIN CAPITAL LETTER T WITH RETROFLEX HOOK
        + "È"  // U+021A: LATIN CAPITAL LETTER T WITH COMMA BELOW
        + "È¾"  // U+023E: LATIN CAPITAL LETTER T WITH DIAGONAL STROKE
        + "á´"  // U+1D1B: LATIN LETTER SMALL CAPITAL T
        + "á¹ª"  // U+1E6A: LATIN CAPITAL LETTER T WITH DOT ABOVE
        + "á¹¬"  // U+1E6C: LATIN CAPITAL LETTER T WITH DOT BELOW
        + "á¹®"  // U+1E6E: LATIN CAPITAL LETTER T WITH LINE BELOW
        + "á¹°"  // U+1E70: LATIN CAPITAL LETTER T WITH CIRCUMFLEX BELOW
        + "â"  // U+24C9: CIRCLED LATIN CAPITAL LETTER T
        + "ê"  // U+A786: LATIN CAPITAL LETTER INSULAR T
        + "ï¼´"  // U+FF34: FULLWIDTH LATIN CAPITAL LETTER T
       ,"T", // Folded result
 
        "Å£"  // U+0163: LATIN SMALL LETTER T WITH CEDILLA
        + "Å¥"  // U+0165: LATIN SMALL LETTER T WITH CARON
        + "Å§"  // U+0167: LATIN SMALL LETTER T WITH STROKE
        + "Æ«"  // U+01AB: LATIN SMALL LETTER T WITH PALATAL HOOK
        + "Æ­"  // U+01AD: LATIN SMALL LETTER T WITH HOOK
        + "È"  // U+021B: LATIN SMALL LETTER T WITH COMMA BELOW
        + "È¶"  // U+0236: LATIN SMALL LETTER T WITH CURL
        + "Ê"  // U+0287: LATIN SMALL LETTER TURNED T
        + "Ê"  // U+0288: LATIN SMALL LETTER T WITH RETROFLEX HOOK
        + "áµµ"  // U+1D75: LATIN SMALL LETTER T WITH MIDDLE TILDE
        + "á¹«"  // U+1E6B: LATIN SMALL LETTER T WITH DOT ABOVE
        + "á¹­"  // U+1E6D: LATIN SMALL LETTER T WITH DOT BELOW
        + "á¹¯"  // U+1E6F: LATIN SMALL LETTER T WITH LINE BELOW
        + "á¹±"  // U+1E71: LATIN SMALL LETTER T WITH CIRCUMFLEX BELOW
        + "áº"  // U+1E97: LATIN SMALL LETTER T WITH DIAERESIS
        + "â£"  // U+24E3: CIRCLED LATIN SMALL LETTER T
        + "â±¦"  // U+2C66: LATIN SMALL LETTER T WITH DIAGONAL STROKE
        + "ï½"  // U+FF54: FULLWIDTH LATIN SMALL LETTER T
       ,"t", // Folded result
 
        "Ã"  // U+00DE: LATIN CAPITAL LETTER THORN
        + "ê¦"  // U+A766: LATIN CAPITAL LETTER THORN WITH STROKE THROUGH DESCENDER
       ,"TH", // Folded result
 
        "ê¨"  // U+A728: LATIN CAPITAL LETTER TZ
       ,"TZ", // Folded result
 
        "â¯"  // U+24AF: PARENTHESIZED LATIN SMALL LETTER T
       ,"(t)", // Folded result
 
        "Ê¨"  // U+02A8: LATIN SMALL LETTER TC DIGRAPH WITH CURL
       ,"tc", // Folded result
 
        "Ã¾"  // U+00FE: LATIN SMALL LETTER THORN
        + "áµº"  // U+1D7A: LATIN SMALL LETTER TH WITH STRIKETHROUGH
        + "ê§"  // U+A767: LATIN SMALL LETTER THORN WITH STROKE THROUGH DESCENDER
       ,"th", // Folded result
 
        "Ê¦"  // U+02A6: LATIN SMALL LETTER TS DIGRAPH
       ,"ts", // Folded result
 
        "ê©"  // U+A729: LATIN SMALL LETTER TZ
       ,"tz", // Folded result
 
        "Ã"  // U+00D9: LATIN CAPITAL LETTER U WITH GRAVE
        + "Ã"  // U+00DA: LATIN CAPITAL LETTER U WITH ACUTE
        + "Ã"  // U+00DB: LATIN CAPITAL LETTER U WITH CIRCUMFLEX
        + "Ã"  // U+00DC: LATIN CAPITAL LETTER U WITH DIAERESIS
        + "Å¨"  // U+0168: LATIN CAPITAL LETTER U WITH TILDE
        + "Åª"  // U+016A: LATIN CAPITAL LETTER U WITH MACRON
        + "Å¬"  // U+016C: LATIN CAPITAL LETTER U WITH BREVE
        + "Å®"  // U+016E: LATIN CAPITAL LETTER U WITH RING ABOVE
        + "Å°"  // U+0170: LATIN CAPITAL LETTER U WITH DOUBLE ACUTE
        + "Å²"  // U+0172: LATIN CAPITAL LETTER U WITH OGONEK
        + "Æ¯"  // U+01AF: LATIN CAPITAL LETTER U WITH HORN
        + "Ç"  // U+01D3: LATIN CAPITAL LETTER U WITH CARON
        + "Ç"  // U+01D5: LATIN CAPITAL LETTER U WITH DIAERESIS AND MACRON
        + "Ç"  // U+01D7: LATIN CAPITAL LETTER U WITH DIAERESIS AND ACUTE
        + "Ç"  // U+01D9: LATIN CAPITAL LETTER U WITH DIAERESIS AND CARON
        + "Ç"  // U+01DB: LATIN CAPITAL LETTER U WITH DIAERESIS AND GRAVE
        + "È"  // U+0214: LATIN CAPITAL LETTER U WITH DOUBLE GRAVE
        + "È"  // U+0216: LATIN CAPITAL LETTER U WITH INVERTED BREVE
        + "É"  // U+0244: LATIN CAPITAL LETTER U BAR
        + "á´"  // U+1D1C: LATIN LETTER SMALL CAPITAL U
        + "áµ¾"  // U+1D7E: LATIN SMALL CAPITAL LETTER U WITH STROKE
        + "á¹²"  // U+1E72: LATIN CAPITAL LETTER U WITH DIAERESIS BELOW
        + "á¹´"  // U+1E74: LATIN CAPITAL LETTER U WITH TILDE BELOW
        + "á¹¶"  // U+1E76: LATIN CAPITAL LETTER U WITH CIRCUMFLEX BELOW
        + "á¹¸"  // U+1E78: LATIN CAPITAL LETTER U WITH TILDE AND ACUTE
        + "á¹º"  // U+1E7A: LATIN CAPITAL LETTER U WITH MACRON AND DIAERESIS
        + "á»¤"  // U+1EE4: LATIN CAPITAL LETTER U WITH DOT BELOW
        + "á»¦"  // U+1EE6: LATIN CAPITAL LETTER U WITH HOOK ABOVE
        + "á»¨"  // U+1EE8: LATIN CAPITAL LETTER U WITH HORN AND ACUTE
        + "á»ª"  // U+1EEA: LATIN CAPITAL LETTER U WITH HORN AND GRAVE
        + "á»¬"  // U+1EEC: LATIN CAPITAL LETTER U WITH HORN AND HOOK ABOVE
        + "á»®"  // U+1EEE: LATIN CAPITAL LETTER U WITH HORN AND TILDE
        + "á»°"  // U+1EF0: LATIN CAPITAL LETTER U WITH HORN AND DOT BELOW
        + "â"  // U+24CA: CIRCLED LATIN CAPITAL LETTER U
        + "ï¼µ"  // U+FF35: FULLWIDTH LATIN CAPITAL LETTER U
       ,"U", // Folded result
 
        "Ã¹"  // U+00F9: LATIN SMALL LETTER U WITH GRAVE
        + "Ãº"  // U+00FA: LATIN SMALL LETTER U WITH ACUTE
        + "Ã»"  // U+00FB: LATIN SMALL LETTER U WITH CIRCUMFLEX
        + "Ã¼"  // U+00FC: LATIN SMALL LETTER U WITH DIAERESIS
        + "Å©"  // U+0169: LATIN SMALL LETTER U WITH TILDE
        + "Å«"  // U+016B: LATIN SMALL LETTER U WITH MACRON
        + "Å­"  // U+016D: LATIN SMALL LETTER U WITH BREVE
        + "Å¯"  // U+016F: LATIN SMALL LETTER U WITH RING ABOVE
        + "Å±"  // U+0171: LATIN SMALL LETTER U WITH DOUBLE ACUTE
        + "Å³"  // U+0173: LATIN SMALL LETTER U WITH OGONEK
        + "Æ°"  // U+01B0: LATIN SMALL LETTER U WITH HORN
        + "Ç"  // U+01D4: LATIN SMALL LETTER U WITH CARON
        + "Ç"  // U+01D6: LATIN SMALL LETTER U WITH DIAERESIS AND MACRON
        + "Ç"  // U+01D8: LATIN SMALL LETTER U WITH DIAERESIS AND ACUTE
        + "Ç"  // U+01DA: LATIN SMALL LETTER U WITH DIAERESIS AND CARON
        + "Ç"  // U+01DC: LATIN SMALL LETTER U WITH DIAERESIS AND GRAVE
        + "È"  // U+0215: LATIN SMALL LETTER U WITH DOUBLE GRAVE
        + "È"  // U+0217: LATIN SMALL LETTER U WITH INVERTED BREVE
        + "Ê"  // U+0289: LATIN SMALL LETTER U BAR
        + "áµ¤"  // U+1D64: LATIN SUBSCRIPT SMALL LETTER U
        + "á¶"  // U+1D99: LATIN SMALL LETTER U WITH RETROFLEX HOOK
        + "á¹³"  // U+1E73: LATIN SMALL LETTER U WITH DIAERESIS BELOW
        + "á¹µ"  // U+1E75: LATIN SMALL LETTER U WITH TILDE BELOW
        + "á¹·"  // U+1E77: LATIN SMALL LETTER U WITH CIRCUMFLEX BELOW
        + "á¹¹"  // U+1E79: LATIN SMALL LETTER U WITH TILDE AND ACUTE
        + "á¹»"  // U+1E7B: LATIN SMALL LETTER U WITH MACRON AND DIAERESIS
        + "á»¥"  // U+1EE5: LATIN SMALL LETTER U WITH DOT BELOW
        + "á»§"  // U+1EE7: LATIN SMALL LETTER U WITH HOOK ABOVE
        + "á»©"  // U+1EE9: LATIN SMALL LETTER U WITH HORN AND ACUTE
        + "á»«"  // U+1EEB: LATIN SMALL LETTER U WITH HORN AND GRAVE
        + "á»­"  // U+1EED: LATIN SMALL LETTER U WITH HORN AND HOOK ABOVE
        + "á»¯"  // U+1EEF: LATIN SMALL LETTER U WITH HORN AND TILDE
        + "á»±"  // U+1EF1: LATIN SMALL LETTER U WITH HORN AND DOT BELOW
        + "â¤"  // U+24E4: CIRCLED LATIN SMALL LETTER U
        + "ï½"  // U+FF55: FULLWIDTH LATIN SMALL LETTER U
       ,"u", // Folded result
 
        "â°"  // U+24B0: PARENTHESIZED LATIN SMALL LETTER U
       ,"(u)", // Folded result
 
        "áµ«"  // U+1D6B: LATIN SMALL LETTER UE
       ,"ue", // Folded result
 
        "Æ²"  // U+01B2: LATIN CAPITAL LETTER V WITH HOOK
        + "É"  // U+0245: LATIN CAPITAL LETTER TURNED V
        + "á´ "  // U+1D20: LATIN LETTER SMALL CAPITAL V
        + "á¹¼"  // U+1E7C: LATIN CAPITAL LETTER V WITH TILDE
        + "á¹¾"  // U+1E7E: LATIN CAPITAL LETTER V WITH DOT BELOW
        + "á»¼"  // U+1EFC: LATIN CAPITAL LETTER MIDDLE-WELSH V
        + "â"  // U+24CB: CIRCLED LATIN CAPITAL LETTER V
        + "ê"  // U+A75E: LATIN CAPITAL LETTER V WITH DIAGONAL STROKE
        + "ê¨"  // U+A768: LATIN CAPITAL LETTER VEND
        + "ï¼¶"  // U+FF36: FULLWIDTH LATIN CAPITAL LETTER V
       ,"V", // Folded result
 
        "Ê"  // U+028B: LATIN SMALL LETTER V WITH HOOK
        + "Ê"  // U+028C: LATIN SMALL LETTER TURNED V
        + "áµ¥"  // U+1D65: LATIN SUBSCRIPT SMALL LETTER V
        + "á¶"  // U+1D8C: LATIN SMALL LETTER V WITH PALATAL HOOK
        + "á¹½"  // U+1E7D: LATIN SMALL LETTER V WITH TILDE
        + "á¹¿"  // U+1E7F: LATIN SMALL LETTER V WITH DOT BELOW
        + "â¥"  // U+24E5: CIRCLED LATIN SMALL LETTER V
        + "â±±"  // U+2C71: LATIN SMALL LETTER V WITH RIGHT HOOK
        + "â±´"  // U+2C74: LATIN SMALL LETTER V WITH CURL
        + "ê"  // U+A75F: LATIN SMALL LETTER V WITH DIAGONAL STROKE
        + "ï½"  // U+FF56: FULLWIDTH LATIN SMALL LETTER V
       ,"v", // Folded result
 
        "ê "  // U+A760: LATIN CAPITAL LETTER VY
       ,"VY", // Folded result
 
        "â±"  // U+24B1: PARENTHESIZED LATIN SMALL LETTER V
       ,"(v)", // Folded result
 
        "ê¡"  // U+A761: LATIN SMALL LETTER VY
       ,"vy", // Folded result
 
        "Å´"  // U+0174: LATIN CAPITAL LETTER W WITH CIRCUMFLEX
        + "Ç·"  // U+01F7: LATIN CAPITAL LETTER WYNN
        + "á´¡"  // U+1D21: LATIN LETTER SMALL CAPITAL W
        + "áº"  // U+1E80: LATIN CAPITAL LETTER W WITH GRAVE
        + "áº"  // U+1E82: LATIN CAPITAL LETTER W WITH ACUTE
        + "áº"  // U+1E84: LATIN CAPITAL LETTER W WITH DIAERESIS
        + "áº"  // U+1E86: LATIN CAPITAL LETTER W WITH DOT ABOVE
        + "áº"  // U+1E88: LATIN CAPITAL LETTER W WITH DOT BELOW
        + "â"  // U+24CC: CIRCLED LATIN CAPITAL LETTER W
        + "â±²"  // U+2C72: LATIN CAPITAL LETTER W WITH HOOK
        + "ï¼·"  // U+FF37: FULLWIDTH LATIN CAPITAL LETTER W
       ,"W", // Folded result
 
        "Åµ"  // U+0175: LATIN SMALL LETTER W WITH CIRCUMFLEX
        + "Æ¿"  // U+01BF: LATIN LETTER WYNN
        + "Ê"  // U+028D: LATIN SMALL LETTER TURNED W
        + "áº"  // U+1E81: LATIN SMALL LETTER W WITH GRAVE
        + "áº"  // U+1E83: LATIN SMALL LETTER W WITH ACUTE
        + "áº"  // U+1E85: LATIN SMALL LETTER W WITH DIAERESIS
        + "áº"  // U+1E87: LATIN SMALL LETTER W WITH DOT ABOVE
        + "áº"  // U+1E89: LATIN SMALL LETTER W WITH DOT BELOW
        + "áº"  // U+1E98: LATIN SMALL LETTER W WITH RING ABOVE
        + "â¦"  // U+24E6: CIRCLED LATIN SMALL LETTER W
        + "â±³"  // U+2C73: LATIN SMALL LETTER W WITH HOOK
        + "ï½"  // U+FF57: FULLWIDTH LATIN SMALL LETTER W
       ,"w", // Folded result
 
        "â²"  // U+24B2: PARENTHESIZED LATIN SMALL LETTER W
       ,"(w)", // Folded result
 
        "áº"  // U+1E8A: LATIN CAPITAL LETTER X WITH DOT ABOVE
        + "áº"  // U+1E8C: LATIN CAPITAL LETTER X WITH DIAERESIS
        + "â"  // U+24CD: CIRCLED LATIN CAPITAL LETTER X
        + "ï¼¸"  // U+FF38: FULLWIDTH LATIN CAPITAL LETTER X
       ,"X", // Folded result
 
        "á¶"  // U+1D8D: LATIN SMALL LETTER X WITH PALATAL HOOK
        + "áº"  // U+1E8B: LATIN SMALL LETTER X WITH DOT ABOVE
        + "áº"  // U+1E8D: LATIN SMALL LETTER X WITH DIAERESIS
        + "â"  // U+2093: LATIN SUBSCRIPT SMALL LETTER X
        + "â§"  // U+24E7: CIRCLED LATIN SMALL LETTER X
        + "ï½"  // U+FF58: FULLWIDTH LATIN SMALL LETTER X
       ,"x", // Folded result
 
        "â³"  // U+24B3: PARENTHESIZED LATIN SMALL LETTER X
       ,"(x)", // Folded result
 
        "Ã"  // U+00DD: LATIN CAPITAL LETTER Y WITH ACUTE
        + "Å¶"  // U+0176: LATIN CAPITAL LETTER Y WITH CIRCUMFLEX
        + "Å¸"  // U+0178: LATIN CAPITAL LETTER Y WITH DIAERESIS
        + "Æ³"  // U+01B3: LATIN CAPITAL LETTER Y WITH HOOK
        + "È²"  // U+0232: LATIN CAPITAL LETTER Y WITH MACRON
        + "É"  // U+024E: LATIN CAPITAL LETTER Y WITH STROKE
        + "Ê"  // U+028F: LATIN LETTER SMALL CAPITAL Y
        + "áº"  // U+1E8E: LATIN CAPITAL LETTER Y WITH DOT ABOVE
        + "á»²"  // U+1EF2: LATIN CAPITAL LETTER Y WITH GRAVE
        + "á»´"  // U+1EF4: LATIN CAPITAL LETTER Y WITH DOT BELOW
        + "á»¶"  // U+1EF6: LATIN CAPITAL LETTER Y WITH HOOK ABOVE
        + "á»¸"  // U+1EF8: LATIN CAPITAL LETTER Y WITH TILDE
        + "á»¾"  // U+1EFE: LATIN CAPITAL LETTER Y WITH LOOP
        + "â"  // U+24CE: CIRCLED LATIN CAPITAL LETTER Y
        + "ï¼¹"  // U+FF39: FULLWIDTH LATIN CAPITAL LETTER Y
       ,"Y", // Folded result
 
        "Ã½"  // U+00FD: LATIN SMALL LETTER Y WITH ACUTE
        + "Ã¿"  // U+00FF: LATIN SMALL LETTER Y WITH DIAERESIS
        + "Å·"  // U+0177: LATIN SMALL LETTER Y WITH CIRCUMFLEX
        + "Æ´"  // U+01B4: LATIN SMALL LETTER Y WITH HOOK
        + "È³"  // U+0233: LATIN SMALL LETTER Y WITH MACRON
        + "É"  // U+024F: LATIN SMALL LETTER Y WITH STROKE
        + "Ê"  // U+028E: LATIN SMALL LETTER TURNED Y
        + "áº"  // U+1E8F: LATIN SMALL LETTER Y WITH DOT ABOVE
        + "áº"  // U+1E99: LATIN SMALL LETTER Y WITH RING ABOVE
        + "á»³"  // U+1EF3: LATIN SMALL LETTER Y WITH GRAVE
        + "á»µ"  // U+1EF5: LATIN SMALL LETTER Y WITH DOT BELOW
        + "á»·"  // U+1EF7: LATIN SMALL LETTER Y WITH HOOK ABOVE
        + "á»¹"  // U+1EF9: LATIN SMALL LETTER Y WITH TILDE
        + "á»¿"  // U+1EFF: LATIN SMALL LETTER Y WITH LOOP
        + "â¨"  // U+24E8: CIRCLED LATIN SMALL LETTER Y
        + "ï½"  // U+FF59: FULLWIDTH LATIN SMALL LETTER Y
       ,"y", // Folded result
 
        "â´"  // U+24B4: PARENTHESIZED LATIN SMALL LETTER Y
       ,"(y)", // Folded result
 
        "Å¹"  // U+0179: LATIN CAPITAL LETTER Z WITH ACUTE
        + "Å»"  // U+017B: LATIN CAPITAL LETTER Z WITH DOT ABOVE
        + "Å½"  // U+017D: LATIN CAPITAL LETTER Z WITH CARON
        + "Æµ"  // U+01B5: LATIN CAPITAL LETTER Z WITH STROKE
        + "È"  // U+021C: LATIN CAPITAL LETTER YOGH
        + "È¤"  // U+0224: LATIN CAPITAL LETTER Z WITH HOOK
        + "á´¢"  // U+1D22: LATIN LETTER SMALL CAPITAL Z
        + "áº"  // U+1E90: LATIN CAPITAL LETTER Z WITH CIRCUMFLEX
        + "áº"  // U+1E92: LATIN CAPITAL LETTER Z WITH DOT BELOW
        + "áº"  // U+1E94: LATIN CAPITAL LETTER Z WITH LINE BELOW
        + "â"  // U+24CF: CIRCLED LATIN CAPITAL LETTER Z
        + "â±«"  // U+2C6B: LATIN CAPITAL LETTER Z WITH DESCENDER
        + "ê¢"  // U+A762: LATIN CAPITAL LETTER VISIGOTHIC Z
        + "ï¼º"  // U+FF3A: FULLWIDTH LATIN CAPITAL LETTER Z
       ,"Z", // Folded result
 
        "Åº"  // U+017A: LATIN SMALL LETTER Z WITH ACUTE
        + "Å¼"  // U+017C: LATIN SMALL LETTER Z WITH DOT ABOVE
        + "Å¾"  // U+017E: LATIN SMALL LETTER Z WITH CARON
        + "Æ¶"  // U+01B6: LATIN SMALL LETTER Z WITH STROKE
        + "È"  // U+021D: LATIN SMALL LETTER YOGH
        + "È¥"  // U+0225: LATIN SMALL LETTER Z WITH HOOK
        + "É"  // U+0240: LATIN SMALL LETTER Z WITH SWASH TAIL
        + "Ê"  // U+0290: LATIN SMALL LETTER Z WITH RETROFLEX HOOK
        + "Ê"  // U+0291: LATIN SMALL LETTER Z WITH CURL
        + "áµ¶"  // U+1D76: LATIN SMALL LETTER Z WITH MIDDLE TILDE
        + "á¶"  // U+1D8E: LATIN SMALL LETTER Z WITH PALATAL HOOK
        + "áº"  // U+1E91: LATIN SMALL LETTER Z WITH CIRCUMFLEX
        + "áº"  // U+1E93: LATIN SMALL LETTER Z WITH DOT BELOW
        + "áº"  // U+1E95: LATIN SMALL LETTER Z WITH LINE BELOW
        + "â©"  // U+24E9: CIRCLED LATIN SMALL LETTER Z
        + "â±¬"  // U+2C6C: LATIN SMALL LETTER Z WITH DESCENDER
        + "ê£"  // U+A763: LATIN SMALL LETTER VISIGOTHIC Z
        + "ï½"  // U+FF5A: FULLWIDTH LATIN SMALL LETTER Z
       ,"z", // Folded result
 
        "âµ"  // U+24B5: PARENTHESIZED LATIN SMALL LETTER Z
       ,"(z)", // Folded result
 
        "â°"  // U+2070: SUPERSCRIPT ZERO
        + "â"  // U+2080: SUBSCRIPT ZERO
        + "âª"  // U+24EA: CIRCLED DIGIT ZERO
        + "â¿"  // U+24FF: NEGATIVE CIRCLED DIGIT ZERO
        + "ï¼"  // U+FF10: FULLWIDTH DIGIT ZERO
       ,"0", // Folded result
 
        "Â¹"  // U+00B9: SUPERSCRIPT ONE
        + "â"  // U+2081: SUBSCRIPT ONE
        + "â "  // U+2460: CIRCLED DIGIT ONE
        + "âµ"  // U+24F5: DOUBLE CIRCLED DIGIT ONE
        + "â¶"  // U+2776: DINGBAT NEGATIVE CIRCLED DIGIT ONE
        + "â"  // U+2780: DINGBAT CIRCLED SANS-SERIF DIGIT ONE
        + "â"  // U+278A: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT ONE
        + "ï¼"  // U+FF11: FULLWIDTH DIGIT ONE
       ,"1", // Folded result
 
        "â"  // U+2488: DIGIT ONE FULL STOP
       ,"1.", // Folded result
 
        "â´"  // U+2474: PARENTHESIZED DIGIT ONE
       ,"(1)", // Folded result
 
        "Â²"  // U+00B2: SUPERSCRIPT TWO
        + "â"  // U+2082: SUBSCRIPT TWO
        + "â¡"  // U+2461: CIRCLED DIGIT TWO
        + "â¶"  // U+24F6: DOUBLE CIRCLED DIGIT TWO
        + "â·"  // U+2777: DINGBAT NEGATIVE CIRCLED DIGIT TWO
        + "â"  // U+2781: DINGBAT CIRCLED SANS-SERIF DIGIT TWO
        + "â"  // U+278B: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT TWO
        + "ï¼"  // U+FF12: FULLWIDTH DIGIT TWO
       ,"2", // Folded result
 
        "â"  // U+2489: DIGIT TWO FULL STOP
       ,"2.", // Folded result
 
        "âµ"  // U+2475: PARENTHESIZED DIGIT TWO
       ,"(2)", // Folded result
 
        "Â³"  // U+00B3: SUPERSCRIPT THREE
        + "â"  // U+2083: SUBSCRIPT THREE
        + "â¢"  // U+2462: CIRCLED DIGIT THREE
        + "â·"  // U+24F7: DOUBLE CIRCLED DIGIT THREE
        + "â¸"  // U+2778: DINGBAT NEGATIVE CIRCLED DIGIT THREE
        + "â"  // U+2782: DINGBAT CIRCLED SANS-SERIF DIGIT THREE
        + "â"  // U+278C: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT THREE
        + "ï¼"  // U+FF13: FULLWIDTH DIGIT THREE
       ,"3", // Folded result
 
        "â"  // U+248A: DIGIT THREE FULL STOP
       ,"3.", // Folded result
 
        "â¶"  // U+2476: PARENTHESIZED DIGIT THREE
       ,"(3)", // Folded result
 
        "â´"  // U+2074: SUPERSCRIPT FOUR
        + "â"  // U+2084: SUBSCRIPT FOUR
        + "â£"  // U+2463: CIRCLED DIGIT FOUR
        + "â¸"  // U+24F8: DOUBLE CIRCLED DIGIT FOUR
        + "â¹"  // U+2779: DINGBAT NEGATIVE CIRCLED DIGIT FOUR
        + "â"  // U+2783: DINGBAT CIRCLED SANS-SERIF DIGIT FOUR
        + "â"  // U+278D: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT FOUR
        + "ï¼"  // U+FF14: FULLWIDTH DIGIT FOUR
       ,"4", // Folded result
 
        "â"  // U+248B: DIGIT FOUR FULL STOP
       ,"4.", // Folded result
 
        "â·"  // U+2477: PARENTHESIZED DIGIT FOUR
       ,"(4)", // Folded result
 
        "âµ"  // U+2075: SUPERSCRIPT FIVE
        + "â"  // U+2085: SUBSCRIPT FIVE
        + "â¤"  // U+2464: CIRCLED DIGIT FIVE
        + "â¹"  // U+24F9: DOUBLE CIRCLED DIGIT FIVE
        + "âº"  // U+277A: DINGBAT NEGATIVE CIRCLED DIGIT FIVE
        + "â"  // U+2784: DINGBAT CIRCLED SANS-SERIF DIGIT FIVE
        + "â"  // U+278E: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT FIVE
        + "ï¼"  // U+FF15: FULLWIDTH DIGIT FIVE
       ,"5", // Folded result
 
        "â"  // U+248C: DIGIT FIVE FULL STOP
       ,"5.", // Folded result
 
        "â¸"  // U+2478: PARENTHESIZED DIGIT FIVE
       ,"(5)", // Folded result
 
        "â¶"  // U+2076: SUPERSCRIPT SIX
        + "â"  // U+2086: SUBSCRIPT SIX
        + "â¥"  // U+2465: CIRCLED DIGIT SIX
        + "âº"  // U+24FA: DOUBLE CIRCLED DIGIT SIX
        + "â»"  // U+277B: DINGBAT NEGATIVE CIRCLED DIGIT SIX
        + "â"  // U+2785: DINGBAT CIRCLED SANS-SERIF DIGIT SIX
        + "â"  // U+278F: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT SIX
        + "ï¼"  // U+FF16: FULLWIDTH DIGIT SIX
       ,"6", // Folded result
 
        "â"  // U+248D: DIGIT SIX FULL STOP
       ,"6.", // Folded result
 
        "â¹"  // U+2479: PARENTHESIZED DIGIT SIX
       ,"(6)", // Folded result
 
        "â·"  // U+2077: SUPERSCRIPT SEVEN
        + "â"  // U+2087: SUBSCRIPT SEVEN
        + "â¦"  // U+2466: CIRCLED DIGIT SEVEN
        + "â»"  // U+24FB: DOUBLE CIRCLED DIGIT SEVEN
        + "â¼"  // U+277C: DINGBAT NEGATIVE CIRCLED DIGIT SEVEN
        + "â"  // U+2786: DINGBAT CIRCLED SANS-SERIF DIGIT SEVEN
        + "â"  // U+2790: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT SEVEN
        + "ï¼"  // U+FF17: FULLWIDTH DIGIT SEVEN
       ,"7", // Folded result
 
        "â"  // U+248E: DIGIT SEVEN FULL STOP
       ,"7.", // Folded result
 
        "âº"  // U+247A: PARENTHESIZED DIGIT SEVEN
       ,"(7)", // Folded result
 
        "â¸"  // U+2078: SUPERSCRIPT EIGHT
        + "â"  // U+2088: SUBSCRIPT EIGHT
        + "â§"  // U+2467: CIRCLED DIGIT EIGHT
        + "â¼"  // U+24FC: DOUBLE CIRCLED DIGIT EIGHT
        + "â½"  // U+277D: DINGBAT NEGATIVE CIRCLED DIGIT EIGHT
        + "â"  // U+2787: DINGBAT CIRCLED SANS-SERIF DIGIT EIGHT
        + "â"  // U+2791: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT EIGHT
        + "ï¼"  // U+FF18: FULLWIDTH DIGIT EIGHT
       ,"8", // Folded result
 
        "â"  // U+248F: DIGIT EIGHT FULL STOP
       ,"8.", // Folded result
 
        "â»"  // U+247B: PARENTHESIZED DIGIT EIGHT
       ,"(8)", // Folded result
 
        "â¹"  // U+2079: SUPERSCRIPT NINE
        + "â"  // U+2089: SUBSCRIPT NINE
        + "â¨"  // U+2468: CIRCLED DIGIT NINE
        + "â½"  // U+24FD: DOUBLE CIRCLED DIGIT NINE
        + "â¾"  // U+277E: DINGBAT NEGATIVE CIRCLED DIGIT NINE
        + "â"  // U+2788: DINGBAT CIRCLED SANS-SERIF DIGIT NINE
        + "â"  // U+2792: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT NINE
        + "ï¼"  // U+FF19: FULLWIDTH DIGIT NINE
       ,"9", // Folded result
 
        "â"  // U+2490: DIGIT NINE FULL STOP
       ,"9.", // Folded result
 
        "â¼"  // U+247C: PARENTHESIZED DIGIT NINE
       ,"(9)", // Folded result
 
        "â©"  // U+2469: CIRCLED NUMBER TEN
        + "â¾"  // U+24FE: DOUBLE CIRCLED NUMBER TEN
        + "â¿"  // U+277F: DINGBAT NEGATIVE CIRCLED NUMBER TEN
        + "â"  // U+2789: DINGBAT CIRCLED SANS-SERIF NUMBER TEN
        + "â"  // U+2793: DINGBAT NEGATIVE CIRCLED SANS-SERIF NUMBER TEN
       ,"10", // Folded result
 
        "â"  // U+2491: NUMBER TEN FULL STOP
       ,"10.", // Folded result
 
        "â½"  // U+247D: PARENTHESIZED NUMBER TEN
       ,"(10)", // Folded result
 
        "âª"  // U+246A: CIRCLED NUMBER ELEVEN
        + "â«"  // U+24EB: NEGATIVE CIRCLED NUMBER ELEVEN
       ,"11", // Folded result
 
        "â"  // U+2492: NUMBER ELEVEN FULL STOP
       ,"11.", // Folded result
 
        "â¾"  // U+247E: PARENTHESIZED NUMBER ELEVEN
       ,"(11)", // Folded result
 
        "â«"  // U+246B: CIRCLED NUMBER TWELVE
        + "â¬"  // U+24EC: NEGATIVE CIRCLED NUMBER TWELVE
       ,"12", // Folded result
 
        "â"  // U+2493: NUMBER TWELVE FULL STOP
       ,"12.", // Folded result
 
        "â¿"  // U+247F: PARENTHESIZED NUMBER TWELVE
       ,"(12)", // Folded result
 
        "â¬"  // U+246C: CIRCLED NUMBER THIRTEEN
        + "â­"  // U+24ED: NEGATIVE CIRCLED NUMBER THIRTEEN
       ,"13", // Folded result
 
        "â"  // U+2494: NUMBER THIRTEEN FULL STOP
       ,"13.", // Folded result
 
        "â"  // U+2480: PARENTHESIZED NUMBER THIRTEEN
       ,"(13)", // Folded result
 
        "â­"  // U+246D: CIRCLED NUMBER FOURTEEN
        + "â®"  // U+24EE: NEGATIVE CIRCLED NUMBER FOURTEEN
       ,"14", // Folded result
 
        "â"  // U+2495: NUMBER FOURTEEN FULL STOP
       ,"14.", // Folded result
 
        "â"  // U+2481: PARENTHESIZED NUMBER FOURTEEN
       ,"(14)", // Folded result
 
        "â®"  // U+246E: CIRCLED NUMBER FIFTEEN
        + "â¯"  // U+24EF: NEGATIVE CIRCLED NUMBER FIFTEEN
       ,"15", // Folded result
 
        "â"  // U+2496: NUMBER FIFTEEN FULL STOP
       ,"15.", // Folded result
 
        "â"  // U+2482: PARENTHESIZED NUMBER FIFTEEN
       ,"(15)", // Folded result
 
        "â¯"  // U+246F: CIRCLED NUMBER SIXTEEN
        + "â°"  // U+24F0: NEGATIVE CIRCLED NUMBER SIXTEEN
       ,"16", // Folded result
 
        "â"  // U+2497: NUMBER SIXTEEN FULL STOP
       ,"16.", // Folded result
 
        "â"  // U+2483: PARENTHESIZED NUMBER SIXTEEN
       ,"(16)", // Folded result
 
        "â°"  // U+2470: CIRCLED NUMBER SEVENTEEN
        + "â±"  // U+24F1: NEGATIVE CIRCLED NUMBER SEVENTEEN
       ,"17", // Folded result
 
        "â"  // U+2498: NUMBER SEVENTEEN FULL STOP
       ,"17.", // Folded result
 
        "â"  // U+2484: PARENTHESIZED NUMBER SEVENTEEN
       ,"(17)", // Folded result
 
        "â±"  // U+2471: CIRCLED NUMBER EIGHTEEN
        + "â²"  // U+24F2: NEGATIVE CIRCLED NUMBER EIGHTEEN
       ,"18", // Folded result
 
        "â"  // U+2499: NUMBER EIGHTEEN FULL STOP
       ,"18.", // Folded result
 
        "â"  // U+2485: PARENTHESIZED NUMBER EIGHTEEN
       ,"(18)", // Folded result
 
        "â²"  // U+2472: CIRCLED NUMBER NINETEEN
        + "â³"  // U+24F3: NEGATIVE CIRCLED NUMBER NINETEEN
       ,"19", // Folded result
 
        "â"  // U+249A: NUMBER NINETEEN FULL STOP
       ,"19.", // Folded result
 
        "â"  // U+2486: PARENTHESIZED NUMBER NINETEEN
       ,"(19)", // Folded result
 
        "â³"  // U+2473: CIRCLED NUMBER TWENTY
        + "â´"  // U+24F4: NEGATIVE CIRCLED NUMBER TWENTY
       ,"20", // Folded result
 
        "â"  // U+249B: NUMBER TWENTY FULL STOP
       ,"20.", // Folded result
 
        "â"  // U+2487: PARENTHESIZED NUMBER TWENTY
       ,"(20)", // Folded result
 
        "Â«"  // U+00AB: LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
        + "Â»"  // U+00BB: RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
        + "â"  // U+201C: LEFT DOUBLE QUOTATION MARK
        + "â"  // U+201D: RIGHT DOUBLE QUOTATION MARK
        + "â"  // U+201E: DOUBLE LOW-9 QUOTATION MARK
        + "â³"  // U+2033: DOUBLE PRIME
        + "â¶"  // U+2036: REVERSED DOUBLE PRIME
        + "â"  // U+275D: HEAVY DOUBLE TURNED COMMA QUOTATION MARK ORNAMENT
        + "â"  // U+275E: HEAVY DOUBLE COMMA QUOTATION MARK ORNAMENT
        + "â®"  // U+276E: HEAVY LEFT-POINTING ANGLE QUOTATION MARK ORNAMENT
        + "â¯"  // U+276F: HEAVY RIGHT-POINTING ANGLE QUOTATION MARK ORNAMENT
        + "ï¼"  // U+FF02: FULLWIDTH QUOTATION MARK
       ,"\"", // Folded result
 
        "â"  // U+2018: LEFT SINGLE QUOTATION MARK
        + "â"  // U+2019: RIGHT SINGLE QUOTATION MARK
        + "â"  // U+201A: SINGLE LOW-9 QUOTATION MARK
        + "â"  // U+201B: SINGLE HIGH-REVERSED-9 QUOTATION MARK
        + "â²"  // U+2032: PRIME
        + "âµ"  // U+2035: REVERSED PRIME
        + "â¹"  // U+2039: SINGLE LEFT-POINTING ANGLE QUOTATION MARK
        + "âº"  // U+203A: SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
        + "â"  // U+275B: HEAVY SINGLE TURNED COMMA QUOTATION MARK ORNAMENT
        + "â"  // U+275C: HEAVY SINGLE COMMA QUOTATION MARK ORNAMENT
        + "ï¼"  // U+FF07: FULLWIDTH APOSTROPHE
       ,"'", // Folded result
 
        "â"  // U+2010: HYPHEN
        + "â"  // U+2011: NON-BREAKING HYPHEN
        + "â"  // U+2012: FIGURE DASH
        + "â"  // U+2013: EN DASH
        + "â"  // U+2014: EM DASH
        + "â»"  // U+207B: SUPERSCRIPT MINUS
        + "â"  // U+208B: SUBSCRIPT MINUS
        + "ï¼"  // U+FF0D: FULLWIDTH HYPHEN-MINUS
       ,"-", // Folded result
 
        "â"  // U+2045: LEFT SQUARE BRACKET WITH QUILL
        + "â²"  // U+2772: LIGHT LEFT TORTOISE SHELL BRACKET ORNAMENT
        + "ï¼»"  // U+FF3B: FULLWIDTH LEFT SQUARE BRACKET
       ,"[", // Folded result
 
        "â"  // U+2046: RIGHT SQUARE BRACKET WITH QUILL
        + "â³"  // U+2773: LIGHT RIGHT TORTOISE SHELL BRACKET ORNAMENT
        + "ï¼½"  // U+FF3D: FULLWIDTH RIGHT SQUARE BRACKET
       ,"]", // Folded result
 
        "â½"  // U+207D: SUPERSCRIPT LEFT PARENTHESIS
        + "â"  // U+208D: SUBSCRIPT LEFT PARENTHESIS
        + "â¨"  // U+2768: MEDIUM LEFT PARENTHESIS ORNAMENT
        + "âª"  // U+276A: MEDIUM FLATTENED LEFT PARENTHESIS ORNAMENT
        + "ï¼"  // U+FF08: FULLWIDTH LEFT PARENTHESIS
       ,"(", // Folded result
 
        "â¸¨"  // U+2E28: LEFT DOUBLE PARENTHESIS
       ,"((", // Folded result
 
        "â¾"  // U+207E: SUPERSCRIPT RIGHT PARENTHESIS
        + "â"  // U+208E: SUBSCRIPT RIGHT PARENTHESIS
        + "â©"  // U+2769: MEDIUM RIGHT PARENTHESIS ORNAMENT
        + "â«"  // U+276B: MEDIUM FLATTENED RIGHT PARENTHESIS ORNAMENT
        + "ï¼"  // U+FF09: FULLWIDTH RIGHT PARENTHESIS
       ,")", // Folded result
 
        "â¸©"  // U+2E29: RIGHT DOUBLE PARENTHESIS
       ,"))", // Folded result
 
        "â¬"  // U+276C: MEDIUM LEFT-POINTING ANGLE BRACKET ORNAMENT
        + "â°"  // U+2770: HEAVY LEFT-POINTING ANGLE BRACKET ORNAMENT
        + "ï¼"  // U+FF1C: FULLWIDTH LESS-THAN SIGN
       ,"<", // Folded result
 
        "â­"  // U+276D: MEDIUM RIGHT-POINTING ANGLE BRACKET ORNAMENT
        + "â±"  // U+2771: HEAVY RIGHT-POINTING ANGLE BRACKET ORNAMENT
        + "ï¼"  // U+FF1E: FULLWIDTH GREATER-THAN SIGN
       ,">", // Folded result
 
        "â´"  // U+2774: MEDIUM LEFT CURLY BRACKET ORNAMENT
        + "ï½"  // U+FF5B: FULLWIDTH LEFT CURLY BRACKET
       ,"{", // Folded result
 
        "âµ"  // U+2775: MEDIUM RIGHT CURLY BRACKET ORNAMENT
        + "ï½"  // U+FF5D: FULLWIDTH RIGHT CURLY BRACKET
       ,"}", // Folded result
 
        "âº"  // U+207A: SUPERSCRIPT PLUS SIGN
        + "â"  // U+208A: SUBSCRIPT PLUS SIGN
        + "ï¼"  // U+FF0B: FULLWIDTH PLUS SIGN
       ,"+", // Folded result
 
        "â¼"  // U+207C: SUPERSCRIPT EQUALS SIGN
        + "â"  // U+208C: SUBSCRIPT EQUALS SIGN
        + "ï¼"  // U+FF1D: FULLWIDTH EQUALS SIGN
       ,"=", // Folded result
 
        "ï¼"  // U+FF01: FULLWIDTH EXCLAMATION MARK
       ,"!", // Folded result
 
        "â¼"  // U+203C: DOUBLE EXCLAMATION MARK
       ,"!!", // Folded result
 
        "â"  // U+2049: EXCLAMATION QUESTION MARK
       ,"!?", // Folded result
 
        "ï¼"  // U+FF03: FULLWIDTH NUMBER SIGN
       ,"#", // Folded result
 
        "ï¼"  // U+FF04: FULLWIDTH DOLLAR SIGN
       ,"$", // Folded result
 
        "â"  // U+2052: COMMERCIAL MINUS SIGN
        + "ï¼"  // U+FF05: FULLWIDTH PERCENT SIGN
       ,"%", // Folded result
 
        "ï¼"  // U+FF06: FULLWIDTH AMPERSAND
       ,"&", // Folded result
 
        "â"  // U+204E: LOW ASTERISK
        + "ï¼"  // U+FF0A: FULLWIDTH ASTERISK
       ,"*", // Folded result
 
        "ï¼"  // U+FF0C: FULLWIDTH COMMA
       ,",", // Folded result
 
        "ï¼"  // U+FF0E: FULLWIDTH FULL STOP
       ,".", // Folded result
 
        "â"  // U+2044: FRACTION SLASH
        + "ï¼"  // U+FF0F: FULLWIDTH SOLIDUS
       ,"/", // Folded result
 
        "ï¼"  // U+FF1A: FULLWIDTH COLON
       ,":", // Folded result
 
        "â"  // U+204F: REVERSED SEMICOLON
        + "ï¼"  // U+FF1B: FULLWIDTH SEMICOLON
       ,";", // Folded result
 
        "ï¼"  // U+FF1F: FULLWIDTH QUESTION MARK
       ,"?", // Folded result
 
        "â"  // U+2047: DOUBLE QUESTION MARK
       ,"??", // Folded result
 
        "â"  // U+2048: QUESTION EXCLAMATION MARK
       ,"?!", // Folded result
 
        "ï¼ "  // U+FF20: FULLWIDTH COMMERCIAL AT
       ,"@", // Folded result
 
        "ï¼¼"  // U+FF3C: FULLWIDTH REVERSE SOLIDUS
       ,"\\", // Folded result
 
        "â¸"  // U+2038: CARET
        + "ï¼¾"  // U+FF3E: FULLWIDTH CIRCUMFLEX ACCENT
       ,"^", // Folded result
 
        "ï¼¿"  // U+FF3F: FULLWIDTH LOW LINE
       ,"_", // Folded result
 
        "â"  // U+2053: SWUNG DASH
        + "ï½"  // U+FF5E: FULLWIDTH TILDE
       ,"~", // Folded result
     };
 
     // Construct input text and expected output tokens
     List<String> expectedOutputTokens = new ArrayList<String>();
     StringBuilder inputText = new StringBuilder();
     for (int n = 0 ; n < foldings.length ; n += 2) {
       if (n > 0) {
         inputText.append(' ');  // Space between tokens
       }
       inputText.append(foldings[n]);
 
       // Construct the expected output token: the ASCII string to fold to,
       // duplicated as many times as the number of characters in the input text.
       StringBuilder expected = new StringBuilder();
       int numChars = foldings[n].length();
       for (int m = 0 ; m < numChars; ++m) {
         expected.append(foldings[n + 1]);
       }
       expectedOutputTokens.add(expected.toString());
     }
 
    TokenStream stream = new MockTokenizer(new StringReader(inputText.toString()), MockTokenizer.WHITESPACE, false);
     ASCIIFoldingFilter filter = new ASCIIFoldingFilter(stream);
     CharTermAttribute termAtt = filter.getAttribute(CharTermAttribute.class);
     Iterator<String> expectedIter = expectedOutputTokens.iterator();
    filter.reset();
     while (expectedIter.hasNext()) {
       assertTermEquals(expectedIter.next(), filter, termAtt);
     }
     assertFalse(filter.incrementToken());
   }
   
   void assertTermEquals(String expected, TokenStream stream, CharTermAttribute termAtt) throws Exception {
     assertTrue(stream.incrementToken());
     assertEquals(expected, termAtt.toString());
   }
 }
