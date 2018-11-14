 /*
 
    Derby - Class org.apache.derby.impl.sql.compile.StatementNode
 
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
 
 package	org.apache.derby.impl.sql.compile;
 
 import org.apache.derby.iapi.services.context.ContextManager;
 
 import org.apache.derby.iapi.error.StandardException;
 
 import org.apache.derby.iapi.sql.compile.CompilerContext;
 
 import org.apache.derby.impl.sql.compile.ActivationClassBuilder;
 import org.apache.derby.iapi.sql.dictionary.DataDictionary;
 import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
 import org.apache.derby.iapi.store.access.ConglomerateController;
 import org.apache.derby.iapi.store.access.TransactionController;
 
 import org.apache.derby.iapi.services.compiler.MethodBuilder;
 
 import org.apache.derby.iapi.reference.SQLState;
 import org.apache.derby.iapi.reference.ClassName;
 import org.apache.derby.iapi.services.loader.GeneratedClass;
 
 import org.apache.derby.iapi.util.ByteArray;
 import org.apache.derby.iapi.services.classfile.VMOpcode;
 
 import org.apache.derby.iapi.services.sanity.SanityManager;
 
 import java.lang.reflect.Modifier;
 
 /**
  * A StatementNode represents a single statement in the language.  It is
  * the top node for any statement.
  * <p>
  * StatementNode controls the class generation for query tree nodes.
  *
  * @author Jeff Lichtman
  */
 
 /*
 * History:
 *	5/8/97	Rick Hilleags	Moved node-name-string to child classes.
 */
 
 abstract class StatementNode extends QueryTreeNode
 {
 
 	/**
 	 * By default, assume StatementNodes are atomic.
 	 * The rare statements that aren't atomic (e.g.
 	 * CALL method()) override this.
 	 *
 	 * @return true if the statement is atomic
 	 *
 	 * @exception StandardException		Thrown on error
 	 */	
 	public boolean isAtomic() throws StandardException
 	{
 		return true;
 	}
 
 	/**
 	 * Convert this object to a String.  See comments in QueryTreeNode.java
 	 * for how this should be done for tree printing.
 	 *
 	 * @return	This object as a String
 	 */
 
 	public String toString()
 	{
 		if (SanityManager.DEBUG)
 		{
 			return "statementType: " + statementToString() + "\n" +
 				super.toString();
 		}
 		else
 		{
 			return "";
 		}
 	}
 
 	public abstract String statementToString();
 
 	/**
 	 * create the outer shell class builder for the class we will
 	 * be generating, generate the expression to stuff in it,
 	 * and turn it into a class.
 	 */
 	static final int NEED_DDL_ACTIVATION = 5;
 	static final int NEED_CURSOR_ACTIVATION = 4;
 	static final int NEED_PARAM_ACTIVATION = 2;
 	static final int NEED_ROW_ACTIVATION = 1;
 	static final int NEED_NOTHING_ACTIVATION = 0;
 
 	abstract int activationKind();
 
 	/* We need to get some kind of table lock (IX here) at the beginning of
 	 * compilation of DMLModStatementNode and DDLStatementNode, to prevent the
 	 * interference of insert/update/delete/DDL compilation and DDL execution,
 	 * see beetle 3976, 4343, and $WS/language/SolutionsToConcurrencyIssues.txt
 	 */
 	protected TableDescriptor lockTableForCompilation(TableDescriptor td)
 		throws StandardException
 	{
 		DataDictionary dd = getDataDictionary();
 
 		/* we need to lock only if the data dictionary is in DDL cache mode
 		 */
 		if (dd.getCacheMode() == DataDictionary.DDL_MODE)
 		{
 			ConglomerateController  heapCC;
 			TransactionController tc =
 				getLanguageConnectionContext().getTransactionCompile();
 
 			heapCC = tc.openConglomerate(td.getHeapConglomerateId(),
                                     false,
 									TransactionController.OPENMODE_FORUPDATE |
 									TransactionController.OPENMODE_FOR_LOCK_ONLY,
 									TransactionController.MODE_RECORD,
 									TransactionController.ISOLATION_SERIALIZABLE);
 			heapCC.close();
 			/*
 			** Need to get TableDescriptor again after getting the lock, in
 			** case for example, a concurrent add column thread commits
 			** while we are binding.
 			*/
 			String tableName = td.getName();
 			td = getTableDescriptor(td.getName(), getSchemaDescriptor(td.getSchemaName()));
 			if (td == null)
 			{
 				throw StandardException.newException(SQLState.LANG_TABLE_NOT_FOUND, tableName);
 			}
 		}
 		return td;
 	}
 
 
 	/**
 	 * Do code generation for this statement.
 	 *
 	 * @param byteCode	the generated byte code for this statement.
 	 *			if non-null, then the byte code is saved
 	 *			here.
 	 *
 	 * @return		A GeneratedClass for this statement
 	 *
 	 * @exception StandardException		Thrown on error
 	 */
 	public GeneratedClass generate(ByteArray byteCode) throws StandardException
 	{
 		// start the new activation class.
 		// it starts with the Execute method
 		// and the appropriate superclass (based on
 		// statement type, from inspecting the queryTree).
 
 		int nodeChoice = activationKind();
 
 		/* RESOLVE: Activation hierarchy was way too complicated
 		 * and added no value.  Simple thing to do was to simply
 		 * leave calling code alone and to handle here and to
 		 * eliminate unnecessary classes.
 		 */
 		String superClass;
 		switch (nodeChoice)
 		{
 		case NEED_CURSOR_ACTIVATION:
 			superClass = ClassName.CursorActivation;
 			break;
 		case NEED_DDL_ACTIVATION:
 			return getClassFactory().loadGeneratedClass(
 				"org.apache.derby.impl.sql.execute.ConstantActionActivation", null);
 
 		case NEED_NOTHING_ACTIVATION :
 		case NEED_ROW_ACTIVATION :
 		case NEED_PARAM_ACTIVATION :
 			superClass = ClassName.BaseActivation;
 			break;
 		default :
 			throw StandardException.newException(SQLState.LANG_UNAVAILABLE_ACTIVATION_NEED,
 					String.valueOf(nodeChoice));
 		}
 
 		ActivationClassBuilder generatingClass = new ActivationClassBuilder(
 										superClass, 
 										getCompilerContext());
 		MethodBuilder executeMethod = generatingClass.getExecuteMethod();
 
 
 		/*
 		** the resultSet variable is cached.
 		**
 		** 	resultSet = (resultSet == null) ? ... : resultSet
 		*/
 
 		executeMethod.pushThis();
 		executeMethod.getField(ClassName.BaseActivation, "resultSet", ClassName.ResultSet);
 		executeMethod.conditionalIfNull();
 
 			/* We should generate the result set here.  However, the generated
 			 * code size may be too big to fit in a conditional statement for
 			 * Java compiler to handle (it has a jump/branch step limit).  For
 			 * example, a extremely huge insert is issued with many many rows
 			 * (beetle 4293).  We fork a worker method here to get the
 			 * generated result set, pass our parameter to it and call it.
 			 */
 			MethodBuilder mbWorker = generatingClass.getClassBuilder().newMethodBuilder(
 														Modifier.PROTECTED,
 														ClassName.ResultSet,
 														"fillResultSet");
 			mbWorker.addThrownException(ClassName.StandardException);
 
 			// we expect to get back an expression that will give a resultSet
 			// the nodes use the generatingClass: they add expression functions
 			// to it, and then use those functions in their expressions.
 			generate(generatingClass, mbWorker);
 
 			mbWorker.methodReturn();
 			mbWorker.complete();
 			executeMethod.pushThis();
 			executeMethod.callMethod(VMOpcode.INVOKEVIRTUAL, (String) null,
 									 "fillResultSet", ClassName.ResultSet, 0);
 
 		executeMethod.startElseCode(); // this is here as the compiler only supports ? :
 			executeMethod.pushThis();
 			executeMethod.getField(ClassName.BaseActivation, "resultSet", ClassName.ResultSet);
 		executeMethod.completeConditional();
 
 		executeMethod.pushThis();
 		executeMethod.swap();
 		executeMethod.putField(ClassName.BaseActivation, "resultSet", ClassName.ResultSet);
 
 		executeMethod.endStatement();
 
    		// wrap up the activation class definition
 		// generate on the tree gave us back the newExpr
 		// for getting a result set on the tree.
 		// we put it in a return statement and stuff
 		// it in the execute method of the activation.
 		// The generated statement is the expression:
 		// the activation class builder takes care of constructing it
 		// for us, given the resultSetExpr to use.
 		//   return (this.resultSet = #resultSetExpr);
 		generatingClass.finishExecuteMethod(this instanceof CursorNode);
 
 		// wrap up the constructor by putting a return at the end of it
 		generatingClass.finishConstructor();
 
 		try {
 			// cook the completed class into a real class
 			// and stuff it into activationClass
 			GeneratedClass activationClass = generatingClass.getGeneratedClass(byteCode);
 
 			return activationClass;
 		} catch (StandardException e) {
 			
 			String msgId = e.getMessageId();
 
 			if (SQLState.GENERATED_CLASS_LIMIT_EXCEEDED.equals(msgId)
 					|| SQLState.GENERATED_CLASS_LINKAGE_ERROR.equals(msgId))
 			{
 				throw StandardException.newException(
 						SQLState.LANG_QUERY_TOO_COMPLEX, e);
 			}
 	
 			throw e;
 		}
 	 }
 }
