package add.features.detector.repairpatterns;

import java.util.ArrayList;
import java.util.List;

import com.github.gumtreediff.tree.ITree;

import add.entities.PatternInstance;
import add.entities.PropertyPair;
import add.entities.RepairPatterns;
import add.main.Config;
import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.diff.operations.DeleteOperation;
import gumtree.spoon.diff.operations.InsertOperation;
import gumtree.spoon.diff.operations.Operation;
import gumtree.spoon.diff.operations.UpdateOperation;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.LineFilter;

/**
 * Created by tdurieux
 */
public class WrongReferenceDetector extends AbstractPatternDetector {

	public static final String WRONG_VAR_REF = "wrongVarRef";
	public static final String WRONG_METHOD_REF = "wrongMethodRef";
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
				if (srcNode instanceof CtVariableAccess || srcNode instanceof CtTypeAccess) {
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

								// if we have the element that has be inserted
								if (newElementReplacementOfTheVar != null) {
									PropertyPair propertyNewElement = new PropertyPair("New",
											"Added_" + newElementReplacementOfTheVar.getClass().getSimpleName()
													.replace("Ct", "").replace("Impl", ""));

									metadata = new PropertyPair[] { propertyOldElemet, propertyNewElement };
								} else
									metadata = new PropertyPair[] { propertyOldElemet };

								// Case 1

								repairPatterns.incrementFeatureCounterInstance(WRONG_VAR_REF, new PatternInstance(
										WRONG_VAR_REF, operationDelete, patch, susp, parentLine, lineTree, metadata));

							}
						}
					}
				} else {
					// Inside delete but node is Not access var

					if (srcNode.getRoleInParent() == CtRole.ARGUMENT) {

						CtElement susp = null;// operationDelete.getSrcNode();
						susp = operationDelete.getSrcNode().getParent(CtInvocation.class);
						if (susp == null)
							susp = operationDelete.getSrcNode().getParent(CtConstructorCall.class);

						CtElement patch = null;

						CtElement parentLine = MappingAnalysis.getParentLine(new LineFilter(), susp);
						ITree lineTree = (ITree) ((parentLine.getMetadata("tree") != null)
								? parentLine.getMetadata("tree")
								: parentLine.getMetadata("gtnode"));

						ITree parentRight = MappingAnalysis.getParentInRight(diff, operationDelete.getAction());
						if (parentRight != null) {
							patch = (CtElement) parentRight.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);
						}

						repairPatterns.incrementFeatureCounterInstance(WRONG_METHOD_REF,
								new PatternInstance(WRONG_METHOD_REF, operationDelete, patch, susp, parentLine,
										lineTree,
										//
										new PropertyPair("Change", "ArgumentRemovement")

								));

					}
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
				if (srcNode instanceof CtVariableAccess || srcNode instanceof CtTypeAccess) {
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
					}
				}
			}
		}
	}

}
