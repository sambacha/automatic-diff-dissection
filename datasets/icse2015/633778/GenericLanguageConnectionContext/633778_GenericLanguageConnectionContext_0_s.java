 /*
 
    Derby - Class org.apache.derby.impl.sql.conn.GenericLanguageConnectionContext
 
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to you under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 
  */
 
 package org.apache.derby.impl.sql.conn;
 
 import org.apache.derby.iapi.services.context.ContextImpl;
 import org.apache.derby.iapi.services.cache.CacheManager;
 
 import org.apache.derby.impl.sql.compile.CompilerContextImpl;
 import org.apache.derby.impl.sql.execute.InternalTriggerExecutionContext;
 import org.apache.derby.impl.sql.execute.AutoincrementCounter;
 import org.apache.derby.impl.sql.GenericPreparedStatement;
 import org.apache.derby.impl.sql.GenericStatement;
 import org.apache.derby.iapi.sql.Statement;
 
 import org.apache.derby.iapi.services.property.PropertyUtil;
 import org.apache.derby.iapi.services.context.ContextManager;
 import org.apache.derby.iapi.services.monitor.Monitor;
 import org.apache.derby.iapi.services.sanity.SanityManager;
 import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
 import org.apache.derby.iapi.services.loader.GeneratedClass;
 import org.apache.derby.iapi.services.cache.Cacheable;
 import org.apache.derby.iapi.db.Database;
 import org.apache.derby.iapi.error.StandardException;
 import org.apache.derby.iapi.sql.compile.CompilerContext;
 import org.apache.derby.iapi.sql.compile.OptimizerFactory;
 import org.apache.derby.iapi.sql.conn.Authorizer;
 import org.apache.derby.iapi.error.ExceptionSeverity;
 import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
 import org.apache.derby.iapi.sql.conn.LanguageConnectionFactory;
 import org.apache.derby.iapi.sql.conn.StatementContext;
 import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
 import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptorList;
 import org.apache.derby.iapi.sql.dictionary.DataDictionary;
 import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
 import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
 import org.apache.derby.iapi.types.DataValueFactory;
 import org.apache.derby.iapi.sql.compile.TypeCompilerFactory;
 import org.apache.derby.iapi.sql.depend.DependencyManager;
 import org.apache.derby.iapi.sql.depend.Provider;
 import org.apache.derby.iapi.reference.SQLState;
 import org.apache.derby.iapi.reference.Limits;
 import org.apache.derby.iapi.sql.execute.ConstantAction;
 import org.apache.derby.iapi.sql.execute.CursorActivation;
 import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
 import org.apache.derby.iapi.sql.execute.ExecutionContext;
 import org.apache.derby.iapi.sql.execute.ExecutionStmtValidator;
 import org.apache.derby.iapi.sql.Activation;
 import org.apache.derby.iapi.sql.LanguageFactory;
 import org.apache.derby.iapi.sql.PreparedStatement;
 import org.apache.derby.iapi.sql.ResultSet;
 import org.apache.derby.iapi.sql.ParameterValueSet;
 
 import org.apache.derby.iapi.store.access.TransactionController;
 import org.apache.derby.iapi.store.access.XATransactionController;
 import org.apache.derby.iapi.util.IdUtil;
 import org.apache.derby.iapi.util.StringUtil;
 
 import org.apache.derby.catalog.UUID;
 import org.apache.derby.iapi.sql.execute.RunTimeStatistics;
 import org.apache.derby.iapi.db.TriggerExecutionContext;
 import org.apache.derby.iapi.reference.Property;
 
 import java.util.List;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 /**
  * LanguageConnectionContext keeps the pool of prepared statements,
  * activations, and cursors in use by the current connection.
  * <p>
  * The generic impl does not provide statement caching.
  *
  *
  */
 public class GenericLanguageConnectionContext
 	extends ContextImpl 
 	implements LanguageConnectionContext
 {
 
 	// make sure these are not zeros
 	private final static int NON_XA = 0;
 	private final static int XA_ONE_PHASE = 1;
 	private final static int XA_TWO_PHASE = 2;
 
 	/*
 		fields
 	 */
 
 	private final ArrayList acts;
 	private volatile boolean unusedActs=false;
 	/** The maximum size of acts since the last time it was trimmed. Used to
 	 * determine whether acts should be trimmed to reclaim space. */
 	private int maxActsSize;
 	protected int bindCount;
 	private boolean ddWriteMode;
 	private boolean runTimeStatisticsSetting ;
 	private boolean statisticsTiming;
 
 	//all the temporary tables declared for this connection
 	private ArrayList allDeclaredGlobalTempTables;
 	//The currentSavepointLevel is used to provide the rollback behavior of temporary tables.
 	//At any point, this variable has the total number of savepoints defined for the transaction.
 	private int currentSavepointLevel = 0;
 
 	protected long	nextCursorId;
 
 	protected int	nextSavepointId;
 
 	private RunTimeStatistics runTimeStatisticsObject;
 	private StringBuffer sb;
 
 	private Database db;
 
 	private final int instanceNumber;
 	private String drdaID;
 	private String dbname;
     
 	/**
 	The transaction to use within this language connection context.  It may
 	be more appropriate to have it in a separate context (LanguageTransactionContext?).
 	REVISIT (nat): I shoehorned the transaction context that
 	the language uses into this class.  The main purpose is so
 	that the various language code can find out what its
 	transaction is.
 	**/
 	private final TransactionController tran;
 
 	/**
 	 * If non-null indicates that a read-only nested 
      * user transaction is in progress.
 	 */
 	private TransactionController readOnlyNestedTransaction;
 	
 	/**
 	 * queryNestingDepth is a counter used to keep track of how many calls 
 	 * have been made to begin read-only nested transactions. Only the first call 
 	 * actually starts a Nested User Transaction with the store. Subsequent
 	 * calls simply increment this counter. commitNestedTransaction only
 	 * decrements the counter and when it drops to 0 actually commits the 
 	 * nested user transaction.
 	 */
 	private int queryNestingDepth;
 
 	protected DataValueFactory dataFactory;
 	protected LanguageFactory langFactory;
 	protected TypeCompilerFactory tcf;
 	protected OptimizerFactory of;
 	protected LanguageConnectionFactory connFactory;
 	
 	/* 
 	 * A statement context is "pushed" and "popped" at the beginning and
      * end of every statement so that only that statement is cleaned up
      * on a Statement Exception.  As a performance optimization, we only push
      * the outermost statement context once, and never pop it.  Also, we
 	 * save off a 2nd StatementContext for speeding server side method
 	 * invocation, though we still push and pop it as needed.  All other
      * statement contexts will allocated and pushed and popped on demand.
      */
 	private final StatementContext[] statementContexts = new StatementContext[2];
 	private int     statementDepth;
 	protected int	  outermostTrigger = -1;
 
     protected Authorizer authorizer;
 	protected String userName = null; //The name the user connects with.
 	                                  //May still be quoted.
 	
 	protected SchemaDescriptor	sd;
 
 	// RESOLVE - How do we want to set the default.
     private int defaultIsolationLevel = ExecutionContext.READ_COMMITTED_ISOLATION_LEVEL;
 	protected int isolationLevel = defaultIsolationLevel;
 
 	private boolean isolationLevelExplicitlySet = false;
 	// Isolation level can be changed using JDBC api Connection.setTransactionIsolation
 	// or it can be changed using SQL "set current isolation = NEWLEVEL".
 	// 
 	// In XA transactions, BrokeredConnection keeps isolation state information.
 	// When isolation is changed in XA transaction using JDBC, that state gets
 	// correctly set in BrokeredConnection.setTransactionIsolation method. But
 	// when SQL is used to set the isolation level, the code path is different
 	// and it does not go through BrokeredConnection's setTransactionIsolation
 	// method and hence the state is not maintained correctly when coming through
 	// SQL. To get around this, I am adding following flag which will get set
 	// everytime the isolation level is set using JDBC or SQL. This flag will be
 	// checked at global transaction start and end time. If the flag is set to true
 	// then BrokeredConnection's isolation level state will be brought upto date
 	// with Real Connection's isolation level and this flag will be set to false
 	// after that.
 	private boolean isolationLevelSetUsingSQLorJDBC = false;
 
 	// isolation level to when preparing statements.
 	// if unspecified, the statement won't be prepared with a specific 
 	// scan isolationlevel
 	protected int prepareIsolationLevel = ExecutionContext.UNSPECIFIED_ISOLATION_LEVEL;
 
 	// Whether or not to write executing statement info to db2j.log
 	private boolean logStatementText;
 	private boolean logQueryPlan;
 	private HeaderPrintWriter istream;
 
 	// this used to be computed in OptimizerFactoryContextImpl; i.e everytime a
 	// connection was made. To keep the semantics same I'm putting it out here
 	// instead of in the OptimizerFactory which is only initialized when the
 	// database is booted.
 	private int lockEscalationThreshold; 
 
 	private ArrayList stmtValidators;
 	private ArrayList triggerExecutionContexts;
 	private ArrayList triggerTables;
 
 	// OptimizerTrace
 	private boolean optimizerTrace;
 	private boolean optimizerTraceHtml;
 	private String lastOptimizerTraceOutput;
 	private String optimizerTraceOutput;
 
 	//// Support for AUTOINCREMENT
 
 	/**
 	 * To support lastAutoincrementValue: This is a hashtable which maps
 	 * schemaName,tableName,columnName to a Long value.
 	 */
 	private HashMap autoincrementHT;
 	/**
 	 * whether to allow updates or not. 
 	 */
 	private boolean autoincrementUpdate;
 	private long identityVal;	//support IDENTITY_VAL_LOCAL function
 	private boolean identityNotNull;	//frugal programmer
 
 	// cache of ai being handled in memory (bulk insert + alter table).
 	private HashMap autoincrementCacheHashtable;
 
 	/*
 	   constructor
 	*/
 	public GenericLanguageConnectionContext
 	(
 	 ContextManager cm,
 	 TransactionController tranCtrl,
 
 	 LanguageFactory lf,
 	 LanguageConnectionFactory lcf,
 	 Database db,
 	 String userName,
 	 int instanceNumber,
 	 String drdaID,
 	 String dbname)
 		 throws StandardException
 	{
 		super(cm, org.apache.derby.iapi.reference.ContextId.LANG_CONNECTION);
 		acts = new ArrayList();
 		tran = tranCtrl;
 
 		dataFactory = lcf.getDataValueFactory();
 		tcf = lcf.getTypeCompilerFactory();
 		of = lcf.getOptimizerFactory();
 		langFactory =  lf;
 		connFactory =  lcf;
         this.db = db;
 		this.userName = userName;
 		this.instanceNumber = instanceNumber;
 		this.drdaID = drdaID;
 		this.dbname = dbname;
 
 		/* Find out whether or not to log info on executing statements to error log
 		 */
 		String logStatementProperty = PropertyUtil.getServiceProperty(getTransactionCompile(),
 					"derby.language.logStatementText");
 		logStatementText = Boolean.valueOf(logStatementProperty).booleanValue();
 
 		String logQueryPlanProperty = PropertyUtil.getServiceProperty(getTransactionCompile(),
 					"derby.language.logQueryPlan");
 		logQueryPlan = Boolean.valueOf(logQueryPlanProperty).booleanValue();
 
 		setRunTimeStatisticsMode(logQueryPlan);
 
 		lockEscalationThreshold = 
 			PropertyUtil.getServiceInt(tranCtrl,
 									   Property.LOCKS_ESCALATION_THRESHOLD,
 									   Property.MIN_LOCKS_ESCALATION_THRESHOLD,
 									   Integer.MAX_VALUE,
 									   Property.DEFAULT_LOCKS_ESCALATION_THRESHOLD);															 
 		stmtValidators = new ArrayList();
 		triggerExecutionContexts = new ArrayList();
 		triggerTables = new ArrayList();
 	}
 
 	public void initialize() throws StandardException
 	{
 		//
 		//Creating the authorizer authorizes the connection.
 		authorizer = new GenericAuthorizer(IdUtil.getUserAuthorizationId(userName),this);
 
 		/*
 		** Set the authorization id.  User shouldn't
 		** be null or else we are going to blow up trying
 		** to create a schema for this user.
 		*/
 		if (SanityManager.DEBUG)
 		{
 			if (getAuthorizationId() == null)
 			{
 				SanityManager.THROWASSERT("User name is null," +
 					" check the connection manager to make sure it is set" +
 					" reasonably");
 			}
 		}
 
 
 		setDefaultSchema(initDefaultSchemaDescriptor());
 	}
 
 	protected SchemaDescriptor initDefaultSchemaDescriptor()
 		throws StandardException {
 		/*
 		** - If the database supports schemas and a schema with the
 		** same name as the user's name exists (has been created using
 		** create schema already) the database will set the users
 		** default schema to the the schema with the same name as the
 		** user.
         ** - Else Set the default schema to APP.
         */
 		// SchemaDescriptor sd;
 
 		DataDictionary dd = getDataDictionary();
         String authorizationId = getAuthorizationId();
 	
 		if ( (sd = dd.getSchemaDescriptor(authorizationId, getTransactionCompile(), false)) == null )
 		{
 			sd = new SchemaDescriptor(dd, authorizationId, authorizationId, (UUID) null, false);
 		}
 		return sd;
 	}
 
 	//
 	// LanguageConnectionContext interface
 	//
 	/**
 	 * @see LanguageConnectionContext#getLogStatementText
 	 */
 	public boolean getLogStatementText()
 	{
 		return logStatementText;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#setLogStatementText
 	 */
 	public void setLogStatementText(boolean logStatementText)
 	{
 		this.logStatementText = logStatementText;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#getLogQueryPlan
 	 */
 	public boolean getLogQueryPlan()
 	{
 		return logQueryPlan;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#usesSqlAuthorization
 	 */
 	public boolean usesSqlAuthorization()
 	{
 		return getDataDictionary().usesSqlAuthorization();
 	}
 
 	/**
 	 * get the lock escalation threshold.
 	 */
 	public int getLockEscalationThreshold()
 	{
 		return lockEscalationThreshold;
 	}
 
 	/**
 	 * Add the activation to those known about by this connection.
 	 */
 	public void addActivation(Activation a) 
 		throws StandardException {
 		acts.add(a);
 
 		if (acts.size() > maxActsSize) {
 			maxActsSize = acts.size();
 		}
 	}
 
 	public void closeUnusedActivations()
 			throws StandardException
 	{
 		// DERBY-418. Activations which are marked unused,
 		// are closed here. Activations Vector is iterated 
 		// to identify and close unused activations, only if 
 		// unusedActs flag is set to true and if the total 
 		// size exceeds 20.
 		if( (unusedActs) && (acts.size() > 20) ) {
 			unusedActs = false;
 
 			for (int i = acts.size() - 1; i >= 0; i--) {
 
 				// it maybe the case that a Activation's reset() ends up
 				// closing one or more activation leaving our index beyond
 				// the end of the array
 				if (i >= acts.size())
 					continue;
 
 				Activation a1 = (Activation) acts.get(i);
 				if (!a1.isInUse()) {
 					a1.close();
 				}
 			}
 		}
 
 		if (SanityManager.DEBUG) {
 
 			if (SanityManager.DEBUG_ON("memoryLeakTrace")) {
 
 				if (acts.size() > 20)
 					System.out.println("memoryLeakTrace:GenericLanguageContext:activations " + acts.size());
 			}
 		}
 	}
 
 	/**
 	 * Make a note that some activations are marked unused
 	 */
 	public void notifyUnusedActivation() {
 	    unusedActs = true;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#checkIfAnyDeclaredGlobalTempTablesForThisConnection
 	 */
 	public boolean checkIfAnyDeclaredGlobalTempTablesForThisConnection() {
 		return (allDeclaredGlobalTempTables == null ? false : true);
 	}
 
 	/**
 	 * @see LanguageConnectionContext#addDeclaredGlobalTempTable
 	 */
 	public void addDeclaredGlobalTempTable(TableDescriptor td)
 		throws StandardException {
 
 		if (findDeclaredGlobalTempTable(td.getName()) != null) //if table already declared, throw an exception
 		{
 			throw
 				StandardException.newException(
 											   SQLState.LANG_OBJECT_ALREADY_EXISTS_IN_OBJECT,
 											   "Declared global temporary table",
 											   td.getName(),
 											   "Schema",
 											   SchemaDescriptor.STD_DECLARED_GLOBAL_TEMPORARY_TABLES_SCHEMA_NAME);
 		}
 
 		//save all the information about temp table in this special class
 		TempTableInfo tempTableInfo = new TempTableInfo(td, currentSavepointLevel);
 
 		if (allDeclaredGlobalTempTables == null)
 			allDeclaredGlobalTempTables = new ArrayList();
 
 		allDeclaredGlobalTempTables.add(tempTableInfo);
 	}
 
 	/**
 	 * @see LanguageConnectionContext#dropDeclaredGlobalTempTable
 	 */
 	public boolean dropDeclaredGlobalTempTable(String tableName) {
     TempTableInfo tempTableInfo = findDeclaredGlobalTempTable(tableName);
 		if (tempTableInfo != null)
 		{
 			if (SanityManager.DEBUG)
 				if (tempTableInfo.getDeclaredInSavepointLevel() > currentSavepointLevel)
 					SanityManager.THROWASSERT("declared in savepoint level can not be higher than the current savepoint level");
 
 			//following checks if the table was declared in the current unit of work.
 			if (tempTableInfo.getDeclaredInSavepointLevel() == currentSavepointLevel)
 			{
 				//since the table was declared in this unit of work,
 				//the drop table method should remove it from the valid list of temp table for this unit of work
 				allDeclaredGlobalTempTables.remove(allDeclaredGlobalTempTables.indexOf(tempTableInfo));
 				if (allDeclaredGlobalTempTables.size() == 0)
 					allDeclaredGlobalTempTables = null;
 			}
 			else
 			{
 				//since the table was not declared in this unit of work, the drop table method will just mark the table as dropped
 				//in the current unit of work. This information will be used at rollback time.
 				tempTableInfo.setDroppedInSavepointLevel(currentSavepointLevel);
 			}
 			return true;
 		} else
 			return false;
 	}
 
 	/**
 	 * After a release of a savepoint, we need to go through our temp tables list. If there are tables with their declare or drop
 	 * or modified in savepoint levels set to savepoint levels higher than the current savepoint level, then we should change them
 	 * to the current savepoint level
 	 */
 	private void tempTablesReleaseSavepointLevels() {
     //unlike rollback, here we check for dropped in / declared in / modified in savepoint levels > current savepoint level only.
     //This is because the temp tables with their savepoint levels same as currentSavepointLevel have correct value assigned to them and
     //do not need to be changed and hence no need to check for >=
 		for (int i = 0; i < allDeclaredGlobalTempTables.size(); i++) {
 			TempTableInfo tempTableInfo = (TempTableInfo)allDeclaredGlobalTempTables.get(i);
 			if (tempTableInfo.getDroppedInSavepointLevel() > currentSavepointLevel)
 				tempTableInfo.setDroppedInSavepointLevel(currentSavepointLevel);
 
 			if (tempTableInfo.getDeclaredInSavepointLevel() > currentSavepointLevel)
 				tempTableInfo.setDeclaredInSavepointLevel(currentSavepointLevel);
 
 			if (tempTableInfo.getModifiedInSavepointLevel() > currentSavepointLevel)
 				tempTableInfo.setModifiedInSavepointLevel(currentSavepointLevel);
 		}
 	}
 
 	/**
 	 * do the necessary work at commit time for temporary tables
 	 * 1)If a temporary table was marked as dropped in this transaction, then remove it from the list of temp tables for this connection
 	 * 2)If a temporary table was not dropped in this transaction, then mark it's declared savepoint level and modified savepoint level as -1
 	 */
 	private void tempTablesAndCommit() {
 		for (int i = allDeclaredGlobalTempTables.size()-1; i >= 0; i--) {
 			TempTableInfo tempTableInfo = (TempTableInfo)allDeclaredGlobalTempTables.get(i);
 			if (tempTableInfo.getDroppedInSavepointLevel() != -1)
 			{
 				//this means table was dropped in this unit of work and hence should be removed from valid list of temp tables
 				allDeclaredGlobalTempTables.remove(i);
 			} else //this table was not dropped in this unit of work, hence set its declaredInSavepointLevel as -1 and also mark it as not modified 
 			{
 				tempTableInfo.setDeclaredInSavepointLevel(-1);
 				tempTableInfo.setModifiedInSavepointLevel(-1);
 			}
 		}
 	}
 
 	/**
 		Reset the connection before it is returned (indirectly) by
 		a PooledConnection object. See EmbeddedConnection.
 	 */
 	public void resetFromPool()
 		 throws StandardException
 	{
 		// Reset IDENTITY_VAL_LOCAL
 		identityNotNull = false;
 
 		// drop all temp tables.
 		dropAllDeclaredGlobalTempTables();
 	}
 
 	/**
 	 * Drop all the declared global temporary tables associated with this connection. This gets called
 	 * when a getConnection() is done on a PooledConnection. This will ensure all the temporary tables
 	 * declared on earlier connection handle associated with this physical database connection are dropped
 	 * before a new connection handle is issued on that same physical database connection.
 	 */
 	private void dropAllDeclaredGlobalTempTables() throws StandardException {
 		if (allDeclaredGlobalTempTables == null)
 			return;
     
 		DependencyManager dm = getDataDictionary().getDependencyManager();
 		StandardException topLevelStandardException = null;
 
 		//collect all the exceptions we might receive while dropping the temporary tables and throw them as one chained exception at the end.
 		for (int i = 0; i < allDeclaredGlobalTempTables.size(); i++) {
 			try {
 				TempTableInfo tempTableInfo = (TempTableInfo)allDeclaredGlobalTempTables.get(i);
 				TableDescriptor td = tempTableInfo.getTableDescriptor();
 				//the following 2 lines of code has been copied from DropTableConstantAction. If there are any changes made there in future,
 				//we should check if they need to be made here too.
 				dm.invalidateFor(td, DependencyManager.DROP_TABLE, this);
 				tran.dropConglomerate(td.getHeapConglomerateId());
 			} catch (StandardException e) {
 				if (topLevelStandardException == null) {
 					// always keep the first exception unchanged
 					topLevelStandardException = e;
 				} else {
 					try {
 						// Try to create a chain of exceptions. If successful,
 						// the current exception is the top-level exception,
 						// and the previous exception the cause of it.
 						e.initCause(topLevelStandardException);
 						topLevelStandardException = e;
 					} catch (IllegalStateException ise) {
 						// initCause() has already been called on e. We don't
 						// expect this to happen, but if it happens, just skip
 						// the current exception from the chain. This is safe
 						// since we always keep the first exception.
 					}
 				}
 			}
 		}
     
 		allDeclaredGlobalTempTables = null;
 		try {
 			internalCommit(true);
 		} catch (StandardException e) {
 			// do the same chaining as above
 			if (topLevelStandardException == null) {
 				topLevelStandardException = e;
 			} else {
 				try {
 					e.initCause(topLevelStandardException);
 					topLevelStandardException = e;
 				} catch (IllegalStateException ise) { /* ignore */ }
 			}
 		}
 		if (topLevelStandardException != null) throw topLevelStandardException;
 	}
 
 	//do the necessary work at rollback time for temporary tables
 	/**
 	 * do the necessary work at rollback time for temporary tables
 	 * 1)If a temp table was declared in the UOW, then drop it and remove it from list of temporary tables.
 	 * 2)If a temp table was declared and dropped in the UOW, then remove it from list of temporary tables.
 	 * 3)If an existing temp table was dropped in the UOW, then recreate it with no data.
 	 * 4)If an existing temp table was modified in the UOW, then get rid of all the rows from the table.
 	 */
 	private void tempTablesAndRollback()
 		throws StandardException {
 		for (int i = allDeclaredGlobalTempTables.size()-1; i >= 0; i--) {
 			TempTableInfo tempTableInfo = (TempTableInfo)allDeclaredGlobalTempTables.get(i);
 			if (tempTableInfo.getDeclaredInSavepointLevel() >= currentSavepointLevel)
 			{
 				if (tempTableInfo.getDroppedInSavepointLevel() == -1)
 				{
 					//the table was declared but not dropped in the unit of work getting rolled back and hence we will remove
 					//it from valid list of temporary tables and drop the conglomerate associated with it
 					TableDescriptor td = tempTableInfo.getTableDescriptor();
 					tran.dropConglomerate(td.getHeapConglomerateId()); //remove the conglomerate created for this temp table
 					allDeclaredGlobalTempTables.remove(i); //remove it from the list of temp tables
 				} else if (tempTableInfo.getDroppedInSavepointLevel() >= currentSavepointLevel)
 				{
 					//the table was declared and dropped in the unit of work getting rolled back
 					allDeclaredGlobalTempTables.remove(i);
 				}
 			} else if (tempTableInfo.getDroppedInSavepointLevel() >= currentSavepointLevel) //this means the table was declared in an earlier savepoint unit / transaction and then dropped in current UOW 
 			{
 				//restore the old definition of temp table because drop is being rolledback
 				TableDescriptor td = tempTableInfo.getTableDescriptor();
 				td = cleanupTempTableOnCommitOrRollback(td, false);
 				//In order to store the old conglomerate information for the temp table, we need to replace the
 				//existing table descriptor with the old table descriptor which has the old conglomerate information
 				tempTableInfo.setTableDescriptor(td);
 				tempTableInfo.setDroppedInSavepointLevel(-1);
 				//following will mark the table as not modified. This is because the table data has been deleted as part of the current rollback
 				tempTableInfo.setModifiedInSavepointLevel(-1);
 				allDeclaredGlobalTempTables.set(i, tempTableInfo);
 			} else if (tempTableInfo.getModifiedInSavepointLevel() >= currentSavepointLevel) //this means the table was declared in an earlier savepoint unit / transaction and modified in current UOW
 			{
 				//following will mark the table as not modified. This is because the table data will be deleted as part of the current rollback
 				tempTableInfo.setModifiedInSavepointLevel(-1);
 				TableDescriptor td = tempTableInfo.getTableDescriptor();
 				getDataDictionary().getDependencyManager().invalidateFor(td, DependencyManager.DROP_TABLE, this);
 				cleanupTempTableOnCommitOrRollback(td, true);
 			} // there is no else here because there is no special processing required for temp tables declares in earlier work of unit/transaction and not modified
 		}
     
 		if (allDeclaredGlobalTempTables.size() == 0)
 			allDeclaredGlobalTempTables = null;
 	}
 
 	/**
 	 * This is called at the commit time for temporary tables with ON COMMIT DELETE ROWS
 	 * If a temp table with ON COMMIT DELETE ROWS doesn't have any held cursor open on them, we delete the data from
 	 * them by dropping the conglomerate and recreating the conglomerate. In order to store the new conglomerate
 	 * information for the temp table, we need to replace the existing table descriptor with the new table descriptor
 	 * which has the new conglomerate information
 	 * @param tableName Temporary table name whose table descriptor is getting changed
 	 * @param td New table descriptor for the temporary table
 	 */
 	private void replaceDeclaredGlobalTempTable(String tableName, TableDescriptor td) {
     TempTableInfo tempTableInfo = findDeclaredGlobalTempTable(tableName);
 		tempTableInfo.setDroppedInSavepointLevel(-1);
 		tempTableInfo.setDeclaredInSavepointLevel(-1);
 		tempTableInfo.setTableDescriptor(td);
 		allDeclaredGlobalTempTables.set(allDeclaredGlobalTempTables.indexOf(tempTableInfo), tempTableInfo);
   }
 
 	/**
 	 * @see LanguageConnectionContext#getTableDescriptorForDeclaredGlobalTempTable
 	 */
 	public TableDescriptor getTableDescriptorForDeclaredGlobalTempTable(String tableName) {
     TempTableInfo tempTableInfo = findDeclaredGlobalTempTable(tableName);
 		if (tempTableInfo == null)
 			return null;
 		else
 			return tempTableInfo.getTableDescriptor();
 	}
 
 	/**
 	 * Find the declared global temporary table in the list of temporary tables known by this connection.
 	 * @param tableName look for this table name in the saved list
 	 * @return data structure defining the temporary table if found. Else, return null 
 	 *
 	 */
 	private TempTableInfo findDeclaredGlobalTempTable(String tableName) {
 		if (allDeclaredGlobalTempTables == null)
 			return null;
 
 		for (int i = 0; i < allDeclaredGlobalTempTables.size(); i++) {
 			if (((TempTableInfo)allDeclaredGlobalTempTables.get(i)).matches(tableName))
 				return (TempTableInfo)allDeclaredGlobalTempTables.get(i);
 		}
 		return null;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#markTempTableAsModifiedInUnitOfWork
 	 */
 	public void markTempTableAsModifiedInUnitOfWork(String tableName) {
     TempTableInfo tempTableInfo = findDeclaredGlobalTempTable(tableName);
     tempTableInfo.setModifiedInSavepointLevel(currentSavepointLevel);
 	}
 
         /**
 	 * @see LanguageConnectionContext#prepareInternalStatement
 	 */
         public PreparedStatement prepareInternalStatement(SchemaDescriptor compilationSchema, String sqlText, boolean isForReadOnly, boolean forMetaData) 
 	    throws StandardException 
         {
 	    return connFactory.getStatement(compilationSchema, sqlText, isForReadOnly).prepare(this, forMetaData);
     	}
 
         /**
     	 * @see LanguageConnectionContext#prepareInternalStatement
     	 */
         public PreparedStatement prepareInternalStatement(String sqlText) 
 	    throws StandardException 
         {
     	    return connFactory.getStatement(sd, sqlText, true).prepare(this);
     	}      
 
 	/**
 	 * Remove the activation to those known about by this connection.
 	 *
 	 */
 	public void removeActivation(Activation a) 
 	{
 		if (SanityManager.DEBUG) {
 			SanityManager.ASSERT(a.isClosed(), "Activation is not closed");
 		}
 
 		acts.remove(a);
 
 		if (maxActsSize > 20 && (maxActsSize > 2 * acts.size())) {
 			acts.trimToSize();
 			maxActsSize = acts.size();
 		}
 	}
 
 	/**
 	 * Return the number of activations known for this connection.
 	 * Note that some of these activations may not be in use
 	 * (when a prepared statement is finalized, its activations
 	 * are marked as unused and later closed and removed on
 	 * the next commit/rollback).
 	 */
 	public int getActivationCount() {
 		return acts.size();
 	}
 
 	/**
 	 * See if a given cursor is available for use.
 	 * if so return its activation. Returns null if not found.
 	 * For use in execution.
 	 *
 	 * @return the activation for the given cursor, null
 	 *	if none was found.
 	 */
 	public CursorActivation lookupCursorActivation(String cursorName) {
 
 		int size = acts.size();
 		if (size > 0)
 		{
 			for (int i = 0; i < size; i++) {
 				 Activation a = (Activation) acts.get(i);
 
 				 if (!a.isInUse())
 				 {
 					continue;
 				 }
 
 
 
 				String executingCursorName = a.getCursorName();
 
 				 if (cursorName.equals(executingCursorName)) {
 
 					ResultSet rs = a.getResultSet();
 					if (rs == null)
 						continue;
 
 					 // if the result set is closed, the the cursor doesn't exist
 					 if (rs.isClosed()) {					
 						continue;
 					 }
 
 				 	return (CursorActivation)a;
 				 }
 			}
 		}
 		return null;
 	}
 
 	/**
 	*  This method will remove a statement from the  statement cache.
 	*  It will be called,  for example, if there is an exception preparing
 	*  the statement.
 	*
 	*  @param statement Statement to remove
 	*  @exception StandardException thrown if lookup goes wrong.
 	*/	
 	public void removeStatement(Statement statement)
 		throws StandardException {
         
         CacheManager statementCache =
             getLanguageConnectionFactory().getStatementCache();
 
 		if (statementCache == null)
 			return;
  
 			Cacheable cachedItem = statementCache.findCached(statement);
 			if (cachedItem != null)
 				statementCache.remove(cachedItem);
 	}
 
 	/**
 	 * See if a given statement has already been compiled for this user, and
 	 * if so use its prepared statement. Returns null if not found.
 	 *
 	 * @exception StandardException thrown if lookup goes wrong.
 	 * @return the prepared statement for the given string, null
 	 *	if none was found.
 	 */
 	public PreparedStatement lookupStatement(GenericStatement statement)
 		throws StandardException {
 
         CacheManager statementCache =
             getLanguageConnectionFactory().getStatementCache();
             
 		if (statementCache == null)
 			return null;
 
 		// statement caching disable when in DDL mode
 		if (dataDictionaryInWriteMode()) {
 			return null;
 		}
 
 		Cacheable cachedItem = statementCache.find(statement);
 
 		CachedStatement cs = (CachedStatement) cachedItem;
 
 
 		GenericPreparedStatement ps = cs.getPreparedStatement();
 
 		synchronized (ps) {
 			if (ps.upToDate()) {
 				GeneratedClass ac = ps.getActivationClass();
 
 				// Check to see if the statement was prepared before some change
 				// in the class loading set. If this is the case then force it to be invalid
 				int currentClasses =
 						getLanguageConnectionFactory().getClassFactory().getClassLoaderVersion();
 
 				if (ac.getClassLoaderVersion() != currentClasses) {
 					ps.makeInvalid(DependencyManager.INTERNAL_RECOMPILE_REQUEST, this);
 				}
 
 				// note that the PreparedStatement is not kept in the cache. This is because
 				// having items kept in the cache that ultimately are held onto by
 				// user code is impossible to manage. E.g. an open ResultSet would hold onto
 				// a PreparedStatement (through its activation) and the user can allow
 				// this object to be garbage collected. Pushing a context stack is impossible
 				// in garbage collection as it may deadlock with the open connection and
 				// the context manager assumes a singel current thread per context stack
 			}
 		}
 
 		statementCache.release(cachedItem);
 		return ps;
 	}
 
 	/**
 		Get a connection unique system generated name for a cursor.
 	*/
 	public String getUniqueCursorName() 
 	{
 		return getNameString("SQLCUR", nextCursorId++);
 	}
 
 	/**
 		Get a connection unique system generated name for an unnamed savepoint.
 	*/
 	public String getUniqueSavepointName()
 	{
 		return getNameString("SAVEPT", nextSavepointId++);
 	}
 
 	/**
 		Get a connection unique system generated id for an unnamed savepoint.
 	*/
 	public int getUniqueSavepointID()
 	{
 		return nextSavepointId-1;
 	}
 
 	/**
 	 * Build a String for a statement name.
 	 *
 	 * @param prefix	The prefix for the statement name.
 	 * @param number	The number to append for uniqueness
 	 *
 	 * @return	A unique String for a statement name.
 	 */
 	private String getNameString(String prefix, long number)
 	{
 		if (sb != null)
 		{
 			sb.setLength(0);
 		}
 		else
 		{
 			sb = new StringBuffer();
 		}
 		sb.append(prefix).append(number);
 
 		return sb.toString();
 	}
 
 	/**
 	 * Do a commit as appropriate for an internally generated
 	 * commit (e.g. as needed by sync, or autocommit).
 	 *
 	 * @param	commitStore	true if we should commit the Store transaction
 	 *
 	 * @exception StandardException thrown on failure
 	 */
 	public void internalCommit(boolean commitStore) throws StandardException
 	{
 		doCommit(commitStore,
 								  true,
 								  NON_XA,
 								  false);
 	}
 
 	/**
 	 * Do a commmit as is appropriate for a user requested
 	 * commit (e.g. a java.sql.Connection.commit() or a language
 	 * 'COMMIT' statement.  Does some extra checking to make
 	 * sure that users aren't doing anything bad.
 	 *
 	 * @exception StandardException thrown on failure
 	 */
 	public void userCommit() throws StandardException
 	{
 		doCommit(true,
 								  true,
 								  NON_XA,
 								  true);
 	}
 
 
 	/**
 		Commit the language transaction by doing a commitNoSync()
 		on the store's TransactionController.
 
 		<p>
 		Do *NOT* tell the data dictionary that the transaction is
 		finished. The reason is that this would allow other transactions
 		to see comitted DDL that could be undone in the event of a
 		system crash.
 
 		@param	commitflag	the flags to pass to commitNoSync in the store's
 							TransactionController
 
 		@exception StandardException thrown on failure
 	 */
 	public final void internalCommitNoSync(int commitflag) throws StandardException
 	{
 		doCommit(true, false, commitflag, false);
 	}
 
 
 	/**
 		Same as userCommit except commit a distributed transaction.   
 		This commit always commit store and sync the commit.
 
 		@param onePhase if true, allow it to commit without first going thru a
 		prepared state.
 
 		@exception StandardException	thrown if something goes wrong
 	 */
 	public final void xaCommit(boolean onePhase) throws StandardException
 	{
 		// further overload internalCommit to make it understand 2 phase commit
 		doCommit(true /* commit store */,
 								  true /* sync */,
 								  onePhase ? XA_ONE_PHASE : XA_TWO_PHASE,
 								  true);
 	}
 
 
 	/**
 	 * This is where the work on internalCommit(), userCOmmit() and 
 	 * internalCommitNoSync() actually takes place.
 	 * <p>
 	 * When a commit happens, the language connection context
 	 * will close all open activations/cursors and commit the
 	 * Store transaction.
 	 * <p>
 	 * REVISIT: we talked about having a LanguageTransactionContext,
 	 * but since store transaction management is currently in flux
 	 * and our context might want to delegate to that context,
 	 * for now all commit/rollback actions are handled directly by
 	 * the language connection context.
 	 * REVISIT: this may need additional alterations when
 	 * RELEASE SAVEPOINT/ROLLBACK TO SAVEPOINT show up.
 	 * <P>
 	 * Since the access manager's own context takes care of its own
 	 * resources on commit, and the transaction stays open, there is
 	 * nothing that this context has to do with the transaction controller.
 	 * <p>
 	 * Also, tell the data dictionary that the transaction is finished,
 	 * if necessary (that is, if the data dictionary was put into
 	 * DDL mode in this transaction.
 	 *
 	 *
 	 * @param	commitStore	true if we should commit the Store transaction
 	 * @param	sync		true means do a synchronized commit,
 	 *						false means do an unsynchronized commit
 	 * @param	commitflag	if this is an unsynchronized commit, the flags to
 	 *						pass to commitNoSync in the store's
 	 *						TransactionController.  If this is a synchronized
 	 *						commit, this flag is overloaded for xacommit.
      * @param   requestedByUser    False iff the commit is for internal use and
 	 *                      we should ignore the check to prevent commits
 	 *                      in an atomic statement.
 	 *
 	 * @exception StandardException		Thrown on error
 	 */
 
 	protected void doCommit(boolean commitStore,
 											   boolean sync,
 											   int commitflag,
 											   boolean requestedByUser)
 		 throws StandardException
 	{
 		StatementContext statementContext = getStatementContext();
 		if (requestedByUser  &&
 			(statementContext != null) &&
 			statementContext.inUse() &&
 			statementContext.isAtomic())
 		{
 			throw StandardException.newException(SQLState.LANG_NO_COMMIT_IN_NESTED_CONNECTION);
 		}
 
 		// Log commit to error log, if appropriate
 		if (logStatementText)
 		{
 			if (istream == null)
 			{
 				istream = Monitor.getStream();
 			}
 			String xactId = tran.getTransactionIdString();
 			istream.printlnWithHeader(LanguageConnectionContext.xidStr + 
 									  xactId + 
 									  "), " +
 									  LanguageConnectionContext.lccStr +
 									  instanceNumber +
 									  "), " + LanguageConnectionContext.dbnameStr +
 										  dbname +
 										  "), " +
 										  LanguageConnectionContext.drdaStr +
 										  drdaID +
 									  "), Committing");
 		}
 
 		endTransactionActivationHandling(false);
 
 		//do the clean up work required for temporary tables at the commit time. This cleanup work
 		//can possibly remove entries from allDeclaredGlobalTempTables and that's why we need to check
 		//again later to see if we there are still any entries in allDeclaredGlobalTempTables
 		if (allDeclaredGlobalTempTables != null)
 		{
 			tempTablesAndCommit();
 			//at commit time, for all the temp tables declared with ON COMMIT DELETE ROWS, make sure there are no held cursor open on them.
 			//If there are no held cursors open on ON COMMIT DELETE ROWS, drop those temp tables and redeclare them to get rid of all the data in them
 			if (allDeclaredGlobalTempTables != null) {
 				for (int i=0; i<allDeclaredGlobalTempTables.size(); i++)
 				{
 					TableDescriptor td = ((TempTableInfo)(allDeclaredGlobalTempTables.get(i))).getTableDescriptor();
 					if (td.isOnCommitDeleteRows() == false) //do nothing for temp table with ON COMMIT PRESERVE ROWS
 					{
 						continue;
 					}
 					if (checkIfAnyActivationHasHoldCursor(td.getName()) == false)//temp tables with ON COMMIT DELETE ROWS and no open held cursors
 					{
 						getDataDictionary().getDependencyManager().invalidateFor(td, DependencyManager.DROP_TABLE, this);
 						cleanupTempTableOnCommitOrRollback(td, true);
 					}
 				}
 			}
 		}
 
 
 		currentSavepointLevel = 0; //reset the current savepoint level for the connection to 0 at the end of commit work for temp tables
 
 		// Do *NOT* tell the DataDictionary to start using its cache again
 		// if this is an unsynchronized commit. The reason is that it
 		// would allow other transactions to see this transaction's DDL,
 		// which could be rolled back in case of a system crash.
 		if (sync)
 		{
 			finishDDTransaction();
 		}
         
         // Check that any nested transaction has been destoyed
         // before a commit.
         if (SanityManager.DEBUG)
         {
             if (readOnlyNestedTransaction != null)
             {
                 SanityManager.THROWASSERT("Nested transaction active!");
             }
         }
 
 		// now commit the Store transaction
 		TransactionController tc = getTransactionExecute();
 		if ( tc != null && commitStore ) 
 		{ 
 			if (sync)
 			{
 				if (commitflag == NON_XA)
 				{
 					// regular commit
 					tc.commit();
 				}
 				else
 				{
 					// This may be a xa_commit, check overloaded commitflag.
 
 					if (SanityManager.DEBUG)
 						SanityManager.ASSERT(commitflag == XA_ONE_PHASE ||
 											 commitflag == XA_TWO_PHASE,
 											   "invalid commit flag");
 
 					((XATransactionController)tc).xa_commit(commitflag == XA_ONE_PHASE);
 
 				}
 			}
 			else
 			{
 				tc.commitNoSync(commitflag);
 			}
 
 			// reset the savepoints to the new
 			// location, since any outer nesting
 			// levels expect there to be a savepoint
 			resetSavepoints();
 		}
 	}
 
 	/**
 	 * If dropAndRedeclare is true, that means we have come here for temp 
      * tables with on commit delete rows and no held curosr open on them. We 
      * will drop the existing conglomerate and redeclare a new conglomerate
 	 * similar to old conglomerate. This is a more efficient way of deleting 
      * all rows from the table.
 	 *
 	 * If dropAndRedeclare is false, that means we have come here for the 
      * rollback cleanup work. We are trying to restore old definition of the 
      * temp table (because the drop on it is being rolled back).
 	 */
 	private TableDescriptor cleanupTempTableOnCommitOrRollback(
     TableDescriptor td, 
     boolean         dropAndRedeclare)
 		 throws StandardException
 	{
 		//create new conglomerate with same properties as the old conglomerate 
         //and same row template as the old conglomerate
 		long conglomId = 
             tran.createConglomerate(
                 "heap", // we're requesting a heap conglomerate
                 td.getEmptyExecRow().getRowArray(), // row template
                 null, //column sort order - not required for heap
                 td.getColumnCollationIds(),  // same ids as old conglomerate
                 null, // properties
                 (TransactionController.IS_TEMPORARY | 
                  TransactionController.IS_KEPT));
 
 		long cid = td.getHeapConglomerateId();
 
 		//remove the old conglomerate descriptor from the table descriptor
 		ConglomerateDescriptor cgd = td.getConglomerateDescriptor(cid);
 		td.getConglomerateDescriptorList().dropConglomerateDescriptorByUUID(cgd.getUUID());
 		//add the new conglomerate descriptor to the table descriptor
 		cgd = getDataDictionary().getDataDescriptorGenerator().newConglomerateDescriptor(conglomId, null, false, null, false, null, td.getUUID(),
 		td.getSchemaDescriptor().getUUID());
 		ConglomerateDescriptorList conglomList = td.getConglomerateDescriptorList();
 		conglomList.add(cgd);
 
 		//reset the heap conglomerate number in table descriptor to -1 so it will be refetched next time with the new value
 		td.resetHeapConglomNumber();
 
 		if(dropAndRedeclare)
 		{
 			tran.dropConglomerate(cid); //remove the old conglomerate from the system
 			replaceDeclaredGlobalTempTable(td.getName(), td);
 		}
 
 		return(td);
 	}
 
 	/**
 	  Do a rollback as appropriate for an internally generated
 	  rollback (e.g. as needed by sync, or autocommit).
 	 
 	  When a rollback happens, we 
 	  close all open activations and invalidate their
 	  prepared statements.  We then tell the cache to
 	  age out everything else, which effectively invalidates
 	  them.  Thus, all prepared statements will be
 	  compiled anew on their 1st execution after
 	  a rollback.
 	  <p>
 	  The invalidated statements can revalidate themselves without
 	  a full recompile if they verify their dependencies' providers still
 	  exist unchanged. REVISIT when invalidation types are created.
 	  <p>
 	  REVISIT: this may need additional alterations when
 	  RELEASE SAVEPOINT/ROLLBACK TO SAVEPOINT show up.
 	  <p>
 	  Also, tell the data dictionary that the transaction is finished,
 	  if necessary (that is, if the data dictionary was put into
 	  DDL mode in this transaction.
 
 	  @exception StandardException thrown on failure
 	 */
 
 	public void internalRollback() throws StandardException 
 	{
 		doRollback(false /* non-xa */, false);
 	}
 
 	/**
 	 * Do a rollback as is appropriate for a user requested
 	 * rollback (e.g. a java.sql.Connection.rollback() or a language
 	 * 'ROLLBACk' statement.  Does some extra checking to make
 	 * sure that users aren't doing anything bad.
 	 *
 	 * @exception StandardException thrown on failure
 	 */
 	public void userRollback() throws StandardException
 	{
 		doRollback(false /* non-xa */, true);
 	}
 
 	/**
 	    Same as userRollback() except rolls back a distrubuted transaction.
 
 		@exception StandardException	thrown if something goes wrong
 	 */
 	public void xaRollback() throws StandardException
 	{
 		doRollback(true /* xa */, true);
 	}
 
 	/**
 	 * When a rollback happens, the language connection context
 	 * will close all open activations and invalidate
 	 * their prepared statements. Then the language will abort the
 	 * Store transaction.
 	 * <p>
 	 * The invalidated statements can revalidate themselves without
 	 * a full recompile if they verify their dependencies' providers still
 	 * exist unchanged. REVISIT when invalidation types are created.
 	 * <p>
 	 * REVISIT: this may need additional alterations when
 	 * RELEASE SAVEPOINT/ROLLBACK TO SAVEPOINT show up.
 	 * <p>
 	 * Also, tell the data dictionary that the transaction is finished,
 	 * if necessary (that is, if the data dictionary was put into
 	 * DDL mode in this transaction.
  	 *
  	 * @param xa	true if this is an xa rollback
  	 * @param requestedByUser	true if requested by user
  	 *
 	 * @exception StandardException thrown on failure
 	 */
 	private void doRollback(boolean xa, boolean requestedByUser) throws StandardException
 	{
 		StatementContext statementContext = getStatementContext();
 		if (requestedByUser &&
 			(statementContext != null) &&
 			statementContext.inUse() &&
 			statementContext.isAtomic())
 		{
 			throw StandardException.newException(SQLState.LANG_NO_ROLLBACK_IN_NESTED_CONNECTION);
 		}
 
 		// Log rollback to error log, if appropriate
 		if (logStatementText)
 		{
 			if (istream == null)
 			{
 				istream = Monitor.getStream();
 			}
 			String xactId = tran.getTransactionIdString();
 			istream.printlnWithHeader(LanguageConnectionContext.xidStr +
 									  xactId + 
 									  "), " +
 									  LanguageConnectionContext.lccStr +
 									  instanceNumber +
 									  "), " + LanguageConnectionContext.dbnameStr +
 										  dbname +
 										  "), " +
 										  LanguageConnectionContext.dbnameStr +
 										  dbname +
 										  "), " +
 										  LanguageConnectionContext.drdaStr +
 										  drdaID +
 									  "), Rolling back");
 		}
 
 		endTransactionActivationHandling(true);
 
 		currentSavepointLevel = 0; //reset the current savepoint level for the connection to 0 at the beginning of rollback work for temp tables
 		if (allDeclaredGlobalTempTables != null)
 			tempTablesAndRollback();
 
 		finishDDTransaction();
         
         // If a nested transaction is active then
         // ensure it is destroyed before working
         // with the user transaction.
         if (readOnlyNestedTransaction != null)
         {
             readOnlyNestedTransaction.destroy();
             readOnlyNestedTransaction = null;
             queryNestingDepth = 0;
         }
 
 		// now rollback the Store transaction
 		TransactionController tc = getTransactionExecute();
 		if (tc != null) 
 		{	
 			if (xa)
 				((XATransactionController)tc).xa_rollback();
 			else
 				tc.abort(); 
             
 			// reset the savepoints to the new
 			// location, since any outer nesting
 			// levels expet there to be a savepoint
 			resetSavepoints();
 		}
 	}
 
 	/**
 	 * Reset all statement savepoints. Traverses the StatementContext
 	 * stack from bottom to top, calling resetSavePoint()
 	 * on each element.
 	 *
 	 * @exception StandardException thrown if something goes wrong
 	 */
 	private void resetSavepoints() throws StandardException 
 	{
 		final ContextManager cm = getContextManager();
 		final List stmts = cm.getContextStack(org.apache.derby.
 											  iapi.reference.
 											  ContextId.LANG_STATEMENT);
 		final int end = stmts.size();
 		for (int i = 0; i < end; ++i) {
 			((StatementContext)stmts.get(i)).resetSavePoint();
 		}
 	}
 
 	/**
 	 * Let the context deal with a rollback to savepoint
 	 *
 	 * @param	savepointName	Name of the savepoint that needs to be rolled back
 	 * @param	refreshStyle	boolean indicating whether or not the controller should close
 	 * open conglomerates and scans. Also used to determine if language should close
 	 * open activations.
 	 * @param	kindOfSavepoint	 A NULL value means it is an internal savepoint (ie not a user defined savepoint)
 	 * Non NULL value means it is a user defined savepoint which can be a SQL savepoint or a JDBC savepoint
 	 *   A String value for kindOfSavepoint would mean it is SQL savepoint
 	 *   A JDBC Savepoint object value for kindOfSavepoint would mean it is JDBC savepoint
 	 *
 	 * @exception StandardException thrown if something goes wrong
 	 */
 	public void internalRollbackToSavepoint( String savepointName, boolean refreshStyle, Object kindOfSavepoint )
 		throws StandardException
 	{
 		// now rollback the Store transaction to the savepoint
 		TransactionController tc = getTransactionExecute();
 		if (tc != null)
 		{
 			boolean		closeConglomerates;
 
 			if ( refreshStyle ) 
 			{
 				closeConglomerates = true;
 				// bug 5145 - don't forget to close the activations while rolling
 				// back to a savepoint
 				endTransactionActivationHandling(true);
 			}
 			else { closeConglomerates = false; }
 
 			currentSavepointLevel = tc.rollbackToSavePoint( savepointName, closeConglomerates, kindOfSavepoint );
 		}
 
 		if (tc != null && refreshStyle && allDeclaredGlobalTempTables != null)
 			tempTablesAndRollback();
 	}
 
 	/**
 	  Let the context deal with a release of a savepoint
 
 	  @param	savepointName	Name of the savepoint that needs to be released
 	  @param	kindOfSavepoint	 A NULL value means it is an internal savepoint (ie not a user defined savepoint)
 	  Non NULL value means it is a user defined savepoint which can be a SQL savepoint or a JDBC savepoint
 	  A String value for kindOfSavepoint would mean it is SQL savepoint
 	  A JDBC Savepoint object value for kindOfSavepoint would mean it is JDBC savepoint
 
 	  @exception StandardException thrown if something goes wrong
 	 */
 	public	void	releaseSavePoint( String savepointName, Object kindOfSavepoint )  throws StandardException
 	{
 		TransactionController tc = getTransactionExecute();
 		if (tc != null)
 		{
 			currentSavepointLevel = tc.releaseSavePoint( savepointName, kindOfSavepoint );
 			//after a release of a savepoint, we need to go through our temp tables list.
 			if (allDeclaredGlobalTempTables != null)
 				tempTablesReleaseSavepointLevels();
 		}
 	}
 
 	/**
 	  Sets a savepoint. Causes the Store to set a savepoint.
 
 	  @param	savepointName	name of savepoint
 	  @param	kindOfSavepoint	 A NULL value means it is an internal savepoint (ie not a user defined savepoint)
 	  Non NULL value means it is a user defined savepoint which can be a SQL savepoint or a JDBC savepoint
 	  A String value for kindOfSavepoint would mean it is SQL savepoint
 	  A JDBC Savepoint object value for kindOfSavepoint would mean it is JDBC savepoint
 
 		@exception StandardException thrown if something goes wrong
 	  */
 	public	void	languageSetSavePoint( String savepointName, Object kindOfSavepoint )  throws StandardException
 	{
 		TransactionController tc = getTransactionExecute();
 		if (tc != null)
 		{
 			currentSavepointLevel = tc.setSavePoint( savepointName, kindOfSavepoint );
 		}
 	}
 
 	/**
 	 * Start a Nested User Transaction (NUT) with the store. If a NUT is 
 	 * already active simply increment a counter, queryNestingDepth, to keep
 	 * track of how many times we have tried to start a NUT.
 	 */
 	public void beginNestedTransaction(boolean readOnly) throws StandardException
 	{
         // DERBY-2490 incremental rework, currently this is only called
         // with read-only true. Future changes will have this
         // method support read-write nested transactions as well
         // instead of callers using the startNestedUserTransaction
         // directly on tran.
 		if (readOnlyNestedTransaction == null)
 			readOnlyNestedTransaction = tran.startNestedUserTransaction(readOnly);
 		queryNestingDepth++;
 	}
 
 	public void commitNestedTransaction()
 		throws StandardException
 	{
 		if (--queryNestingDepth == 0)
 		{
 			readOnlyNestedTransaction.commit();
 			readOnlyNestedTransaction.destroy();
 			readOnlyNestedTransaction = null;
 		}
 	}
 
 	/**
 	 * Get the transaction controller to use at compile time with this language
 	 * connection context. If a NUT is active then return NUT else return parent
 	 * transaction.
 	 */
 	public TransactionController getTransactionCompile()
 	{
 		return (readOnlyNestedTransaction != null) ? readOnlyNestedTransaction : tran;
 	}
 
 	public TransactionController getTransactionExecute()
 	{
 		return tran;
 	}
 
  /** Get the data value factory to use with this language connection
 		context.
 	 */
 	public DataValueFactory getDataValueFactory() {
 		return dataFactory;
 	}
 	
 	/**
 		Get the language factory to use with this language connection
 		context.
 	 */
 	public LanguageFactory getLanguageFactory() {
 		return langFactory;
 	}
 		
 	public OptimizerFactory getOptimizerFactory() {
 		return of;
 	}
 
 	/**
 		Get the language connection factory to use with this language connection
 		context.
 	 */
 	public LanguageConnectionFactory getLanguageConnectionFactory() {
 		return connFactory;
 	}
 
 	/**
 	 * check if there are any activations that reference this temporary table
 	 * @param tableName look for any activations referencing this table name
 	 * @return boolean  false if found no activations
 	 */
 	private boolean checkIfAnyActivationHasHoldCursor(String tableName)
 			throws StandardException
 	{
 		for (int i = acts.size() - 1; i >= 0; i--) {
 			Activation a = (Activation) acts.get(i);
 			if (a.checkIfThisActivationHasHoldCursor(tableName))
 				return true;
     }
     return false;
 	}
 
 
 	/**
 	 * Verify that there are no activations with open held result sets.
 	 *
 	 * @return boolean  Found no open (held) resultsets.
 	 *
 	 * @exception StandardException thrown on failure
 	 */
 	/* This gets used in case of hold cursors. If there are any hold cursors open
 	 * then user can't change the isolation level without closing them. At the
 	 * execution time, set transaction isolation level calls this method before
 	 * changing the isolation level.
 	 */
 	public boolean verifyAllHeldResultSetsAreClosed()
 			throws StandardException
 	{
 		boolean seenOpenResultSets = false;
 
 		/* For every activation */
 		for (int i = acts.size() - 1; i >= 0; i--) {
 
 			Activation a = (Activation) acts.get(i);
 
 			if (SanityManager.DEBUG)
 			{
 				SanityManager.ASSERT(a instanceof CursorActivation, "a is not a CursorActivation");
 			}
 
 			if (!a.isInUse())
 			{
 				continue;
 			}
 
 			if (!a.getResultSetHoldability())
 			{
 				continue;
 			}
 
 			ResultSet rs = ((CursorActivation) a).getResultSet();
 
 			/* is there an open result set? */
 			if ((rs != null) && !rs.isClosed() && rs.returnsRows())
 			{
 				seenOpenResultSets = true;
 				break;
 			}
 		}
 
 		if (!seenOpenResultSets)
 			return(true);
 
 		// There may be open ResultSet's that are yet to be garbage collected
 		// let's try and force these out rather than throw an error
 		System.gc();
 		System.runFinalization();
 
 
 		/* For every activation */
 		for (int i = acts.size() - 1; i >= 0; i--) {
 				
 			Activation a = (Activation) acts.get(i);
 
 			if (SanityManager.DEBUG)
 			{
 				SanityManager.ASSERT(a instanceof CursorActivation, "a is not a CursorActivation");
 			}
 
 			if (!a.isInUse())
 			{
 				continue;
 			}
 
 			if (!a.getResultSetHoldability())
 			{
 				continue;
 			}
 
 			ResultSet rs = ((CursorActivation) a).getResultSet();
 
 			/* is there an open held result set? */
 			if ((rs != null) && !rs.isClosed() && rs.returnsRows())
 			{
 				return(false);
 			}
 		}
 		return(true);
 	}
 
 	/**
 	 * Verify that there are no activations with open result sets
 	 * on the specified prepared statement.
 	 *
 	 * @param pStmt		The prepared Statement
 	 * @param provider	The object precipitating a possible invalidation
 	 * @param action	The action causing the possible invalidation
 	 *
 	 * @return Nothing.
 	 *
 	 * @exception StandardException thrown on failure
 	 */
 	public boolean verifyNoOpenResultSets(PreparedStatement pStmt, Provider provider,
 									   int action)
 			throws StandardException
 	{
 		/*
 		** It is not a problem to create an index when there is an open
 		** result set, since it doesn't invalidate the access path that was
 		** chosen for the result set.
 		*/
 		boolean seenOpenResultSets = false;
 
 		/* For every activation */
 
 		// synchronize on acts as other threads may be closing activations
 		// in this list, thus invalidating the Enumeration
 		for (int i = acts.size() - 1; i >= 0; i--) {
 				
 			Activation a = (Activation) acts.get(i);
 
 			if (!a.isInUse())
 			{
 				continue;
 			}
 			
 			/* for this prepared statement */
 			if (pStmt == a.getPreparedStatement()) {
 				ResultSet rs = a.getResultSet();
 
 				/* is there an open result set? */
 				if (rs != null && ! rs.isClosed())
 				{
 					if (!rs.returnsRows())
 						continue;
 					seenOpenResultSets = true;
 					break;
 				}
 				
 			}
 		}
 
 		if (!seenOpenResultSets)
 			return false;
 
 		// There may be open ResultSet's that are yet to be garbage collected
 		// let's try and force these out rather than throw an error
 		System.gc();
 		System.runFinalization();
 
 
 		/* For every activation */
 		// synchronize on acts as other threads may be closing activations
 		// in this list, thus invalidating the Enumeration
 		for (int i = acts.size() - 1; i >= 0; i--) {
 				
 			Activation a = (Activation) acts.get(i);
 
 			if (!a.isInUse())
 			{
 				continue;
 			}
 
 			/* for this prepared statement */
 			if (pStmt == a.getPreparedStatement()) {
 				ResultSet rs = a.getResultSet();
 
 				/* is there an open result set? */
 				if (rs != null && ! rs.isClosed())
 				{
 					if ((provider != null) && rs.returnsRows()) {
 					DependencyManager dmgr = getDataDictionary().getDependencyManager();
 
 					throw StandardException.newException(SQLState.LANG_CANT_INVALIDATE_OPEN_RESULT_SET, 
 									dmgr.getActionString(action), 
 									provider.getObjectName());
 
 					}
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	/**
 	 *	Get the Authorization Id
 	 *
 	 * @return String	the authorization id
 	 */
 	public String getAuthorizationId()
 	{ 
 		return authorizer.getAuthorizationId();
 	}
 
 	/**
 	 *	Get the default schema
 	 *
 	 * @return SchemaDescriptor	the default schema
 	 */
 	public SchemaDescriptor getDefaultSchema() 
 	{ 
 		return sd; 
 	}
 	/**
 	 * Get the current schema name
 	 *
 	 * @return current schema name
 	 */
 	public String getCurrentSchemaName()
 	{
         if( null == sd)
             return null;
 		return sd.getSchemaName();
 	}
 
 	/**
 	 * Set the default schema -- used by SET SCHEMA.
 	 * 
 	 * @param sd the new default schema.
 	 * If null, then the default schema descriptor is used.
 	 *
 	 * @exception StandardException thrown on failure
 	 */
 	public void setDefaultSchema(SchemaDescriptor sd)
 		throws StandardException
 	{ 	
 		if (sd == null)
 		{	
 		    sd = initDefaultSchemaDescriptor();
 		}
 		this.sd = sd;
 		
 	}
 
 	/**
 	 * Get the identity column value most recently generated.
 	 *
 	 * @return the generated identity column value
 	 */
 	public Long getIdentityValue()
 	{
 		return identityNotNull ? new Long(identityVal) : null;
 	}
 
 	/**
 	 * Set the field of most recently generated identity column value.
 	 *
 	 * @param val the generated identity column value
 	 */
 	public void setIdentityValue(long val)
 	{
 		identityVal = val;
 		identityNotNull = true;
 	}
 
 	/**
 	 * Push a CompilerContext on the context stack with
 	 * the current default schema as the default schema
 	 * which we compile against.
 	 *
 	 * @return the compiler context
 	 *
 	 * @exception StandardException thrown on failure
 	 */
 	public	final CompilerContext pushCompilerContext()
 	{
 		return pushCompilerContext((SchemaDescriptor)null);
 	}
 
 	/**
 	 * Push a CompilerContext on the context stack with
 	 * the passed in schema sd as the default schema
 	 * we compile against.
 	 *
 	 * @param sd the default schema 
 	 *
 	 * @return the compiler context
 	 *
 	 * For the parameter sd, there are 3 possible values(of interest) that can 
 	 * get passed into this method:
 	 * 
 	 * a) A null SchemaDescriptor which indicates to the system to use the 
 	 *    CURRENT SCHEMA as the compilation schema.
 	 *    
 	 * b) A SchemaDescriptor with its UUID == null, this indicates that either 
 	 *    the schema has not been physically created yet or that the LCC's 
 	 *    getDefaultSchema() is not yet up-to-date with its actual UUID. 
 	 *    The system will use the CURRENT SCHEMA as the compilation schema. 
 	 *    
 	 * c) A SchemaDescriptor with its UUID != null, this means that the schema 
 	 *    has been physically created.  The system will use this schema as the 
 	 *    compilation schema (e.g.: for trigger or view recompilation cases, 
 	 *    etc.). 
 	 *    
 	 * The compiler context's compilation schema will be set accordingly based 
 	 * on the given input above.   
 	 */
 	public	CompilerContext pushCompilerContext(SchemaDescriptor sd)
 	{
 		CompilerContext cc;
 		boolean			firstCompilerContext = false;
 
 		//	DEBUG	END
 
 		cc = (CompilerContext) (getContextManager().getContext(CompilerContext.CONTEXT_ID));
 
 		/*
 		** If there is no compiler context, this is the first one on the
 		** stack, so don't pop it when we're done (saves time).
 		*/
 		if (cc == null) { firstCompilerContext = true; }
 
 		if (cc == null || cc.getInUse())
 		{
 			cc = new CompilerContextImpl(getContextManager(), this, tcf);
 			if (firstCompilerContext) { cc.firstOnStack(); }
 		}
 		else
 		{
 			/* Reset the next column,table, subquery and ResultSet numbers at 
 		 	* the beginning of each statement 
 		 	*/
 			cc.resetContext();
 		}
 
 		cc.setInUse(true);
 
 		// Save off the current isolation level on entry so that it gets restored
 		cc.setEntryIsolationLevel( getCurrentIsolationLevel());
 
 		StatementContext sc = getStatementContext();
 		if (sc.getSystemCode())
 			cc.setReliability(CompilerContext.INTERNAL_SQL_LEGAL);
 
 		/*
 		 * Set the compilation schema when its UUID is available.
 		 * i.e.:  Schema may not have been physically created yet, so
 		 *        its UUID will be null.
 		 * 
 		 * o For trigger SPS recompilation, the system must use its
 		 *   compilation schema to recompile the statement. 
 		 * 
 		 * o For view recompilation, we set the compilation schema
 		 *   for this compiler context if its UUID is available.
 		 *   Otherwise, the compilation schema will be determined
 		 *   at execution time of view creation.
 		 */
 		if (sd != null && sd.getUUID() != null)
 		{
 			cc.setCompilationSchema(sd);
 		}
 		
 		return	cc;
 	}
 
 
 	/**
 	 * Pop a CompilerContext off the context stack.
 	 *
 	 * @param cc  The compiler context.
 	 */
 	public void popCompilerContext(CompilerContext cc)
 	{
 		cc.setCurrentDependent(null);
 
 		cc.setInUse(false);
 
 		// Restore the isolation level at the time of entry to CompilerContext
 		isolationLevel = cc.getEntryIsolationLevel();
 
 		/*
 		** Only pop the compiler context if it's not the first one
 		** on the stack.
 		*/
 		if (! cc.isFirstOnStack()) 
 		{ 
 			cc.popMe(); 
 		}
 		else
 		{
 			cc.setCompilationSchema((SchemaDescriptor)null);
 		}
 	}
 
 	/**
 	 * Push a StatementContext on the context stack.
 	 *
 	 * @param isAtomic whether this statement is atomic or not
 	 * @param isForReadOnly whether this statement is for a read only resultset
 	 * @param stmtText the text of the statement.  Needed for any language
 	 * 	statement (currently, for any statement that can cause a trigger
 	 * 	to fire).  Please set this unless you are some funky jdbc setXXX
 	 *	method or something.
 	 * @param pvs parameter value set, if it has one
 	 * @param rollbackParentContext True if 1) the statement context is
 	 * 	NOT a top-level context, AND 2) in the event of a statement-level
 	 *	exception, the parent context needs to be rolled back, too.
      * @param timeoutMillis timeout value for this statement, in milliseconds.
      *  The value 0 means that no timeout is set.
 	 *
 	 * @return StatementContext  The statement context.
 	 *
 	 */
 	public StatementContext pushStatementContext (boolean isAtomic, boolean isForReadOnly, 
 						      String stmtText, ParameterValueSet pvs, 
 						      boolean rollbackParentContext, 
 						      long timeoutMillis)
 	{
 		int					parentStatementDepth = statementDepth;
 		boolean				inTrigger = false;
 		boolean				parentIsAtomic = false;
 
 		// by default, assume we are going to use the outermost statement context
 		StatementContext	statementContext = statementContexts[0];
 
 		/*
 		** If we haven't allocated any statement contexts yet, allocate
 		** the outermost stmt context now and push it.
 		*/
 		if (statementContext == null)
 		{
 			statementContext = statementContexts[0] = new GenericStatementContext(this);
 		}
 		else if (statementDepth > 0)
 		{
 			StatementContext	parentStatementContext;
 			/*
 			** We also cache a 2nd statement context, though we still
 			** push and pop it. Note, new contexts are automatically pushed.
 			*/
 			if (statementDepth == 1)
 			{
 				statementContext = statementContexts[1];
 
 				if (statementContext == null)
 					statementContext = statementContexts[1] = new GenericStatementContext(this);
 				else
 					statementContext.pushMe();
 
 				parentStatementContext = statementContexts[0];
 			}
 			else
 			{
 				parentStatementContext = getStatementContext();
 				statementContext = new GenericStatementContext(this);
 			}
 
 			inTrigger = parentStatementContext.inTrigger() || (outermostTrigger == parentStatementDepth);
 			parentIsAtomic = parentStatementContext.isAtomic();
 			statementContext.setSQLAllowed(parentStatementContext.getSQLAllowed(), false);
 			if (parentStatementContext.getSystemCode())
 				statementContext.setSystemCode();
 		}
 
 		incrementStatementDepth();
 
 		statementContext.setInUse(inTrigger, isAtomic || parentIsAtomic, isForReadOnly, stmtText, pvs, timeoutMillis);
 		if (rollbackParentContext)
 			statementContext.setParentRollback();
 		return statementContext;
 	}
 
 	/**
 	 * Pop a StatementContext of the context stack.
 	 *
 	 * @param statementContext  The statement context.
 	 * @param error				The error, if any  (Only relevant for DEBUG)
 	 */
 	public void popStatementContext(StatementContext statementContext,
 									Throwable error) 
 	{
 		if ( statementContext != null ) 
 		{ 
 			/*
 			** If someone beat us to the punch, then it is ok,
 			** just silently ignore it.  We probably got here
 			** because we had a try catch block around a push/pop
 			** statement context, and we already got the context
 			** on a cleanupOnError.
 			*/
 			if (!statementContext.inUse())
 			{
 				return;
 			}
 			statementContext.clearInUse(); 
 		}
 
 		decrementStatementDepth();
 		if (statementDepth == -1)
 		{
 			/*
 			 * Only ignore the pop request for an already
 			 * empty stack when dealing with a session exception.
 			 */
 			if (SanityManager.DEBUG)
 			{
 				int severity = (error instanceof StandardException) ?
 									((StandardException)error).getSeverity() :
 								0;
 				SanityManager.ASSERT(error != null,
 					"Must have error to try popStatementContext with 0 depth");
 				SanityManager.ASSERT(
 					(severity == ExceptionSeverity.SESSION_SEVERITY),
 					"Must have session severity error to try popStatementContext with 0 depth");
 				SanityManager.ASSERT(statementContext == statementContexts[0],
 					"statementContext is expected to equal statementContexts[0]");
 			}
 			resetStatementDepth(); // pretend we did nothing.
 		}
 		else if (statementDepth == 0)
 		{
 			if (SanityManager.DEBUG)
 			{
 				/* Okay to pop last context on a session exception.
 				 * (We call clean up on error when exiting connection.)
 				 */
 				int severity = (error instanceof StandardException) ?
 									((StandardException)error).getSeverity() :
 								0;
 				if ((error == null) || 
 					(severity != ExceptionSeverity.SESSION_SEVERITY))
 				{
 					SanityManager.ASSERT(statementContext == statementContexts[0],
 						"statementContext is expected to equal statementContexts[0]");
 				}
 			}
 		}
 		else
 		{
 			if (SanityManager.DEBUG)
 			{
 				SanityManager.ASSERT(statementContext != statementContexts[0],
 					"statementContext is not expected to equal statementContexts[0]");
 				if (statementDepth <= 0)
 					SanityManager.THROWASSERT(
 						"statement depth expected to be >0, was "+statementDepth);
                 
                 if (getContextManager().getContext(statementContext.getIdName()) != statementContext)
                 {
                     SanityManager.THROWASSERT("trying to pop statement context from middle of stack");
                 }
 			}
 
             statementContext.popMe();		
 		}
 
 	}
 
 	/**
 	 * Push a new execution statement validator.  An execution statement 
 	 * validator is an object that validates the current statement to
 	 * ensure that it is permitted given the current execution context.
 	 * An example of a validator a trigger ExecutionStmtValidator that
 	 * doesn't allow ddl on the trigger target table.
 	 * <p>
 	 * Multiple ExecutionStmtValidators may be active at any given time.
 	 * This mirrors the way there can be multiple connection nestings
 	 * at a single time.  The validation is performed by calling each
 	 * validator's validateStatement() method.  This yields the union
 	 * of all validations.
 	 *
 	 * @param validator the validator to add
 	 */
 	public void pushExecutionStmtValidator(ExecutionStmtValidator validator)
 	{
 		stmtValidators.add(validator);
 	}
 
 	/**
 	 * Remove the validator.  Does an object identity (validator == validator)
  	 * comparison.  Asserts that the validator is found.
 	 *
 	 * @param validator the validator to remove
 	 *
 	 * @exception StandardException on error
 	 */
 	public void popExecutionStmtValidator(ExecutionStmtValidator validator)
 		throws StandardException
 	{
 		boolean foundElement = stmtValidators.remove(validator);
 		if (SanityManager.DEBUG)
 		{
 			if (!foundElement)
 			{
 				SanityManager.THROWASSERT("statement validator "+validator+" not found");
 			}
 		}
 	}
 
 	/**
 	 * Push a new trigger execution context.
 	 * <p>
 	 * Multiple TriggerExecutionContexts may be active at any given time.
 	 *
 	 * @param tec the trigger execution context
 	 *
 	 * @exception StandardException on trigger recursion error
 	 */
 	public void pushTriggerExecutionContext(TriggerExecutionContext tec) throws StandardException
 	{
 		if (outermostTrigger == -1) 
 		{
 			outermostTrigger = statementDepth; 
 		}
 
 		/* Maximum 16 nesting levels allowed */
 		if (triggerExecutionContexts.size() >= Limits.DB2_MAX_TRIGGER_RECURSION)
 		{
 			throw StandardException.newException(SQLState.LANG_TRIGGER_RECURSION_EXCEEDED);
 		}
 
 		triggerExecutionContexts.add(tec);
 	}
 
 	/**
 	 * Remove the tec.  Does an object identity (tec == tec)
  	 * comparison.  Asserts that the tec is found.
 	 *
 	 * @param tec the tec to remove
 	 *
 	 * @exception StandardException on error
 	 */
 	public void popTriggerExecutionContext(TriggerExecutionContext tec)
 		throws StandardException
 	{
 		if (outermostTrigger == statementDepth)
 		{
 			outermostTrigger = -1; 
 		}
 
 		boolean foundElement = triggerExecutionContexts.remove(tec);
 		if (SanityManager.DEBUG)
 		{
 			if (!foundElement)
 			{
 				SanityManager.THROWASSERT("trigger execution context "+tec+" not found");
 			}
 		}
 	}
 
 	/**
 	 * Get the topmost tec.  
 	 *
 	 * @return the tec
 	 */
 	public TriggerExecutionContext getTriggerExecutionContext()
 	{
 		return triggerExecutionContexts.size() == 0 ? 
 				(TriggerExecutionContext)null :
 				(TriggerExecutionContext)triggerExecutionContexts.get(
 					triggerExecutionContexts.size() - 1);	
 	}
 
 	/**
 	 * Validate a statement.  Does so by stepping through all the validators
 	 * and executing them.  If a validator throws and exception, then the
 	 * checking is stopped and the exception is passed up.
 	 *
 	 * @param constantAction the constantAction that is about to be executed (and
 	 *	should be validated
  	 *
 	 * @exception StandardException on validation failure
 	 */
 	public void validateStmtExecution(ConstantAction constantAction)
 		throws StandardException
 	{
 		if (SanityManager.DEBUG)
 		{
 			SanityManager.ASSERT(constantAction!=null, "constantAction is null");
 		}
 
 		if (stmtValidators.size() > 0)
 		{
 			for (Iterator it = stmtValidators.iterator(); it.hasNext(); )
 			{
 				((ExecutionStmtValidator)it.next())
 					.validateStatement(constantAction);
 			}
 		}
 	}
 	
 	/**
 	 * Set the trigger table descriptor.  Used to compile
 	 * statements that may special trigger pseudo tables.
 	 *
 	 * @param td the table that the trigger is 
 	 * defined upon
 	 *
 	 */
 	public void pushTriggerTable(TableDescriptor td)
 	{
 		triggerTables.add(td);
 	}
 
 	/**
 	 * Remove the trigger table descriptor.
 	 *
 	 * @param td the table to remove from the stack.
 	 */
 	public void popTriggerTable(TableDescriptor td)
 	{
 		boolean foundElement = triggerTables.remove(td);
 		if (SanityManager.DEBUG)
 		{
 			if (!foundElement)
 			{
 				SanityManager.THROWASSERT("trigger table not found: "+td);
 			}
 		}
 	}
 
 	/**
 	 * Get the topmost trigger table descriptor
 	 *
 	 * @return the table descriptor, or null if we
 	 * aren't in the middle of compiling a create
 	 * trigger.
 	 */
 	public TableDescriptor getTriggerTable()
 	{
 		return triggerTables.size() == 0 ? 
 			(TableDescriptor)null :
 			(TableDescriptor)triggerTables.get(triggerTables.size() - 1);
 	}
 	/**
 	 * @see LanguageConnectionContext#getDatabase
 	 */
 	public Database
 	getDatabase()
 	{
 		return db;
 	}
 
 	/** @see LanguageConnectionContext#incrementBindCount */
 	public int incrementBindCount()
 	{
 		bindCount++;
 		return bindCount;
 	}
 
 	
 	/** @see LanguageConnectionContext#decrementBindCount */
 	public int decrementBindCount()
 	{
 		bindCount--;
 
 		if (SanityManager.DEBUG)
 		{
 			if (bindCount < 0)
 				SanityManager.THROWASSERT(
 					"Level of nested binding == " + bindCount);
 		}
 
 		return bindCount;
 	}
 
 	/** @see LanguageConnectionContext#getBindCount */
 	public int getBindCount()
 	{
 		return bindCount;
 	}
 
 	/** @see LanguageConnectionContext#setDataDictionaryWriteMode */
 	public final void setDataDictionaryWriteMode()
 	{
 		ddWriteMode = true;
 	}
 
 	/** @see LanguageConnectionContext#dataDictionaryInWriteMode */
 	public final boolean dataDictionaryInWriteMode()
 	{
 		return ddWriteMode;
 	}
 
 	/** @see LanguageConnectionContext#setRunTimeStatisticsMode */
 	public void setRunTimeStatisticsMode(boolean onOrOff)
 	{
 		runTimeStatisticsSetting = onOrOff;
 	}
 
 	/** @see LanguageConnectionContext#getRunTimeStatisticsMode */
 	public boolean getRunTimeStatisticsMode()
 	{
 		return runTimeStatisticsSetting;
 	}
 
 	/** @see LanguageConnectionContext#setStatisticsTiming */
 	public void setStatisticsTiming(boolean onOrOff)
 	{
 		statisticsTiming = onOrOff;
 	}
 
 	/** @see LanguageConnectionContext#getStatisticsTiming */
 	public boolean getStatisticsTiming()
 	{
 		return statisticsTiming;
 	}
 
 	/** @see LanguageConnectionContext#setRunTimeStatisticsObject */
 	public void setRunTimeStatisticsObject(RunTimeStatistics runTimeStatisticsObject)
 	{
 		this.runTimeStatisticsObject = runTimeStatisticsObject;
 	}
 
 	/** @see LanguageConnectionContext#getRunTimeStatisticsObject */
 	public RunTimeStatistics getRunTimeStatisticsObject()
 	{
 		return runTimeStatisticsObject;
 	}
 
 
     /**
 	  *	Reports how many statement levels deep we are.
 	  *
 	  *	@return	a statement level >= OUTERMOST_STATEMENT
 	  */
 	public	int		getStatementDepth()
 	{ return statementDepth; }
 
 	/**
 	 * @see LanguageConnectionContext#isIsolationLevelSetUsingSQLorJDBC
 	 */
 	public boolean isIsolationLevelSetUsingSQLorJDBC()
 	{
 		return isolationLevelSetUsingSQLorJDBC;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#resetIsolationLevelFlagUsedForSQLandJDBC
 	 */
 	public void resetIsolationLevelFlagUsedForSQLandJDBC()
 	{
 		isolationLevelSetUsingSQLorJDBC = false;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#setIsolationLevel
 	 */
 	public void setIsolationLevel(int isolationLevel) throws StandardException
 	{
 		StatementContext stmtCtxt = getStatementContext();
 		if (stmtCtxt!= null && stmtCtxt.inTrigger())
 			throw StandardException.newException(SQLState.LANG_NO_XACT_IN_TRIGGER, getTriggerExecutionContext().toString());
 
 		// find if there are any held cursors from previous isolation level.
 		// if yes, then throw an exception that isolation change not allowed until
 		// the held cursors are closed.
 		// I had to move this check outside of transaction idle check because if a
 		// transactions creates held cursors and commits the transaction, then
 		// there still would be held cursors but the transaction state would be idle.
 		// In order to check the above mentioned case, the held cursor check
 		// shouldn't rely on transaction state.
 		if (this.isolationLevel != isolationLevel)
 		{
 			if (!verifyAllHeldResultSetsAreClosed())
 			{
 				throw StandardException.newException(SQLState.LANG_CANT_CHANGE_ISOLATION_HOLD_CURSOR);
 			}
 		}
 
 		/* Commit and set to new isolation level.
 		 * NOTE: We commit first in case there's some kind
 		 * of error, like can't commit within a server side jdbc call.
 		 */
 		TransactionController tc = getTransactionExecute();
 		if (!tc.isIdle())
 		{
 			// If this transaction is in progress, commit it.
 			// However, do not allow commit to happen if this is a global
 			// transaction.
 			if (tc.isGlobal())
 				throw StandardException.newException(SQLState.LANG_NO_SET_TRAN_ISO_IN_GLOBAL_CONNECTION);
 
 			userCommit();
 		}
 		this.isolationLevel = isolationLevel;
 		this.isolationLevelExplicitlySet = true;
 		this.isolationLevelSetUsingSQLorJDBC = true;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#getCurrentIsolationLevel
 	 */
 	public int getCurrentIsolationLevel()
 	{
 		return (isolationLevel == ExecutionContext.UNSPECIFIED_ISOLATION_LEVEL) ? defaultIsolationLevel : isolationLevel;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#getCurrentIsolationLevel
 	 */
 	public String getCurrentIsolationLevelStr()
 	{
         if( isolationLevel >= 0 && isolationLevel < ExecutionContext.CS_TO_SQL_ISOLATION_MAP.length)
             return ExecutionContext.CS_TO_SQL_ISOLATION_MAP[ isolationLevel][0];
         return ExecutionContext.CS_TO_SQL_ISOLATION_MAP[ ExecutionContext.UNSPECIFIED_ISOLATION_LEVEL][0];
 	}
 
 	/**
 	 * @see LanguageConnectionContext#setPrepareIsolationLevel
 	 */
 	public void setPrepareIsolationLevel(int level) 
 	{
 			prepareIsolationLevel = level;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#getPrepareIsolationLevel
 	 */
 	public int getPrepareIsolationLevel()
 	{
 		if (!isolationLevelExplicitlySet)
 			return prepareIsolationLevel;
 		else
 			return ExecutionContext.UNSPECIFIED_ISOLATION_LEVEL;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#getExecutionContext
 	 */
 	public ExecutionContext getExecutionContext()
 	{
 		return (ExecutionContext) getContextManager().getContext(ExecutionContext.CONTEXT_ID);
 	}
 
 	/**
 	 * @see LanguageConnectionContext#getStatementContext
 	 */
 	public StatementContext getStatementContext()
 	{	
 		return (StatementContext) getContextManager().getContext(org.apache.derby.iapi.reference.ContextId.LANG_STATEMENT);
 	}
 
 	/**
 	 * @see LanguageConnectionContext#setOptimizerTrace
 	 */
 	public boolean setOptimizerTrace(boolean onOrOff)
 	{
 		if (of == null)
 		{
 			return false;
 		}
 		if (! of.supportsOptimizerTrace())
 		{
 			return false;
 		}
 		optimizerTrace = onOrOff;
 		return true;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#getOptimizerTrace
 	 */
 	public boolean getOptimizerTrace()
 	{
 		return optimizerTrace;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#setOptimizerTraceHtml
 	 */
 	public boolean setOptimizerTraceHtml(boolean onOrOff)
 	{
 		if (of == null)
 		{
 			return false;
 		}
 		if (! of.supportsOptimizerTrace())
 		{
 			return false;
 		}
 		optimizerTraceHtml = onOrOff;
 		return true;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#getOptimizerTraceHtml
 	 */
 	public boolean getOptimizerTraceHtml()
 	{
 		return optimizerTraceHtml;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#setOptimizerTraceOutput
 	 */
 	public void setOptimizerTraceOutput(String startingText)
 	{
 		if (optimizerTrace)
 		{
 			lastOptimizerTraceOutput = optimizerTraceOutput;
 			optimizerTraceOutput = startingText;
 		}
 	}
 
 	/**
 	 * @see LanguageConnectionContext#appendOptimizerTraceOutput
 	 */
 	public void appendOptimizerTraceOutput(String output)
 	{
 		optimizerTraceOutput = 
 			(optimizerTraceOutput == null) ? output : optimizerTraceOutput + output;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#getOptimizerTraceOutput
 	 */
 	public String getOptimizerTraceOutput()
 	{
 		return lastOptimizerTraceOutput;
 	}
 
     /**
 	  *	Reports whether there is any outstanding work in the transaction.
 	  *
 	  *	@return	true if there is outstanding work in the transaction
 	  *				false otherwise
 	  */
 	public	boolean	isTransactionPristine()
 	{
 		return getTransactionExecute().isPristine();
 	}
 
 
 	//
 	// Context interface
 	//
 	/**
 		If worse than a transaction error, everything goes; we
 		rely on other contexts to kill the context manager
 		for this session.
 		<p>
 		If a transaction error, act like we saw a rollback.
 		<p>
 		If more severe or a java error, the outer cleanup
 		will shutdown the connection, so we don't have to clean up.
 		<p>
 		REMIND: connection should throw out all contexts and start
 		over when the connection is closed... perhaps by throwing
 		out the context manager?
 		<p>
 		REVISIT: If statement error, should we do anything?
 		<P>
 		Since the access manager's own context takes care of its own
 		resources on errors, there is nothing that this context has
 		to do with the transaction controller.
 
 		@exception StandardException thrown on error. REVISIT: don't want
 		cleanupOnError's to throw exceptions.
 	 */
 	public void cleanupOnError(Throwable error) throws StandardException {
 
 		/*
 		** If it isn't a StandardException, then assume
 		** session severity. It is probably an unexpected
 		** java error somewhere in the language.
         ** Store layer treats JVM error as session severity, 
         ** hence to be consistent and to avoid getting rawstore
         ** protocol violation errors, we treat java errors here
         ** to be of session severity.
         */  
 
 		int severity = (error instanceof StandardException) ?
 			((StandardException) error).getSeverity() :
 			ExceptionSeverity.SESSION_SEVERITY;
  
 		if (statementContexts[0] != null)
 		{
 			statementContexts[0].clearInUse();
             
             // Force the StatementContext that's normally
             // left on the stack for optimization to be popped
             // when the session is closed. Ensures full cleanup
             // and no hanging refrences in the ContextManager.
             if (severity >= ExceptionSeverity.SESSION_SEVERITY)
                 statementContexts[0].popMe();
 		}
 		if (statementContexts[1] != null)
 		{
 			statementContexts[1].clearInUse();                
 		}
 
 		// closing the activations closes all the open cursors.
 		// the activations are, for all intents and purposes, the
 		// cursors.
 		if (severity >= ExceptionSeverity.SESSION_SEVERITY) 
 		{
 			for (int i = acts.size() - 1; i >= 0; i--) {
 				// it maybe the case that a reset()/close() ends up closing
 				// one or more activation leaving our index beyond
 				// the end of the array
 				if (i >= acts.size())
 					continue;
 				Activation a = (Activation) acts.get(i);
 				a.reset();
 				a.close();
 			}
                        
 			popMe();
 		}
 
 		/*
 		** We have some global state that we need
 		** to clean up no matter what.  Be sure
 		** to do so.
 		*/
 		else if (severity >= ExceptionSeverity.TRANSACTION_SEVERITY) 
 		{
 			internalRollback();
 		}
 	}
 
 	/**
 	 * @see org.apache.derby.iapi.services.context.Context#isLastHandler
 	 */
 	public boolean isLastHandler(int severity)
 	{
 		return false;
 	}
 
 	//
 	// class implementation
 	//
 
 	/**
 		If we are called as part of rollback code path, then we will reset all 
		the activations. 
 		
 		If we are called as part of commit code path, then we will do one of 
 		the following if the activation has resultset assoicated with it. Also,
 		we will clear the conglomerate used while scanning for update/delete
 		1)Close result sets that return rows and are not held across commit.
 		2)Clear the current row of the resultsets that return rows and are
 		held across commit.
 		3)Leave the result sets untouched if they do not return rows
 		
 		Additionally, clean up (close()) activations that have been
 		marked as unused during statement finalization.
 
 		@exception StandardException thrown on failure
 	 */
 	private void endTransactionActivationHandling(boolean forRollback) throws StandardException {
 
 		// don't use an enumeration as the activation may remove
 		// itself from the list, thus invalidating the Enumeration
 		for (int i = acts.size() - 1; i >= 0; i--) {
 
 			// it maybe the case that a reset() ends up closing
 			// one or more activation leaving our index beyond
 			// the end of the array
 			if (i >= acts.size())
 				continue;
 
 			Activation a = (Activation) acts.get(i);
 			/*
 			** Look for stale activations.  Activations are
 			** marked as unused during statement finalization.
 			** Here, we sweep and remove this inactive ones.
 			*/	
 			if (!a.isInUse())
 			{
 				a.close();
 				continue;
 			}
 
 			if (forRollback) { 
				//Since we are dealing with rollback, we need to reset the 
				//activation no matter what the holdability might be or no
				//matter whether the associated resultset returns rows or not.
 				a.reset();
 				// Only invalidate statements if we performed DDL.
 				if (dataDictionaryInWriteMode()) {
 					ExecPreparedStatement ps = a.getPreparedStatement();
 					if (ps != null) {
 						ps.makeInvalid(DependencyManager.ROLLBACK, this);
 					}
 				}
 			} else {
 				//We are dealing with commit here. 
				if (a.getResultSet() != null) {
					ResultSet activationResultSet = a.getResultSet();
					boolean resultsetReturnsRows = activationResultSet.returnsRows();
					//if the activation has resultset associated with it, then 
					//use following criteria to take the action
 					if (resultsetReturnsRows){
 						if (a.getResultSetHoldability() == false)
 							//Close result sets that return rows and are not held 
 							//across commit. This is to implement closing JDBC 
 							//result sets that are CLOSE_CURSOR_ON_COMMIT at commit 
 							//time. 
 							activationResultSet.close();
 						else 
 							//Clear the current row of the result sets that return
 							//rows and are held across commit. This is to implement
 							//keeping JDBC result sets open that are 
 							//HOLD_CURSORS_OVER_COMMIT at commit time and marking
 							//the resultset to be not on a valid row position. The 
 							//user will need to reposition within the resultset 
 							//before doing any row operations.
 							activationResultSet.clearCurrentRow();							
 					}
				}
 				a.clearHeapConglomerateController();
 			}
 		}
 	}
 
 	/**
 		Finish the data dictionary transaction, if any.
 
 		@exception StandardException	Thrown on error
 	 */
 	private void finishDDTransaction() throws StandardException {
 
 		/* Was the data dictionary put into write mode? */
 		if (ddWriteMode) {
 			DataDictionary dd = getDataDictionary();
 
 			/* Tell the data dictionary that the transaction is finished */
 			dd.transactionFinished();
 
 			/* The data dictionary isn't in write mode any more */
 			ddWriteMode = false;
 		}
 	}
 
 	////////////////////////////////////////////////////////////////////
 	//
 	//	MINIONS
 	//
 	////////////////////////////////////////////////////////////////////
 
 	/**
 	  *	Increments the statement depth.
 	  */
     private	void	incrementStatementDepth() { statementDepth++; }
 
     /**
 	  * Decrements the statement depth
 	  */
 	private	void	decrementStatementDepth()
 	{
 		statementDepth--;
 	}
 
 	/**
 	  *	Resets the statementDepth.
 	  */
 	protected	void	resetStatementDepth()
 	{
 		statementDepth = 0;
 	}
 
 	public DataDictionary getDataDictionary()
 	{
         return getDatabase().getDataDictionary();
 	}
 
 	/**
 	  @see LanguageConnectionContext#setReadOnly
 	  @exception StandardException The operation is disallowed.
 	  */
 	public void setReadOnly(boolean on) throws StandardException
 	{
 		if (!tran.isPristine())
 			throw StandardException.newException(SQLState.AUTH_SET_CONNECTION_READ_ONLY_IN_ACTIVE_XACT);
 		authorizer.setReadOnlyConnection(on,true);
 	}
 
 	/**
 	  @see LanguageConnectionContext#isReadOnly
 	  */
 	public boolean isReadOnly()
 	{
 		return authorizer.isReadOnlyConnection();
 	}
 
 	/**
 	  @see LanguageConnectionContext#getAuthorizer
 	 */
 	public Authorizer getAuthorizer()
 	{
 		return authorizer;
 	}
 
 	/**
 	 * Implements ConnectionInfo.lastAutoincrementValue.
 	 * lastAutoincrementValue searches for the last autoincrement value inserted
 	 * into a column specified by the user. The search for the "last" value
 	 * supports nesting levels caused by triggers (Only triggers cause nesting,
 	 * not server side JDBC). 
 	 * If lastAutoincrementValue is called from within a trigger, the search
 	 * space for ai-values are those values that are inserted by this trigger as
 	 * well as previous triggers; 
 	 * i.e if a SQL statement fires trigger T1, which in turn does something
 	 * that fires trigger t2, and if lastAutoincrementValue is called from
 	 * within t2, then autoincrement values genereated by t1 are visible to
 	 * it. By the same logic, if it is called from within t1, then it does not
 	 * see values inserted by t2.
 	 *
 	 * @see LanguageConnectionContext#lastAutoincrementValue
 	 * @see org.apache.derby.iapi.db.ConnectionInfo#lastAutoincrementValue
 	 */
 	public Long lastAutoincrementValue(String schemaName, String tableName,
 									   String columnName)
 	{
 		String aiKey = AutoincrementCounter.makeIdentity(schemaName, tableName, columnName);
 		
 		int size = triggerExecutionContexts.size();
 		//		System.out.println(" searching for " + aiKey);
 		for (int i = size - 1; i >= 0; i--)
 		{
 			// first loop through triggers.
 			InternalTriggerExecutionContext itec = 
 				(InternalTriggerExecutionContext)triggerExecutionContexts.get(i);
 			Long value = itec.getAutoincrementValue(aiKey);
 			if (value == null)
 				continue;
 
 			return value;
 		}
 		if (autoincrementHT == null)
 			return null;
 		return (Long)autoincrementHT.get(aiKey);
 	}	
 
 	/**
 	 * @see LanguageConnectionContext#setAutoincrementUpdate
 	 */
 	public void setAutoincrementUpdate(boolean flag)
 	{
 		autoincrementUpdate = flag;
 	}
 	
 	/**
 	 * @see LanguageConnectionContext#getAutoincrementUpdate
 	 */
 	public boolean getAutoincrementUpdate()
 	{
 		return autoincrementUpdate;
 	}
 	
 	/**
 	 * @see LanguageConnectionContext#autoincrementCreateCounter
 	 */
 	public void autoincrementCreateCounter(String s, String t, String c,
 										   Long initialValue, long increment,
 										   int position)
 	{
 		String key = AutoincrementCounter.makeIdentity(s,t,c);
 		
 		if (autoincrementCacheHashtable == null)
 		{
 			autoincrementCacheHashtable = new HashMap();
 		}
 
 		AutoincrementCounter aic = 
 			(AutoincrementCounter)autoincrementCacheHashtable.get(key);
 		if (aic != null)
 		{
 			if (SanityManager.DEBUG)			
 			{
 				SanityManager.THROWASSERT(
 							  "Autoincrement Counter already exists:" + key);
 			}
 			return;
 		}
 		
 		aic = new AutoincrementCounter(initialValue, 
 									   increment, 0, s, t, c, position);
 		autoincrementCacheHashtable.put(key, aic);
 	}
 
 	/**
 	 * returns the <b>next</b> value to be inserted into an autoincrement col.
 	 * This is used internally by the system to generate autoincrement values
 	 * which are going to be inserted into a autoincrement column. This is
 	 * used when as autoincrement column is added to a table by an alter 
 	 * table statemenet and during bulk insert.
 	 *
 	 * @param schemaName
 	 * @param tableName
 	 * @param columnName identify the column uniquely in the system.
 	 */
 	public long nextAutoincrementValue(String schemaName, String tableName,
 									   String columnName)
 			throws StandardException						   
 	{
 		String key = AutoincrementCounter.makeIdentity(schemaName,tableName,
 													   columnName);
 		
 		AutoincrementCounter aic = 
 			(AutoincrementCounter)autoincrementCacheHashtable.get(key);
 
 		if (aic == null)
 		{
 			if (SanityManager.DEBUG)			
 			{
 				SanityManager.THROWASSERT("counter doesn't exist:" + key);
 			}
 			return 0;
 		}
 		else
 		{
 			return aic.update();
 		}
 	}
 	
 	/**
 	 * Flush the cache of autoincrement values being kept by the lcc.
 	 * This will result in the autoincrement values being written to the
 	 * SYSCOLUMNS table as well as the mapping used by lastAutoincrementValue
 	 * 
 	 * @exception StandardException thrown on error.
 	 * @see LanguageConnectionContext#lastAutoincrementValue
 	 * @see GenericLanguageConnectionContext#lastAutoincrementValue
 	 * @see org.apache.derby.iapi.db.ConnectionInfo#lastAutoincrementValue
 	 */
 	public void autoincrementFlushCache(UUID tableUUID)
 		throws StandardException
 	{
 		if (autoincrementCacheHashtable == null)
 			return;
 
 		if (autoincrementHT == null)
 			autoincrementHT = new HashMap();
 
 		DataDictionary dd = getDataDictionary();
 		for (Iterator it = autoincrementCacheHashtable.keySet().iterator();
 			 it.hasNext(); )
 		{
 			Object key = it.next();
 			AutoincrementCounter aic = 
 				(AutoincrementCounter)autoincrementCacheHashtable.get(key);
 			Long value = aic.getCurrentValue();
 			aic.flushToDisk(getTransactionExecute(), dd, tableUUID);
 			if (value != null)
 			{
 				autoincrementHT.put(key, value);
 			}
 		}
 		autoincrementCacheHashtable.clear();
 	}
 
 	/**
 	 * Copies an existing autoincrement mapping
 	 * into autoincrementHT, the cache of autoincrement values 
 	 * kept in the languageconnectioncontext.
 	 */
 	public void copyHashtableToAIHT(Map from)
 	{
 		if (from.isEmpty())
 			return;
 		if (autoincrementHT == null)
 			autoincrementHT = new HashMap();
 		
 		autoincrementHT.putAll(from);
 	}
 	
 	/**
 	 * @see LanguageConnectionContext#getInstanceNumber
 	 */
 	public int getInstanceNumber()
 	{
 		return instanceNumber;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#getDrdaID
 	 */
 	public String getDrdaID()
 	{
 		return drdaID;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#setDrdaID
 	 */
 	public void setDrdaID(String drdaID)
 	{
 		this.drdaID = drdaID;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#getDbname
 	 */
 	public String getDbname()
 	{
 		return dbname;
 	}
 
 	/**
 	 * @see LanguageConnectionContext#getLastActivation
 	 */
 	public Activation getLastActivation()
 	{
 		return (Activation)acts.get(acts.size() - 1);
 	}
 
 	public StringBuffer appendErrorInfo() {
 
 		TransactionController tc = getTransactionExecute();
 		if (tc == null)
 			return null;
 
 		StringBuffer sb = new StringBuffer(200);
 
 		sb.append(LanguageConnectionContext.xidStr);
 		sb.append(tc.getTransactionIdString());
 		sb.append("), ");
 
 		sb.append(LanguageConnectionContext.lccStr);
 		sb.append(Integer.toString(getInstanceNumber()));
 		sb.append("), ");
 
 		sb.append(LanguageConnectionContext.dbnameStr);
 		sb.append(getDbname());
 		sb.append("), ");
 
 		sb.append(LanguageConnectionContext.drdaStr);
 		sb.append(getDrdaID());
 		sb.append("), ");
 
 		return sb;
 	}
 }
