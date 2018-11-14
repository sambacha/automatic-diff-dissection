 /*
    Derby - Class org.apache.derbyTesting.functionTests.tests.derbynet.SqlExceptionTest
  
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
  
  */
 package org.apache.derbyTesting.functionTests.tests.derbynet;
 
import org.apache.derbyTesting.junit.BaseJDBCTestCase;
 import org.apache.derby.client.am.SqlException;
 import org.apache.derby.client.am.ClientMessageId;
 import org.apache.derby.shared.common.reference.SQLState;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
 
 /**
  * This is used for testing the SqlException class.  This test can be added
  * to.  My itch right now is to verify that exception chaining is working
  * correctly.
 *
 * This test also verifies that a SQLException object generated out of the
 * derby network client driver can be serialized (DERBY-790).
  */
 
public class SqlExceptionTest extends BaseJDBCTestCase
 {    
     public SqlExceptionTest(String name)
     {
         super(name);
     }
     
     /**
      * Makes sure exception chaining works correctly (DERBY-1117)
      */
     public void testChainedException() {
         IOException ioe = new IOException("Test exception");
         SqlException sqle = new SqlException(null,
             new ClientMessageId(SQLState.NOGETCONN_ON_CLOSED_POOLED_CONNECTION),
             ioe);
         SQLException javae = sqle.getSQLException();
         
         // The underlying SqlException is the first cause; the IOException
         // should be the second cause        
         assertEquals(sqle, javae.getCause());
         assertEquals(ioe, javae.getCause().getCause());
         assertNull(sqle.getNextException());
     }
     
     /**
      * Make sure a SQLException is chained as a nextSQLException()
      * rather than as a chained exception
      */
     public void testNextException() {
         SQLException nexte = new SQLException("test");
         SqlException sqle = new SqlException(null,
             new ClientMessageId(SQLState.NOGETCONN_ON_CLOSED_POOLED_CONNECTION),
             nexte);
         SQLException javae = sqle.getSQLException();
         
         assertEquals(sqle, javae.getCause());
         assertNull(javae.getCause().getCause());
         assertEquals(nexte, javae.getNextException());
         
         // Make sure exception chaining works with Derby's SqlException
         // just as well as java.sql.SQLException
         SqlException internalException = 
             new SqlException(null, 
                 new ClientMessageId("08000"));
         
         javae = new SqlException(null, 
             new ClientMessageId(SQLState.NOGETCONN_ON_CLOSED_POOLED_CONNECTION),
             internalException).getSQLException();
         
         assertNotNull(javae.getNextException());
         assertEquals(javae.getNextException().getSQLState(), "08000");
     }

    /**
     * Verify that a SQLException generated by the derby network client
     * driver can be serialized (DERBY-790).
     */
    public void testSerializedException() throws Exception {

        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            // generate some exception by inserting some duplicate
            // primary keys in the same batch
            // This will generate some chained / nested transactions
            // as well
            String insertData = "INSERT INTO tableWithPK values " +
                "(1, 1), (2, 2), (3, 3), (4, 4), (5, 5)";
            stmt.addBatch(insertData);
            stmt.addBatch(insertData);
            stmt.addBatch(insertData);
            stmt.executeBatch();

            // In case the statement completes successfully which is not
            // expected
            fail("Unexpected: SQL statement should have failed");
        } catch (SQLException se) {
            // Verify the SQLException can be serialized (DERBY-790)
            SQLException se_ser = recreateSQLException(se);
            // and that the original and serialized exceptions are equals
            assertSQLState("Unexpected SQL State", se.getSQLState(), se_ser);
            assertSQLExceptionEquals(se, se_ser);
        }
    }

    /**
     * Set up the connection to the database.
     */
    public void setUp() throws Exception {
        Connection conn = getConnection();
        String createTableWithPK = "CREATE TABLE tableWithPK (" +
                "c1 int primary key," +
                "c2 int)";
        Statement stmt = conn.createStatement();
        stmt.execute(createTableWithPK);
        stmt.close();
        conn.close();
    }

    /**
     * Drop the table
     */
    public void tearDown() throws Exception {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DROP TABLE tableWithPK");
        stmt.close();
        conn.close();
        super.tearDown();
    }

    /**
     * Recreate a SQLException by serializing the passed-in one and
     * deserializing it into a new one that we're returning.
     */
    private SQLException recreateSQLException(SQLException se)
    throws Exception
    {
        SQLException recreatedDS = null;

        // Serialize and recreate (deserialize) the passed-in Exception
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(se);
        oos.flush();
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        recreatedDS = (SQLException) ois.readObject();
        ois.close();
        assertNotNull(recreatedDS);

        return recreatedDS;
    }
 }
