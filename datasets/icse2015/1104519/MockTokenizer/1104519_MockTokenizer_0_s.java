 package org.apache.lucene.analysis;
 
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
 
 import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
 import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
 import org.apache.lucene.util.automaton.CharacterRunAutomaton;
 import org.apache.lucene.util.automaton.RegExp;
 
 /**
  * Automaton-based tokenizer for testing. Optionally lowercases.
  */
 public class MockTokenizer extends Tokenizer {
   /** Acts Similar to WhitespaceTokenizer */
   public static final CharacterRunAutomaton WHITESPACE = 
     new CharacterRunAutomaton(new RegExp("[^ \t\r\n]+").toAutomaton());
   /** Acts Similar to KeywordTokenizer.
    * TODO: Keyword returns an "empty" token for an empty reader... 
    */
   public static final CharacterRunAutomaton KEYWORD =
     new CharacterRunAutomaton(new RegExp(".*").toAutomaton());
   /** Acts like LetterTokenizer. */
   // the ugly regex below is Unicode 5.2 [:Letter:]
   public static final CharacterRunAutomaton SIMPLE =
     new CharacterRunAutomaton(new RegExp("[A-Za-zÂªÂµÂºÃ-ÃÃ-Ã¶Ã¸-ËË-ËË -Ë¤Ë¬Ë®Í°-Í´Í¶Í·Íº-Í½ÎÎ-ÎÎÎ-Î¡Î£-ÏµÏ·-ÒÒ-Ô¥Ô±-ÕÕÕ¡-Ö×-×ª×°-×²Ø¡-ÙÙ®Ù¯Ù±-ÛÛÛ¥Û¦Û®Û¯Ûº-Û¼Û¿ÜÜ-Ü¯Ý-Þ¥Þ±ß-ßªß´ßµßºà -à à à ¤à ¨à¤-à¤¹à¤½à¥à¥-à¥¡à¥±à¥²à¥¹-à¥¿à¦-à¦à¦à¦à¦-à¦¨à¦ª-à¦°à¦²à¦¶-à¦¹à¦½à§à§à§à§-à§¡à§°à§±à¨-à¨à¨à¨à¨-à¨¨à¨ª-à¨°à¨²à¨³à¨µà¨¶à¨¸à¨¹à©-à©à©à©²-à©´àª-àªàª-àªàª-àª¨àªª-àª°àª²àª³àªµ-àª¹àª½à«à« à«¡à¬-à¬à¬à¬à¬-à¬¨à¬ª-à¬°à¬²à¬³à¬µ-à¬¹à¬½à­à­à­-à­¡à­±à®à®-à®à®-à®à®-à®à®à®à®à®à®à®£à®¤à®¨-à®ªà®®-à®¹à¯à°-à°à°-à°à°-à°¨à°ª-à°³à°µ-à°¹à°½à±à±à± à±¡à²-à²à²-à²à²-à²¨à²ª-à²³à²µ-à²¹à²½à³à³ à³¡à´-à´à´-à´à´-à´¨à´ª-à´¹à´½àµ àµ¡àµº-àµ¿à¶-à¶à¶-à¶±à¶³-à¶»à¶½à·-à·à¸-à¸°à¸²à¸³à¹-à¹àºàºàºàºàºàºàºàº-àºàº-àºàº¡-àº£àº¥àº§àºªàº«àº­-àº°àº²àº³àº½à»-à»à»à»à»à¼à½-à½à½-à½¬à¾-à¾á-áªá¿á-áá-áá¡á¥á¦á®-á°áµ-ááá -áá-áºá¼á-áá-áá-ááá-áá -áá-áá-á°á²-áµá¸-á¾áá-áá-áá-áá-áá-áá-áá -á´á-á¬á¯-á¿á-áá -áªá-áá-áá -á±á-áá -á¬á®-á°á-á³ááá  -á¡·á¢-á¢¨á¢ªá¢°-á£µá¤-á¤á¥-á¥­á¥°-á¥´á¦-á¦«á§-á§á¨-á¨á¨ -á©áª§á¬-á¬³á­-á­á®-á® á®®á®¯á°-á°£á±-á±á±-á±½á³©-á³¬á³®-á³±á´-á¶¿á¸-á¼á¼-á¼á¼ -á½á½-á½á½-á½á½á½á½á½-á½½á¾-á¾´á¾¶-á¾¼á¾¾á¿-á¿á¿-á¿á¿-á¿á¿-á¿á¿ -á¿¬á¿²-á¿´á¿¶-á¿¼â±â¿â-ââââ-âââ-ââ¤â¦â¨âª-â­â¯-â¹â¼-â¿â-âââââ°-â°®â°°-â±â± -â³¤â³«-â³®â´-â´¥â´°-âµ¥âµ¯â¶-â¶â¶ -â¶¦â¶¨-â¶®â¶°-â¶¶â¶¸-â¶¾â·-â·â·-â·â·-â·â·-â·â¸¯ããã±-ãµã»ã¼ã-ãã-ãã¡-ãºã¼-ã¿ã-ã­ã±-ãã -ã·ã°-ã¿ã-ä¶µä¸-é¿ê-êê-ê½ê-êê-êêªê«ê-êê¢-ê®ê¿-êê -ê¥ê-êê¢-êêêê»-ê ê -ê ê -ê ê -ê ¢ê¡-ê¡³ê¢-ê¢³ê£²-ê£·ê£»ê¤-ê¤¥ê¤°-ê¥ê¥ -ê¥¼ê¦-ê¦²ê§ê¨-ê¨¨ê©-ê©ê©-ê©ê© -ê©¶ê©ºêª-êª¯êª±êªµêª¶êª¹-êª½ê«ê«ê«-ê«ê¯-ê¯¢ê°-í£í°-íí-í»ï¤-ï¨­ï¨°-ï©­ï©°-ï«ï¬-ï¬ï¬-ï¬ï¬ï¬-ï¬¨ï¬ª-ï¬¶ï¬¸-ï¬¼ï¬¾ï­ï­ï­ï­ï­-ï®±ï¯-ï´½ïµ-ï¶ï¶-ï·ï·°-ï·»ï¹°-ï¹´ï¹¶-ï»¼ï¼¡-ï¼ºï½-ï½ï½¦-ï¾¾ï¿-ï¿ï¿-ï¿ï¿-ï¿ï¿-ï¿ð-ðð-ð¦ð¨-ðºð¼ð½ð¿-ðð-ðð-ðºð-ðð -ðð-ðð°-ðð-ðð-ðð -ðð-ðð-ðð -ð ð ð -ð µð ·ð ¸ð ¼ð ¿-ð¡ð¤-ð¤ð¤ -ð¤¹ð¨ð¨-ð¨ð¨-ð¨ð¨-ð¨³ð© -ð©¼ð¬-ð¬µð­-ð­ð­ -ð­²ð°-ð±ð-ð¯ð-ð®ð-ð®ð-ðð-ðððð¢ð¥ð¦ð©-ð¬ð®-ð¹ð»ð½-ðð-ðð-ðð-ðð-ðð-ð¹ð»-ð¾ð-ððð-ðð-ð¥ð¨-ðð-ðð-ðºð¼-ðð-ð´ð¶-ðð-ð®ð°-ðð-ð¨ðª-ðð-ðð -ðªðª-ð«´ð¯ -ð¯¨]+").toAutomaton());
 
   private final CharacterRunAutomaton runAutomaton;
   private final boolean lowerCase;
   private int state;
 
   private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
   private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
   int off = 0;
 
   // TODO: "register" with LuceneTestCase to ensure all streams are closed() ?
   // currently, we can only check that the lifecycle is correct if someone is reusing,
   // but not for "one-offs".
   private static enum State { 
     SETREADER,       // consumer set a reader input either via ctor or via reset(Reader)
     RESET,           // consumer has called reset()
     INCREMENT,       // consumer is consuming, has called incrementToken() == true
     INCREMENT_FALSE, // consumer has called incrementToken() which returned false
     END,             // consumer has called end() to perform end of stream operations
     CLOSE            // consumer has called close() to release any resources
   };
   
   private State streamState = State.CLOSE;
   private boolean enableChecks = true;
   
   public MockTokenizer(AttributeFactory factory, Reader input, CharacterRunAutomaton runAutomaton, boolean lowerCase) {
     super(factory, input);
     this.runAutomaton = runAutomaton;
     this.lowerCase = lowerCase;
     this.state = runAutomaton.getInitialState();
     this.streamState = State.SETREADER;
   }
 
   public MockTokenizer(Reader input, CharacterRunAutomaton runAutomaton, boolean lowerCase) {
     super(input);
     this.runAutomaton = runAutomaton;
     this.lowerCase = lowerCase;
     this.state = runAutomaton.getInitialState();
     this.streamState = State.SETREADER;
   }
   
   @Override
   public final boolean incrementToken() throws IOException {
     assert !enableChecks || (streamState == State.RESET || streamState == State.INCREMENT) 
                             : "incrementToken() called while in wrong state: " + streamState;
     clearAttributes();
     for (;;) {
       int startOffset = off;
       int cp = readCodePoint();
       if (cp < 0) {
         break;
       } else if (isTokenChar(cp)) {
         int endOffset;
         do {
           char chars[] = Character.toChars(normalize(cp));
           for (int i = 0; i < chars.length; i++)
             termAtt.append(chars[i]);
           endOffset = off;
           cp = readCodePoint();
         } while (cp >= 0 && isTokenChar(cp));
        offsetAtt.setOffset(startOffset, endOffset);
         streamState = State.INCREMENT;
         return true;
       }
     }
     streamState = State.INCREMENT_FALSE;
     return false;
   }
 
   protected int readCodePoint() throws IOException {
     int ch = input.read();
     if (ch < 0) {
       return ch;
     } else {
       assert !Character.isLowSurrogate((char) ch);
       off++;
       if (Character.isHighSurrogate((char) ch)) {
         int ch2 = input.read();
         if (ch2 >= 0) {
           off++;
           assert Character.isLowSurrogate((char) ch2);
           return Character.toCodePoint((char) ch, (char) ch2);
         }
       }
       return ch;
     }
   }
 
   protected boolean isTokenChar(int c) {
     state = runAutomaton.step(state, c);
     if (state < 0) {
       state = runAutomaton.getInitialState();
       return false;
     } else {
       return true;
     }
   }
   
   protected int normalize(int c) {
     return lowerCase ? Character.toLowerCase(c) : c;
   }
 
   @Override
   public void reset() throws IOException {
     super.reset();
     state = runAutomaton.getInitialState();
     off = 0;
     assert !enableChecks || streamState != State.RESET : "double reset()";
     streamState = State.RESET;
   }
   
   @Override
   public void close() throws IOException {
     super.close();
     // in some exceptional cases (e.g. TestIndexWriterExceptions) a test can prematurely close()
     // these tests should disable this check, by default we check the normal workflow.
     // TODO: investigate the CachingTokenFilter "double-close"... for now we ignore this
     assert !enableChecks || streamState == State.END || streamState == State.CLOSE : "close() called in wrong state: " + streamState;
     streamState = State.CLOSE;
   }
 
   @Override
   public void reset(Reader input) throws IOException {
     super.reset(input);
     assert !enableChecks || streamState == State.CLOSE : "setReader() called in wrong state: " + streamState;
     streamState = State.SETREADER;
   }
 
   @Override
   public void end() throws IOException {
     int finalOffset = correctOffset(off);
     offsetAtt.setOffset(finalOffset, finalOffset);
     // some tokenizers, such as limiting tokenizers, call end() before incrementToken() returns false.
     // these tests should disable this check (in general you should consume the entire stream)
     assert !enableChecks || streamState == State.INCREMENT_FALSE : "end() called before incrementToken() returned false!";
     streamState = State.END;
   }
 
   /** 
    * Toggle consumer workflow checking: if your test consumes tokenstreams normally you
    * should leave this enabled.
    */
   public void setEnableChecks(boolean enableChecks) {
     this.enableChecks = enableChecks;
   }
 }
