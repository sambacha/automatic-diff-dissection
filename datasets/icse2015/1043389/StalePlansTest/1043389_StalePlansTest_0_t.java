 /*
  * Class org.apache.derbyTesting.functionTests.tests.lang.StalePlansTest
  *
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing,
  * software distributed under the License is distributed on an
  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  * KIND, either express or implied.  See the License for the
  * specific language governing permissions and limitations
  * under the License.
  */
 package org.apache.derbyTesting.functionTests.tests.lang;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.Properties;
 import junit.framework.Test;
 import junit.framework.TestSuite;
 import org.apache.derbyTesting.functionTests.util.Formatters;
 import org.apache.derbyTesting.junit.BaseJDBCTestCase;
 import org.apache.derbyTesting.junit.CleanDatabaseTestSetup;
 import org.apache.derbyTesting.junit.DatabasePropertyTestSetup;
 import org.apache.derbyTesting.junit.JDBC;
 import org.apache.derbyTesting.junit.SQLUtilities;
 
 /**
  * This is the test for stale plan invalidation. The system determines at
  * execution whether the tables used by a DML statement have grown or shrunk
  * significantly, and if so, causes the statement to be recompiled at the next
  * execution.
  */
 public class StalePlansTest extends BaseJDBCTestCase {
     public StalePlansTest(String name) {
         super(name);
     }
 
     /**
      * Create the test suite. This test is not run in client/server mode since
      * it only tests the query plans generated by the embedded driver.
      */
     public static Test suite() {
         Properties props = new Properties();
        // Check for stale plans on every 10th execution (default 100) to
        // reduce the number of times we need to execute each statement.
         props.setProperty("derby.language.stalePlanCheckInterval", "10");
         Test suite = new DatabasePropertyTestSetup(
             new TestSuite(StalePlansTest.class), props, true);
        return new CleanDatabaseTestSetup(suite);
     }
 
     /**
      * Create tables and indexes needed by the test cases. Enable collection of
      * run-time statistics.
      */
     protected void setUp() throws SQLException {
         getConnection().setAutoCommit(false);
         Statement stmt = createStatement();
         stmt.executeUpdate("create table t (c1 int, c2 int, c3 varchar(255))");
         stmt.executeUpdate("create index idx on t (c1)");
         stmt.executeUpdate("call SYSCS_UTIL.SYSCS_SET_RUNTIMESTATISTICS(1)");
         stmt.close();
         commit();
     }
 
     /**
      * Drop tables used in the test.
      */
     protected void tearDown() throws Exception {
         Statement stmt = createStatement();
         stmt.executeUpdate("drop table t");
         commit();
         super.tearDown();
     }
 
     /**
     * Flush the cache so that row count changes are visible. When a dirty
     * page is written to disk, the row count estimate for the container will
     * be updated with the number of added/deleted rows on that page since
     * the last time the page was read from disk or written to disk. We invoke
     * a checkpoint in order to force all dirty pages to be flushed and make
     * all row count changes visible.
      */
     private void flushRowCount(Statement stmt) throws SQLException {
        stmt.execute("CALL SYSCS_UTIL.SYSCS_CHECKPOINT_DATABASE()");
     }
 
     /**
      * Negative test - set stalePlanCheckInterval to a value out of range.
      */
     public void testStalePlanCheckIntervalOutOfRange() throws SQLException {
         Statement stmt = createStatement();
         assertStatementError("XCY00", stmt,
                              "call SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(" +
                              "'derby.language.stalePlanCheckInterval', '2')");
         stmt.close();
     }
 
     /**
      * Test that the query plan is changed when the size of a small table
      * changes.
      *
      * <p><b>Note:</b> This test is outdated since Derby now tries to use index
      * scans whenever possible on small tables (primarily to avoid table locks
      * for certain isolation levels, but also because a small table is likely
      * to grow).
      */
     public void testStalePlansOnSmallTable() throws SQLException {
         Statement stmt = createStatement();
 
         PreparedStatement insert =
             prepareStatement("insert into t values (?,?,?)");
         insert.setInt(1, 1);
         insert.setInt(2, 100);
         insert.setString(3, Formatters.padString("abc", 255));
         insert.executeUpdate();
         commit();
 
         // Make sure row count from insert is flushed out
         flushRowCount(stmt);
 
         PreparedStatement ps =
             prepareStatement("select count(c1 + c2) from t where c1 = 1");
 
         // Expect this to do an index scan
         String[][] expected = {{ "1" }};
         JDBC.assertFullResultSet(ps.executeQuery(), expected);
         assertTrue(SQLUtilities.
                    getRuntimeStatisticsParser(stmt).usedIndexScan());
 
         // Execute 11 more times, the plan should not change
         for (int i = 0; i < 11; i++) {
             JDBC.assertFullResultSet(ps.executeQuery(), expected);
         }
 
         // Expect index scan
         assertTrue(SQLUtilities.
                    getRuntimeStatisticsParser(stmt).usedIndexScan());
         commit();
 
         // Now increase the size of the table
         insert.setInt(2, 100);
         for (int i = 2; i <= 10; i++) {
             insert.setInt(1, i);
             insert.executeUpdate();
         }
         commit();
 
         // Make sure row count from inserts is flushed out
         flushRowCount(stmt);
 
         // Execute 11 times, the plan should not change
         for (int i = 0; i < 11; i++) {
             JDBC.assertFullResultSet(ps.executeQuery(), expected);
         }
 
         // Expect this to use index
         JDBC.assertFullResultSet(ps.executeQuery(), expected);
         assertTrue(SQLUtilities.
                    getRuntimeStatisticsParser(stmt).usedIndexScan());
         commit();
 
         // Now shrink the table back to its original size
         stmt.executeUpdate("delete from t where c1 >= 2");
 
         // Execute 11 times, the plan should not change
         for (int i = 0; i < 11; i++) {
             JDBC.assertFullResultSet(ps.executeQuery(), expected);
         }
 
         // Expect this to do an index scan
         assertTrue(SQLUtilities.
                    getRuntimeStatisticsParser(stmt).usedIndexScan());
 
         stmt.close();
         ps.close();
         insert.close();
     }
 
     /**
      * Test that the query plan changes when a large table is modified.
      */
     public void testStalePlansOnLargeTable() throws SQLException {
         Statement stmt = createStatement();
 
         PreparedStatement insert =
             prepareStatement("insert into t values (?,?,?)");
         insert.setInt(1, 1);
         insert.setInt(2, 1);
         insert.setString(3, Formatters.padString("abc", 255));
         insert.executeUpdate();
 
         PreparedStatement insert2 =
             prepareStatement("insert into t select c1+?, c2+?, c3 from t");
         for (int i = 1; i <= 512; i *= 2) {
             insert2.setInt(1, i);
             insert2.setInt(2, i);
             insert2.executeUpdate();
         }
 
         commit();
 
         // Make sure row count from inserts is flushed out
         flushRowCount(stmt);
 
         PreparedStatement ps = prepareStatement(
             "select count(c1 + c2) from t where c1 = 1");
 
         // Expect this to use index
         String[][] expected = {{ "1" }};
         JDBC.assertFullResultSet(ps.executeQuery(), expected);
         assertTrue(SQLUtilities.
                    getRuntimeStatisticsParser(stmt).usedIndexScan());
         commit();
 
        // Change the row count a little bit. A recompile will only be
        // triggered if the row count changes by 10% or more.
        for (int i = 1025; i <= 1250; i++) {
             insert.setInt(1, i);
             insert.setInt(2, i);
             insert.executeUpdate();
         }
         commit();
 
         // Change the data so a table scan would make more sense.
         // Use a qualifier to convince TableScanResultSet not to
         // update the row count in the store (which would make it
         // hard for this test to control when recompilation takes
         // place).
         stmt.executeUpdate("update t set c1 = 1 where c1 > 0");
 
         // Make sure row count from inserts is flushed out
         flushRowCount(stmt);
 
         // Execute 11 more times, the plan should not change
         for (int i = 0; i < 11; i++) {
            JDBC.assertSingleValueResultSet(ps.executeQuery(), "1250");
         }
 
         // Expect this to use table scan, as the above update has basically
         // made all the rows in the table be equal to "1", thus using the index
         // does not help if all the rows are going to qualify.
         assertTrue(SQLUtilities.
                    getRuntimeStatisticsParser(stmt).usedTableScan());
 
         // Change the row count significantly
         stmt.executeUpdate("insert into t select c1,c2,c3 from t where c1<128");
 
         // Make sure row count from inserts is flushed out
         flushRowCount(stmt);
 
         // Execute 11 times, the plan should change
         for (int i = 0; i < 11; i++) {
            JDBC.assertSingleValueResultSet(ps.executeQuery(), "2500");
         }
 
         // Expect this to do table scan
         assertTrue(SQLUtilities.
                    getRuntimeStatisticsParser(stmt).usedTableScan());
 
         // Change the distribution back to where an index makes sense
         stmt.executeUpdate("update t set c1 = c2");
 
         // Change the row count significantly
         stmt.executeUpdate("insert into t select c1, c2, c3 from t");
 
         // Make sure row count from inserts is flushed out
         flushRowCount(stmt);
 
         // Execute 11 times, the plan should change
         for (int i = 0; i < 11; i++) {
             JDBC.assertFullResultSet(ps.executeQuery(),
                                      new String[][] { { "4" } });
         }
 
         // Expect this to do index to baserow
         assertTrue(SQLUtilities.
                    getRuntimeStatisticsParser(stmt).usedIndexRowToBaseRow());
 
         stmt.close();
         insert.close();
         insert2.close();
         ps.close();
     }
 }
