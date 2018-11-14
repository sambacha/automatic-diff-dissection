 /*
  
    Derby - Class org.apache.derby.impl.jdbc.EmbedPreparedStatement40
  
   Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 
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
 
 package org.apache.derby.impl.jdbc;
 
 import java.io.InputStream;
 import java.io.Reader;
 import java.sql.RowId;
 import java.sql.NClob;
 import java.sql.ParameterMetaData;
 import java.sql.SQLException;
 import java.sql.SQLXML;
 import java.sql.Types;
 import org.apache.derby.iapi.reference.SQLState;
 import org.apache.derby.iapi.error.StandardException;
 
 public class EmbedPreparedStatement40 extends  EmbedPreparedStatement30 {
     
     public EmbedPreparedStatement40(EmbedConnection conn, String sql, boolean forMetaData,
         int resultSetType, int resultSetConcurrency, int resultSetHoldability,
         int autoGeneratedKeys, int[] columnIndexes, String[] columnNames) throws SQLException {
         super(conn, sql, forMetaData, resultSetType, resultSetConcurrency, resultSetHoldability,
             autoGeneratedKeys, columnIndexes, columnNames);
     }
     
     public void setRowId(int parameterIndex, RowId x) throws SQLException{
         throw Util.notImplemented();
     }
     
     public void setNString(int index, String value) throws SQLException{
         throw Util.notImplemented();
     }
 
     public void setNCharacterStream(int parameterIndex, Reader value)
             throws SQLException {
         throw Util.notImplemented();
     }
 
     public void setNCharacterStream(int index, Reader value, long length) throws SQLException{
         throw Util.notImplemented();
     }
 
     public void setNClob(int parameterIndex, Reader reader)
             throws SQLException {
         throw Util.notImplemented();
     }
 
     public void setNClob(int index, NClob value) throws SQLException{
         throw Util.notImplemented();
     }    
 
     public void setNClob(int parameterIndex, Reader reader, long length)
     throws SQLException{
         throw Util.notImplemented();
     }
     
     public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException{
         throw Util.notImplemented();
     }
     
    /**
     * JDBC 4.0
     *
     * Retrieves the number, types and properties of this PreparedStatement
     * object's parameters.
     *
     * @return a ParameterMetaData object that contains information about the
     * number, types and properties of this PreparedStatement object's parameters.
     * @exception SQLException if a database access error occurs
     *
     */
     public ParameterMetaData getParameterMetaData()
         throws SQLException
     {
 	  checkStatus();
 	  return new EmbedParameterMetaData40(
 				getParms(), preparedStatement.getParameterTypes());
     }
     
     /**
      * Returns false unless <code>interfaces</code> is implemented 
      * 
      * @param  interfaces             a Class defining an interface.
      * @return true                   if this implements the interface or 
      *                                directly or indirectly wraps an object 
      *                                that does.
      * @throws java.sql.SQLException  if an error occurs while determining 
      *                                whether this is a wrapper for an object 
      *                                with the given interface.
      */
     public boolean isWrapperFor(Class<?> interfaces) throws SQLException {
         checkStatus();
         return interfaces.isInstance(this);
     }
     
     /**
      * Returns <code>this</code> if this class implements the interface
      *
      * @param  interfaces a Class defining an interface
      * @return an object that implements the interface
      * @throws java.sql.SQLExption if no object if found that implements the 
      * interface
      */
     public <T> T unwrap(java.lang.Class<T> interfaces) 
                             throws SQLException{
         checkStatus();
         try {
             return interfaces.cast(this);
         } catch (ClassCastException cce) {
             throw newSQLException(SQLState.UNABLE_TO_UNWRAP,interfaces);
         }
     }
 }    
 
