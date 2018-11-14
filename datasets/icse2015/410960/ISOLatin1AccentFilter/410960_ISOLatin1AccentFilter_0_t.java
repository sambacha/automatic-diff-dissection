 package org.apache.lucene.analysis;
 
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
 
 /**
  * A filter that replaces accented characters in the ISO Latin 1 character set 
  * (ISO-8859-1) by their unaccented equivalent. The case will not be altered.
  * <p>
  * For instance, '&agrave;' will be replaced by 'a'.
  * <p>
  */
 public class ISOLatin1AccentFilter extends TokenFilter {
 	public ISOLatin1AccentFilter(TokenStream input) {
 		super(input);
 	}
 
 	public final Token next() throws java.io.IOException {
 		final Token t = input.next();
    if (t != null)
      t.setTermText(removeAccents(t.termText()));
    return t;
 	}
 
 	/**
 	 * To replace accented characters in a String by unaccented equivalents.
 	 */
 	public final static String removeAccents(String input) {
 		final StringBuffer output = new StringBuffer();
 		for (int i = 0; i < input.length(); i++) {
 			switch (input.charAt(i)) {
 				case '\u00C0' : // Ã
 				case '\u00C1' : // Ã
 				case '\u00C2' : // Ã
 				case '\u00C3' : // Ã
 				case '\u00C4' : // Ã
 				case '\u00C5' : // Ã
 					output.append("A");
 					break;
 				case '\u00C6' : // Ã
 					output.append("AE");
 					break;
 				case '\u00C7' : // Ã
 					output.append("C");
 					break;
 				case '\u00C8' : // Ã
 				case '\u00C9' : // Ã
 				case '\u00CA' : // Ã
 				case '\u00CB' : // Ã
 					output.append("E");
 					break;
 				case '\u00CC' : // Ã
 				case '\u00CD' : // Ã
 				case '\u00CE' : // Ã
 				case '\u00CF' : // Ã
 					output.append("I");
 					break;
 				case '\u00D0' : // Ã
 					output.append("D");
 					break;
 				case '\u00D1' : // Ã
 					output.append("N");
 					break;
 				case '\u00D2' : // Ã
 				case '\u00D3' : // Ã
 				case '\u00D4' : // Ã
 				case '\u00D5' : // Ã
 				case '\u00D6' : // Ã
 				case '\u00D8' : // Ã
 					output.append("O");
 					break;
 				case '\u0152' : // Å
 					output.append("OE");
 					break;
 				case '\u00DE' : // Ã
 					output.append("TH");
 					break;
 				case '\u00D9' : // Ã
 				case '\u00DA' : // Ã
 				case '\u00DB' : // Ã
 				case '\u00DC' : // Ã
 					output.append("U");
 					break;
 				case '\u00DD' : // Ã
 				case '\u0178' : // Å¸
 					output.append("Y");
 					break;
 				case '\u00E0' : // Ã 
 				case '\u00E1' : // Ã¡
 				case '\u00E2' : // Ã¢
 				case '\u00E3' : // Ã£
 				case '\u00E4' : // Ã¤
 				case '\u00E5' : // Ã¥
 					output.append("a");
 					break;
 				case '\u00E6' : // Ã¦
 					output.append("ae");
 					break;
 				case '\u00E7' : // Ã§
 					output.append("c");
 					break;
 				case '\u00E8' : // Ã¨
 				case '\u00E9' : // Ã©
 				case '\u00EA' : // Ãª
 				case '\u00EB' : // Ã«
 					output.append("e");
 					break;
 				case '\u00EC' : // Ã¬
 				case '\u00ED' : // Ã­
 				case '\u00EE' : // Ã®
 				case '\u00EF' : // Ã¯
 					output.append("i");
 					break;
 				case '\u00F0' : // Ã°
 					output.append("d");
 					break;
 				case '\u00F1' : // Ã±
 					output.append("n");
 					break;
 				case '\u00F2' : // Ã²
 				case '\u00F3' : // Ã³
 				case '\u00F4' : // Ã´
 				case '\u00F5' : // Ãµ
 				case '\u00F6' : // Ã¶
 				case '\u00F8' : // Ã¸
 					output.append("o");
 					break;
 				case '\u0153' : // Å
 					output.append("oe");
 					break;
 				case '\u00DF' : // Ã
 					output.append("ss");
 					break;
 				case '\u00FE' : // Ã¾
 					output.append("th");
 					break;
 				case '\u00F9' : // Ã¹
 				case '\u00FA' : // Ãº
 				case '\u00FB' : // Ã»
 				case '\u00FC' : // Ã¼
 					output.append("u");
 					break;
 				case '\u00FD' : // Ã½
 				case '\u00FF' : // Ã¿
 					output.append("y");
 					break;
 				default :
 					output.append(input.charAt(i));
 					break;
 			}
 		}
 		return output.toString();
 	}
 }
