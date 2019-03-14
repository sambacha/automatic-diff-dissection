package add.features.detector.repairpatterns;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.github.gumtreediff.tree.ITree;

import add.entities.PatternInstance;
import add.entities.PropertyPair;
import add.entities.RepairPatterns;
import add.features.detector.spoon.RepairPatternUtils;
import add.features.detector.spoon.SpoonHelper;
import add.features.detector.spoon.filter.NullCheckFilter;
import add.main.Config;
import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.diff.operations.DeleteOperation;
import gumtree.spoon.diff.operations.InsertOperation;
import gumtree.spoon.diff.operations.Operation;
import gumtree.spoon.diff.operations.UpdateOperation;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.LineFilter;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * Created by tdurieux
 */
public class WrongReferenceDetector extends AbstractPatternDetector {

	public static final String WRONG_VAR_REF = "wrongVarRef";
	public static final String WRONG_METHOD_REF = "wrongMethodRef";
	public static final String UNWRAP_METHOD = "unwrapMethod";
	public static final String WRAPS_METHOD = "wrapsMethod";
	public static final String CONST_CHANGE = "constChange";
	public static final String WRAPS_IF_ELSE = "wrapsIfElse";
	public static final String UNWRAP_IF_ELSE = "unwrapIfElse";
	public static final String MISS_NULL_CHECK_N = "missNullCheckN";
	public static final String MISS_NULL_CHECK_P = "missNullCheckP";

	private Config config;

	public WrongReferenceDetector(Config config, List<Operation> operations) {
		super(operations);
		this.config = config;
	}

	@Override
	public void detect(RepairPatterns repairPatterns) {
		for (int i = 0; i < operations.size(); i++) {

			Operation operation = operations.get(i);
			// Operation operationInsert = null;
			if (operation instanceof DeleteOperation) {
				Operation operationDelete = operation;
				CtElement srcNode = operationDelete.getSrcNode();

//				if (srcNode instanceof CtVariableAccess || srcNode instanceof CtTypeAccess
//						|| srcNode instanceof CtInvocation) {
					if (srcNode instanceof CtVariableAccess 
							|| srcNode instanceof CtInvocation || srcNode instanceof CtConstructorCall) {
					if (srcNode.getMetadata("delete") != null) {
						CtElement statementParent = srcNode.getParent(CtStatement.class);
						if (statementParent.getMetadata("delete") == null) {
							// skip when it's a wrap with method call
							CtElement newElementReplacementOfTheVar = null;
							boolean wasVariableWrapped = false;
							
							for (int j = 0; j < operations.size(); j++) {
								Operation operation2 = operations.get(j);
								if (operation2 instanceof InsertOperation) {
									CtElement node2 = operation2.getSrcNode();
									// operationInsert = operation2;
									if (node2 instanceof CtInvocation || node2 instanceof CtConstructorCall) {
										if (((InsertOperation) operation2).getParent() != null) {
											if (srcNode.getParent() == ((InsertOperation) operation2).getParent()) {
												List<CtExpression> invocationArguments = new ArrayList<>();
												if (node2 instanceof CtInvocation) {
													invocationArguments = ((CtInvocation) node2).getArguments();
												}
												if (node2 instanceof CtConstructorCall) {
													invocationArguments = ((CtConstructorCall) node2).getArguments();
												}
												for (CtExpression ctExpression : invocationArguments) {
													if (srcNode instanceof CtVariableAccess
															&& ctExpression instanceof CtVariableAccess) {
														CtVariableAccess srcVariableAccess = (CtVariableAccess) srcNode;
														CtVariableAccess dstVariableAccess = (CtVariableAccess) ctExpression;
														if (srcVariableAccess.getVariable().getSimpleName().equals(
																dstVariableAccess.getVariable().getSimpleName())) {
															wasVariableWrapped = true;
														}
													} else {
														if (srcNode instanceof CtTypeAccess
																&& ctExpression instanceof CtTypeAccess) {
															CtTypeAccess srcTypeAccess = (CtTypeAccess) srcNode;
															CtTypeAccess dstTypeAccess = (CtTypeAccess) ctExpression;
															if (srcTypeAccess.getAccessedType().getSimpleName().equals(
																	dstTypeAccess.getAccessedType().getSimpleName())) {
																wasVariableWrapped = true;
															}
														}
													}
												}
											}
										}

									}
									// Save an inserted node with inside the same parent:
		
									if (srcNode.getParent() == ((InsertOperation) operation2).getParent()) {
										newElementReplacementOfTheVar = node2;
									}
								}
							}
							// EN Dskype
							if (!wasVariableWrapped) {

								CtElement susp = operationDelete.getSrcNode();
								CtElement patch = null;

								ITree parentRight = MappingAnalysis.getParentInRight(diff, operationDelete.getAction());
								if (parentRight != null) {
									patch = (CtElement) parentRight.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);
								}

								CtElement parentLine = MappingAnalysis.getParentLine(new LineFilter(), susp);
								ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(parentLine);

								// Let's create the metadata
								PropertyPair[] metadata = null;

								PropertyPair propertyOldElemet = new PropertyPair("Old", "Removed_"
										+ srcNode.getClass().getSimpleName().replace("Ct", "").replace("Impl", ""));

								String replaceElementType="";
								// if we have the element that has be inserted
								if (newElementReplacementOfTheVar != null) {
									PropertyPair propertyNewElement = new PropertyPair("New",
											"Added_" + newElementReplacementOfTheVar.getClass().getSimpleName()
													.replace("Ct", "").replace("Impl", ""));
									replaceElementType=newElementReplacementOfTheVar.getClass().getSimpleName()
											.replace("Ct", "").replace("Impl", "");

									metadata = new PropertyPair[] { propertyOldElemet, propertyNewElement };
								} else
									metadata = new PropertyPair[] { propertyOldElemet };

								Boolean whetherConsiderInitial=false;
								
								if(metadata.length==2) {
									if(replaceElementType.equals("Invocation")||replaceElementType.equals("VariableRead")
											||replaceElementType.equals("FieldRead")||replaceElementType.equals("ConstructorCall")
											||replaceElementType.equals("Literal")||replaceElementType.equals("FieldWrite")
											||replaceElementType.equals("VariableWrite")||replaceElementType.equals("TypeAccess"))
										whetherConsiderInitial=true;
								}
								// Case 1
								if(whetherConsiderInitial) {
								   if (srcNode instanceof CtInvocation) {
//									repairPatterns.incrementFeatureCounterInstance(WRONG_VAR_REF,
//											new PatternInstance(WRONG_METHOD_REF, operationDelete, patch, susp,
//													parentLine, lineTree, metadata));
									   
									   if(newElementReplacementOfTheVar instanceof CtInvocation &&  
											((CtInvocation)srcNode).getExecutable().getSimpleName().
											equals(((CtInvocation)newElementReplacementOfTheVar).getExecutable().getSimpleName())
											&&((CtInvocation) srcNode).getArguments().size()==((CtInvocation)newElementReplacementOfTheVar).getArguments().size())
									   { }
									   else repairPatterns.incrementFeatureCounterInstance(WRONG_METHOD_REF,
										new PatternInstance(WRONG_METHOD_REF, operationDelete, patch, susp,
												parentLine, lineTree, metadata));
								   
								       if(newElementReplacementOfTheVar instanceof CtInvocation) {
								    	    List<CtExpression> invocationArgumentsold = new ArrayList<>();
											if (srcNode instanceof CtInvocation) {
												invocationArgumentsold = ((CtInvocation) srcNode).getArguments();
											}
											
											List<CtExpression> invocationArgumentnew = new ArrayList<>();
											if (newElementReplacementOfTheVar instanceof CtInvocation) {
												invocationArgumentnew = ((CtInvocation) newElementReplacementOfTheVar).getArguments();
											}

											detectVarArgumentChange(invocationArgumentsold, invocationArgumentnew, repairPatterns, srcNode, newElementReplacementOfTheVar);
								       }
								    }
								  else  if (srcNode instanceof CtConstructorCall) {
								   
								   if(newElementReplacementOfTheVar instanceof CtConstructorCall &&  
										((CtConstructorCall)srcNode).getExecutable().getSimpleName().
										equals(((CtConstructorCall)newElementReplacementOfTheVar).getExecutable().getSimpleName())
										&&((CtConstructorCall) srcNode).getArguments().size()==((CtConstructorCall)newElementReplacementOfTheVar).getArguments().size())
								   { }
								   else repairPatterns.incrementFeatureCounterInstance(WRONG_METHOD_REF,
									new PatternInstance(WRONG_METHOD_REF, operationDelete, patch, susp,
											parentLine, lineTree, metadata));
							   
							       if(newElementReplacementOfTheVar instanceof CtConstructorCall) {
							    	    List<CtExpression> invocationArgumentsold = new ArrayList<>();
										if (srcNode instanceof CtConstructorCall) {
											invocationArgumentsold = ((CtConstructorCall) srcNode).getArguments();
										}
										
										List<CtExpression> invocationArgumentnew = new ArrayList<>();
										if (newElementReplacementOfTheVar instanceof CtConstructorCall) {
											invocationArgumentnew = ((CtConstructorCall) newElementReplacementOfTheVar).getArguments();
										}
											
										detectVarArgumentChange(invocationArgumentsold, invocationArgumentnew, repairPatterns, srcNode, newElementReplacementOfTheVar);
							       }
							    }
								  else
									  repairPatterns.incrementFeatureCounterInstance(WRONG_VAR_REF,
											new PatternInstance(WRONG_VAR_REF, operationDelete, patch, susp, parentLine,
													lineTree, metadata));
								}
							}
						}
					}
				} else {
					// Inside delete but node is Not access var

//					if (srcNode.getRoleInParent() == CtRole.ARGUMENT) {
//
//						CtElement susp = null;// operationDelete.getSrcNode();
//						susp = operationDelete.getSrcNode().getParent(CtInvocation.class);
//						if (susp == null)
//							susp = operationDelete.getSrcNode().getParent(CtConstructorCall.class);
//
//						CtElement patch = null;
//
//						CtElement parentLine = MappingAnalysis.getParentLine(new LineFilter(), susp);
//						ITree lineTree = (ITree) ((parentLine.getMetadata("tree") != null)
//								? parentLine.getMetadata("tree")
//								: parentLine.getMetadata("gtnode"));
//
//						ITree parentRight = MappingAnalysis.getParentInRight(diff, operationDelete.getAction());
//						if (parentRight != null) {
//							patch = (CtElement) parentRight.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);
//						}
//
//						repairPatterns.incrementFeatureCounterInstance(WRONG_METHOD_REF,
//								new PatternInstance(WRONG_METHOD_REF, operationDelete, patch, susp, parentLine,
//										lineTree,
//										//
//										new PropertyPair("Change", "ArgumentRemovement")
//
//								));
//
//					}
				}
			}
			/// UPDATE NODE
			if (operation instanceof UpdateOperation) {
				CtElement srcNode = operation.getSrcNode();
				CtElement dstNode = operation.getDstNode();

				if (dstNode.getParent().getMetadata("new") != null
						|| dstNode.getParent().getMetadata("isMoved") != null) {
					continue;
				}
				if (srcNode.getParent().getMetadata("new") != null
						|| srcNode.getParent().getMetadata("isMoved") != null) {
					continue;
				}
		//		if (srcNode instanceof CtVariableAccess || srcNode instanceof CtTypeAccess) {// let CtTypeAccess be constant related changes
				if (srcNode instanceof CtVariableAccess) {
					if (operation.getDstNode() instanceof CtVariableAccess
							|| operation.getDstNode() instanceof CtTypeAccess
							|| operation.getDstNode() instanceof CtInvocation) {
						// repairPatterns.incrementFeatureCounter(WRONG_VAR_REF, operationUpdate);

						CtElement susp = operation.getSrcNode();
						CtElement patch = null;

						CtElement parentLine = MappingAnalysis.getParentLine(new LineFilter(), susp);
						ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(parentLine);

						ITree parentRight = MappingAnalysis.getParentInRight(diff, operation.getAction());
						if (parentRight != null) {
							patch = (CtElement) parentRight.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);
						}

						repairPatterns.incrementFeatureCounterInstance(WRONG_VAR_REF, new PatternInstance(WRONG_VAR_REF,
								operation, patch, susp, parentLine, lineTree, new PropertyPair("Change", "Update_"
										+ srcNode.getClass().getSimpleName().replace("Ct", "").replace("Impl", ""))));
					}
				}
				if (!(srcNode instanceof CtInvocation) && !(srcNode instanceof CtConstructorCall)) {
					continue;
				}
				// Update a method invocation
				if (dstNode instanceof CtInvocation || dstNode instanceof CtConstructorCall) {
					boolean wasMethodDefUpdated = false;

					String srcCallMethodName;
					CtTargetedExpression srcInvocation = (CtTargetedExpression) srcNode;
					List<CtTypeReference> srcCallArguments;
					List<CtTypeReference> srcCallArgumentsFromExec;
					List<CtTypeReference> srcCallRealArguments;
					if (srcNode instanceof CtInvocation) {
						srcCallMethodName = ((CtInvocation) srcNode).getExecutable().getSimpleName();
						srcCallArgumentsFromExec = ((CtInvocation) srcNode).getExecutable().getParameters();
						srcCallArguments = ((CtInvocation) srcNode).getActualTypeArguments();
						srcCallRealArguments = ((CtInvocation) srcNode).getArguments();
					} else {
						srcCallMethodName = ((CtConstructorCall) srcNode).getExecutable().getSimpleName();
						srcCallArguments = ((CtConstructorCall) srcNode).getActualTypeArguments();
						srcCallRealArguments = ((CtConstructorCall) srcNode).getArguments();
						srcCallArgumentsFromExec = ((CtConstructorCall) srcNode).getExecutable().getParameters();

					}
					String dstCallMethodName;
					CtElement dst = dstNode;
					List<CtTypeReference> dstCallArguments;
					List<CtTypeReference> dstCallArgumentsFromExec;
					List<CtTypeReference> dstCallRealArguments;

					CtTargetedExpression dstInvocation = null;
					if (dstNode instanceof CtInvocation) {
						dstCallMethodName = ((CtInvocation) dstNode).getExecutable().getSimpleName();
						dstCallArguments = ((CtInvocation) dstNode).getActualTypeArguments();
						dstCallArgumentsFromExec = ((CtInvocation) dstNode).getExecutable().getParameters();
						dstInvocation = (CtTargetedExpression) dstNode;
						dstCallRealArguments = ((CtInvocation) dstNode).getArguments();

					} else {
						dstCallMethodName = ((CtConstructorCall) dstNode).getExecutable().getSimpleName();
						dstCallArguments = ((CtConstructorCall) dstNode).getActualTypeArguments();
						dstCallRealArguments = ((CtConstructorCall) dstNode).getArguments();
						dstCallArgumentsFromExec = ((CtConstructorCall) dstNode).getExecutable().getParameters();
						dstInvocation = (CtTargetedExpression) dstNode;
					}

					for (Operation operation2 : operations) {
						if (operation2 instanceof InsertOperation) {
							CtElement insertedNode = operation2.getSrcNode();
							// See whether a method signature was modified
							if (insertedNode instanceof CtParameter) {
								CtElement ctElement = ((InsertOperation) operation2).getParent();
								if (ctElement instanceof CtMethod) {
									CtMethod oldMethod = (CtMethod) ctElement;
									CtMethod newMethod = insertedNode.getParent(CtMethod.class);

									if (oldMethod.getSimpleName().equals(srcCallMethodName)
											&& newMethod.getSimpleName().equals(dstCallMethodName)) {
										boolean oldParEquals = true;
										List<CtParameter> oldMethodPars = oldMethod.getParameters();
										for (int k = 0; k < oldMethodPars.size(); k++) {
											CtTypeReference methodParType = oldMethodPars.get(k).getType();
											if (k < srcCallArguments.size()) {
												CtTypeReference methodCallArgType = srcCallArguments.get(k);
												if (!methodParType.getQualifiedName()
														.equals(methodCallArgType.getQualifiedName())) {
													oldParEquals = false;
													break;
												}
											}
										}
										if (oldParEquals) {
											boolean newParEquals = true;
											List<CtParameter> newMethodPars = newMethod.getParameters();
											for (int k = 0; k < newMethodPars.size(); k++) {
												CtTypeReference methodParType = newMethodPars.get(k).getType();
												if (k < dstCallArguments.size()) {
													CtTypeReference methodCallArgType = dstCallArguments.get(k);
													if (!methodParType.getQualifiedName()
															.equals(methodCallArgType.getQualifiedName())) {
														newParEquals = false;
														break;
													}
												}
											} // not sure
											if (newParEquals) {
												wasMethodDefUpdated = true;
											}
										}
									}
								}
								
								if (ctElement instanceof CtConstructor) {
									CtConstructor oldConstructor= (CtConstructor) ctElement;
									CtConstructor newConstructor= insertedNode.getParent(CtConstructor.class);

									if (oldConstructor.getSimpleName().equals(srcCallMethodName)
											&& newConstructor.getSimpleName().equals(dstCallMethodName)) {
										boolean oldParEquals = true;
										List<CtParameter> oldMethodPars = oldConstructor.getParameters();
										for (int k = 0; k < oldMethodPars.size(); k++) {
											CtTypeReference methodParType = oldMethodPars.get(k).getType();
											if (k < srcCallArguments.size()) {
												CtTypeReference methodCallArgType = srcCallArguments.get(k);
												if (!methodParType.getQualifiedName()
														.equals(methodCallArgType.getQualifiedName())) {
													oldParEquals = false;
													break;
												}
											}
										}
										if (oldParEquals) {
											boolean newParEquals = true;
											List<CtParameter> newMethodPars = newConstructor.getParameters();
											for (int k = 0; k < newMethodPars.size(); k++) {
												CtTypeReference methodParType = newMethodPars.get(k).getType();
												if (k < dstCallArguments.size()) {
													CtTypeReference methodCallArgType = dstCallArguments.get(k);
													if (!methodParType.getQualifiedName()
															.equals(methodCallArgType.getQualifiedName())) {
														newParEquals = false;
														break;
													}
												}
											} // not sure
											if (newParEquals) {
												wasMethodDefUpdated = true;
											}
										}
									}
								}
							}
						}
						
						if (operation2 instanceof DeleteOperation) {
							CtElement deleteNode = operation2.getSrcNode();
							// See whether a method signature was modified
							if (deleteNode instanceof CtParameter) {
								CtElement ctElement = deleteNode.getParent(CtMethod.class);
								if (ctElement instanceof CtMethod) {
									CtMethod oldMethod = (CtMethod) ctElement;
									CtMethod newMethod;
									
									ITree rightTreemethod= MappingAnalysis.getRightFromLeftNodeMapped(diff, oldMethod); 

									if(rightTreemethod==null)
										newMethod=null;
									else newMethod = (CtMethod) rightTreemethod.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

									if (oldMethod.getSimpleName().equals(srcCallMethodName)
											&& newMethod.getSimpleName().equals(dstCallMethodName)) {
										boolean oldParEquals = true;
										List<CtParameter> oldMethodPars = oldMethod.getParameters();
										for (int k = 0; k < oldMethodPars.size(); k++) {
											CtTypeReference methodParType = oldMethodPars.get(k).getType();
											if (k < srcCallArguments.size()) {
												CtTypeReference methodCallArgType = srcCallArguments.get(k);
												if (!methodParType.getQualifiedName()
														.equals(methodCallArgType.getQualifiedName())) {
													oldParEquals = false;
													break;
												}
											}
										}
										if (oldParEquals) {
											boolean newParEquals = true;
											List<CtParameter> newMethodPars = newMethod.getParameters();
											for (int k = 0; k < newMethodPars.size(); k++) {
												CtTypeReference methodParType = newMethodPars.get(k).getType();
												if (k < dstCallArguments.size()) {
													CtTypeReference methodCallArgType = dstCallArguments.get(k);
													if (!methodParType.getQualifiedName()
															.equals(methodCallArgType.getQualifiedName())) {
														newParEquals = false;
														break;
													}
												}
											} // not sure
											if (newParEquals) {
												wasMethodDefUpdated = true;
											}
										}
									}
								}
								
								ctElement = deleteNode.getParent(CtConstructor.class);
								if (ctElement instanceof CtConstructor) {
									CtConstructor oldConstructor = (CtConstructor) ctElement;
									CtConstructor newConstructor;
									
									ITree rightTreemethod= MappingAnalysis.getRightFromLeftNodeMapped(diff, oldConstructor); 

									if(rightTreemethod==null)
										newConstructor=null;
									else newConstructor = (CtConstructor) rightTreemethod.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

									if (oldConstructor.getSimpleName().equals(srcCallMethodName)
											&& newConstructor.getSimpleName().equals(dstCallMethodName)) {
										boolean oldParEquals = true;
										List<CtParameter> oldMethodPars = oldConstructor.getParameters();
										for (int k = 0; k < oldMethodPars.size(); k++) {
											CtTypeReference methodParType = oldMethodPars.get(k).getType();
											if (k < srcCallArguments.size()) {
												CtTypeReference methodCallArgType = srcCallArguments.get(k);
												if (!methodParType.getQualifiedName()
														.equals(methodCallArgType.getQualifiedName())) {
													oldParEquals = false;
													break;
												}
											}
										}
										if (oldParEquals) {
											boolean newParEquals = true;
											List<CtParameter> newMethodPars = newConstructor.getParameters();
											for (int k = 0; k < newMethodPars.size(); k++) {
												CtTypeReference methodParType = newMethodPars.get(k).getType();
												if (k < dstCallArguments.size()) {
													CtTypeReference methodCallArgType = dstCallArguments.get(k);
													if (!methodParType.getQualifiedName()
															.equals(methodCallArgType.getQualifiedName())) {
														newParEquals = false;
														break;
													}
												}
											} // not sure
											if (newParEquals) {
												wasMethodDefUpdated = true;
											}
										}
									}
								}
							}
						}
					}

					if (!wasMethodDefUpdated) {
						// Let's check if the Target of the expression are the same, otherwise we
						// discard this case
						if (srcInvocation != null && dstInvocation != null && srcInvocation.getTarget() != null
								&& dstInvocation.getTarget() != null
								&& !srcInvocation.getTarget().equals(dstInvocation.getTarget())) {
							continue;
						}

						CtElement parentLine = MappingAnalysis.getParentLine(new LineFilter(), srcInvocation);
						ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(parentLine);

						if (!srcCallMethodName.equals(dstCallMethodName)) {
							// repairPatterns.incrementFeatureCounter(WRONG_METHOD_REF, operation);
							repairPatterns.incrementFeatureCounterInstance(WRONG_METHOD_REF,
									new PatternInstance(WRONG_METHOD_REF, operation, dst, srcInvocation, parentLine,
											lineTree,
											//
											new PropertyPair("Change", "differentMethodName")));

						} else {
							if (
							// srcCallArguments.size() != dstCallArguments.size()
							// horrible workaround
							// || srcCallArgumentsFromExec.size() != dstCallArgumentsFromExec.size()
							// ||
							srcCallRealArguments.size() != dstCallRealArguments.size()

							) {
								// repairPatterns.incrementFeatureCounter(WRONG_METHOD_REF, operation);
								repairPatterns.incrementFeatureCounterInstance(WRONG_METHOD_REF,
										new PatternInstance(WRONG_METHOD_REF, operation, dst, srcInvocation, parentLine,
												lineTree,
												//
												new PropertyPair("Change", "SameNamedifferentArgument")));
							}
						}
						
						List<CtExpression> invocationArgumentsold = new ArrayList<>();
						if (srcNode instanceof CtInvocation) {
							invocationArgumentsold = ((CtInvocation) srcNode).getArguments();
						}
						if (srcNode instanceof CtConstructorCall) {
							invocationArgumentsold = ((CtConstructorCall) srcNode).getArguments();
						}
						
						List<CtExpression> invocationArgumentnew = new ArrayList<>();
						if (dstNode instanceof CtInvocation) {
							invocationArgumentnew = ((CtInvocation) dstNode).getArguments();
						}
						if (dstNode instanceof CtConstructorCall) {
							invocationArgumentnew = ((CtConstructorCall) dstNode).getArguments();
						}
						
						detectVarArgumentChange(invocationArgumentsold, invocationArgumentnew, repairPatterns, srcNode, dstNode);
					}
				}
			}
		}
	}
	
	public void detectVarArgumentChange(List<CtExpression> invocationArgumentsold, List<CtExpression> invocationArgumentsnew, 
			RepairPatterns repairPatterns, CtElement source, CtElement destination) {
		
		for (Operation operation : diff.getAllOperations()) {
			this.detectWrapsMethod(invocationArgumentsold, invocationArgumentsnew, operation, repairPatterns, source, destination);
			this.detecfWrapIfChange(invocationArgumentsold, invocationArgumentsnew, operation, repairPatterns, source, destination);
		}
		
		this.detectConstantChange(invocationArgumentsold,invocationArgumentsnew,repairPatterns, source, destination);
		this.detectNullCheckChange(invocationArgumentsold,invocationArgumentsnew,repairPatterns, source, destination);
		
		List<CtElement> alreadyconsidered = new ArrayList<CtElement>();
		for (int i = 0; i < diff.getAllOperations().size(); i++) {

			Operation operation = diff.getAllOperations().get(i);
			// Operation operationInsert = null;
			if (operation instanceof DeleteOperation) {
				Operation operationDelete = operation;
				CtElement srcNode = operationDelete.getSrcNode();
				if ((srcNode instanceof CtVariableAccess || srcNode instanceof CtInvocation ||
						srcNode instanceof CtConstructorCall) && invocationArgumentsold.contains(srcNode)
						&&  srcNode.getParent()==source) { 
						// skip when it's a wrap with method call
					CtElement newElementReplacementOfTheVar = null;
					boolean wasVariableWrapped = false;
					for (int j = 0; j < diff.getAllOperations().size(); j++) {
						Operation operation2 = diff.getAllOperations().get(j);
						if (operation2 instanceof InsertOperation) {
							CtElement node2 = operation2.getSrcNode();
							// operationInsert = operation2;
							if ((node2 instanceof CtInvocation || node2 instanceof CtConstructorCall) && 
									invocationArgumentsnew.contains(node2) && node2.getParent()==destination) {

									List<CtExpression> invocationArguments = new ArrayList<>();
									if (node2 instanceof CtInvocation) {
										 invocationArguments = ((CtInvocation) node2).getArguments();
									}
									if (node2 instanceof CtConstructorCall) {
										 invocationArguments = ((CtConstructorCall) node2).getArguments();
									}
									for (CtExpression ctExpression : invocationArguments) {
										if (srcNode instanceof CtVariableAccess && ctExpression instanceof CtVariableAccess) {
											 CtVariableAccess srcVariableAccess = (CtVariableAccess) srcNode;
											 CtVariableAccess dstVariableAccess = (CtVariableAccess) ctExpression;
											 if (srcVariableAccess.getVariable().getSimpleName().equals(
													dstVariableAccess.getVariable().getSimpleName())) {
														wasVariableWrapped = true;
											}
										} 
									}		
							  }
						 }
				    }
					
					for (int j = 0; j < diff.getAllOperations().size(); j++) {
						Operation operation2 = diff.getAllOperations().get(j);
						if (operation2 instanceof InsertOperation) {
							CtElement node2 = operation2.getSrcNode();
									
							if (invocationArgumentsnew.contains(node2) && node2.getParent()==destination &&
									!alreadyconsidered.contains(node2)) {
									newElementReplacementOfTheVar = node2;
									alreadyconsidered.add(node2);
									break;
							}
						 }
				    }
							
					if (!wasVariableWrapped) {

						CtElement susp = operationDelete.getSrcNode();
						CtElement patch = null;

						ITree parentRight = MappingAnalysis.getParentInRight(diff, operationDelete.getAction());
						if (parentRight != null) {
							patch = (CtElement) parentRight.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);
						}

						CtElement parentLine = MappingAnalysis.getParentLine(new LineFilter(), susp);
						ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(parentLine);

								// Let's create the metadata
						PropertyPair[] metadata = null;
						PropertyPair propertyOldElemet = new PropertyPair("Old", "Removed_"
										+ srcNode.getClass().getSimpleName().replace("Ct", "").replace("Impl", ""));

						String replaceElementType="";
								// if we have the element that has be inserted
						if (newElementReplacementOfTheVar != null) {
							 PropertyPair propertyNewElement = new PropertyPair("New",
										"Added_" + newElementReplacementOfTheVar.getClass().getSimpleName()
													.replace("Ct", "").replace("Impl", ""));
							 replaceElementType=newElementReplacementOfTheVar.getClass().getSimpleName()
											.replace("Ct", "").replace("Impl", "");

							 metadata = new PropertyPair[] { propertyOldElemet, propertyNewElement };
						 } else
							 metadata = new PropertyPair[] { propertyOldElemet };

						 Boolean whetherConsiderInitial=false;
								
						 if(metadata.length==2) {
							if(replaceElementType.equals("Invocation")||replaceElementType.equals("VariableRead")
											||replaceElementType.equals("FieldRead")||replaceElementType.equals("ConstructorCall")
											||replaceElementType.equals("Literal")||replaceElementType.equals("FieldWrite")
											||replaceElementType.equals("VariableWrite")||replaceElementType.equals("TypeAccess"))
								whetherConsiderInitial=true;
						  }
								
						  if(whetherConsiderInitial) {
							   if (srcNode instanceof CtInvocation) {
							   
								   if(newElementReplacementOfTheVar instanceof CtInvocation &&  
										((CtInvocation)srcNode).getExecutable().getSimpleName().
										equals(((CtInvocation)newElementReplacementOfTheVar).getExecutable().getSimpleName())
										&&((CtInvocation) srcNode).getArguments().size()==((CtInvocation)newElementReplacementOfTheVar).getArguments().size())
								   { }
								   else repairPatterns.incrementFeatureCounterInstance(WRONG_METHOD_REF,
									new PatternInstance(WRONG_METHOD_REF, operationDelete, patch, susp,
											parentLine, lineTree, metadata));
							    }
							  else  if (srcNode instanceof CtConstructorCall) {
							   
							   if(newElementReplacementOfTheVar instanceof CtConstructorCall &&  
									((CtConstructorCall)srcNode).getExecutable().getSimpleName().
									equals(((CtConstructorCall)newElementReplacementOfTheVar).getExecutable().getSimpleName())
									&&((CtConstructorCall) srcNode).getArguments().size()==((CtConstructorCall)newElementReplacementOfTheVar).getArguments().size())
							   { }
							   else repairPatterns.incrementFeatureCounterInstance(WRONG_METHOD_REF,
								new PatternInstance(WRONG_METHOD_REF, operationDelete, patch, susp,
										parentLine, lineTree, metadata));
						   
						      }
							  else
								  repairPatterns.incrementFeatureCounterInstance(WRONG_VAR_REF,
										new PatternInstance(WRONG_VAR_REF, operationDelete, patch, susp, parentLine,
												lineTree, metadata));
						  }
					  }
				  } 
			}
			/// UPDATE NODE
			if (operation instanceof UpdateOperation) {
				CtElement srcNode = operation.getSrcNode();
				CtElement dstNode = operation.getDstNode();

				if(!invocationArgumentsold.contains(srcNode) || srcNode.getParent()!= source
						|| !invocationArgumentsnew.contains(dstNode) || dstNode.getParent()!=destination)
					continue;
				
				if (srcNode instanceof CtVariableAccess) {
					if (operation.getDstNode() instanceof CtVariableAccess
							|| operation.getDstNode() instanceof CtTypeAccess
							|| operation.getDstNode() instanceof CtInvocation) {
						// repairPatterns.incrementFeatureCounter(WRONG_VAR_REF, operationUpdate);

						CtElement susp = operation.getSrcNode();
						CtElement patch = null;

						CtElement parentLine = MappingAnalysis.getParentLine(new LineFilter(), susp);
						ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(parentLine);

						ITree parentRight = MappingAnalysis.getParentInRight(diff, operation.getAction());
						if (parentRight != null) {
							patch = (CtElement) parentRight.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);
						}

						repairPatterns.incrementFeatureCounterInstance(WRONG_VAR_REF, new PatternInstance(WRONG_VAR_REF,
								operation, patch, susp, parentLine, lineTree, new PropertyPair("Change", "Update_"
										+ srcNode.getClass().getSimpleName().replace("Ct", "").replace("Impl", ""))));
					}
				}
				if (!(srcNode instanceof CtInvocation) && !(srcNode instanceof CtConstructorCall)) {
					continue;
				}
				// Update a method invocation
				if (dstNode instanceof CtInvocation || dstNode instanceof CtConstructorCall) {
					boolean wasMethodDefUpdated = false;

					String srcCallMethodName;
					CtTargetedExpression srcInvocation = (CtTargetedExpression) srcNode;
					List<CtTypeReference> srcCallArguments;
					List<CtTypeReference> srcCallArgumentsFromExec;
					List<CtTypeReference> srcCallRealArguments;
					if (srcNode instanceof CtInvocation) {
						srcCallMethodName = ((CtInvocation) srcNode).getExecutable().getSimpleName();
						srcCallArgumentsFromExec = ((CtInvocation) srcNode).getExecutable().getParameters();
						srcCallArguments = ((CtInvocation) srcNode).getActualTypeArguments();
						srcCallRealArguments = ((CtInvocation) srcNode).getArguments();
					} else {
						srcCallMethodName = ((CtConstructorCall) srcNode).getExecutable().getSimpleName();
						srcCallArguments = ((CtConstructorCall) srcNode).getActualTypeArguments();
						srcCallRealArguments = ((CtConstructorCall) srcNode).getArguments();
						srcCallArgumentsFromExec = ((CtConstructorCall) srcNode).getExecutable().getParameters();

					}
					String dstCallMethodName;
					CtElement dst = dstNode;
					List<CtTypeReference> dstCallArguments;
					List<CtTypeReference> dstCallArgumentsFromExec;
					List<CtTypeReference> dstCallRealArguments;

					CtTargetedExpression dstInvocation = null;
					if (dstNode instanceof CtInvocation) {
						dstCallMethodName = ((CtInvocation) dstNode).getExecutable().getSimpleName();
						dstCallArguments = ((CtInvocation) dstNode).getActualTypeArguments();
						dstCallArgumentsFromExec = ((CtInvocation) dstNode).getExecutable().getParameters();
						dstInvocation = (CtTargetedExpression) dstNode;
						dstCallRealArguments = ((CtInvocation) dstNode).getArguments();

					} else {
						dstCallMethodName = ((CtConstructorCall) dstNode).getExecutable().getSimpleName();
						dstCallArguments = ((CtConstructorCall) dstNode).getActualTypeArguments();
						dstCallRealArguments = ((CtConstructorCall) dstNode).getArguments();
						dstCallArgumentsFromExec = ((CtConstructorCall) dstNode).getExecutable().getParameters();
						dstInvocation = (CtTargetedExpression) dstNode;
					}

					for (Operation operation2 : operations) {
						if (operation2 instanceof InsertOperation) {
							CtElement insertedNode = operation2.getSrcNode();
							// See whether a method signature was modified
							if (insertedNode instanceof CtParameter) {
								CtElement ctElement = ((InsertOperation) operation2).getParent();
								if (ctElement instanceof CtMethod) {
									CtMethod oldMethod = (CtMethod) ctElement;
									CtMethod newMethod = insertedNode.getParent(CtMethod.class);

									if (oldMethod.getSimpleName().equals(srcCallMethodName)
											&& newMethod.getSimpleName().equals(dstCallMethodName)) {
										boolean oldParEquals = true;
										List<CtParameter> oldMethodPars = oldMethod.getParameters();
										for (int k = 0; k < oldMethodPars.size(); k++) {
											CtTypeReference methodParType = oldMethodPars.get(k).getType();
											if (k < srcCallArguments.size()) {
												CtTypeReference methodCallArgType = srcCallArguments.get(k);
												if (!methodParType.getQualifiedName()
														.equals(methodCallArgType.getQualifiedName())) {
													oldParEquals = false;
													break;
												}
											}
										}
										if (oldParEquals) {
											boolean newParEquals = true;
											List<CtParameter> newMethodPars = newMethod.getParameters();
											for (int k = 0; k < newMethodPars.size(); k++) {
												CtTypeReference methodParType = newMethodPars.get(k).getType();
												if (k < dstCallArguments.size()) {
													CtTypeReference methodCallArgType = dstCallArguments.get(k);
													if (!methodParType.getQualifiedName()
															.equals(methodCallArgType.getQualifiedName())) {
														newParEquals = false;
														break;
													}
												}
											} // not sure
											if (newParEquals) {
												wasMethodDefUpdated = true;
											}
										}
									}
								}
								
								if (ctElement instanceof CtConstructor) {
									CtConstructor oldConstructor= (CtConstructor) ctElement;
									CtConstructor newConstructor= insertedNode.getParent(CtConstructor.class);

									if (oldConstructor.getSimpleName().equals(srcCallMethodName)
											&& newConstructor.getSimpleName().equals(dstCallMethodName)) {
										boolean oldParEquals = true;
										List<CtParameter> oldMethodPars = oldConstructor.getParameters();
										for (int k = 0; k < oldMethodPars.size(); k++) {
											CtTypeReference methodParType = oldMethodPars.get(k).getType();
											if (k < srcCallArguments.size()) {
												CtTypeReference methodCallArgType = srcCallArguments.get(k);
												if (!methodParType.getQualifiedName()
														.equals(methodCallArgType.getQualifiedName())) {
													oldParEquals = false;
													break;
												}
											}
										}
										if (oldParEquals) {
											boolean newParEquals = true;
											List<CtParameter> newMethodPars = newConstructor.getParameters();
											for (int k = 0; k < newMethodPars.size(); k++) {
												CtTypeReference methodParType = newMethodPars.get(k).getType();
												if (k < dstCallArguments.size()) {
													CtTypeReference methodCallArgType = dstCallArguments.get(k);
													if (!methodParType.getQualifiedName()
															.equals(methodCallArgType.getQualifiedName())) {
														newParEquals = false;
														break;
													}
												}
											} // not sure
											if (newParEquals) {
												wasMethodDefUpdated = true;
											}
										}
									}
								}
							}
						}					

						if (operation2 instanceof DeleteOperation) {
							CtElement deleteNode = operation2.getSrcNode();
							// See whether a method signature was modified
							if (deleteNode instanceof CtParameter) {
								CtElement ctElement = deleteNode.getParent(CtMethod.class);
								if (ctElement instanceof CtMethod) {
									CtMethod oldMethod = (CtMethod) ctElement;
									CtMethod newMethod;
									
									ITree rightTreemethod= MappingAnalysis.getRightFromLeftNodeMapped(diff, oldMethod); 

									if(rightTreemethod==null)
										continue;
									else newMethod = (CtMethod) rightTreemethod.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

									if (oldMethod.getSimpleName().equals(srcCallMethodName)
											&& newMethod.getSimpleName().equals(dstCallMethodName)) {
										boolean oldParEquals = true;
										List<CtParameter> oldMethodPars = oldMethod.getParameters();
										for (int k = 0; k < oldMethodPars.size(); k++) {
											CtTypeReference methodParType = oldMethodPars.get(k).getType();
											if (k < srcCallArguments.size()) {
												CtTypeReference methodCallArgType = srcCallArguments.get(k);
												if (!methodParType.getQualifiedName()
														.equals(methodCallArgType.getQualifiedName())) {
													oldParEquals = false;
													break;
												}
											}
										}
										if (oldParEquals) {
											boolean newParEquals = true;
											List<CtParameter> newMethodPars = newMethod.getParameters();
											for (int k = 0; k < newMethodPars.size(); k++) {
												CtTypeReference methodParType = newMethodPars.get(k).getType();
												if (k < dstCallArguments.size()) {
													CtTypeReference methodCallArgType = dstCallArguments.get(k);
													if (!methodParType.getQualifiedName()
															.equals(methodCallArgType.getQualifiedName())) {
														newParEquals = false;
														break;
													}
												}
											} // not sure
											if (newParEquals) {
												wasMethodDefUpdated = true;
											}
										}
									}
								}
								
								ctElement = deleteNode.getParent(CtConstructor.class);
								if (ctElement instanceof CtConstructor) {
									CtConstructor oldConstructor = (CtConstructor) ctElement;
									CtConstructor newConstructor;
									
									ITree rightTreemethod= MappingAnalysis.getRightFromLeftNodeMapped(diff, oldConstructor); 

									if(rightTreemethod==null)
										newConstructor=null;
									else newConstructor = (CtConstructor) rightTreemethod.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

									if (oldConstructor.getSimpleName().equals(srcCallMethodName)
											&& newConstructor.getSimpleName().equals(dstCallMethodName)) {
										boolean oldParEquals = true;
										List<CtParameter> oldMethodPars = oldConstructor.getParameters();
										for (int k = 0; k < oldMethodPars.size(); k++) {
											CtTypeReference methodParType = oldMethodPars.get(k).getType();
											if (k < srcCallArguments.size()) {
												CtTypeReference methodCallArgType = srcCallArguments.get(k);
												if (!methodParType.getQualifiedName()
														.equals(methodCallArgType.getQualifiedName())) {
													oldParEquals = false;
													break;
												}
											}
										}
										if (oldParEquals) {
											boolean newParEquals = true;
											List<CtParameter> newMethodPars = newConstructor.getParameters();
											for (int k = 0; k < newMethodPars.size(); k++) {
												CtTypeReference methodParType = newMethodPars.get(k).getType();
												if (k < dstCallArguments.size()) {
													CtTypeReference methodCallArgType = dstCallArguments.get(k);
													if (!methodParType.getQualifiedName()
															.equals(methodCallArgType.getQualifiedName())) {
														newParEquals = false;
														break;
													}
												}
											} // not sure
											if (newParEquals) {
												wasMethodDefUpdated = true;
											}
										}
									}
								}
							}
						}
					}

					if (!wasMethodDefUpdated) {

						// Let's check if the Target of the expression are the same, otherwise we
						// discard this case
						if (srcInvocation != null && dstInvocation != null && srcInvocation.getTarget() != null
								&& dstInvocation.getTarget() != null
								&& !srcInvocation.getTarget().equals(dstInvocation.getTarget())) {
							continue;
						}

						CtElement parentLine = MappingAnalysis.getParentLine(new LineFilter(), srcInvocation);
						ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(parentLine);

						if (!srcCallMethodName.equals(dstCallMethodName)) {
							repairPatterns.incrementFeatureCounterInstance(WRONG_METHOD_REF,
									new PatternInstance(WRONG_METHOD_REF, operation, dst, srcInvocation, parentLine,
											lineTree, new PropertyPair("Change", "differentMethodName")));
						} else {
							if (srcCallRealArguments.size() != dstCallRealArguments.size()) {
							   repairPatterns.incrementFeatureCounterInstance(WRONG_METHOD_REF,
										new PatternInstance(WRONG_METHOD_REF, operation, dst, srcInvocation, parentLine,
											lineTree, new PropertyPair("Change", "SameNamedifferentArgument")));
							}
						}
					}
				}
			}
		}
	}
	
	private void detectWrapsMethod(List<CtExpression> invocationArgumentsold, List<CtExpression> invocationArgumentsnew, 
			Operation operation, RepairPatterns repairPatterns, CtElement source, CtElement destination) {
		
		if (operation.getSrcNode() instanceof CtInvocation) {
			if (operation instanceof InsertOperation) {
				CtInvocation ctInvocation = (CtInvocation) operation.getSrcNode();
				List<CtExpression> invocationArguments = ctInvocation.getArguments();
				if(invocationArgumentsnew.contains(ctInvocation) && ctInvocation.getParent()==destination) {
				
				  for (Operation operation2 : diff.getAllOperations()) {
					 if (operation2 instanceof DeleteOperation) {
						CtElement elementRemoved = operation2.getSrcNode();
						
						if (elementRemoved instanceof CtVariableRead && invocationArgumentsold.contains(elementRemoved)
								&& elementRemoved.getParent()==source) {

							if (invocationArguments.contains(elementRemoved)) {
								CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(), elementRemoved);
								ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(lineP);
								repairPatterns.incrementFeatureCounterInstance(WRAPS_METHOD,
										new PatternInstance(WRAPS_METHOD, operation, ctInvocation, elementRemoved,
												lineP, lineTree, new PropertyPair("Old", "VarRead"),
												new PropertyPair("New", "Invocation")));
							}
						}
					 }
				  }

				 for(CtExpression ctExpression : invocationArguments) {
					if (ctExpression.getMetadata("isMoved") != null) {
						// Operation is an Insert
						// TODO:
						List<CtElement> suspLeft = MappingAnalysis.getTreeLeftMovedFromRight(diff, ctInvocation);
						if (suspLeft == null || suspLeft.isEmpty())
							return;

						CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(), suspLeft.get(0));
						ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(lineP);
						
						if(RepairPatternUtils.getIsInvocationInStatemnt(diff, lineP, ctInvocation) &&
							!(RepairPatternUtils.getElementInOld(diff, ctExpression).getParent() instanceof CtConstructorCall)
							&& !(RepairPatternUtils.getElementInOld(diff, ctExpression).getParent() instanceof CtInvocation) &&
							invocationArgumentsold.contains(ctExpression) && ctExpression.getParent()==source)
								
						   repairPatterns.incrementFeatureCounterInstance(WRAPS_METHOD,
								new PatternInstance(WRAPS_METHOD, operation, ctInvocation, ctExpression, lineP,
										lineTree, new PropertyPair("Old", "MovedExpression"),
										new PropertyPair("New", "Invocation")));
					}
				}
			  }
			} else {
				if (operation instanceof DeleteOperation) {
					CtInvocation ctInvocation = (CtInvocation) operation.getSrcNode();
					CtStatement statementParent = ctInvocation.getParent(new TypeFilter<>(CtStatement.class));
					
					if(invocationArgumentsold.contains(ctInvocation) && ctInvocation.getParent()==source)
					  if (statementParent.getMetadata("delete") == null) {
						List<CtExpression> invocationArguments = ctInvocation.getArguments();

						for (CtExpression ctExpression : invocationArguments) {
							if (ctExpression.getMetadata("isMoved") != null
									&& ctExpression.getMetadata("movingSrc") != null) {

								if(RepairPatternUtils.getIsMovedExpressionInStatemnt(diff, statementParent, ctExpression) &&
										invocationArgumentsnew.contains(ctExpression)&& ctExpression.getParent()==destination)
								{
								   CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(),
										operation.getSrcNode());
								   ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(lineP);

								   repairPatterns.incrementFeatureCounterInstance(UNWRAP_METHOD, new PatternInstance(
										UNWRAP_METHOD, operation, statementParent, ctInvocation, lineP, lineTree));
								}
							}
						}
					}
				}
			}
		}
		
		if (operation.getSrcNode() instanceof CtConstructorCall) {
			if (operation instanceof InsertOperation) {
				CtConstructorCall ctInvocation = (CtConstructorCall) operation.getSrcNode();
				List<CtExpression> invocationArguments = ctInvocation.getArguments();
				if(invocationArgumentsnew.contains(ctInvocation) && ctInvocation.getParent()==destination) {
				  for (Operation operation2 : diff.getAllOperations()) {
					 if (operation2 instanceof DeleteOperation) {
						CtElement elementRemoved = operation2.getSrcNode();
						
						if (elementRemoved instanceof CtVariableRead && invocationArgumentsold.contains(elementRemoved)
								&& elementRemoved.getParent()==source) {

							if (invocationArguments.contains(elementRemoved)) {
								CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(), elementRemoved);
								ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(lineP);
								repairPatterns.incrementFeatureCounterInstance(WRAPS_METHOD,
										new PatternInstance(WRAPS_METHOD, operation, ctInvocation, elementRemoved,
												lineP, lineTree, new PropertyPair("Old", "VarRead"),
												new PropertyPair("New", "Constructor")));
							}
						}
					 }
				  }

				 for(CtExpression ctExpression : invocationArguments) {
					if (ctExpression.getMetadata("isMoved") != null) {
						// Operation is an Insert
						// TODO:
						List<CtElement> suspLeft = MappingAnalysis.getTreeLeftMovedFromRight(diff, ctInvocation);
						if (suspLeft == null || suspLeft.isEmpty())
							return;

						CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(), suspLeft.get(0));
						ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(lineP);
							
						if(RepairPatternUtils.getIsInvocationInStatemnt(diff, lineP, ctInvocation) &&
							!(RepairPatternUtils.getElementInOld(diff, ctExpression).getParent() instanceof CtInvocation)
						   && !(RepairPatternUtils.getElementInOld(diff, ctExpression).getParent() instanceof CtConstructorCall) &&
						   invocationArgumentsold.contains(ctExpression) && ctExpression.getParent()==source)
						   repairPatterns.incrementFeatureCounterInstance(WRAPS_METHOD,
								new PatternInstance(WRAPS_METHOD, operation, ctInvocation, ctExpression, lineP,
										lineTree, new PropertyPair("Old", "MovedExpression"),
										new PropertyPair("New", "Constructor")));
					}
				}
			  }
			} else {
				if (operation instanceof DeleteOperation) {
					CtConstructorCall ctInvocation = (CtConstructorCall) operation.getSrcNode();
					CtStatement statementParent = ctInvocation.getParent(new TypeFilter<>(CtStatement.class));

					if(invocationArgumentsold.contains(ctInvocation) && ctInvocation.getParent()==source)
					  if (statementParent.getMetadata("delete") == null) {
						List<CtExpression> invocationArguments = ctInvocation.getArguments();

						for (CtExpression ctExpression : invocationArguments) {
							if (ctExpression.getMetadata("isMoved") != null
									&& ctExpression.getMetadata("movingSrc") != null) {

								if(RepairPatternUtils.getIsMovedExpressionInStatemnt(diff, statementParent, ctExpression) &&
										invocationArgumentsnew.contains(ctExpression) &&
										ctExpression.getParent()==destination)
								{
								   CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(),
										operation.getSrcNode());
								   ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(lineP);

								   repairPatterns.incrementFeatureCounterInstance(UNWRAP_METHOD, new PatternInstance(
										UNWRAP_METHOD, operation, statementParent, ctInvocation, lineP, lineTree));
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void detectConstantChange(List<CtExpression> invocationArgumentsold, List<CtExpression> invocationArgumentsnew, 
			 RepairPatterns repairPatterns, CtElement source, CtElement destination) {
		List<CtElement> alreadyconsidered = new ArrayList<CtElement>();
		for (int i = 0; i < diff.getAllOperations().size(); i++) {
			Operation operation = diff.getAllOperations().get(i);

			if ((operation instanceof UpdateOperation) && invocationArgumentsold.contains(operation.getSrcNode()) &&
					operation.getSrcNode().getParent()==source && operation.getDstNode().getParent()==destination
					&& invocationArgumentsnew.contains(operation.getDstNode())) {
				CtElement srcNode = operation.getSrcNode();
				
				CtElement parent = MappingAnalysis.getParentLine(new LineFilter(), srcNode);
				ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(parent);

				if (srcNode instanceof CtLiteral) {
					repairPatterns.incrementFeatureCounterInstance(CONST_CHANGE,
							new PatternInstance(CONST_CHANGE, operation, operation.getDstNode(), srcNode, parent,
									lineTree, new PropertyPair("type", "literal")));
				}

				// note enum type will be deemed as type access for partial program
				if (srcNode instanceof CtTypeAccess
							&& !RepairPatternUtils.isThisAccess((CtTypeAccess) srcNode)) {

					CtVariableRead parentVarRead = srcNode.getParent(CtVariableRead.class);
					// The change is not inside a VariableRead (wich ast has as node a TypeAccess)
					if (parentVarRead == null) {

						repairPatterns.incrementFeatureCounterInstance(CONST_CHANGE,
								new PatternInstance(CONST_CHANGE, operation, operation.getDstNode(), srcNode, parent,
										lineTree, new PropertyPair("type", "typeaccess")));
					}
				}
			} else {			

				if (operation instanceof DeleteOperation && operation.getSrcNode() instanceof CtLiteral &&
						invocationArgumentsold.contains(operation.getSrcNode()) && operation.getSrcNode().getParent()==source) {

					CtLiteral ctLiteral = (CtLiteral) operation.getSrcNode();
					
					// try to search a replacement for the literal
					for (int j = 0; j < diff.getAllOperations().size(); j++) {
						Operation operation2Insert = diff.getAllOperations().get(j);
						if (operation2Insert instanceof InsertOperation && 
								invocationArgumentsnew.contains(operation2Insert.getSrcNode()) &&
								operation2Insert.getSrcNode().getParent()==destination) {
							CtElement ctElement = operation2Insert.getSrcNode();

							boolean isConstantVariable = false;
							if (ctElement instanceof CtVariableAccess
									|| (ctElement instanceof CtTypeAccess && !RepairPatternUtils.isThisAccess((CtTypeAccess) ctElement))) {
								isConstantVariable = true;
							}
							if (isConstantVariable && !alreadyconsidered.contains(ctElement)) {
								alreadyconsidered.add(ctElement);
								CtElement parent = MappingAnalysis.getParentLine(new LineFilter(), ctLiteral);
								ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(parent);

								repairPatterns.incrementFeatureCounterInstance(CONST_CHANGE,
										new PatternInstance(CONST_CHANGE, operation2Insert,
												operation2Insert.getSrcNode(), ctLiteral, parent, lineTree,
												new PropertyPair("type", "literal_by_varaccess")));
								break;
							}
						}
					}
				}
			
				if (operation instanceof DeleteOperation && operation.getSrcNode() instanceof CtTypeAccess
						&& invocationArgumentsold.contains(operation.getSrcNode()) && operation.getSrcNode().getParent()==source) {
					CtTypeAccess cttypeaccess = (CtTypeAccess) operation.getSrcNode();
					
					// try to search a replacement for the literal
					if(!RepairPatternUtils.isThisAccess((CtTypeAccess) cttypeaccess))
					  for (int j = 0; j < diff.getAllOperations().size(); j++) {
						Operation operation2Insert = diff.getAllOperations().get(j);
						if (operation2Insert instanceof InsertOperation && 
								invocationArgumentsnew.contains(operation2Insert.getSrcNode())
								&& operation2Insert.getSrcNode().getParent()==destination) {
							CtElement ctElement = operation2Insert.getSrcNode();
							boolean isliteralorvariable = false;
							if (ctElement instanceof CtLiteral
									|| (ctElement instanceof CtVariableAccess)) {
								isliteralorvariable = true;
							}
							if (isliteralorvariable && !alreadyconsidered.contains(ctElement)) {
								alreadyconsidered.add(ctElement);
								CtElement parent = MappingAnalysis.getParentLine(new LineFilter(), cttypeaccess);
								ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(parent);

								repairPatterns.incrementFeatureCounterInstance(CONST_CHANGE,
										new PatternInstance(CONST_CHANGE, operation2Insert,
												operation2Insert.getSrcNode(), cttypeaccess, parent, lineTree,
												new PropertyPair("type", "typeaccess_by_literalvariable")));
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public void detecfWrapIfChange(List<CtExpression> invocationArgumentsold, List<CtExpression> invocationArgumentsnew, 
			Operation operation, RepairPatterns repairPatterns, CtElement source, CtElement destination) {

		if (operation instanceof InsertOperation || operation instanceof DeleteOperation) {
			CtElement ctElement = operation.getSrcNode();
			SpoonHelper.printInsertOrDeleteOperation(ctElement.getFactory().getEnvironment(), ctElement, operation);

			List<CtConditional> conditionalList = ctElement.getElements(new TypeFilter<>(CtConditional.class));
			for (CtConditional ctConditional : conditionalList) {
				if (ctConditional.getMetadata("new") != null && (invocationArgumentsold.contains(ctConditional)||
						invocationArgumentsnew.contains(ctConditional))) {
					
					CtExpression thenExpression = ctConditional.getThenExpression();
					CtExpression elseExpression = ctConditional.getElseExpression();
					
					if (thenExpression.getMetadata("new") == null || elseExpression.getMetadata("new") == null) {
						CtElement statementParent = ctConditional.getParent(new TypeFilter<>(CtStatement.class));
						if (operation instanceof InsertOperation) {

							if (statementParent.getMetadata("new") == null) {
								// We get the not new (the moved).
								// As the then/else is inserted with the Condition, it's always in the right
								CtElement suspRigh = (thenExpression.getMetadata("new") != null) ? elseExpression
										: thenExpression;

								ITree leftMoved = MappingAnalysis.getLeftFromRightNodeMapped(diff,
										(ITree) suspRigh.getMetadata("gtnode"));

								CtElement susp = (CtElement) leftMoved.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

								CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(), susp);
								ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(lineP);

								if(invocationArgumentsnew.contains(ctConditional) && ctConditional.getParent()==destination
										&& invocationArgumentsold.contains(susp) && susp.getParent()==source)
								   repairPatterns.incrementFeatureCounterInstance(WRAPS_IF_ELSE, new PatternInstance(
										WRAPS_IF_ELSE, operation, ctConditional, susp, lineP, lineTree, 
										new PropertyPair("case", "elsenotnull")));
							}
						} else {
							if (statementParent.getMetadata("delete") == null) {
								List susps = new ArrayList();
								CtExpression patch = null;
								if (thenExpression.getMetadata("new") == null) {
									patch = thenExpression;
									susps.add(elseExpression);
									susps.add(ctConditional.getCondition());

								} else {
									patch = elseExpression;
									susps.add(thenExpression);
									susps.add(ctConditional.getCondition());
								}
								CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(),
										(CtElement) susps.get(0));
								ITree lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(lineP);
								
								ITree rightMoved = MappingAnalysis.getRightFromLeftNodeMapped(diff,
										(ITree) patch.getMetadata("gtnode"));

								CtElement remaining = (CtElement) rightMoved.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

								if(invocationArgumentsnew.contains(remaining) && remaining.getParent()==destination
										&& invocationArgumentsold.contains(ctConditional) && ctConditional.getParent()==source)
								  repairPatterns.incrementFeatureCounterInstance(UNWRAP_IF_ELSE,
										new PatternInstance(UNWRAP_IF_ELSE, operation, patch, susps, lineP, lineTree));
							}
						}
					} 
				}
			}
		}
	}
	
	public void detectNullCheckChange(List<CtExpression> invocationArgumentsold, List<CtExpression> invocationArgumentsnew, 
			 RepairPatterns repairPatterns, CtElement source, CtElement destination) {

		for (Operation operation : diff.getAllOperations()) {
			if (operation instanceof InsertOperation) {

				CtElement srcNode = operation.getSrcNode();

				if (srcNode instanceof spoon.reflect.declaration.CtMethod || !invocationArgumentsnew.contains(srcNode)
						|| srcNode.getParent()!=destination)
					continue;

				SpoonHelper.printInsertOrDeleteOperation(srcNode.getFactory().getEnvironment(), srcNode, operation);

				List<CtBinaryOperator> binaryOperatorList = srcNode.getElements(new NullCheckFilter());
				
				for (CtBinaryOperator binaryOperator : binaryOperatorList) {
					if (RepairPatternUtils.isNewBinaryOperator(binaryOperator)) {
						if (RepairPatternUtils.isNewConditionInBinaryOperator(binaryOperator)) {

							final CtElement referenceExpression;
							if (binaryOperator.getRightHandOperand().toString().equals("null")) {
								referenceExpression = binaryOperator.getLeftHandOperand();
							} else {
								referenceExpression = binaryOperator.getRightHandOperand();
							}

							boolean wasPatternFound = false;

							List soldt = null;
							List soldelse = null;
							
							CtElement parent = binaryOperator.getParent(new LineFilter());

							if (binaryOperator.getParent() instanceof CtConditional) {
								
								CtConditional c = (CtConditional) binaryOperator.getParent();
								CtElement thenExpr = c.getThenExpression();
								CtElement elseExp = c.getElseExpression();

								if (thenExpr != null) {
									soldt = new ArrayList<>();
									// If it's not new the THEN
									if (thenExpr.getMetadata("new") == null && invocationArgumentsold.
											contains(RepairPatternUtils.getElementInOld(diff, thenExpr))
											&& RepairPatternUtils.getElementInOld(diff, thenExpr).getParent()==source) {
									//	soldelse.add(thenExpr);
										soldt.add(RepairPatternUtils.getElementInOld(diff, thenExpr));
										wasPatternFound = true;
									}
								}
								if (elseExp != null) {
									soldelse = new ArrayList<>();
									// If it's not new the ELSE
									if (elseExp.getMetadata("new") == null && invocationArgumentsold.
											contains(RepairPatternUtils.getElementInOld(diff, elseExp))
											&& RepairPatternUtils.getElementInOld(diff, elseExp).getParent()==source) {
									//	soldelse.add(elseExp);
										soldt.add(RepairPatternUtils.getElementInOld(diff, elseExp));
										wasPatternFound = true;
									}
								}
							}

							if (wasPatternFound) {

								List<CtElement> susp = new ArrayList<>();
								if (soldt != null)
									susp.addAll(soldt);
								if (soldelse != null)
									susp.addAll(soldelse);

								CtElement lineP = null;
								ITree lineTree = null;
								if (!susp.isEmpty()) {

									lineP = MappingAnalysis.getParentLine(new LineFilter(), (CtElement) susp.get(0));
									lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(lineP);

								} else {

									// The next
									InsertOperation operationIns = (InsertOperation) operation;

									List<ITree> treeInLeft = MappingAnalysis.getFollowStatementsInLeft(diff,
											operationIns.getAction());

									if (treeInLeft.isEmpty()) {
										System.out.println(
												"Problem!!!! Empty parent in " + MISS_NULL_CHECK_N.toLowerCase());
										continue;
									}

									for (ITree iTree : treeInLeft) {
										susp.add((CtElement) iTree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT));
									}

									lineP = susp.get(0);
								//	lineTree = treeInLeft.get(0);
									lineTree = MappingAnalysis.getFormatedTreeFromControlFlow(lineP);
								}

								if (binaryOperator.getKind().equals(BinaryOperatorKind.EQ)) {
									repairPatterns.incrementFeatureCounterInstance(MISS_NULL_CHECK_P,
											new PatternInstance(MISS_NULL_CHECK_P, operation, parent, susp, lineP,
													lineTree));
								} else {
									repairPatterns.incrementFeatureCounterInstance(MISS_NULL_CHECK_N,
											new PatternInstance(MISS_NULL_CHECK_N, operation, parent, susp, lineP,
													lineTree));
								}
							}
						}
					}
				}
			}
		}
	}
}
