 package org.apache.lucene.search;
 
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
import java.util.Collections;
import java.util.LinkedList;

import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
 import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
 import org.apache.lucene.document.Document;
 import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
 import org.apache.lucene.util.LuceneTestCase;
 
 /**
  * This class tests the MultiPhraseQuery class.
  *
  *
  */
 public class TestMultiPhraseQuery extends LuceneTestCase
 {
     public TestMultiPhraseQuery(String name) {
         super(name);
     }
 
     public void testPhrasePrefix() throws IOException {
         RAMDirectory indexStore = new RAMDirectory();
         IndexWriter writer = new IndexWriter(indexStore, new SimpleAnalyzer(), true, IndexWriter.MaxFieldLength.LIMITED);
         add("blueberry pie", writer);
         add("blueberry strudel", writer);
         add("blueberry pizza", writer);
         add("blueberry chewing gum", writer);
         add("bluebird pizza", writer);
         add("bluebird foobar pizza", writer);
         add("piccadilly circus", writer);
         writer.optimize();
         writer.close();
 
         IndexSearcher searcher = new IndexSearcher(indexStore, true);
 
         // search for "blueberry pi*":
         MultiPhraseQuery query1 = new MultiPhraseQuery();
         // search for "strawberry pi*":
         MultiPhraseQuery query2 = new MultiPhraseQuery();
         query1.add(new Term("body", "blueberry"));
         query2.add(new Term("body", "strawberry"));
 
         LinkedList termsWithPrefix = new LinkedList();
         IndexReader ir = IndexReader.open(indexStore, true);
 
         // this TermEnum gives "piccadilly", "pie" and "pizza".
         String prefix = "pi";
         TermEnum te = ir.terms(new Term("body", prefix));
         do {
             if (te.term().text().startsWith(prefix))
             {
                 termsWithPrefix.add(te.term());
             }
         } while (te.next());
 
         query1.add((Term[])termsWithPrefix.toArray(new Term[0]));
         assertEquals("body:\"blueberry (piccadilly pie pizza)\"", query1.toString());
         query2.add((Term[])termsWithPrefix.toArray(new Term[0]));
         assertEquals("body:\"strawberry (piccadilly pie pizza)\"", query2.toString());
 
         ScoreDoc[] result;
         result = searcher.search(query1, null, 1000).scoreDocs;
         assertEquals(2, result.length);
         result = searcher.search(query2, null, 1000).scoreDocs;
         assertEquals(0, result.length);
 
         // search for "blue* pizza":
         MultiPhraseQuery query3 = new MultiPhraseQuery();
         termsWithPrefix.clear();
         prefix = "blue";
         te = ir.terms(new Term("body", prefix));
         do {
             if (te.term().text().startsWith(prefix))
             {
                 termsWithPrefix.add(te.term());
             }
         } while (te.next());
         query3.add((Term[])termsWithPrefix.toArray(new Term[0]));
         query3.add(new Term("body", "pizza"));
 
         result = searcher.search(query3, null, 1000).scoreDocs;
         assertEquals(2, result.length); // blueberry pizza, bluebird pizza
         assertEquals("body:\"(blueberry bluebird) pizza\"", query3.toString());
 
         // test slop:
         query3.setSlop(1);
         result = searcher.search(query3, null, 1000).scoreDocs;
         assertEquals(3, result.length); // blueberry pizza, bluebird pizza, bluebird foobar pizza
 
         MultiPhraseQuery query4 = new MultiPhraseQuery();
         try {
           query4.add(new Term("field1", "foo"));
           query4.add(new Term("field2", "foobar"));
           fail();
         } catch(IllegalArgumentException e) {
           // okay, all terms must belong to the same field
         }
         
         searcher.close();
         indexStore.close();
 
     }
     
     private void add(String s, IndexWriter writer) throws IOException {
       Document doc = new Document();
       doc.add(new Field("body", s, Field.Store.YES, Field.Index.ANALYZED));
       writer.addDocument(doc);
     }
     
     public void testBooleanQueryContainingSingleTermPrefixQuery() throws IOException {
       // this tests against bug 33161 (now fixed)
       // In order to cause the bug, the outer query must have more than one term 
       // and all terms required.
       // The contained PhraseMultiQuery must contain exactly one term array.
 
       RAMDirectory indexStore = new RAMDirectory();
       IndexWriter writer = new IndexWriter(indexStore, new SimpleAnalyzer(), true, IndexWriter.MaxFieldLength.LIMITED);
       add("blueberry pie", writer);
       add("blueberry chewing gum", writer);
       add("blue raspberry pie", writer);
       writer.optimize();
       writer.close();
 
       IndexSearcher searcher = new IndexSearcher(indexStore, true);
       // This query will be equivalent to +body:pie +body:"blue*"
       BooleanQuery q = new BooleanQuery();
       q.add(new TermQuery(new Term("body", "pie")), BooleanClause.Occur.MUST);
 
       MultiPhraseQuery trouble = new MultiPhraseQuery();
       trouble.add(new Term[] {
           new Term("body", "blueberry"),
           new Term("body", "blue")
       });
       q.add(trouble, BooleanClause.Occur.MUST);
 
       // exception will be thrown here without fix
       ScoreDoc[] hits = searcher.search(q, null, 1000).scoreDocs;
 
       assertEquals("Wrong number of hits", 2, hits.length);
       searcher.close();
   }
     
   public void testPhrasePrefixWithBooleanQuery() throws IOException {
     RAMDirectory indexStore = new RAMDirectory();
     IndexWriter writer = new IndexWriter(indexStore, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_CURRENT, Collections.emptySet()), true, IndexWriter.MaxFieldLength.LIMITED);
     add("This is a test", "object", writer);
     add("a note", "note", writer);
     writer.close();
     
     IndexSearcher searcher = new IndexSearcher(indexStore, true);
 
     // This query will be equivalent to +type:note +body:"a t*"
     BooleanQuery q = new BooleanQuery();
     q.add(new TermQuery(new Term("type", "note")), BooleanClause.Occur.MUST);
 
     MultiPhraseQuery trouble = new MultiPhraseQuery();
     trouble.add(new Term("body", "a"));
     trouble.add(new Term[] { new Term("body", "test"), new Term("body", "this") });
     q.add(trouble, BooleanClause.Occur.MUST);
 
     // exception will be thrown here without fix for #35626:
     ScoreDoc[] hits = searcher.search(q, null, 1000).scoreDocs;
     assertEquals("Wrong number of hits", 0, hits.length);
     searcher.close();
   }
   
   public void testHashCodeAndEquals(){
     MultiPhraseQuery query1 = new MultiPhraseQuery();
     MultiPhraseQuery query2 = new MultiPhraseQuery();
     
     assertEquals(query1.hashCode(), query2.hashCode());
     assertEquals(query1,query2);
     
     Term term1= new Term("someField","someText");
     
     query1.add(term1);
     query2.add(term1);
     
     assertEquals(query1.hashCode(), query2.hashCode());
     assertEquals(query1,query2);
     
     Term term2= new Term("someField","someMoreText");
     
     query1.add(term2);
     
     assertFalse(query1.hashCode()==query2.hashCode());
     assertFalse(query1.equals(query2));
     
     query2.add(term2);
     
     assertEquals(query1.hashCode(), query2.hashCode());
     assertEquals(query1,query2);
   }
 
   
   private void add(String s, String type, IndexWriter writer) throws IOException {
     Document doc = new Document();
     doc.add(new Field("body", s, Field.Store.YES, Field.Index.ANALYZED));
     doc.add(new Field("type", type, Field.Store.YES, Field.Index.NOT_ANALYZED));
     writer.addDocument(doc);
   }
 
  private static class TokenAndPos {
    public final String token;
    public final int pos;
    public TokenAndPos(String token, int pos) {
      this.token = token;
      this.pos = pos;
    }
  }

  private static class CannedAnalyzer extends Analyzer {
    private final TokenAndPos[] tokens;
    
    public CannedAnalyzer(TokenAndPos[] tokens) {
      this.tokens = tokens;
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
      return new CannedTokenizer(tokens);
    }
  }

  private static class CannedTokenizer extends Tokenizer {
    private final TokenAndPos[] tokens;
    private int upto = 0;
    private int lastPos = 0;
    private final TermAttribute termAtt = addAttribute(TermAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);

    public CannedTokenizer(TokenAndPos[] tokens) {
      this.tokens = tokens;
    }

    @Override
    public final boolean incrementToken() throws IOException {
      clearAttributes();      
      if (upto < tokens.length) {
        final TokenAndPos token = tokens[upto++];
        termAtt.setTermBuffer(token.token);
        posIncrAtt.setPositionIncrement(token.pos - lastPos);
        lastPos = token.pos;
        return true;
      } else {
        return false;
      }
    }
  }

  public void testZeroPosIncr() throws IOException {
    Directory dir = new RAMDirectory();
    final TokenAndPos[] tokens = new TokenAndPos[3];
    tokens[0] = new TokenAndPos("a", 0);
    tokens[1] = new TokenAndPos("b", 0);
    tokens[2] = new TokenAndPos("c", 0);

    IndexWriter writer = new IndexWriter(dir, new CannedAnalyzer(tokens), true, IndexWriter.MaxFieldLength.LIMITED);
    Document doc = new Document();
    doc.add(new Field("field", "", Field.Store.NO, Field.Index.ANALYZED));
    writer.addDocument(doc);
    writer.addDocument(doc);
    IndexReader r = writer.getReader();
    writer.close();
    IndexSearcher s = new IndexSearcher(r);
    MultiPhraseQuery mpq = new MultiPhraseQuery();
    //mpq.setSlop(1);

    // NOTE: not great that if we do the else clause here we
    // get different scores!  MultiPhraseQuery counts that
    // phrase as occurring twice per doc (it should be 1, I
    // think?).  This is because MultipleTermPositions is able to
    // return the same position more than once (0, in this
    // case):
    if (true) {
      mpq.add(new Term[] {new Term("field", "b"), new Term("field", "c")}, 0);
      mpq.add(new Term[] {new Term("field", "a")}, 0);
    } else {
      mpq.add(new Term[] {new Term("field", "a")}, 0);
      mpq.add(new Term[] {new Term("field", "b"), new Term("field", "c")}, 0);
    }
    TopDocs hits = s.search(mpq, 2);
    assert hits.totalHits == 2;
    assertEquals(hits.scoreDocs[0].score, hits.scoreDocs[1].score, 1e-5);
    /*
    for(int hit=0;hit<hits.totalHits;hit++) {
      ScoreDoc sd = hits.scoreDocs[hit];
      System.out.println("  hit doc=" + sd.doc + " score=" + sd.score);
    }
    */
    r.close();
    dir.close();
  }
 }
