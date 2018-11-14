 package org.apache.lucene.analysis.ru;
 
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
 import java.util.Arrays;
 import java.util.Map;
 import java.util.Set;
 
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.CharArraySet;
 import org.apache.lucene.analysis.LowerCaseFilter;
 import org.apache.lucene.analysis.snowball.SnowballFilter;
 import org.apache.lucene.analysis.standard.StandardFilter;
 import org.apache.lucene.analysis.standard.StandardTokenizer;
 import org.apache.lucene.analysis.KeywordMarkerFilter;
 import org.apache.lucene.analysis.StopFilter;
 import org.apache.lucene.analysis.StopwordAnalyzerBase;
 import org.apache.lucene.analysis.TokenStream;
 import org.apache.lucene.analysis.Tokenizer;
 import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.util.IOUtils;
 import org.apache.lucene.util.Version;
 
 /**
  * {@link Analyzer} for Russian language. 
  * <p>
  * Supports an external list of stopwords (words that
  * will not be indexed at all).
  * A default set of stopwords is used unless an alternative list is specified.
  * </p>
  * <a name="version"/>
  * <p>You must specify the required {@link Version}
  * compatibility when creating RussianAnalyzer:
  * <ul>
  *   <li> As of 3.1, StandardTokenizer is used, Snowball stemming is done with
  *        SnowballFilter, and Snowball stopwords are used by default.
  * </ul>
  */
 public final class RussianAnalyzer extends StopwordAnalyzerBase
 {
     /**
      * List of typical Russian stopwords. (for backwards compatibility)
      * @deprecated Remove this for LUCENE 5.0
      */
     @Deprecated
     private static final String[] RUSSIAN_STOP_WORDS_30 = {
       "Ð°", "Ð±ÐµÐ·", "Ð±Ð¾Ð»ÐµÐµ", "Ð±Ñ", "Ð±ÑÐ»", "Ð±ÑÐ»Ð°", "Ð±ÑÐ»Ð¸", "Ð±ÑÐ»Ð¾", "Ð±ÑÑÑ", "Ð²",
       "Ð²Ð°Ð¼", "Ð²Ð°Ñ", "Ð²ÐµÑÑ", "Ð²Ð¾", "Ð²Ð¾Ñ", "Ð²ÑÐµ", "Ð²ÑÐµÐ³Ð¾", "Ð²ÑÐµÑ", "Ð²Ñ", "Ð³Ð´Ðµ", 
       "Ð´Ð°", "Ð´Ð°Ð¶Ðµ", "Ð´Ð»Ñ", "Ð´Ð¾", "ÐµÐ³Ð¾", "ÐµÐµ", "ÐµÐ¹", "ÐµÑ", "ÐµÑÐ»Ð¸", "ÐµÑÑÑ", 
       "ÐµÑÐµ", "Ð¶Ðµ", "Ð·Ð°", "Ð·Ð´ÐµÑÑ", "Ð¸", "Ð¸Ð·", "Ð¸Ð»Ð¸", "Ð¸Ð¼", "Ð¸Ñ", "Ðº", "ÐºÐ°Ðº",
       "ÐºÐ¾", "ÐºÐ¾Ð³Ð´Ð°", "ÐºÑÐ¾", "Ð»Ð¸", "Ð»Ð¸Ð±Ð¾", "Ð¼Ð½Ðµ", "Ð¼Ð¾Ð¶ÐµÑ", "Ð¼Ñ", "Ð½Ð°", "Ð½Ð°Ð´Ð¾", 
       "Ð½Ð°Ñ", "Ð½Ðµ", "Ð½ÐµÐ³Ð¾", "Ð½ÐµÐµ", "Ð½ÐµÑ", "Ð½Ð¸", "Ð½Ð¸Ñ", "Ð½Ð¾", "Ð½Ñ", "Ð¾", "Ð¾Ð±", 
       "Ð¾Ð´Ð½Ð°ÐºÐ¾", "Ð¾Ð½", "Ð¾Ð½Ð°", "Ð¾Ð½Ð¸", "Ð¾Ð½Ð¾", "Ð¾Ñ", "Ð¾ÑÐµÐ½Ñ", "Ð¿Ð¾", "Ð¿Ð¾Ð´", "Ð¿ÑÐ¸", 
       "Ñ", "ÑÐ¾", "ÑÐ°Ðº", "ÑÐ°ÐºÐ¶Ðµ", "ÑÐ°ÐºÐ¾Ð¹", "ÑÐ°Ð¼", "ÑÐµ", "ÑÐµÐ¼", "ÑÐ¾", "ÑÐ¾Ð³Ð¾", 
       "ÑÐ¾Ð¶Ðµ", "ÑÐ¾Ð¹", "ÑÐ¾Ð»ÑÐºÐ¾", "ÑÐ¾Ð¼", "ÑÑ", "Ñ", "ÑÐ¶Ðµ", "ÑÐ¾ÑÑ", "ÑÐµÐ³Ð¾", "ÑÐµÐ¹", 
       "ÑÐµÐ¼", "ÑÑÐ¾", "ÑÑÐ¾Ð±Ñ", "ÑÑÐµ", "ÑÑÑ", "ÑÑÐ°", "ÑÑÐ¸", "ÑÑÐ¾", "Ñ"
     };
     
     /** File containing default Russian stopwords. */
     public final static String DEFAULT_STOPWORD_FILE = "russian_stop.txt";
     
     private static class DefaultSetHolder {
       /** @deprecated remove this for Lucene 5.0 */
       @Deprecated
       static final Set<?> DEFAULT_STOP_SET_30 = CharArraySet
           .unmodifiableSet(new CharArraySet(Version.LUCENE_CURRENT, 
               Arrays.asList(RUSSIAN_STOP_WORDS_30), false));
       static final Set<?> DEFAULT_STOP_SET;
       
       static {
         try {
          DEFAULT_STOP_SET = WordlistLoader.getSnowballWordSet(IOUtils.getDecodingReader(SnowballFilter.class, 
              DEFAULT_STOPWORD_FILE, IOUtils.CHARSET_UTF_8), Version.LUCENE_CURRENT);
         } catch (IOException ex) {
           // default set should always be present as it is part of the
           // distribution (JAR)
          throw new RuntimeException("Unable to load default stopword set", ex);
         }
       }
     }
     
     private final Set<?> stemExclusionSet;
     
     /**
      * Returns an unmodifiable instance of the default stop-words set.
      * 
      * @return an unmodifiable instance of the default stop-words set.
      */
     public static Set<?> getDefaultStopSet() {
       return DefaultSetHolder.DEFAULT_STOP_SET;
     }
 
     public RussianAnalyzer(Version matchVersion) {
       this(matchVersion,
         matchVersion.onOrAfter(Version.LUCENE_31) ? DefaultSetHolder.DEFAULT_STOP_SET
             : DefaultSetHolder.DEFAULT_STOP_SET_30);
     }
   
     /**
      * Builds an analyzer with the given stop words.
      * @deprecated use {@link #RussianAnalyzer(Version, Set)} instead
      */
     @Deprecated
     public RussianAnalyzer(Version matchVersion, String... stopwords) {
       this(matchVersion, StopFilter.makeStopSet(matchVersion, stopwords));
     }
     
     /**
      * Builds an analyzer with the given stop words
      * 
      * @param matchVersion
      *          lucene compatibility version
      * @param stopwords
      *          a stopword set
      */
     public RussianAnalyzer(Version matchVersion, Set<?> stopwords){
       this(matchVersion, stopwords, CharArraySet.EMPTY_SET);
     }
     
     /**
      * Builds an analyzer with the given stop words
      * 
      * @param matchVersion
      *          lucene compatibility version
      * @param stopwords
      *          a stopword set
      * @param stemExclusionSet a set of words not to be stemmed
      */
     public RussianAnalyzer(Version matchVersion, Set<?> stopwords, Set<?> stemExclusionSet){
       super(matchVersion, stopwords);
       this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(matchVersion, stemExclusionSet));
     }
    
    
     /**
      * Builds an analyzer with the given stop words.
      * TODO: create a Set version of this ctor
      * @deprecated use {@link #RussianAnalyzer(Version, Set)} instead
      */
     @Deprecated
     public RussianAnalyzer(Version matchVersion, Map<?,?> stopwords)
     {
       this(matchVersion, stopwords.keySet());
     }
 
   /**
    * Creates
    * {@link org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents}
    * used to tokenize all the text in the provided {@link Reader}.
    * 
    * @return {@link org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents}
    *         built from a {@link StandardTokenizer} filtered with
    *         {@link StandardFilter}, {@link LowerCaseFilter}, {@link StopFilter}
    *         , {@link KeywordMarkerFilter} if a stem exclusion set is
    *         provided, and {@link SnowballFilter}
    */
     @Override
     protected TokenStreamComponents createComponents(String fieldName,
         Reader reader) {
       if (matchVersion.onOrAfter(Version.LUCENE_31)) {
         final Tokenizer source = new StandardTokenizer(matchVersion, reader);
         TokenStream result = new StandardFilter(matchVersion, source);
         result = new LowerCaseFilter(matchVersion, result);
         result = new StopFilter(matchVersion, result, stopwords);
         if (!stemExclusionSet.isEmpty()) result = new KeywordMarkerFilter(
             result, stemExclusionSet);
         result = new SnowballFilter(result, new org.tartarus.snowball.ext.RussianStemmer());
         return new TokenStreamComponents(source, result);
       } else {
         final Tokenizer source = new RussianLetterTokenizer(matchVersion, reader);
         TokenStream result = new LowerCaseFilter(matchVersion, source);
         result = new StopFilter(matchVersion, result, stopwords);
         if (!stemExclusionSet.isEmpty()) result = new KeywordMarkerFilter(
           result, stemExclusionSet);
         return new TokenStreamComponents(source, new RussianStemFilter(result));
       }
     }
 }
