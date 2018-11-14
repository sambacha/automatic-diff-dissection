 /* Generated By:JavaCC: Do not edit this line. QueryParser.java */
 package org.apache.lucene.queryParser;
 
 import java.util.Vector;
 import java.io.*;
 import java.text.*;
 import java.util.*;
 import org.apache.lucene.index.Term;
 import org.apache.lucene.analysis.*;
 import org.apache.lucene.document.*;
 import org.apache.lucene.search.*;
 
 /**
  * This class is generated by JavaCC.  The only method that clients should need
 * to call is <a href="#parse">parse()</a>.
  *
  * The syntax for query strings is as follows:
  * A Query is a series of clauses.
  * A clause may be prefixed by:
  * <ul>
  * <li> a plus (<code>+</code>) or a minus (<code>-</code>) sign, indicating
  * that the clause is required or prohibited respectively; or
  * <li> a term followed by a colon, indicating the field to be searched.
  * This enables one to construct queries which search multiple fields.
  * </ul>
  *
  * A clause may be either:
  * <ul>
  * <li> a term, indicating all the documents that contain this term; or
  * <li> a nested query, enclosed in parentheses.  Note that this may be used
  * with a <code>+</code>/<code>-</code> prefix to require any of a set of
  * terms.
  * </ul>
  *
  * Thus, in BNF, the query grammar is:
  * <pre>
  *   Query  ::= ( Clause )*
  *   Clause ::= ["+", "-"] [&lt;TERM&gt; ":"] ( &lt;TERM&gt; | "(" Query ")" )
  * </pre>
  *
  * <p>
  * Examples of appropriately formatted queries can be found in the <a
 * href="http://jakarta.apache.org/lucene/src/test/org/apache/lucene/queryParser/TestQueryParser.java">test cases</a>.
  * </p>
  *
  * @author Brian Goetz
  * @author Peter Halacsy
  * @author Tatu Saloranta
  */
 
 public class QueryParser implements QueryParserConstants {
 
   private static final int CONJ_NONE   = 0;
   private static final int CONJ_AND    = 1;
   private static final int CONJ_OR     = 2;
 
   private static final int MOD_NONE    = 0;
   private static final int MOD_NOT     = 10;
   private static final int MOD_REQ     = 11;
 
   public static final int DEFAULT_OPERATOR_OR  = 0;
   public static final int DEFAULT_OPERATOR_AND = 1;
 
   /** The actual operator that parser uses to combine query terms */
   private int operator = DEFAULT_OPERATOR_OR;
 
  /**
   * Whether terms of wildcard and prefix queries are to be automatically
   * lower-cased or not.  Default is <code>true</code>.
   */
   boolean lowercaseWildcardTerms = true;
 
   Analyzer analyzer;
   String field;
   int phraseSlop = 0;
   float fuzzyMinSim = FuzzyQuery.defaultMinSimilarity;
   int fuzzyPrefixLength = FuzzyQuery.defaultPrefixLength;
   Locale locale = Locale.getDefault();
 
   /** Parses a query string, returning a {@link org.apache.lucene.search.Query}.
    *  @param query  the query string to be parsed.
    *  @param field  the default field for query terms.
    *  @param analyzer   used to find terms in the query text.
    *  @throws ParseException if the parsing fails
    */
   static public Query parse(String query, String field, Analyzer analyzer)
        throws ParseException {
     QueryParser parser = new QueryParser(field, analyzer);
     return parser.parse(query);
   }
 
   /** Constructs a query parser.
    *  @param f  the default field for query terms.
    *  @param a   used to find terms in the query text.
    */
   public QueryParser(String f, Analyzer a) {
     this(new FastCharStream(new StringReader("")));
     analyzer = a;
     field = f;
   }
 
  /** Parses a query string, returning a
   * <a href="lucene.search.Query.html">Query</a>.
    *  @param query  the query string to be parsed.
    *  @throws ParseException if the parsing fails
    */
   public Query parse(String query) throws ParseException {
     ReInit(new FastCharStream(new StringReader(query)));
     try {
       return Query(field);
     }
     catch (TokenMgrError tme) {
       throw new ParseException(tme.getMessage());
     }
     catch (BooleanQuery.TooManyClauses tmc) {
       throw new ParseException("Too many boolean clauses");
     }
   }
 
    /**
    * @return Returns the analyzer.
    */
   public Analyzer getAnalyzer() {
     return analyzer;
   }
 
   /**
    * @return Returns the field.
    */
   public String getField() {
     return field;
   }
 
    /**
    * Get the minimal similarity for fuzzy queries.
    */
   public float getFuzzyMinSim() {
       return fuzzyMinSim;
   }
 
   /**
    * Set the minimum similarity for fuzzy queries.
    * Default is 0.5f.
    */
   public void setFuzzyMinSim(float fuzzyMinSim) {
       this.fuzzyMinSim = fuzzyMinSim;
   }
 
    /**
    * Get the prefix length for fuzzy queries. 
    * @return Returns the fuzzyPrefixLength.
    */
   public int getFuzzyPrefixLength() {
     return fuzzyPrefixLength;
   }
 
   /**
   * Set the prefix length for fuzzy queries. Default is 2.
    * @param fuzzyPrefixLength The fuzzyPrefixLength to set.
    */
   public void setFuzzyPrefixLength(int fuzzyPrefixLength) {
     this.fuzzyPrefixLength = fuzzyPrefixLength;
   }
 
   /**
    * Sets the default slop for phrases.  If zero, then exact phrase matches
    * are required.  Default value is zero.
    */
   public void setPhraseSlop(int phraseSlop) {
     this.phraseSlop = phraseSlop;
   }
 
   /**
    * Gets the default slop for phrases.
    */
   public int getPhraseSlop() {
     return phraseSlop;
   }
 
   /**
    * Sets the boolean operator of the QueryParser.
   * In classic mode (<code>DEFAULT_OPERATOR_OR</code>) terms without any modifiers
    * are considered optional: for example <code>capital of Hungary</code> is equal to
    * <code>capital OR of OR Hungary</code>.<br/>
    * In <code>DEFAULT_OPERATOR_AND</code> terms are considered to be in conjuction: the
    * above mentioned query is parsed as <code>capital AND of AND Hungary</code>
    */
   public void setOperator(int operator) {
     this.operator = operator;
   }
 
   /**
    * Gets implicit operator setting, which will be either DEFAULT_OPERATOR_AND
    * or DEFAULT_OPERATOR_OR.
    */
   public int getOperator() {
     return operator;
   }
 
   public void setLowercaseWildcardTerms(boolean lowercaseWildcardTerms) {
     this.lowercaseWildcardTerms = lowercaseWildcardTerms;
   }
 
   public boolean getLowercaseWildcardTerms() {
     return lowercaseWildcardTerms;
   }
 
   /**
    * Set locale used by date range parsing.
    */
   public void setLocale(Locale locale) {
     this.locale = locale;
   }
 
   /**
    * Returns current locale, allowing access by subclasses.
    */
   public Locale getLocale() {
     return locale;
   }
 
   protected void addClause(Vector clauses, int conj, int mods, Query q) {
     boolean required, prohibited;
 
     // If this term is introduced by AND, make the preceding term required,
     // unless it's already prohibited
     if (clauses.size() > 0 && conj == CONJ_AND) {
       BooleanClause c = (BooleanClause) clauses.elementAt(clauses.size()-1);
       if (!c.isProhibited())
         c.setOccur(BooleanClause.Occur.MUST);
     }
 
     if (clauses.size() > 0 && operator == DEFAULT_OPERATOR_AND && conj == CONJ_OR) {
       // If this term is introduced by OR, make the preceding term optional,
       // unless it's prohibited (that means we leave -a OR b but +a OR b-->a OR b)
       // notice if the input is a OR b, first term is parsed as required; without
       // this modification a OR b would parsed as +a OR b
       BooleanClause c = (BooleanClause) clauses.elementAt(clauses.size()-1);
       if (!c.isProhibited())
         c.setOccur(BooleanClause.Occur.SHOULD);
     }
 
     // We might have been passed a null query; the term might have been
     // filtered away by the analyzer.
     if (q == null)
       return;
 
     if (operator == DEFAULT_OPERATOR_OR) {
       // We set REQUIRED if we're introduced by AND or +; PROHIBITED if
       // introduced by NOT or -; make sure not to set both.
       prohibited = (mods == MOD_NOT);
       required = (mods == MOD_REQ);
       if (conj == CONJ_AND && !prohibited) {
         required = true;
       }
     } else {
       // We set PROHIBITED if we're introduced by NOT or -; We set REQUIRED
       // if not PROHIBITED and not introduced by OR
       prohibited = (mods == MOD_NOT);
       required   = (!prohibited && conj != CONJ_OR);
     }
     if (required && !prohibited)
       clauses.addElement(new BooleanClause(q, BooleanClause.Occur.MUST));
     else if (!required && !prohibited)
       clauses.addElement(new BooleanClause(q, BooleanClause.Occur.SHOULD));
     else if (!required && prohibited)
       clauses.addElement(new BooleanClause(q, BooleanClause.Occur.MUST_NOT));
     else
       throw new RuntimeException("Clause cannot be both required and prohibited");
   }
 
   /**
    * Note that parameter analyzer is ignored. Calls inside the parser always
    * use class member analyzer.
    *
    * @exception ParseException throw in overridden method to disallow
    * @deprecated use {@link #getFieldQuery(String, String)}
    */
   protected Query getFieldQuery(String field,
                                                     Analyzer analyzer,
                                                     String queryText)  throws ParseException {
     return getFieldQuery(field, queryText);
   }
 
   /**
    * @exception ParseException throw in overridden method to disallow
    */
   protected Query getFieldQuery(String field, String queryText)  throws ParseException {
     // Use the analyzer to get all the tokens, and then build a TermQuery,
     // PhraseQuery, or nothing based on the term count
 
     TokenStream source = analyzer.tokenStream(field,
                                               new StringReader(queryText));
     Vector v = new Vector();
     org.apache.lucene.analysis.Token t;
 
     while (true) {
       try {
         t = source.next();
       }
       catch (IOException e) {
         t = null;
       }
       if (t == null)
         break;
       v.addElement(t.termText());
     }
     try {
       source.close();
     }
     catch (IOException e) {
       // ignore
     }
 
     if (v.size() == 0)
       return null;
     else if (v.size() == 1)
       return new TermQuery(new Term(field, (String) v.elementAt(0)));
     else {
       PhraseQuery q = new PhraseQuery();
       q.setSlop(phraseSlop);
       for (int i=0; i<v.size(); i++) {
         q.add(new Term(field, (String) v.elementAt(i)));
       }
       return q;
     }
   }
 
   /**
    * Note that parameter analyzer is ignored. Calls inside the parser always
    * use class member analyzer.
    *
    * @exception ParseException throw in overridden method to disallow
    * @deprecated use {@link #getFieldQuery(String, String, int)}
    */
   protected Query getFieldQuery(String field,
                                                     Analyzer analyzer,
                                                     String queryText,
                                                     int slop) throws ParseException {
     return getFieldQuery(field, queryText, slop);
   }
 
   /**
    * Base implementation delegates to {@link #getFieldQuery(String,String)}.
    * This method may be overridden, for example, to return
    * a SpanNearQuery instead of a PhraseQuery.
    *
    * @exception ParseException throw in overridden method to disallow
    */
   protected Query getFieldQuery(String field, String queryText, int slop)
         throws ParseException {
     Query query = getFieldQuery(field, queryText);
 
     if (query instanceof PhraseQuery) {
       ((PhraseQuery) query).setSlop(slop);
     }
 
     return query;
   }
 
   /**
    * Note that parameter analyzer is ignored. Calls inside the parser always
    * use class member analyzer.
    *
    * @exception ParseException throw in overridden method to disallow
    * @deprecated use {@link #getRangeQuery(String, String, String, boolean)}
    */
   protected Query getRangeQuery(String field,
       Analyzer analyzer,
       String part1,
       String part2,
       boolean inclusive) throws ParseException {
     return getRangeQuery(field, part1, part2, inclusive);
   }
 
   /**
    * @exception ParseException throw in overridden method to disallow
    */
   protected Query getRangeQuery(String field,
                                 String part1,
                                 String part2,
                                 boolean inclusive) throws ParseException
   {
     try {
       DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
       df.setLenient(true);
       Date d1 = df.parse(part1);
       Date d2 = df.parse(part2);
       part1 = DateField.dateToString(d1);
       part2 = DateField.dateToString(d2);
     }
     catch (Exception e) { }
 
     return new RangeQuery(new Term(field, part1),
                           new Term(field, part2),
                           inclusive);
   }
 
   /**
    * Factory method for generating query, given a set of clauses.
    * By default creates a boolean query composed of clauses passed in.
    *
    * Can be overridden by extending classes, to modify query being
    * returned.
    *
    * @param clauses Vector that contains {@link BooleanClause} instances
    *    to join.
    *
    * @return Resulting {@link Query} object.
    * @exception ParseException throw in overridden method to disallow
    */
   protected Query getBooleanQuery(Vector clauses) throws ParseException
   {
     BooleanQuery query = new BooleanQuery();
     for (int i = 0; i < clauses.size(); i++) {
   query.add((BooleanClause)clauses.elementAt(i));
     }
     return query;
   }
 
   /**
    * Factory method for generating a query. Called when parser
    * parses an input term token that contains one or more wildcard
    * characters (? and *), but is not a prefix term token (one
    * that has just a single * character at the end)
    *<p>
    * Depending on settings, prefix term may be lower-cased
    * automatically. It will not go through the default Analyzer,
    * however, since normal Analyzers are unlikely to work properly
    * with wildcard templates.
    *<p>
    * Can be overridden by extending classes, to provide custom handling for
    * wildcard queries, which may be necessary due to missing analyzer calls.
    *
    * @param field Name of the field query will use.
    * @param termStr Term token that contains one or more wild card
    *   characters (? or *), but is not simple prefix term
    *
    * @return Resulting {@link Query} built for the term
    * @exception ParseException throw in overridden method to disallow
    */
   protected Query getWildcardQuery(String field, String termStr) throws ParseException
   {
     if (lowercaseWildcardTerms) {
   termStr = termStr.toLowerCase();
     }
     Term t = new Term(field, termStr);
     return new WildcardQuery(t);
   }
 
   /**
    * Factory method for generating a query (similar to
   * ({@link #getWildcardQuery}). Called when parser parses an input term
    * token that uses prefix notation; that is, contains a single '*' wildcard
    * character as its last character. Since this is a special case
    * of generic wildcard term, and such a query can be optimized easily,
    * this usually results in a different query object.
    *<p>
    * Depending on settings, a prefix term may be lower-cased
    * automatically. It will not go through the default Analyzer,
    * however, since normal Analyzers are unlikely to work properly
    * with wildcard templates.
    *<p>
    * Can be overridden by extending classes, to provide custom handling for
    * wild card queries, which may be necessary due to missing analyzer calls.
    *
    * @param field Name of the field query will use.
    * @param termStr Term token to use for building term for the query
    *    (<b>without</b> trailing '*' character!)
    *
    * @return Resulting {@link Query} built for the term
    * @exception ParseException throw in overridden method to disallow
    */
   protected Query getPrefixQuery(String field, String termStr) throws ParseException
   {
     if (lowercaseWildcardTerms) {
   termStr = termStr.toLowerCase();
     }
     Term t = new Term(field, termStr);
     return new PrefixQuery(t);
   }
 
  /**
    * @deprecated use {@link #getFuzzyQuery(String, String, float)}
    */
   protected Query getFuzzyQuery(String field, String termStr) throws ParseException {
     return getFuzzyQuery(field, termStr, fuzzyMinSim);
   }
 
    /**
    * Factory method for generating a query (similar to
   * ({@link #getWildcardQuery}). Called when parser parses
    * an input term token that has the fuzzy suffix (~) appended.
    *
    * @param field Name of the field query will use.
    * @param termStr Term token to use for building term for the query
    *
    * @return Resulting {@link Query} built for the term
    * @exception ParseException throw in overridden method to disallow
    */
   protected Query getFuzzyQuery(String field, String termStr, float minSimilarity) throws ParseException
   {
     Term t = new Term(field, termStr);
     return new FuzzyQuery(t, minSimilarity, fuzzyPrefixLength);
   }
 
   /**
    * Returns a String where the escape char has been
    * removed, or kept only once if there was a double escape.
    */
   private String discardEscapeChar(String input) {
     char[] caSource = input.toCharArray();
     char[] caDest = new char[caSource.length];
     int j = 0;
     for (int i = 0; i < caSource.length; i++) {
       if ((caSource[i] != '\\') || (i > 0 && caSource[i-1] == '\\')) {
         caDest[j++]=caSource[i];
       }
     }
     return new String(caDest, 0, j);
   }
 
   /**
    * Returns a String where those characters that QueryParser
    * expects to be escaped are escaped by a preceding <code>\</code>.
    */
   public static String escape(String s) {
     StringBuffer sb = new StringBuffer();
     for (int i = 0; i < s.length(); i++) {
       char c = s.charAt(i);
       // NOTE: keep this in sync with _ESCAPED_CHAR below!
       if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':'
         || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
         || c == '*' || c == '?') {
         sb.append('\\');
       }
       sb.append(c);
     }
     return sb.toString();
   }
 
   public static void main(String[] args) throws Exception {
     QueryParser qp = new QueryParser("field",
                            new org.apache.lucene.analysis.SimpleAnalyzer());
     Query q = qp.parse(args[0]);
     System.out.println(q.toString("field"));
   }
 
 // *   Query  ::= ( Clause )*
 // *   Clause ::= ["+", "-"] [<TERM> ":"] ( <TERM> | "(" Query ")" )
   final public int Conjunction() throws ParseException {
   int ret = CONJ_NONE;
     switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
     case AND:
     case OR:
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case AND:
         jj_consume_token(AND);
             ret = CONJ_AND;
         break;
       case OR:
         jj_consume_token(OR);
               ret = CONJ_OR;
         break;
       default:
         jj_la1[0] = jj_gen;
         jj_consume_token(-1);
         throw new ParseException();
       }
       break;
     default:
       jj_la1[1] = jj_gen;
       ;
     }
     {if (true) return ret;}
     throw new Error("Missing return statement in function");
   }
 
   final public int Modifiers() throws ParseException {
   int ret = MOD_NONE;
     switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
     case NOT:
     case PLUS:
     case MINUS:
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case PLUS:
         jj_consume_token(PLUS);
               ret = MOD_REQ;
         break;
       case MINUS:
         jj_consume_token(MINUS);
                  ret = MOD_NOT;
         break;
       case NOT:
         jj_consume_token(NOT);
                ret = MOD_NOT;
         break;
       default:
         jj_la1[2] = jj_gen;
         jj_consume_token(-1);
         throw new ParseException();
       }
       break;
     default:
       jj_la1[3] = jj_gen;
       ;
     }
     {if (true) return ret;}
     throw new Error("Missing return statement in function");
   }
 
   final public Query Query(String field) throws ParseException {
   Vector clauses = new Vector();
   Query q, firstQuery=null;
   int conj, mods;
     mods = Modifiers();
     q = Clause(field);
     addClause(clauses, CONJ_NONE, mods, q);
     if (mods == MOD_NONE)
         firstQuery=q;
     label_1:
     while (true) {
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case AND:
       case OR:
       case NOT:
       case PLUS:
       case MINUS:
       case LPAREN:
       case QUOTED:
       case TERM:
       case PREFIXTERM:
       case WILDTERM:
       case RANGEIN_START:
       case RANGEEX_START:
       case NUMBER:
         ;
         break;
       default:
         jj_la1[4] = jj_gen;
         break label_1;
       }
       conj = Conjunction();
       mods = Modifiers();
       q = Clause(field);
       addClause(clauses, conj, mods, q);
     }
       if (clauses.size() == 1 && firstQuery != null)
         {if (true) return firstQuery;}
       else {
   {if (true) return getBooleanQuery(clauses);}
       }
     throw new Error("Missing return statement in function");
   }
 
   final public Query Clause(String field) throws ParseException {
   Query q;
   Token fieldToken=null, boost=null;
     if (jj_2_1(2)) {
       fieldToken = jj_consume_token(TERM);
       jj_consume_token(COLON);
       field=discardEscapeChar(fieldToken.image);
     } else {
       ;
     }
     switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
     case QUOTED:
     case TERM:
     case PREFIXTERM:
     case WILDTERM:
     case RANGEIN_START:
     case RANGEEX_START:
     case NUMBER:
       q = Term(field);
       break;
     case LPAREN:
       jj_consume_token(LPAREN);
       q = Query(field);
       jj_consume_token(RPAREN);
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case CARAT:
         jj_consume_token(CARAT);
         boost = jj_consume_token(NUMBER);
         break;
       default:
         jj_la1[5] = jj_gen;
         ;
       }
       break;
     default:
       jj_la1[6] = jj_gen;
       jj_consume_token(-1);
       throw new ParseException();
     }
       if (boost != null) {
         float f = (float)1.0;
   try {
     f = Float.valueOf(boost.image).floatValue();
           q.setBoost(f);
   } catch (Exception ignored) { }
       }
       {if (true) return q;}
     throw new Error("Missing return statement in function");
   }
 
   final public Query Term(String field) throws ParseException {
   Token term, boost=null, fuzzySlop=null, goop1, goop2;
   boolean prefix = false;
   boolean wildcard = false;
   boolean fuzzy = false;
   boolean rangein = false;
   Query q;
     switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
     case TERM:
     case PREFIXTERM:
     case WILDTERM:
     case NUMBER:
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case TERM:
         term = jj_consume_token(TERM);
         break;
       case PREFIXTERM:
         term = jj_consume_token(PREFIXTERM);
                              prefix=true;
         break;
       case WILDTERM:
         term = jj_consume_token(WILDTERM);
                            wildcard=true;
         break;
       case NUMBER:
         term = jj_consume_token(NUMBER);
         break;
       default:
         jj_la1[7] = jj_gen;
         jj_consume_token(-1);
         throw new ParseException();
       }
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case FUZZY_SLOP:
         fuzzySlop = jj_consume_token(FUZZY_SLOP);
                                 fuzzy=true;
         break;
       default:
         jj_la1[8] = jj_gen;
         ;
       }
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case CARAT:
         jj_consume_token(CARAT);
         boost = jj_consume_token(NUMBER);
         switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
         case FUZZY_SLOP:
           fuzzySlop = jj_consume_token(FUZZY_SLOP);
                                                          fuzzy=true;
           break;
         default:
           jj_la1[9] = jj_gen;
           ;
         }
         break;
       default:
         jj_la1[10] = jj_gen;
         ;
       }
        String termImage=discardEscapeChar(term.image);
        if (wildcard) {
        q = getWildcardQuery(field, termImage);
        } else if (prefix) {
          q = getPrefixQuery(field,
            discardEscapeChar(term.image.substring
           (0, term.image.length()-1)));
        } else if (fuzzy) {
           float fms = fuzzyMinSim;
           try {
             fms = Float.valueOf(fuzzySlop.image.substring(1)).floatValue();
           } catch (Exception ignored) { }
          if(fms < 0.0f || fms > 1.0f){
            {if (true) throw new ParseException("Minimum similarity for a FuzzyQuery has to be between 0.0f and 1.0f !");}
          }
          if(fms == fuzzyMinSim)
            q = getFuzzyQuery(field, termImage);
          else
            q = getFuzzyQuery(field, termImage, fms);
        } else {
          q = getFieldQuery(field, analyzer, termImage);
        }
       break;
     case RANGEIN_START:
       jj_consume_token(RANGEIN_START);
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case RANGEIN_GOOP:
         goop1 = jj_consume_token(RANGEIN_GOOP);
         break;
       case RANGEIN_QUOTED:
         goop1 = jj_consume_token(RANGEIN_QUOTED);
         break;
       default:
         jj_la1[11] = jj_gen;
         jj_consume_token(-1);
         throw new ParseException();
       }
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case RANGEIN_TO:
         jj_consume_token(RANGEIN_TO);
         break;
       default:
         jj_la1[12] = jj_gen;
         ;
       }
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case RANGEIN_GOOP:
         goop2 = jj_consume_token(RANGEIN_GOOP);
         break;
       case RANGEIN_QUOTED:
         goop2 = jj_consume_token(RANGEIN_QUOTED);
         break;
       default:
         jj_la1[13] = jj_gen;
         jj_consume_token(-1);
         throw new ParseException();
       }
       jj_consume_token(RANGEIN_END);
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case CARAT:
         jj_consume_token(CARAT);
         boost = jj_consume_token(NUMBER);
         break;
       default:
         jj_la1[14] = jj_gen;
         ;
       }
           if (goop1.kind == RANGEIN_QUOTED) {
             goop1.image = goop1.image.substring(1, goop1.image.length()-1);
           } else {
             goop1.image = discardEscapeChar(goop1.image);
           }
           if (goop2.kind == RANGEIN_QUOTED) {
             goop2.image = goop2.image.substring(1, goop2.image.length()-1);
       } else {
         goop2.image = discardEscapeChar(goop2.image);
       }
           q = getRangeQuery(field, analyzer, goop1.image, goop2.image, true);
       break;
     case RANGEEX_START:
       jj_consume_token(RANGEEX_START);
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case RANGEEX_GOOP:
         goop1 = jj_consume_token(RANGEEX_GOOP);
         break;
       case RANGEEX_QUOTED:
         goop1 = jj_consume_token(RANGEEX_QUOTED);
         break;
       default:
         jj_la1[15] = jj_gen;
         jj_consume_token(-1);
         throw new ParseException();
       }
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case RANGEEX_TO:
         jj_consume_token(RANGEEX_TO);
         break;
       default:
         jj_la1[16] = jj_gen;
         ;
       }
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case RANGEEX_GOOP:
         goop2 = jj_consume_token(RANGEEX_GOOP);
         break;
       case RANGEEX_QUOTED:
         goop2 = jj_consume_token(RANGEEX_QUOTED);
         break;
       default:
         jj_la1[17] = jj_gen;
         jj_consume_token(-1);
         throw new ParseException();
       }
       jj_consume_token(RANGEEX_END);
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case CARAT:
         jj_consume_token(CARAT);
         boost = jj_consume_token(NUMBER);
         break;
       default:
         jj_la1[18] = jj_gen;
         ;
       }
           if (goop1.kind == RANGEEX_QUOTED) {
             goop1.image = goop1.image.substring(1, goop1.image.length()-1);
           } else {
             goop1.image = discardEscapeChar(goop1.image);
           }
           if (goop2.kind == RANGEEX_QUOTED) {
             goop2.image = goop2.image.substring(1, goop2.image.length()-1);
       } else {
         goop2.image = discardEscapeChar(goop2.image);
       }
 
           q = getRangeQuery(field, analyzer, goop1.image, goop2.image, false);
       break;
     case QUOTED:
       term = jj_consume_token(QUOTED);
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case FUZZY_SLOP:
         fuzzySlop = jj_consume_token(FUZZY_SLOP);
         break;
       default:
         jj_la1[19] = jj_gen;
         ;
       }
       switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
       case CARAT:
         jj_consume_token(CARAT);
         boost = jj_consume_token(NUMBER);
         break;
       default:
         jj_la1[20] = jj_gen;
         ;
       }
          int s = phraseSlop;
 
          if (fuzzySlop != null) {
            try {
              s = Float.valueOf(fuzzySlop.image.substring(1)).intValue();
            }
            catch (Exception ignored) { }
          }
          q = getFieldQuery(field, analyzer, term.image.substring(1, term.image.length()-1), s);
       break;
     default:
       jj_la1[21] = jj_gen;
       jj_consume_token(-1);
       throw new ParseException();
     }
     if (boost != null) {
       float f = (float) 1.0;
       try {
         f = Float.valueOf(boost.image).floatValue();
       }
       catch (Exception ignored) {
     /* Should this be handled somehow? (defaults to "no boost", if
      * boost number is invalid)
      */
       }
 
       // avoid boosting null queries, such as those caused by stop words
       if (q != null) {
         q.setBoost(f);
       }
     }
     {if (true) return q;}
     throw new Error("Missing return statement in function");
   }
 
   final private boolean jj_2_1(int xla) {
     jj_la = xla; jj_lastpos = jj_scanpos = token;
     try { return !jj_3_1(); }
     catch(LookaheadSuccess ls) { return true; }
     finally { jj_save(0, xla); }
   }
 
   final private boolean jj_3_1() {
     if (jj_scan_token(TERM)) return true;
     if (jj_scan_token(COLON)) return true;
     return false;
   }
 
   public QueryParserTokenManager token_source;
   public Token token, jj_nt;
   private int jj_ntk;
   private Token jj_scanpos, jj_lastpos;
   private int jj_la;
   public boolean lookingAhead = false;
   private boolean jj_semLA;
   private int jj_gen;
   final private int[] jj_la1 = new int[22];
   static private int[] jj_la1_0;
   static {
       jj_la1_0();
    }
    private static void jj_la1_0() {
       jj_la1_0 = new int[] {0x180,0x180,0xe00,0xe00,0xfb1f80,0x8000,0xfb1000,0x9a0000,0x40000,0x40000,0x8000,0xc000000,0x1000000,0xc000000,0x8000,0xc0000000,0x10000000,0xc0000000,0x8000,0x40000,0x8000,0xfb0000,};
    }
   final private JJCalls[] jj_2_rtns = new JJCalls[1];
   private boolean jj_rescan = false;
   private int jj_gc = 0;
 
   public QueryParser(CharStream stream) {
     token_source = new QueryParserTokenManager(stream);
     token = new Token();
     jj_ntk = -1;
     jj_gen = 0;
     for (int i = 0; i < 22; i++) jj_la1[i] = -1;
     for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
   }
 
   public void ReInit(CharStream stream) {
     token_source.ReInit(stream);
     token = new Token();
     jj_ntk = -1;
     jj_gen = 0;
     for (int i = 0; i < 22; i++) jj_la1[i] = -1;
     for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
   }
 
   public QueryParser(QueryParserTokenManager tm) {
     token_source = tm;
     token = new Token();
     jj_ntk = -1;
     jj_gen = 0;
     for (int i = 0; i < 22; i++) jj_la1[i] = -1;
     for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
   }
 
   public void ReInit(QueryParserTokenManager tm) {
     token_source = tm;
     token = new Token();
     jj_ntk = -1;
     jj_gen = 0;
     for (int i = 0; i < 22; i++) jj_la1[i] = -1;
     for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
   }
 
   final private Token jj_consume_token(int kind) throws ParseException {
     Token oldToken;
     if ((oldToken = token).next != null) token = token.next;
     else token = token.next = token_source.getNextToken();
     jj_ntk = -1;
     if (token.kind == kind) {
       jj_gen++;
       if (++jj_gc > 100) {
         jj_gc = 0;
         for (int i = 0; i < jj_2_rtns.length; i++) {
           JJCalls c = jj_2_rtns[i];
           while (c != null) {
             if (c.gen < jj_gen) c.first = null;
             c = c.next;
           }
         }
       }
       return token;
     }
     token = oldToken;
     jj_kind = kind;
     throw generateParseException();
   }
 
   static private final class LookaheadSuccess extends java.lang.Error { }
   final private LookaheadSuccess jj_ls = new LookaheadSuccess();
   final private boolean jj_scan_token(int kind) {
     if (jj_scanpos == jj_lastpos) {
       jj_la--;
       if (jj_scanpos.next == null) {
         jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
       } else {
         jj_lastpos = jj_scanpos = jj_scanpos.next;
       }
     } else {
       jj_scanpos = jj_scanpos.next;
     }
     if (jj_rescan) {
       int i = 0; Token tok = token;
       while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
       if (tok != null) jj_add_error_token(kind, i);
     }
     if (jj_scanpos.kind != kind) return true;
     if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
     return false;
   }
 
   final public Token getNextToken() {
     if (token.next != null) token = token.next;
     else token = token.next = token_source.getNextToken();
     jj_ntk = -1;
     jj_gen++;
     return token;
   }
 
   final public Token getToken(int index) {
     Token t = lookingAhead ? jj_scanpos : token;
     for (int i = 0; i < index; i++) {
       if (t.next != null) t = t.next;
       else t = t.next = token_source.getNextToken();
     }
     return t;
   }
 
   final private int jj_ntk() {
     if ((jj_nt=token.next) == null)
       return (jj_ntk = (token.next=token_source.getNextToken()).kind);
     else
       return (jj_ntk = jj_nt.kind);
   }
 
   private java.util.Vector jj_expentries = new java.util.Vector();
   private int[] jj_expentry;
   private int jj_kind = -1;
   private int[] jj_lasttokens = new int[100];
   private int jj_endpos;
 
   private void jj_add_error_token(int kind, int pos) {
     if (pos >= 100) return;
     if (pos == jj_endpos + 1) {
       jj_lasttokens[jj_endpos++] = kind;
     } else if (jj_endpos != 0) {
       jj_expentry = new int[jj_endpos];
       for (int i = 0; i < jj_endpos; i++) {
         jj_expentry[i] = jj_lasttokens[i];
       }
       boolean exists = false;
       for (java.util.Enumeration e = jj_expentries.elements(); e.hasMoreElements();) {
         int[] oldentry = (int[])(e.nextElement());
         if (oldentry.length == jj_expentry.length) {
           exists = true;
           for (int i = 0; i < jj_expentry.length; i++) {
             if (oldentry[i] != jj_expentry[i]) {
               exists = false;
               break;
             }
           }
           if (exists) break;
         }
       }
       if (!exists) jj_expentries.addElement(jj_expentry);
       if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
     }
   }
 
   public ParseException generateParseException() {
     jj_expentries.removeAllElements();
     boolean[] la1tokens = new boolean[32];
     for (int i = 0; i < 32; i++) {
       la1tokens[i] = false;
     }
     if (jj_kind >= 0) {
       la1tokens[jj_kind] = true;
       jj_kind = -1;
     }
     for (int i = 0; i < 22; i++) {
       if (jj_la1[i] == jj_gen) {
         for (int j = 0; j < 32; j++) {
           if ((jj_la1_0[i] & (1<<j)) != 0) {
             la1tokens[j] = true;
           }
         }
       }
     }
     for (int i = 0; i < 32; i++) {
       if (la1tokens[i]) {
         jj_expentry = new int[1];
         jj_expentry[0] = i;
         jj_expentries.addElement(jj_expentry);
       }
     }
     jj_endpos = 0;
     jj_rescan_token();
     jj_add_error_token(0, 0);
     int[][] exptokseq = new int[jj_expentries.size()][];
     for (int i = 0; i < jj_expentries.size(); i++) {
       exptokseq[i] = (int[])jj_expentries.elementAt(i);
     }
     return new ParseException(token, exptokseq, tokenImage);
   }
 
   final public void enable_tracing() {
   }
 
   final public void disable_tracing() {
   }
 
   final private void jj_rescan_token() {
     jj_rescan = true;
     for (int i = 0; i < 1; i++) {
       JJCalls p = jj_2_rtns[i];
       do {
         if (p.gen > jj_gen) {
           jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
           switch (i) {
             case 0: jj_3_1(); break;
           }
         }
         p = p.next;
       } while (p != null);
     }
     jj_rescan = false;
   }
 
   final private void jj_save(int index, int xla) {
     JJCalls p = jj_2_rtns[index];
     while (p.gen > jj_gen) {
       if (p.next == null) { p = p.next = new JJCalls(); break; }
       p = p.next;
     }
     p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
   }
 
   static final class JJCalls {
     int gen;
     Token first;
     int arg;
     JJCalls next;
   }
 
 }
