 package org.apache.lucene.index;
 
 import java.io.IOException;
 
 import org.apache.lucene.analysis.WhitespaceAnalyzer;
 import org.apache.lucene.document.Document;
 import org.apache.lucene.document.Field;
 import org.apache.lucene.store.Directory;
 import org.apache.lucene.store.RAMDirectory;
 
 import junit.framework.TestCase;
 
 public class TestIndexWriterMergePolicy extends TestCase {
 
   // Test the normal case
   public void testNormalCase() throws IOException {
     Directory dir = new RAMDirectory();
 
     IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
     writer.setMaxBufferedDocs(10);
     writer.setMergeFactor(10);
 
     for (int i = 0; i < 100; i++) {
       addDoc(writer);
       checkInvariants(writer);
     }
 
     writer.close();
   }
 
   // Test to see if there is over merge
   public void testNoOverMerge() throws IOException {
     Directory dir = new RAMDirectory();
 
     IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
     writer.setMaxBufferedDocs(10);
     writer.setMergeFactor(10);
 
     boolean noOverMerge = false;
     for (int i = 0; i < 100; i++) {
       addDoc(writer);
       checkInvariants(writer);
      if (writer.getRAMSegmentCount() + writer.getSegmentCount() >= 18) {
         noOverMerge = true;
       }
     }
     assertTrue(noOverMerge);
 
     writer.close();
   }
 
   // Test the case where flush is forced after every addDoc
   public void testForceFlush() throws IOException {
     Directory dir = new RAMDirectory();
 
     IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
     writer.setMaxBufferedDocs(10);
     writer.setMergeFactor(10);
 
     for (int i = 0; i < 100; i++) {
       addDoc(writer);
       writer.close();
 
       writer = new IndexWriter(dir, new WhitespaceAnalyzer(), false);
       writer.setMaxBufferedDocs(10);
       writer.setMergeFactor(10);
       checkInvariants(writer);
     }
 
     writer.close();
   }
 
   // Test the case where mergeFactor changes
   public void testMergeFactorChange() throws IOException {
     Directory dir = new RAMDirectory();
 
     IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
     writer.setMaxBufferedDocs(10);
     writer.setMergeFactor(100);
 
     for (int i = 0; i < 250; i++) {
       addDoc(writer);
       checkInvariants(writer);
     }
 
     writer.setMergeFactor(5);
 
     // merge policy only fixes segments on levels where merges
     // have been triggered, so check invariants after all adds
     for (int i = 0; i < 10; i++) {
       addDoc(writer);
     }
     checkInvariants(writer);
 
     writer.close();
   }
 
   // Test the case where both mergeFactor and maxBufferedDocs change
   public void testMaxBufferedDocsChange() throws IOException {
     Directory dir = new RAMDirectory();
 
     IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
     writer.setMaxBufferedDocs(101);
     writer.setMergeFactor(101);
 
     // leftmost* segment has 1 doc
     // rightmost* segment has 100 docs
     for (int i = 1; i <= 100; i++) {
       for (int j = 0; j < i; j++) {
         addDoc(writer);
         checkInvariants(writer);
       }
       writer.close();
 
       writer = new IndexWriter(dir, new WhitespaceAnalyzer(), false);
       writer.setMaxBufferedDocs(101);
       writer.setMergeFactor(101);
     }
 
     writer.setMaxBufferedDocs(10);
     writer.setMergeFactor(10);
 
     // merge policy only fixes segments on levels where merges
     // have been triggered, so check invariants after all adds
     for (int i = 0; i < 100; i++) {
       addDoc(writer);
     }
     checkInvariants(writer);
 
     for (int i = 100; i < 1000; i++) {
       addDoc(writer);
     }
     checkInvariants(writer);
 
     writer.close();
   }
 
   // Test the case where a merge results in no doc at all
   public void testMergeDocCount0() throws IOException {
     Directory dir = new RAMDirectory();
 
     IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
     writer.setMaxBufferedDocs(10);
     writer.setMergeFactor(100);
 
     for (int i = 0; i < 250; i++) {
       addDoc(writer);
       checkInvariants(writer);
     }
     writer.close();
 
     IndexReader reader = IndexReader.open(dir);
     reader.deleteDocuments(new Term("content", "aaa"));
     reader.close();
 
     writer = new IndexWriter(dir, new WhitespaceAnalyzer(), false);
     writer.setMaxBufferedDocs(10);
     writer.setMergeFactor(5);
 
     // merge factor is changed, so check invariants after all adds
     for (int i = 0; i < 10; i++) {
       addDoc(writer);
     }
     checkInvariants(writer);
     assertEquals(10, writer.docCount());
 
     writer.close();
   }
 
   private void addDoc(IndexWriter writer) throws IOException {
     Document doc = new Document();
     doc.add(new Field("content", "aaa", Field.Store.NO, Field.Index.TOKENIZED));
     writer.addDocument(doc);
   }
 
   private void checkInvariants(IndexWriter writer) throws IOException {
     int maxBufferedDocs = writer.getMaxBufferedDocs();
     int mergeFactor = writer.getMergeFactor();
     int maxMergeDocs = writer.getMaxMergeDocs();
 
    int ramSegmentCount = writer.getRAMSegmentCount();
     assertTrue(ramSegmentCount < maxBufferedDocs);
 
     int lowerBound = -1;
     int upperBound = maxBufferedDocs;
     int numSegments = 0;
 
     int segmentCount = writer.getSegmentCount();
     for (int i = segmentCount - 1; i >= 0; i--) {
       int docCount = writer.getDocCount(i);
       assertTrue(docCount > lowerBound);
 
       if (docCount <= upperBound) {
         numSegments++;
       } else {
         if (upperBound * mergeFactor <= maxMergeDocs) {
           assertTrue(numSegments < mergeFactor);
         }
 
         do {
           lowerBound = upperBound;
           upperBound *= mergeFactor;
         } while (docCount > upperBound);
         numSegments = 1;
       }
     }
     if (upperBound * mergeFactor <= maxMergeDocs) {
       assertTrue(numSegments < mergeFactor);
     }
 
     String[] files = writer.getDirectory().list();
     int segmentCfsCount = 0;
     for (int i = 0; i < files.length; i++) {
       if (files[i].endsWith(".cfs")) {
         segmentCfsCount++;
       }
     }
     assertEquals(segmentCount, segmentCfsCount);
   }
 
   private void printSegmentDocCounts(IndexWriter writer) {
     int segmentCount = writer.getSegmentCount();
     System.out.println("" + segmentCount + " segments total");
     for (int i = 0; i < segmentCount; i++) {
       System.out.println("  segment " + i + " has " + writer.getDocCount(i)
           + " docs");
     }
   }
 }