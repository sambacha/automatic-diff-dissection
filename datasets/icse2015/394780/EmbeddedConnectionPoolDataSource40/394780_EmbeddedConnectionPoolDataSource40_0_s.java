 /*
  
    Derby - Class org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource40
  
    Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
  
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
  
  */
 package org.apache.derby.jdbc;
 
 import java.sql.BaseQuery;
 import java.sql.QueryObjectFactory;
 import java.sql.QueryObjectGenerator;
 import java.sql.SQLException;
 import javax.sql.ConnectionPoolDataSource;
 import javax.sql.PooledConnection;
 
 /**
 * This class is meant to be used while running the applications with jdbc4.0
 * support. It extends EmbeddedDataSource40 which implements jdbc 4.0 
 * specific methods.
 */
 
 public class EmbeddedConnectionPoolDataSource40 
                                 extends EmbeddedConnectionPoolDataSource 
                                 implements ConnectionPoolDataSource {    
     
     /**
      * returns null indicating that no driver specific implementation for 
      * QueryObjectGenerator available
      * @return null
      */
     public QueryObjectGenerator getQueryObjectGenerator() throws SQLException {
         return null;
     }
     
     /**
      * This method forwards all the calls to default query object provided by 
      * the jdk.
      * @param ifc interface to generated concreate class
      * @return concreat class generated by default qury object generator
      */
     public <T extends BaseQuery> T createQueryObject(Class<T> ifc) 
                                                     throws SQLException {
         return QueryObjectFactory.createDefaultQueryObject (ifc, this);
     }
     
     /**
      * create and returns EmbedPooledConnection.
      */
     protected PooledConnection createPooledConnection (String user, 
             String password, boolean requestPAssword)  throws SQLException {
         return new EmbedPooledConnection40 (this, user, password, true);
     }    
         
 }