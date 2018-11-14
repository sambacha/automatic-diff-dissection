 /*
 
    Derby - Class org.apache.derby.jdbc.EmbedPooledConnection
 
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
 
 package org.apache.derby.jdbc;
 
 import org.apache.derby.iapi.services.sanity.SanityManager;
 import org.apache.derby.iapi.reference.Property;
 import org.apache.derby.iapi.error.ExceptionSeverity;
 import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
 
 /* import impl class */
 import org.apache.derby.impl.jdbc.Util;
 import org.apache.derby.impl.jdbc.EmbedConnection;
 import org.apache.derby.iapi.jdbc.BrokeredConnection;
 import org.apache.derby.iapi.jdbc.BrokeredConnectionControl;
 import org.apache.derby.iapi.jdbc.EngineConnection;
 import org.apache.derby.impl.jdbc.EmbedPreparedStatement;
 import org.apache.derby.impl.jdbc.EmbedCallableStatement;
 
 
 import java.sql.Connection;
import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.sql.PreparedStatement;
 import java.sql.CallableStatement;
 
 import java.util.Vector;
 import java.util.Enumeration;
 
 /* -- New jdbc 20 extension types --- */
 import javax.sql.DataSource;
 import javax.sql.PooledConnection;
 import javax.sql.ConnectionEventListener;
 import javax.sql.ConnectionEvent;
 
 /** 
 	A PooledConnection object is a connection object that provides hooks for
 	connection pool management.
 
 	<P>This is Derby's implementation of a PooledConnection for use in
 	the following environments:
 	<UL>
 	<LI> JDBC 3.0 - Java 2 - JDK 1.4, J2SE 5.0
 	<LI> JDBC 2.0 - Java 2 - JDK 1.2,1.3
 	</UL>
 
  */
 class EmbedPooledConnection implements javax.sql.PooledConnection, BrokeredConnectionControl
 {
 
     /** Static counter for connection ids */
     private static int idCounter = 0;
     
     /** The id for this connection.  */
     private int connectionId;
     
     /** the connection string */
     private String connString;
     
 	private Vector eventListener; // who wants to know I am closed or error
 
 	EmbedConnection realConnection;
 	int defaultIsolationLevel;
 	private boolean defaultReadOnly;
 	BrokeredConnection currentConnectionHandle;
 
 	// set up once by the data source
 	final ReferenceableDataSource dataSource;
 	private final String username;
 	private final String password;
 	/**
 		True if the password was passed in on the connection request, false if it came from the data source property.
 	*/
 	private final boolean requestPassword;
 
 	protected boolean isActive;
     
     private synchronized int nextId()
     {
         return idCounter++;
     }
 
 	EmbedPooledConnection(ReferenceableDataSource ds, String u, String p, boolean requestPassword) throws SQLException
 	{
         connectionId = nextId();
 
 		dataSource = ds;
 		username = u;
 		password = p;
 		this.requestPassword = requestPassword;
 		isActive = true;
 
 		// open the connection up front in order to do authentication
 		openRealConnection();
 
 	}
 
 	String getUsername()
 	{
 		if (username == null || username.equals(""))
 			return Property.DEFAULT_USER_NAME;
 		else
 			return username;
 	}
 
 	String getPassword()
 	{
 		if (password == null)
 			return "";
 		else
 			return password;
 	}
 
 
 	/** 
 		Create an object handle for a database connection.
 
 		@return a Connection object
 
 		@exception SQLException - if a database-access error occurs.
 	*/
 	public synchronized Connection getConnection() throws SQLException
 	{
 		checkActive();
 
 		// need to do this in case the connection is forcibly removed without
 		// first being closed.
 		closeCurrentConnectionHandle();
 
 
 		// RealConnection is not null if the app server yanks a local
 		// connection from one client and give it to another.  In this case,
 		// the real connection is ready to be used.  Otherwise, set it up
 		if (realConnection == null)
 		{
 			// first time we establish a connection
 			openRealConnection();
 		}
 		else
 		{
 			resetRealConnection();
 		}
 
 		// now make a brokered connection wrapper and give this to the user
 		// we reuse the EmbedConnection(ie realConnection).
 		Connection c = getNewCurrentConnectionHandle();		
 		return c;
 	}
 
 	final void openRealConnection() throws SQLException {
 		// first time we establish a connection
 		Connection rc = dataSource.getConnection(username, password, requestPassword);
 
 		this.realConnection = (EmbedConnection) rc;
 		defaultIsolationLevel = rc.getTransactionIsolation();
 		defaultReadOnly = rc.isReadOnly();
 		if (currentConnectionHandle != null)
 			realConnection.setApplicationConnection(currentConnectionHandle);
 	}
 
 	final Connection getNewCurrentConnectionHandle() {
 		Connection applicationConnection = currentConnectionHandle =
 			((org.apache.derby.jdbc.Driver20) (realConnection.getLocalDriver())).newBrokeredConnection(this);
 		realConnection.setApplicationConnection(applicationConnection);
 		return applicationConnection;
 
 	}
 
 	/**
 		In this case the Listeners are *not* notified. JDBC 3.0 spec section 11.4
 	*/
 	private void closeCurrentConnectionHandle() throws SQLException {
 		if (currentConnectionHandle != null)
 		{
 			Vector tmpEventListener = eventListener;
 			eventListener = null;
 
 			try {
 				currentConnectionHandle.close();
 			} finally {
 				eventListener = tmpEventListener;
 			}
 
 			currentConnectionHandle = null;
 		}
 	}
 
 	void resetRealConnection() throws SQLException {
 
 		// ensure any outstanding changes from the previous
 		// user are rolledback.
 		realConnection.rollback();
 
 		// clear any warnings that are left over
 		realConnection.clearWarnings();
 
 		// need to reset transaction isolation, autocommit, readonly, holdability states
 		if (realConnection.getTransactionIsolation() != defaultIsolationLevel) {
 
 			realConnection.setTransactionIsolation(defaultIsolationLevel);
 		}
 
 		if (!realConnection.getAutoCommit())
 			realConnection.setAutoCommit(true);
 
 		if (realConnection.isReadOnly() != defaultReadOnly)
 			realConnection.setReadOnly(defaultReadOnly);
 
		if (realConnection.getHoldability() != ResultSet.HOLD_CURSORS_OVER_COMMIT)
			realConnection.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
 
 		// reset any remaining state of the connection
 		realConnection.resetFromPool();
 		if (SanityManager.DEBUG)
 		{
 			SanityManager.ASSERT(realConnection.transactionIsIdle(),
 			"real connection should have been idle at this point"); 			
 		}
 	}
 
 	/**
 		Close the Pooled connection.
 
 		@exception SQLException - if a database-access error occurs.
 	 */
 	public synchronized void close() throws SQLException
 	{
 		if (!isActive)
 			return;
 
 		closeCurrentConnectionHandle();
 		try {
 			if (realConnection != null) {
 				if (!realConnection.isClosed())
 					realConnection.close();
 			}
 
 		} finally {
 
 			realConnection = null;	// make sure I am not accessed again.
 			isActive = false;
 			eventListener = null;
 		}
 	}
 
 	/**
 		Add an event listener.
 	 */
 	public final synchronized void addConnectionEventListener(ConnectionEventListener listener) 
 	{
 		if (!isActive)
 			return;
 		if (listener == null)
 			return;
 		if (eventListener == null)
 			eventListener = new Vector();
 		eventListener.addElement(listener);
 	}
 
 	/**
 		Remove an event listener.
 	 */
 	public final synchronized void removeConnectionEventListener(ConnectionEventListener listener)
 	{
 		if (listener == null)
 			return;
 		if (eventListener != null)
 			eventListener.removeElement(listener);
 	}
 
 	/*
 	 * class specific method
 	 */
 
 	// called by ConnectionHandle when it needs to forward things to the
 	// underlying connection
 	public synchronized EngineConnection getRealConnection()
        throws SQLException
 	{
 		checkActive();
 
 		return realConnection;
 	}
 
     /**
      * @return The underlying language connection.
      */
 	public synchronized LanguageConnectionContext getLanguageConnection()
        throws SQLException
 	{
 		checkActive();
 
 		return realConnection.getLanguageConnection();
 	}
 
 
 	// my conneciton handle has caught an error (actually, the real connection
 	// has already handled the error, we just need to nofity the listener an
 	// error is about to be thrown to the app).
 	public synchronized void notifyError(SQLException exception)
 	{
 		// only report fatal error to the connection pool manager 
 		if (exception.getErrorCode() < ExceptionSeverity.SESSION_SEVERITY)
 			return;
 
 		// tell my listeners an exception is about to be thrown
 		if (eventListener != null && eventListener.size() > 0)
 		{
 			ConnectionEvent errorEvent = new ConnectionEvent(this, exception);
 
 			for (Enumeration e = eventListener.elements();
 				 e.hasMoreElements(); )
 			{
 				ConnectionEventListener l =
 					(ConnectionEventListener)e.nextElement();
 				l.connectionErrorOccurred(errorEvent);
 			}
 		}
 	}
 
 
        
 
 	final void checkActive() throws SQLException {
 		if (!isActive)
 			throw Util.noCurrentConnection();
 	}
 
 
 	/*
 	** BrokeredConnectionControl api
 	*/
 
 	/**
 		Returns true if isolation level has been set using either JDBC api or SQL
 	 */
 	public boolean isIsolationLevelSetUsingSQLorJDBC() throws SQLException {
 		if (realConnection != null)
 			return realConnection.getLanguageConnection().isIsolationLevelSetUsingSQLorJDBC();
 		else
 			return false;
 	}
 
 	/**
 		Reset the isolation level flag used to keep state in 
 		BrokeredConnection. It will get set to true when isolation level 
 		is set using JDBC/SQL. It will get reset to false at the start
 		and the end of a global transaction.
 	*/
 	public void resetIsolationLevelFlag() throws SQLException {
 		realConnection.getLanguageConnection().resetIsolationLevelFlagUsedForSQLandJDBC();
 	}
 	
 	
 	/**
 		Notify the control class that a SQLException was thrown
 		during a call on one of the brokered connection's methods.
 	*/
 	public void notifyException(SQLException sqle) {
 		this.notifyError(sqle);
 	}
 
 
 	/**
 		Allow control over setting auto commit mode.
 	*/
 	public void checkAutoCommit(boolean autoCommit) throws SQLException {
 	}
 
 	/**
 		Are held cursors allowed.
 	*/
 	public int checkHoldCursors(int holdability, boolean downgrade)
         throws SQLException
     {
         return holdability;
 	}
 
 	/**
 		Allow control over creating a Savepoint (JDBC 3.0)
 	*/
 	public void checkSavepoint() throws SQLException {
 	}
 
 	/**
 		Allow control over calling rollback.
 	*/
 	public void checkRollback() throws SQLException {
 	}
 
 	/**
 		Allow control over calling commit.
 	*/
 	public void checkCommit() throws SQLException {
 	}
 
 	/**
 		Close called on BrokeredConnection. If this call
 		returns true then getRealConnection().close() will be called.
 		
 	
 	Notify listners that connection is closed.
 		Don't close the underlying real connection as
 		it is pooled.
 	*/
 	public synchronized boolean closingConnection() throws SQLException {	    
 		//DERBY-2142-Null out the connection handle BEFORE notifying listeners.
 		//At time of the callback the PooledConnection must be 
 		//disassociated from its previous logical connection.
 		//If not there is a risk that the Pooled
 		//Connection could be returned to the pool, ready for pickup by a 
 		//new thread. This new thread then might obtain a java.sql.Connection 
 		//whose reference might get assigned to the currentConnectionHandle 
 		//field, meanwhile the previous thread completes the close making 
 		//the newly assigned currentConnectionHandle null, resulting in an NPE.
 		currentConnectionHandle = null;
 		// tell my listeners I am closed 
 		if (eventListener != null && eventListener.size() > 0)
 		{
 			ConnectionEvent closeEvent = new ConnectionEvent(this);
 
 			for (Enumeration e = eventListener.elements();
 				 e.hasMoreElements(); )
 			{
 				ConnectionEventListener l =
 					(ConnectionEventListener)e.nextElement();
 				l.connectionClosed(closeEvent);
 			}
 		}
 
 		return false;
 	}
 
 	/**
 		No need to wrap statements for PooledConnections.
 	*/
 	public Statement wrapStatement(Statement s) throws SQLException {
 		return s;
 	}
 	/**
          * Call the setBrokeredConnectionControl method inside the 
          * EmbedPreparedStatement class to set the BrokeredConnectionControl 
          * variable to this instance of EmbedPooledConnection
          * This will then be used to call the onStatementErrorOccurred
          * and onStatementClose events when the corresponding events
          * occur on the PreparedStatement
          *
          * @param  ps            PreparedStatment to be wrapped
          * @param  sql           String
          * @param  generatedKeys Object
          * @return returns the wrapped PreparedStatement
          * @throws java.sql.SQLException
 	 */
 	public PreparedStatement wrapStatement(PreparedStatement ps, String sql, Object generatedKeys) throws SQLException {
                /*
                     
                 */
                 EmbedPreparedStatement ps_ = (EmbedPreparedStatement)ps;
                 ps_.setBrokeredConnectionControl(this);
 		return (PreparedStatement)ps_;
 	}
         
         /**
          * Call the setBrokeredConnectionControl method inside the 
          * EmbedCallableStatement class to set the BrokeredConnectionControl 
          * variable to this instance of EmbedPooledConnection
          * This will then be used to call the onStatementErrorOccurred
          * and onStatementClose events when the corresponding events
          * occur on the CallableStatement
          *
          * @param  cs            CallableStatment to be wrapped
          * @param  sql           String
          * @return returns the wrapped CallableStatement
          * @throws java.sql.SQLException
 	 */
 	public CallableStatement wrapStatement(CallableStatement cs, String sql) throws SQLException {
                 EmbedCallableStatement cs_ = (EmbedCallableStatement)cs;
                 cs_.setBrokeredConnectionControl(this);
 		return (CallableStatement)cs_;
 	}
     
     /** 
      * Get the string representation of this pooled connection.
      *
      * A pooled connection is assigned a separate id from a physical 
      * connection. When a container calls PooledConnection.toString(), 
      * it gets the string representation of this id. This is useful for 
      * developers implementing connection pools when they are trying to
      * debug pooled connections. 
      *
      * @return a string representation of the uniquie id for this pooled
      *    connection.
      *
      */
     public String toString()
     {
         if ( connString == null )
         {
             String physicalConnString = isActive ?
                 realConnection.toString() : "<none>";
             
             connString = 
               this.getClass().getName() + "@" + this.hashCode() + " " +
                 "(ID = " + connectionId + "), " +
                 "Physical Connection = " + physicalConnString;
         }    
         
         return connString;
     }
     
     /*-----------------------------------------------------------------*/
     /*
      * These methods are from the BrokeredConnectionControl interface. 
      * These methods are needed to provide StatementEvent support for 
      * derby. 
      * They are actually implemented in EmbedPooledConnection40 but have
      * a dummy implementation here so that the compilation wont fail when they
      * are compiled with jdk1.4
      */
     
     /**
      * Dummy implementation for the actual methods found in 
      * org.apache.derby.jdbc.EmbedPooledConnection40
      * @param statement PreparedStatement
      */
     public void onStatementClose(PreparedStatement statement) {
         
     }
     
     /**
      * Dummy implementation for the actual methods found in 
      * org.apache.derby.jdbc.EmbedPooledConnection40
      * @param statement PreparedStatement
      * @param sqle      SQLException 
      */
     public void onStatementErrorOccurred(PreparedStatement statement,
             SQLException sqle) {
         
     }
 }
