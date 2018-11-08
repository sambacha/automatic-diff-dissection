package add.features.detector.repairpatterns;

import java.util.ArrayList;
import java.util.List;

import com.github.gumtreediff.tree.ITree;

import add.entities.PatternInstance;
import add.entities.RepairPatterns;
import add.features.detector.spoon.RepairPatternUtils;
import add.features.detector.spoon.SpoonHelper;
import gumtree.spoon.diff.operations.DeleteOperation;
import gumtree.spoon.diff.operations.InsertOperation;
import gumtree.spoon.diff.operations.MoveOperation;
import gumtree.spoon.diff.operations.Operation;
import gumtree.spoon.diff.operations.UpdateOperation;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.LineFilter;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * Created by fermadeiral
 */
public class WrapsWithDetector extends AbstractPatternDetector {

	private static final String WRAPS_LOOP = "wrapsLoop";
	private static final String UNWRAP_METHOD = "unwrapMethod";
	private static final String UNWRAP_TRY_CATCH = "unwrapTryCatch";
	private static final String WRAPS_METHOD = "wrapsMethod";
	private static final String WRAPS_TRY_CATCH = "wrapsTryCatch";
	private static final String WRAPS_ELSE = "wrapsElse";
	private static final String WRAPS_IF_ELSE = "wrapsIfElse";
	private static final String UNWRAP_IF_ELSE = "unwrapIfElse";
	private static final String WRAPS_IF = "wrapsIf";

	public WrapsWithDetector(List<Operation> operations) {
		super(operations);
	}

	@Override
	public void detect(RepairPatterns repairPatterns) {
		for (Operation operation : this.operations) {
			this.detectWrapsIf(operation, repairPatterns);
			this.detectWrapsTryCatch(operation, repairPatterns);
			this.detectWrapsMethod(operation, repairPatterns);
			this.detectWrapsLoop(operation, repairPatterns);
		}
	}

	private void detectWrapsIf(Operation operation, RepairPatterns repairPatterns) {
		if (operation instanceof InsertOperation || operation instanceof DeleteOperation) {
			CtElement ctElement = operation.getSrcNode();
			SpoonHelper.printInsertOrDeleteOperation(ctElement.getFactory().getEnvironment(), ctElement, operation);

			List<CtIf> ifList = ctElement.getElements(new TypeFilter<>(CtIf.class));
			for (CtIf ctIf : ifList) {
				if (RepairPatternUtils.isNewIf(ctIf)) {
					CtBlock thenBlock = ctIf.getThenStatement();
					CtBlock elseBlock = ctIf.getElseStatement();
					if (elseBlock == null) {
						List stmtsMoved = RepairPatternUtils
								.getIsThereOldStatementInStatementList(thenBlock.getStatements());
						if (thenBlock != null && !stmtsMoved.isEmpty()) {
							if (operation instanceof InsertOperation) {

								CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(),
										(CtElement) stmtsMoved.get(0));
								ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null)
										? lineP.getMetadata("tree")
										: lineP.getMetadata("gtnode"));

								repairPatterns.incrementFeatureCounterInstance(WRAPS_IF,
										new PatternInstance(WRAPS_IF, operation, ctIf, stmtsMoved, lineP, lineTree));
							} else {

								List susp = ctElement.getElements(new TypeFilter<>(CtStatement.class));
								for (Object object : stmtsMoved) {
									CtElement e = (CtElement) object;
									susp.removeAll(e.getElements(new TypeFilter<>(CtStatement.class)));
								}

								CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(),
										(CtElement) susp.get(0));

								ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null)
										? lineP.getMetadata("tree")
										: lineP.getMetadata("gtnode"));

								repairPatterns.incrementFeatureCounterInstance(UNWRAP_IF_ELSE, //
										new PatternInstance(UNWRAP_IF_ELSE, operation, (CtElement) stmtsMoved.get(0),
												susp, lineP, lineTree));
							}
						}
					} else {
						List sthen = RepairPatternUtils
								.getIsThereOldStatementInStatementList(thenBlock.getStatements());
						List selse = RepairPatternUtils
								.getIsThereOldStatementInStatementList(elseBlock.getStatements());
						// selse.addAll(sthen);
						if (!sthen.isEmpty() || !selse.isEmpty()) {
							if (operation instanceof InsertOperation) {

								List all = new ArrayList<>();
								all.addAll(sthen);
								all.addAll(selse);
								CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(),
										(CtElement) all.get(0));
								ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null)
										? lineP.getMetadata("tree")
										: lineP.getMetadata("gtnode"));

								repairPatterns.incrementFeatureCounterInstance(WRAPS_IF_ELSE,
										new PatternInstance(WRAPS_IF_ELSE, operation, ctIf, all, lineP, lineTree));
							} else {

								List susp = ctElement.getElements(new TypeFilter<>(CtStatement.class));
								sthen.addAll(selse);
								for (Object object : sthen) {
									CtElement e = (CtElement) object;
									susp.removeAll(e.getElements(new TypeFilter<>(CtStatement.class)));
								}

								CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(),
										(CtElement) susp.get(0));
								ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null)
										? lineP.getMetadata("tree")
										: lineP.getMetadata("gtnode"));

								repairPatterns.incrementFeatureCounterInstance(UNWRAP_IF_ELSE, //
										new PatternInstance(UNWRAP_IF_ELSE, operation, (CtElement) sthen.get(0), susp,
												lineP, lineTree));
							}
						}
					}
				}
			}

			List<CtBlock> blockList = ctElement.getElements(new TypeFilter<>(CtBlock.class));
			for (CtBlock ctBlock : blockList) {
				if (ctBlock.getMetadata("new") != null) {
					if (ctBlock.getParent() instanceof CtIf) {
						CtIf ctIfParent = (CtIf) ctBlock.getParent();
						CtBlock elseBlock = ctIfParent.getElseStatement();
						if (ctBlock == elseBlock) {
							if (!RepairPatternUtils.isNewIf(ctIfParent)) {
								CtBlock thenBlock = ctIfParent.getThenStatement();
								if (thenBlock != null && RepairPatternUtils
										.isThereOldStatementInStatementList(thenBlock.getStatements())) {
									List selse = RepairPatternUtils
											.getIsThereOldStatementInStatementList(elseBlock.getStatements());
									if (!selse.isEmpty()) {

										CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(),
												(CtElement) selse.get(0));
										ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null)
												? lineP.getMetadata("tree")
												: lineP.getMetadata("gtnode"));

										repairPatterns.incrementFeatureCounterInstance(WRAPS_ELSE, new PatternInstance(
												WRAPS_ELSE, operation, ctIfParent, selse, lineP, lineTree));
									}
								}
							}
						}
					}
				}
			}

			List<CtConditional> conditionalList = ctElement.getElements(new TypeFilter<>(CtConditional.class));
			for (CtConditional ctConditional : conditionalList) {
				if (ctConditional.getMetadata("new") != null) {
					CtExpression thenExpression = ctConditional.getThenExpression();
					CtExpression elseExpression = ctConditional.getElseExpression();
					if (thenExpression.getMetadata("new") == null || elseExpression.getMetadata("new") == null) {
						CtElement statementParent = ctConditional.getParent(new TypeFilter<>(CtStatement.class));
						if (operation instanceof InsertOperation) {

							if (statementParent.getMetadata("new") == null) {
								CtElement susp = (thenExpression.getMetadata("new") != null) ? elseExpression
										: thenExpression;

								CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(), susp);
								ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null)
										? lineP.getMetadata("tree")
										: lineP.getMetadata("gtnode"));

								repairPatterns.incrementFeatureCounterInstance(WRAPS_IF_ELSE, new PatternInstance(
										WRAPS_IF_ELSE, operation, ctConditional, susp, lineP, lineTree));
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
								ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null)
										? lineP.getMetadata("tree")
										: lineP.getMetadata("gtnode"));

								repairPatterns.incrementFeatureCounterInstance(UNWRAP_IF_ELSE,
										new PatternInstance(UNWRAP_IF_ELSE, operation, patch, susps, lineP, lineTree));
							}
						}
					} else {
						if (operation instanceof InsertOperation) {
							for (int j = 0; j < operations.size(); j++) {
								Operation operation2 = operations.get(j);
								if (operation2 instanceof DeleteOperation) {
									CtElement node2 = operation2.getSrcNode();
									if (((InsertOperation) operation).getParent() != null) {
										if (node2.getParent() == ((InsertOperation) operation).getParent()) {

											CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(), node2);
											ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null)
													? lineP.getMetadata("tree")
													: lineP.getMetadata("gtnode"));

											repairPatterns.incrementFeatureCounterInstance(WRAPS_IF_ELSE,
													new PatternInstance(WRAPS_IF_ELSE, operation, ctConditional, node2,
															lineP, lineTree));
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void detectWrapsTryCatch(Operation operation, RepairPatterns repairPatterns) {
		if (operation instanceof InsertOperation || operation instanceof DeleteOperation) {
			CtElement ctElement = operation.getSrcNode();
			SpoonHelper.printInsertOrDeleteOperation(ctElement.getFactory().getEnvironment(), ctElement, operation);

			List<CtTry> tryList = ctElement.getElements(new TypeFilter<>(CtTry.class));
			for (CtTry ctTry : tryList) {
				if (ctTry.getMetadata("new") != null) {
					List<CtCatch> catchList = ctTry.getCatchers();
					if (RepairPatternUtils.isThereOnlyNewCatch(catchList)) {
						CtBlock tryBodyBlock = ctTry.getBody();

						List olds = RepairPatternUtils
								.getIsThereOldStatementInStatementList(tryBodyBlock.getStatements());
						if (tryBodyBlock != null && !olds.isEmpty()) {

							CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(), (CtElement) olds.get(0));
							ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null) ? lineP.getMetadata("tree")
									: lineP.getMetadata("gtnode"));

							if (operation instanceof InsertOperation) {

								repairPatterns.incrementFeatureCounterInstance(WRAPS_TRY_CATCH,
										new PatternInstance(WRAPS_TRY_CATCH, operation, ctTry, olds, lineP, lineTree));
							} else {

								repairPatterns.incrementFeatureCounterInstance(UNWRAP_TRY_CATCH, new PatternInstance(
										UNWRAP_TRY_CATCH, operation, (CtElement) olds.get(0), olds, lineP, lineTree));
							}
						} else { // try to find a move into the body of the try
							for (Operation operationAux : this.operations) {
								if (operationAux instanceof MoveOperation) {
									CtElement ctElementDst = operationAux.getDstNode();
									CtTry ctTryParent = ctElementDst.getParent(new TypeFilter<>(CtTry.class));
									if (ctTryParent != null && ctTryParent == ctTry) {
										if (operation instanceof InsertOperation) {

											CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(),
													operationAux.getSrcNode());
											ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null)
													? lineP.getMetadata("tree")
													: lineP.getMetadata("gtnode"));

											repairPatterns.incrementFeatureCounterInstance(WRAPS_TRY_CATCH,
													new PatternInstance(WRAPS_TRY_CATCH, operation, ctTry,
															operationAux.getSrcNode(), lineP, lineTree));
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Return a list with the statemetns inside the first parameter, but removing
	 * those from the second
	 * 
	 * @param ctElements
	 * @param olds
	 * @return
	 */
	private List listSuspicions(CtElement ctElements, List olds) {
		List susp = ctElements.getElements(new TypeFilter<>(CtStatement.class));
		for (Object object : olds) {
			CtElement e = (CtElement) object;
			susp.removeAll(e.getElements(new TypeFilter<>(CtStatement.class)));
		}
		return susp;
	}

	private void detectWrapsMethod(Operation operation, RepairPatterns repairPatterns) {
		if (operation.getSrcNode() instanceof CtInvocation) {
			if (operation instanceof InsertOperation) {
				CtInvocation ctInvocation = (CtInvocation) operation.getSrcNode();
				List<CtExpression> invocationArguments = ctInvocation.getArguments();

				for (Operation operation2 : this.operations) {
					if (operation2 instanceof DeleteOperation) {
						CtElement ctElement = operation2.getSrcNode();

						if (ctElement instanceof CtVariableRead) {

							if (invocationArguments.contains(ctElement)) {

								CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(), ctElement);
								ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null)
										? lineP.getMetadata("tree")
										: lineP.getMetadata("gtnode"));
								repairPatterns.incrementFeatureCounterInstance(WRAPS_METHOD,
										new PatternInstance(WRAPS_METHOD, operation, ctInvocation, ctElement,
												/* parent */ lineP, lineTree));
							}
						}
						if (ctElement instanceof CtAssignment) {
							CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(), ctElement);
							ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null) ? lineP.getMetadata("tree")
									: lineP.getMetadata("gtnode"));

							if (invocationArguments.contains(((CtAssignment) ctElement).getAssignment())) {
								repairPatterns.incrementFeatureCounterInstance(WRAPS_METHOD,
										new PatternInstance(WRAPS_METHOD, operation, ctInvocation,
												((CtAssignment) ctElement).getAssigned(), /* parent */lineP, lineTree));
							}
						}
					}
				}

				for (CtExpression ctExpression : invocationArguments) {
					if (ctExpression.getMetadata("isMoved") != null) {
						// CtElement parent = ctInvocation.getParent(new LineFilter());
						// Operation is an Insert
						CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(),
								((InsertOperation) operation).getParent());
						ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null) ? lineP.getMetadata("tree")
								: lineP.getMetadata("gtnode"));
						repairPatterns.incrementFeatureCounterInstance(WRAPS_METHOD, new PatternInstance(WRAPS_METHOD,
								operation, ctInvocation, ctExpression, /* parent */ lineP, lineTree));
					}
				}
			} else {
				if (operation instanceof DeleteOperation) {
					CtInvocation ctInvocation = (CtInvocation) operation.getSrcNode();
					CtStatement statementParent = ctInvocation.getParent(new TypeFilter<>(CtStatement.class));

					if (statementParent.getMetadata("delete") == null) {
						List<CtExpression> invocationArguments = ctInvocation.getArguments();

						for (CtExpression ctExpression : invocationArguments) {
							if (ctExpression.getMetadata("isMoved") != null
									&& ctExpression.getMetadata("movingSrc") != null) {

								CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(),
										operation.getSrcNode());
								ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null)
										? lineP.getMetadata("tree")
										: lineP.getMetadata("gtnode"));

								repairPatterns.incrementFeatureCounterInstance(UNWRAP_METHOD,
										new PatternInstance(UNWRAP_METHOD, operation, statementParent, ctInvocation,
												/* parent */lineP, lineTree));
							}
						}
					}
				}
			}
		}
	}

	private void detectWrapsLoop(Operation operation, RepairPatterns repairPatterns) {
		if (operation instanceof InsertOperation) {
			CtElement ctElement = operation.getSrcNode();
			SpoonHelper.printInsertOrDeleteOperation(ctElement.getFactory().getEnvironment(), ctElement, operation);

			List<CtLoop> loopList = ctElement.getElements(new TypeFilter<>(CtLoop.class));
			for (CtLoop ctLoop : loopList) {
				if ((ctLoop instanceof CtFor && RepairPatternUtils.isNewFor((CtFor) ctLoop))
						|| (ctLoop instanceof CtForEach && RepairPatternUtils.isNewForEach((CtForEach) ctLoop))
						|| (ctLoop instanceof CtWhile && RepairPatternUtils.isNewWhile((CtWhile) ctLoop))) {
					if (ctLoop.getBody() instanceof CtBlock) {
						CtBlock bodyBlock = (CtBlock) ctLoop.getBody();
						List susp = RepairPatternUtils.getIsThereOldStatementInStatementList(bodyBlock.getStatements());
						if (bodyBlock != null && !susp.isEmpty()) {

							CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(), (CtElement) susp.get(0));
							ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null) ? lineP.getMetadata("tree")
									: lineP.getMetadata("gtnode"));

							repairPatterns.incrementFeatureCounterInstance(WRAPS_LOOP,
									new PatternInstance(WRAPS_LOOP, operation, ctLoop, susp, lineP, lineTree));
						} else { // try to find an update inside the body of the loop
							for (Operation operationAux : this.operations) {
								if (operationAux instanceof UpdateOperation) {
									CtElement ctElementDst = operationAux.getDstNode();
									CtLoop ctLoopParent = ctElementDst.getParent(new TypeFilter<>(CtLoop.class));
									if (ctLoopParent != null && ctLoopParent == ctLoop) {

										// CtStatement sparent = getStmtParent(operationAux.getSrcNode());
										CtElement lineP = MappingAnalysis.getParentLine(new LineFilter(),
												operationAux.getSrcNode());
										ITree lineTree = (ITree) ((lineP.getMetadata("tree") != null)
												? lineP.getMetadata("tree")
												: lineP.getMetadata("gtnode"));

										repairPatterns.incrementFeatureCounterInstance(WRAPS_LOOP,
												new PatternInstance(WRAPS_LOOP, operation, ctLoop,
														operationAux.getSrcNode(), lineP, lineTree));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private CtStatement getStmtParent(CtElement srcNode) {

		CtStatement st = srcNode.getParent(CtStatement.class);
		while (st != null && st.getParent() != null
				&& !(st.getParent() instanceof CtBlock || st.getParent() instanceof CtClass)) {
			st = st.getParent(CtStatement.class);

		}
		return st;
	}

}
