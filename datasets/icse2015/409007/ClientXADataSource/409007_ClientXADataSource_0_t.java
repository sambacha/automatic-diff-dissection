 /*
 
    Derby - Class org.apache.derby.jdbc.ClientXADataSource
 
    Copyright (c) 2001, 2005 The Apache Software Foundation or its licensors, where applicable.
 
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
 
 import java.sql.SQLException;
 import javax.sql.DataSource;
 import javax.sql.XAConnection;
 import javax.sql.XADataSource;
 
 import org.apache.derby.client.ClientXAConnection;
 import org.apache.derby.client.net.NetLogWriter;
 import org.apache.derby.client.am.SqlException;
 
 
 /**
  * <p>
  * This is Derby's network XADataSource for use with JDBC3.0 and JDBC2.0.
  * </p>
  * An XADataSource is a factory for XAConnection objects.  It represents a
  * RM in a DTP environment.  An object that implements the XADataSource
  * interface is typically registered with a JNDI service provider.   	
  * <P>
  * ClientXADataSource automatically supports the correct JDBC specification version
  * for the Java Virtual Machine's environment.
  * <UL>
  * <LI> JDBC 3.0 - Java 2 - JDK 1.4, J2SE 5.0
  * <LI> JDBC 2.0 - Java 2 - JDK 1.2,1.3
  * </UL>
  *
  * <P>ClientXADataSource is serializable and referenceable.</p>
  *
  * <P>See ClientDataSource for DataSource properties.</p>
  */
public class ClientXADataSource extends ClientDataSource implements XADataSource {
     public static final String className__ = "org.apache.derby.jdbc.ClientXADataSource";
 
     // following serialVersionUID was generated by the JDK's serialver program
     // verify it everytime that ClientXADataSource is modified
     private static final long serialVersionUID = 7057075094707674880L;
 
     public ClientXADataSource() {
     }
 
     public XAConnection getXAConnection() throws SQLException {
         return getXAConnection(getUser(), getPassword());
     }
 
     public XAConnection getXAConnection(String user, String password) throws SQLException {
         try
         {
             NetLogWriter dncLogWriter = (NetLogWriter) super.computeDncLogWriterForNewConnection("_xads");
             return new ClientXAConnection(this, dncLogWriter, user, password);
         }
         catch ( SqlException se )
         {
             throw se.getSQLException();
         }
     }    
 }
