 /*
 
    Derby - Class org.apache.derby.impl.sql.compile.CastNode
 
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
 
 import java.lang.reflect.Modifier;
 import java.sql.Types;
 import java.util.List;
 import org.apache.derby.iapi.error.StandardException;
 import org.apache.derby.iapi.reference.ClassName;
 import org.apache.derby.iapi.reference.Limits;
 import org.apache.derby.iapi.reference.SQLState;
 import org.apache.derby.iapi.services.classfile.VMOpcode;
 import org.apache.derby.iapi.services.compiler.LocalField;
 import org.apache.derby.iapi.services.compiler.MethodBuilder;
 import org.apache.derby.iapi.services.context.ContextManager;
 import org.apache.derby.shared.common.sanity.SanityManager;
 import org.apache.derby.iapi.sql.compile.TypeCompiler;
 import org.apache.derby.iapi.sql.compile.Visitor;
 import org.apache.derby.iapi.types.DataTypeDescriptor;
 import org.apache.derby.iapi.types.DataTypeUtilities;
 import org.apache.derby.iapi.types.DataValueDescriptor;
 import org.apache.derby.iapi.types.NumberDataType;
 import org.apache.derby.iapi.types.TypeId;
 import org.apache.derby.iapi.util.JBitSet;
 import org.apache.derby.iapi.util.ReuseFactory;
 import org.apache.derby.iapi.util.StringUtil;
 
 /**
  * An CastNode represents a cast expression.
  *
  */
 
 class CastNode extends ValueNode
 {
 	ValueNode			castOperand;
 	private int					targetCharType;
 	TypeId	sourceCTI = null;
 	private boolean forDataTypeFunction = false;
 
     /** The original, unbound descriptor for the target type, if it is a UDT. */
     private DataTypeDescriptor targetUDT;
 
     /** This variable gets set by the parser to indicate that this CAST node
 	 * has been generated by the parser. This means that we should use the 
      * collation info of the current compilation schema for this node's
 	 * collation setting. If this variable does not get set to true, then it 
 	 * means that this CAST node has been an internally generated node and we 
 	 * should not touch the collation info set for this CAST node because it 
 	 * has been already set correctly by the class that generated this CAST 
 	 * node. Collation info is part of the DataTypeDescriptor that's defined
 	 * on the ValueNode (the super class of this CastNode class)
 	 */ 
 	private boolean externallyGeneratedCastNode = false;
 
 	/*
 	** Static array of valid casts.  Dimentions
 	** produce a single boolean which indicates
 	** whether the case is possible or not.
 	*/
 
     /**
      * Method calls:
      * Argument type has the same semantics as assignment:
      * Section 9.2 (Store assignment). There, General Rule
      * 2.b.v.2 says that the database should raise an exception
      * if truncation occurs when stuffing a string value into a
      * VARCHAR, so make sure CAST doesn't issue warning only.
      */
     private boolean assignmentSemantics = false;
 
     /**
      * The name of the target type if it's a UDT. It is partly redundant, as
      * the name can also be retrieved from the type descriptor. Additionally,
      * it contains information about the location of the UDT name in the
      * query text, which is useful if the query text needs to be rewritten.
      * (Useful for example when rewriting a CHECK constraint definition to
      * have fully qualified names before storing it in the dictionary.) This
      * field is only set for <b>explicit</b> casts to a UDT.
      */
     private TableName udtTargetName;
 
 	/**
      * Constructor for a CastNode
 	 *
 	 * @param castOperand	The operand of the node
 	 * @param castTarget	DataTypeServices (target type of cast)
      * @param cm            The context manager
 	 *
 	 * @exception StandardException		Thrown on error
 	 */
 
     CastNode(ValueNode castOperand,
              DataTypeDescriptor castTarget,
              ContextManager cm) throws StandardException {
         super(cm);
         this.castOperand = castOperand;
 
         // DERBY-6421: setType() tries to bind user defined types. We don't
         // want to do any binding here, since we could be called during
         // parsing. If the target type is a UDT, just store it for now and
         // do the binding later when bindExpression() or bindCastNodeOnly()
         // is called.
         if (castTarget.getTypeId().isUserDefinedTypeId()) {
             targetUDT = castTarget;
         } else {
             setType(castTarget);
         }
 	}
 
 	/**
      * Constructor for a CastNode
 	 *
 	 * @param castOperand	The operand of the node
 	 * @param charType		CHAR or VARCHAR JDBC type as target
 	 * @param charLength	target type length
      * @param cm            The context manager
 	 *
 	 * @exception StandardException		Thrown on error
 	 */
 
     CastNode(ValueNode castOperand,
              int charType,
              int charLength,
              ContextManager cm) throws StandardException {
         super(cm);
         this.castOperand = castOperand;
         int charLen = charLength;
         targetCharType = charType;
 		if (charLen < 0)	// unknown, figure out later
 			return;
 		setType(DataTypeDescriptor.getBuiltInDataTypeDescriptor(targetCharType, charLen));
 	}
 
 	/**
 	 * Convert this object to a String.  See comments in QueryTreeNode.java
 	 * for how this should be done for tree printing.
 	 *
 	 * @return		This object as a String
 	 */
     @Override
 	public String toString()
 	{
 	    if (SanityManager.DEBUG)
 	    {
 	        return "castTarget: " + getTypeServices() + "\n" +
 	        super.toString();
 	    }
 	    else
 	    {
 	        return "";
 	    }
 	}
 
 	/**
 	 * Prints the sub-nodes of this object.  See QueryTreeNode.java for
 	 * how tree printing is supposed to work.
 	 *
 	 * @param depth		The depth of this node in the tree
 	 */
     @Override
     void printSubNodes(int depth)
 	{
 		if (SanityManager.DEBUG)
 		{
 			super.printSubNodes(depth);
 
 			if (castOperand != null)
 			{
 				printLabel(depth, "castOperand: ");
 				castOperand.treePrint(depth + 1);
 			}
 		}
 	}
 	protected int getOrderableVariantType() throws StandardException
 	{
 		return castOperand.getOrderableVariantType();
 	}
 
 	/**
 	 * Bind this expression.  This means binding the sub-expressions,
 	 * as well as figuring out what the return type is for this expression.
 	 *
 	 * @param fromList		The FROM list for the query this
 	 *				expression is in, for binding columns.
 	 * @param subqueryList		The subquery list being built as we find SubqueryNodes
      * @param aggregates        The aggregate list being built as we find AggregateNodes
 	 *
 	 * @return	The new top of the expression tree.
 	 *
 	 * @exception StandardException		Thrown on error
 	 */
     @Override @SuppressWarnings("fallthrough")
     ValueNode bindExpression(FromList fromList, SubqueryList subqueryList, List<AggregateNode> aggregates)
 				throws StandardException
 	{
 		castOperand = castOperand.bindExpression(
 								fromList, subqueryList,
                                 aggregates);
 
         // Bind the target UDT.
         if (targetUDT != null) {
             setType(targetUDT);
         }
 
 		if (getTypeServices() == null)   //CHAR or VARCHAR function without specifying target length
 		{
 			DataTypeDescriptor opndType = castOperand.getTypeServices();
 			int length = -1;
 			TypeId srcTypeId = opndType.getTypeId();
 
             if (srcTypeId.isNumericTypeId())
             {
                 length = opndType.getPrecision() + 1; // 1 for the sign
                 if (opndType.getScale() > 0)
                     length += 1;               // 1 for the decimal .
 
             }
             /*
              * Derby-1132 : The length for the target type was calculated
              * incorrectly while Char & Varchar functions were used. Thus
              * adding the check for Char & Varchar and calculating the
              * length based on the operand type.
              */
             else if(srcTypeId.isStringTypeId())
             {
                 length = opndType.getMaximumWidth();
 
                 // Truncate the target type width to the max width of the
                 // data type
                 if (this.targetCharType == Types.CHAR)
                     length = Math.min(length, Limits.DB2_CHAR_MAXWIDTH);
                 else if (this.targetCharType == Types.VARCHAR)
                     length = Math.min(length, Limits.DB2_VARCHAR_MAXWIDTH);
             }
             else
             {
                 TypeId typeid = opndType.getTypeId();
 
                 if (length < 0) {
                     length = DataTypeUtilities.getColumnDisplaySize(
                         typeid.getJDBCTypeId(), -1);
                 }
             }
 
             if (length < 0)
                 length = 1;  // same default as in parser
 			setType(DataTypeDescriptor.getBuiltInDataTypeDescriptor(targetCharType, length));
 			
 		}
 
 		/* 
 		** If castOperand is an untyped null, 
 		** then we must set the type.
 		*/
 		if (castOperand instanceof UntypedNullConstantNode)
 		{
 			castOperand.setType(getTypeServices());
 		}
 
 		bindCastNodeOnly();
 		
 		/* We can't chop out cast above an untyped null because
 		 * the store can't handle it.
 		 */
 		if ((castOperand instanceof ConstantNode) &&
 			!(castOperand instanceof UntypedNullConstantNode))
 		{
 			/* If the castOperand is a typed constant then we do the cast at
 			 * bind time and return a constant of the correct type.
 			 * NOTE: This could return an exception, but we're prepared to 
 			 * deal with that. (NumberFormatException, etc.)
 			 * We only worry about the easy (and useful)
 			 * converions at bind time.
 			 * Here's what we support:
 			 *			source					destination
 			 *			------					-----------
 			 *			boolean					boolean
 			 *			boolean					char
 			 *			char					boolean
 			 *			char					date/time/ts
 			 *			char					non-decimal numeric
 			 *			date/time/ts			char
 			 *			numeric					char
 			 *			numeric					non-decimal numeric
 			 */
 			/* RESOLVE - to be filled in. */
 			ValueNode retNode = this;
 			int		  sourceJDBCTypeId = sourceCTI.getJDBCTypeId();
 			int		  destJDBCTypeId = getTypeId().getJDBCTypeId();
 
 			switch (sourceJDBCTypeId)
 			{
 				case Types.BIT:
 				case Types.BOOLEAN:
 					// (BIT is boolean)
 					if (destJDBCTypeId == Types.BIT || destJDBCTypeId == Types.BOOLEAN)
 					{
 						retNode = castOperand;
 					}
 					else if (destJDBCTypeId == Types.CHAR)
 					{
 						BooleanConstantNode bcn = (BooleanConstantNode) castOperand;
 						String booleanString = bcn.getValueAsString();
                        retNode = new CharConstantNode(
                                 booleanString,
                                 getTypeServices().getMaximumWidth(),
                                 getContextManager());
 					}
 					break;
 
 					case Types.CHAR:
 						retNode = getCastFromCharConstant(destJDBCTypeId);
 						break;
 
 					case Types.DATE:
 					case Types.TIME:
 					case Types.TIMESTAMP:
 						if (destJDBCTypeId == Types.CHAR)
 						{
 							String castValue =  
 								((UserTypeConstantNode) castOperand).
 											getObjectValue().
 												toString();
                            retNode = new CharConstantNode(
                                castValue,
                                getTypeServices().getMaximumWidth(),
                                getContextManager());
 						}
 						break;
 
 					case Types.DECIMAL:
 						// ignore decimal -> decimal casts for now
 						if (destJDBCTypeId == Types.DECIMAL ||
 							destJDBCTypeId == Types.NUMERIC)
 							break;
 						// fall through
 					case Types.TINYINT:
 					case Types.SMALLINT:
 					case Types.INTEGER:
 					case Types.BIGINT:
 					case Types.DOUBLE:
 					case Types.REAL:
 						retNode = getCastFromNumericType(
 											((ConstantNode) castOperand).getValue(), 
 											destJDBCTypeId);
 						break;
 
 			}
 
 			// Return the new constant if the cast was performed
 			return retNode;
 		}
 
 		return this;
 	}
 
 	/**
 	 * Bind this node but not its child.  Caller has already bound
 	 * the child.
 	 * This is useful for when we generate a CastNode during binding
 	 * after having already bound the child.
 	 *
 	 * @exception StandardException		Thrown on error
 	 */
     void bindCastNodeOnly()
 		throws StandardException
 	{
         // Bind the target UDT.
         if (targetUDT != null) {
             setType(targetUDT);
         }
 
 		/*
 		** The result type is always castTarget.
 		*/
 		sourceCTI = castOperand.getTypeId();
 
 		//If the result type of cast is string data type, then that data type 
 		//should get it's collation type from the current schema. 
 		if (externallyGeneratedCastNode && getTypeId().isStringTypeId()) {
 			//set the collation type to be same as the compilation schema's 
 			//collation type. Collation derivation will be set to "IMPLICIT".
 			setCollationUsingCompilationSchema();
 		}
 		/* 
 		** If it is a java cast, do some work to make sure
 		** the classes are ok and that they are compatible
 		*/
 		if (getTypeId().userType())
 		{
             setType( bindUserType( getTypeServices() ) );
             
 			String className = getTypeId().getCorrespondingJavaTypeName();
 
 			verifyClassExist(className);
 		}
 
         // Set the schema name of the UDT target type.
         if (udtTargetName != null) {
             udtTargetName.setSchemaName(
                     getTypeId().getBaseTypeId().getSchemaName());
         }
 
         // Obviously the type of a parameter that
         // requires its type from context (a parameter)
         // gets its type from the type of the CAST.
 		if (castOperand.requiresTypeFromContext())
 		{
             castOperand.setType(getTypeServices());
 		}
 
 		/*
 		** If it isn't null, then we have
 		** a cast from one JBMS type to another.  So we
 		** have to figure out if it is legit.
 		*/
 		else if (!(castOperand instanceof UntypedNullConstantNode))
 		{
 			/*
 			** Make sure we can assign the two classes
 			*/
 			TypeCompiler tc = castOperand.getTypeCompiler();
 			if (! tc.convertible(getTypeId(), forDataTypeFunction))
 			{
 				throw StandardException.newException(SQLState.LANG_INVALID_CAST, 
 						sourceCTI.getSQLTypeName(),
                         getTypeId().getSQLTypeName());
 			}
 		}	
 
         //
 		// Preserve the nullability of the operand since a CAST
 		// of a non-NULL value is also non-NULL. However, if the source type is
         // a non-nullable string type and the target type is a boolean, then the result
         // still must be nullable because the string "unknown" casts to boolean NULL.
         //
         if (
             castOperand.getTypeServices().getTypeId().isStringTypeId() &&
             getTypeId().isBooleanTypeId()
             )
         { setNullability( true ); }
 		else { setNullability(castOperand.getTypeServices().isNullable()); }
 	}
 
 	/**
 	 * Get a constant representing the cast from a CHAR to another
 	 * type.  If this is not an "easy" cast to perform, then just
 	 * return this cast node.
 	 * Here's what we think is "easy":
 	 *			source			destination
 	 *			------			-----------
 	 *			char			boolean
 	 *			char			date/time/ts
 	 *			char			non-decimal numeric
 	 *
 	 * @param destJDBCTypeId	The destination JDBC TypeId
 	 *
 	 * @return The new top of the tree (this CastNode or a new Constant)
 	 *
 	 * @exception StandardException		Thrown on error
 	 */
 	private ValueNode getCastFromCharConstant(int destJDBCTypeId)
 		throws StandardException
 	{
 		String	  charValue = ((CharConstantNode) castOperand).getString();
 		String	  cleanCharValue = StringUtil.SQLToUpperCase(charValue.trim());
 		ValueNode retNode = this;
 
 		switch (destJDBCTypeId)
 		{
 			case Types.BIT:
 			case Types.BOOLEAN:
 				if (cleanCharValue.equals("TRUE"))
 				{
                    return new BooleanConstantNode(true, getContextManager());
 				}
 				else if (cleanCharValue.equals("FALSE"))
 				{
                    return new BooleanConstantNode(false, getContextManager());
 				}
 				else if (cleanCharValue.equals("UNKNOWN"))
 				{
                    return new BooleanConstantNode(getContextManager());
 				}
 				else
 				{
 					throw StandardException.newException(SQLState.LANG_FORMAT_EXCEPTION, "boolean");
 				}
 
 			case Types.DATE:
                return new UserTypeConstantNode(
 										getDataValueFactory().getDateValue(cleanCharValue, false),
 										getContextManager());
 
 			case Types.TIMESTAMP:
                return new UserTypeConstantNode(
 									getDataValueFactory().getTimestampValue(cleanCharValue, false),
 									getContextManager());
 
 			case Types.TIME:
                return new UserTypeConstantNode(
 										getDataValueFactory().getTimeValue(cleanCharValue, false),
 										getContextManager());
 
 			case Types.TINYINT:
 			case Types.SMALLINT:
 			case Types.INTEGER:
 			case Types.BIGINT:				
 				try 
 				{
 					// #3756 - Truncate decimal portion for casts to integer
 					return getCastFromIntegralType((new Double(cleanCharValue)).longValue(),
 												   destJDBCTypeId);
 				}
 				catch (NumberFormatException nfe)
 				{
 					String sqlName = TypeId.getBuiltInTypeId(destJDBCTypeId).getSQLTypeName();
 					throw StandardException.newException(SQLState.LANG_FORMAT_EXCEPTION, sqlName);
 				}
 			case Types.REAL:
 				Float floatValue;
 				try
 				{
 					floatValue = Float.valueOf(cleanCharValue);
 				}
 				catch (NumberFormatException nfe)
 				{
 					throw StandardException.newException(SQLState.LANG_FORMAT_EXCEPTION, "float");
 				}
                return new NumericConstantNode(
                        TypeId.getBuiltInTypeId(Types.REAL),
                        floatValue,
                        getContextManager());
 			case Types.DOUBLE:
 				Double doubleValue;
 				try
 				{
 					doubleValue = new Double(cleanCharValue);
 				}
 				catch (NumberFormatException nfe)
 				{
 					throw StandardException.newException(SQLState.LANG_FORMAT_EXCEPTION, "double");
 				}
                return new NumericConstantNode(
                        TypeId.getBuiltInTypeId(Types.DOUBLE),
                        doubleValue,
                        getContextManager());
 		}
 
 		return retNode;
 	}
 
 
 	/**
 	 * Get a constant representing the cast from an integral type to another
 	 * type.  If this is not an "easy" cast to perform, then just
 	 * return this cast node.
 	 * Here's what we think is "easy":
 	 *			source				destination
 	 *			------				-----------
 	 *			integral type		 non-decimal numeric
 	 *			integral type		 char
 	 *
 	 * @param longValue			integral type as a long to cast from
 	 * @param destJDBCTypeId	The destination JDBC TypeId
 	 *
 	 * @return The new top of the tree (this CastNode or a new Constant)
 	 *
 	 * @exception StandardException		Thrown on error
 	 */
 	private ValueNode getCastFromIntegralType(
 									  long longValue, 
 									  int destJDBCTypeId)
 		throws StandardException
 	{
 		ValueNode retNode = this;
 
 		switch (destJDBCTypeId)
 		{
 			case Types.CHAR:
                return new CharConstantNode(
                         Long.toString(longValue),
                         getTypeServices().getMaximumWidth(),
                         getContextManager());
 			case Types.TINYINT:
 				if (longValue < Byte.MIN_VALUE ||
 					longValue > Byte.MAX_VALUE)
 				{
 					throw StandardException.newException(SQLState.LANG_OUTSIDE_RANGE_FOR_DATATYPE, "TINYINT");
 				}
                return new NumericConstantNode(
                        TypeId.getBuiltInTypeId(Types.TINYINT),
                        ReuseFactory.getByte((byte) longValue),
                        getContextManager());
 
 			case Types.SMALLINT:
 				if (longValue < Short.MIN_VALUE ||
 					longValue > Short.MAX_VALUE)
 				{
 					throw StandardException.newException(SQLState.LANG_OUTSIDE_RANGE_FOR_DATATYPE, "SHORT");
 				}
                return new NumericConstantNode(
                    TypeId.getBuiltInTypeId(destJDBCTypeId),
                    ReuseFactory.getShort(
                        (short) longValue),
                    getContextManager());
 
 			case Types.INTEGER:
 				if (longValue < Integer.MIN_VALUE ||
 					longValue > Integer.MAX_VALUE)
 				{
 					throw StandardException.newException(SQLState.LANG_OUTSIDE_RANGE_FOR_DATATYPE, "INTEGER");
 				}
                return new NumericConstantNode(
                    TypeId.getBuiltInTypeId(destJDBCTypeId),
                    ReuseFactory.getInteger(
                        (int) longValue),
                    getContextManager());
 
 			case Types.BIGINT:
                return new NumericConstantNode(
                    TypeId.getBuiltInTypeId(destJDBCTypeId),
                    ReuseFactory.getLong(longValue),
                    getContextManager());
 
 			case Types.REAL:
 				if (Math.abs(longValue) > Float.MAX_VALUE)
 				{
 					throw StandardException.newException(SQLState.LANG_OUTSIDE_RANGE_FOR_DATATYPE, "REAL");
 				}
                return new NumericConstantNode(
                    TypeId.getBuiltInTypeId(destJDBCTypeId),
                    new Float((float) longValue),
                    getContextManager());
 
 			case Types.DOUBLE:
                return new NumericConstantNode(
                    TypeId.getBuiltInTypeId(destJDBCTypeId),
                    new Double((double) longValue),
                    getContextManager());
 		}
 
 		return retNode;
 	}
 
 	/**
 	 * Get a constant representing the cast from a non-integral type to another
 	 * type.  If this is not an "easy" cast to perform, then just
 	 * return this cast node.
 	 * Here's what we think is "easy":
 	 *			source				destination
 	 *			------				-----------
 	 *			non-integral type	 non-decimal numeric
 	 *			non-integral type	 char
 	 *
 	 * @param constantValue		non-integral type a a double to cast from
 	 * @param destJDBCTypeId	The destination JDBC TypeId
 	 *
 	 * @return The new top of the tree (this CastNode or a new Constant)
 	 *
 	 * @exception StandardException		Thrown on error
 	 */
 	private ValueNode getCastFromNumericType(
 									  DataValueDescriptor constantValue, 
 									  int destJDBCTypeId)
 		throws StandardException
 	{
 		switch (destJDBCTypeId)
 		{
 			case Types.CHAR:
                return new CharConstantNode(
                         constantValue.getString(),
                         getTypeServices().getMaximumWidth(),
                         getContextManager());
 
 			case Types.TINYINT:
                 return new NumericConstantNode(
                         TypeId.getBuiltInTypeId(destJDBCTypeId),
                         Byte.valueOf(constantValue.getByte()),
                         getContextManager());
 
 			case Types.SMALLINT:
                 return new NumericConstantNode(
                         TypeId.getBuiltInTypeId(destJDBCTypeId),
                         Short.valueOf(constantValue.getShort()),
                         getContextManager());
 
 			case Types.INTEGER:
                 return new NumericConstantNode(
                         TypeId.getBuiltInTypeId(destJDBCTypeId),
                         Integer.valueOf(constantValue.getInt()),
                         getContextManager());
 
 			case Types.BIGINT:
                 return new NumericConstantNode(
                         TypeId.getBuiltInTypeId(destJDBCTypeId),
                         Long.valueOf(constantValue.getLong()),
                         getContextManager());
 
 			case Types.REAL:
                 return new NumericConstantNode(
                         TypeId.getBuiltInTypeId(destJDBCTypeId),
                         Float.valueOf(NumberDataType.normalizeREAL(
                                       constantValue.getDouble())),
                         getContextManager());
 
 			case Types.DOUBLE:
 				// no need to normalize here because no constant could be out of range for a double
                 return new NumericConstantNode(
                         TypeId.getBuiltInTypeId(destJDBCTypeId),
                        Double.valueOf(constantValue.getDouble()),
                         getContextManager());
 		}
 
         return this;
 	}
 
 	/**
 	 * Preprocess an expression tree.  We do a number of transformations
 	 * here (including subqueries, IN lists, LIKE and BETWEEN) plus
 	 * subquery flattening.
 	 * NOTE: This is done before the outer ResultSetNode is preprocessed.
 	 *
 	 * @param	numTables			Number of tables in the DML Statement
 	 * @param	outerFromList		FromList from outer query block
 	 * @param	outerSubqueryList	SubqueryList from outer query block
 	 * @param	outerPredicateList	PredicateList from outer query block
 	 *
 	 * @return		The modified expression
 	 *
 	 * @exception StandardException		Thrown on error
 	 */
     @Override
     ValueNode preprocess(int numTables,
 								FromList outerFromList,
 								SubqueryList outerSubqueryList,
 								PredicateList outerPredicateList) 
 					throws StandardException
 	{
 		castOperand = castOperand.preprocess(numTables,
 											 outerFromList, outerSubqueryList,
 											 outerPredicateList);
 		return this;
 	}
 
 	/**
 	 * Categorize this predicate.  Initially, this means
 	 * building a bit map of the referenced tables for each predicate.
 	 * If the source of this ColumnReference (at the next underlying level) 
 	 * is not a ColumnReference or a VirtualColumnNode then this predicate
 	 * will not be pushed down.
 	 *
 	 * For example, in:
 	 *		select * from (select 1 from s) a (x) where x = 1
 	 * we will not push down x = 1.
 	 * NOTE: It would be easy to handle the case of a constant, but if the
 	 * inner SELECT returns an arbitrary expression, then we would have to copy
 	 * that tree into the pushed predicate, and that tree could contain
 	 * subqueries and method calls.
 	 * RESOLVE - revisit this issue once we have views.
 	 *
 	 * @param referencedTabs	JBitSet with bit map of referenced FromTables
 	 * @param simplePredsOnly	Whether or not to consider method
 	 *							calls, field references and conditional nodes
 	 *							when building bit map
 	 *
 	 * @return boolean		Whether or not source.expression is a ColumnReference
 	 *						or a VirtualColumnNode.
 	 *
 	 * @exception StandardException			Thrown on error
 	 */
     @Override
     boolean categorize(JBitSet referencedTabs, boolean simplePredsOnly)
 		throws StandardException
 	{
 		return castOperand.categorize(referencedTabs, simplePredsOnly);
 	}
 
 	/**
 	 * Remap all ColumnReferences in this tree to be clones of the
 	 * underlying expression.
 	 *
 	 * @return ValueNode			The remapped expression tree.
 	 *
 	 * @exception StandardException			Thrown on error
 	 */
     @Override
     ValueNode remapColumnReferencesToExpressions()
 		throws StandardException
 	{
 		castOperand = castOperand.remapColumnReferencesToExpressions();
 		return this;
 	}
 
 	/**
 	 * Return whether or not this expression tree represents a constant expression.
 	 *
 	 * @return	Whether or not this expression tree represents a constant expression.
 	 */
     @Override
     boolean isConstantExpression()
 	{
 		return castOperand.isConstantExpression();
 	}
 
 	/** @see ValueNode#constantExpression */
     @Override
     boolean constantExpression(PredicateList whereClause)
 	{
 		return castOperand.constantExpression(whereClause);
 	}
 
 	/**
 	 * Return an Object representing the bind time value of this
 	 * expression tree.  If the expression tree does not evaluate to
 	 * a constant at bind time then we return null.
 	 * This is useful for bind time resolution of VTIs.
 	 * RESOLVE: What do we do for primitives?
 	 *
 	 * @return	An Object representing the bind time value of this expression tree.
 	 *			(null if not a bind time constant.)
 	 *
 	 * @exception StandardException		Thrown on error
 	 */
     @Override
 	Object getConstantValueAsObject()
 		throws StandardException
 	{
 		Object sourceObject = castOperand.getConstantValueAsObject();
 
 		// RESOLVE - need to figure out how to handle casts
 		if (sourceObject == null)
 		{
 			return null;
 		}
 
 		// Simple if source and destination are of same type
 		if (sourceCTI.getCorrespondingJavaTypeName().equals(
                 getTypeId().getCorrespondingJavaTypeName()))
 		{
 			return sourceObject;
 		}
 
 		// RESOLVE - simply return null until we can figure out how to 
 		// do the cast
 		return null;
 	}
 
 	/**
 	 * Do code generation for this unary operator.
 	 *
 	 * @param acb	The ExpressionClassBuilder for the class we're generating
 	 * @param mb	The method the code to place the code
 	 *
 	 * @exception StandardException		Thrown on error
 	 */
     @Override
     void generateExpression(ExpressionClassBuilder acb, MethodBuilder mb)
 									throws StandardException
 	{
 		castOperand.generateExpression(acb, mb);
 
 		/* No need to generate code for null constants */
 		if (castOperand instanceof UntypedNullConstantNode)
 		{
 			return;
 		}
 		/* HACK ALERT. When casting a parameter, there
 		 * is not sourceCTI.  Code generation requires one,
 		 * so we simply set it to be the same as the
 		 * destCTI.  The user can still pass whatever
 		 * type they'd like in as a parameter.
 		 * They'll get an exception, as expected, if the
 		 * conversion cannot be performed.
 		 */
 		else if (castOperand.requiresTypeFromContext())
 		{
 			sourceCTI = getTypeId();
 		}
 	
 		genDataValueConversion(acb, mb);
 	}
 
 	private void genDataValueConversion(ExpressionClassBuilder acb,
 											  MethodBuilder mb)
 			throws StandardException
 	{
 		MethodBuilder	acbConstructor = acb.getConstructor();
 
 		String resultTypeName = getTypeCompiler().interfaceName();
 
 		/* field = method call */
 		/* Allocate an object for re-use to hold the result of the operator */
 		LocalField field = acb.newFieldDeclaration(Modifier.PRIVATE, resultTypeName);
 
 		/*
 		** Store the result of the method call in the field, so we can re-use
 		** the object.
 		*/
 
 		acb.generateNull(acbConstructor, getTypeCompiler(getTypeId()), 
                 getTypeServices().getCollationType());
 		acbConstructor.setField(field);
 
 
 		/*
 			For most types generate
 
 			targetDVD.setValue(sourceDVD);
 			
 			For source or destination java types generate
 			
 			Object o = sourceDVD.getObject();
 			targetDVD.setObjectForCast(o, o instanceof dest java type, dest java type);
 
 			// optional for variable length types
 			targetDVD.setWidth();
 		*/
 
 		if (!sourceCTI.userType() && !getTypeId().userType()) {
 			mb.getField(field); // targetDVD reference for the setValue method call
 			mb.swap();
 			mb.upCast(ClassName.DataValueDescriptor);
 			mb.callMethod(VMOpcode.INVOKEINTERFACE, ClassName.DataValueDescriptor,
 					"setValue", "void", 1);
 		}
 		else
 		{
 			/* 
 			** generate: expr.getObject()
 			*/
 			mb.callMethod(VMOpcode.INVOKEINTERFACE, ClassName.DataValueDescriptor,
 					"getObject", "java.lang.Object", 0);
 
 			//castExpr
 
 			mb.getField(field); // instance for the setValue/setObjectForCast method call
 			mb.swap(); // push it before the value
 
 			/*
 			** We are casting a java type, generate:
 			**
 			**		DataValueDescriptor.setObjectForCast(java.lang.Object castExpr, boolean instanceOfExpr, destinationClassName)
 			** where instanceOfExpr is "source instanceof destinationClass".
 			**
 			*/
 			String destinationType = getTypeId().getCorrespondingJavaTypeName();
 
 			// at this point method instance and cast result are on the stack
 			// we duplicate the cast value in order to perform the instanceof check
 			mb.dup();
 			mb.isInstanceOf(destinationType);
 			mb.push(destinationType);
 			mb.callMethod(VMOpcode.INVOKEINTERFACE, ClassName.DataValueDescriptor,
 					"setObjectForCast", "void", 3);
 
 		}
 
 		mb.getField(field);
 
 		/* 
 		** If we are casting to a variable length datatype, we
 		** have to make sure we have set it to the correct
 		** length.
 		*/
 		if (getTypeId().variableLength()) 
 		{
 			boolean isNumber = getTypeId().isNumericTypeId();
 			
 			// to leave the DataValueDescriptor value on the stack, since setWidth is void
 			mb.dup();
 
 			/* setWidth() is on VSDV - upcast since
 			 * decimal implements subinterface
 			 * of VSDV.
 			 */
 			
 			mb.push(isNumber ? getTypeServices().getPrecision() : getTypeServices().getMaximumWidth());
 			mb.push(getTypeServices().getScale());
             mb.push(!sourceCTI.variableLength() ||
                     isNumber ||
                     assignmentSemantics);
 			mb.callMethod(VMOpcode.INVOKEINTERFACE, ClassName.VariableSizeDataValue,
 					"setWidth", "void", 3);
 
 		}
 	}
 
 	/**
 	 * Accept the visitor for all visitable children of this node.
 	 * 
 	 * @param v the visitor
 	 *
 	 * @exception StandardException on error
 	 */
     @Override
 	void acceptChildren(Visitor v)
 		throws StandardException
 	{
 		super.acceptChildren(v);
 
 		if (castOperand != null)
 		{
 			castOperand = (ValueNode)castOperand.accept(v);
 		}
 
         if (udtTargetName != null)
         {
             udtTargetName = (TableName) udtTargetName.accept(v);
         }
 	}
 
 	/** This method gets called by the parser to indiciate that this CAST node 
 	 * has been generated by the parser. This means that we should use the 
 	 * collation info of the current compilation schmea for this node's 
 	 * collation setting. If this method does not get called, then it means
 	 * that this CAST node has been an internally generated node and we should
 	 * not touch the collation of this CAST node because it has been already 
 	 * set correctly by the class that generated this CAST node. 
 	 */
 	void setForExternallyGeneratedCASTnode()
 	{
 		externallyGeneratedCastNode = true;
 	}
 
 	/** set this to be a dataTypeScalarFunction
 	 * 
 	 * @param b true to use function conversion rules
 	 */
 	void setForDataTypeFunction(boolean b)
 	{
 		forDataTypeFunction = b;
 	}
 
     /**
      * Set assignmentSemantics to true. Used by method calls for casting actual
      * arguments
      */
     void setAssignmentSemantics()
     {
         assignmentSemantics = true;
     }
 
     
 	/**
 	 * {@inheritDoc}
 	 * @throws StandardException 
 	 */
     boolean isEquivalent(ValueNode o) throws StandardException
 	{
         if (isSameNodeKind(o)) {
 			CastNode other = (CastNode)o;
 			return getTypeServices().equals(other.getTypeServices())
 				&& castOperand.isEquivalent(other.castOperand);
 		}
 
 		return false;
 	}
 
     /**
      * Set the target type name if this is a cast to a UDT.
      * @param name the name of the target type
      */
     void setTargetUDTName(TableName name) {
         udtTargetName = name;
     }
 }
