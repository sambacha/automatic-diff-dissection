package add.features.detector.repairpatterns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.gumtreediff.tree.ITree;

import add.entities.PatternInstance;
import add.entities.RepairPatterns;
import add.features.detector.spoon.RepairPatternUtils;
import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.diff.operations.InsertOperation;
import gumtree.spoon.diff.operations.MoveOperation;
import gumtree.spoon.diff.operations.Operation;
import gumtree.spoon.diff.operations.UpdateOperation;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.EarlyTerminatingScanner;
import spoon.reflect.visitor.filter.LineFilter;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * Created by tdurieux
 */
public class ExpressionFixDetector extends AbstractPatternDetector {

	private static final String BIN_OPERATOR_MODIF = "binOperatorModif";
	private static final String EXP_LOGIC_MOD = "expLogicMod";
	private static final String EXP_LOGIC_REDUCE = "expLogicReduce";
	private static final String EXP_LOGIC_EXPAND = "expLogicExpand";

	public ExpressionFixDetector(List<Operation> operations) {
		super(operations);
	}

	@Override
	public void detect(RepairPatterns repairPatterns) {
		List<BinaryOperatorKind> mathematicOperator = Arrays.asList(BinaryOperatorKind.SL, BinaryOperatorKind.SR,
				BinaryOperatorKind.USR, BinaryOperatorKind.PLUS, BinaryOperatorKind.MINUS, BinaryOperatorKind.MUL,
				BinaryOperatorKind.DIV, BinaryOperatorKind.MOD);
		List<UnaryOperatorKind> unaryOperators = Arrays.asList(UnaryOperatorKind.PREINC, UnaryOperatorKind.POSTINC,
				UnaryOperatorKind.POSTDEC, UnaryOperatorKind.PREDEC);
		List<BinaryOperatorKind> logicOperators = Arrays.asList(BinaryOperatorKind.OR, BinaryOperatorKind.AND,
				BinaryOperatorKind.BITOR, BinaryOperatorKind.BITXOR, BinaryOperatorKind.BITAND, BinaryOperatorKind.EQ,
				BinaryOperatorKind.NE, BinaryOperatorKind.LT, BinaryOperatorKind.GT, BinaryOperatorKind.LE,
				BinaryOperatorKind.GE);
		List<BinaryOperatorKind> conditionalOperators = Arrays.asList(BinaryOperatorKind.AND, BinaryOperatorKind.OR);
		for (int i = 0; i < operations.size(); i++) {
			Operation operation = operations.get(i);
			if (operation instanceof MoveOperation) {
				continue;
			}
			LineFilter filter = new LineFilter();
			if ((operation instanceof UpdateOperation)) {
				CtElement dstNode = operation.getDstNode();
				CtBinaryOperator binaryOperator = dstNode instanceof CtBinaryOperator ? (CtBinaryOperator) dstNode
						: dstNode.getParent(CtBinaryOperator.class);
				if (binaryOperator != null) {

					CtBinaryOperator buggybinaryOperator = operation.getSrcNode() instanceof CtBinaryOperator
							? (CtBinaryOperator) operation.getSrcNode()
							: operation.getSrcNode().getParent(CtBinaryOperator.class);

					if (buggybinaryOperator != null && binaryOperator != null &&

							idem(buggybinaryOperator.getLeftHandOperand(), binaryOperator.getLeftHandOperand())
							&& idem(buggybinaryOperator.getRightHandOperand(), binaryOperator.getRightHandOperand())) {

						CtElement parentLine = MappingAnalysis.getParentLine(filter, buggybinaryOperator);

						ITree lineTree = (ITree) ((parentLine.getMetadata("tree") != null)
								? parentLine.getMetadata("tree")
								: parentLine.getMetadata("gtnode"));

						repairPatterns.incrementFeatureCounterInstance(BIN_OPERATOR_MODIF, new PatternInstance(
								BIN_OPERATOR_MODIF, operation, dstNode, buggybinaryOperator, parentLine, lineTree));

					}

					if (mathematicOperator.contains(binaryOperator.getKind())) {
						if (!RepairPatternUtils.isStringInvolvedInBinaryOperator(binaryOperator)) {
							// repairPatterns.incrementFeatureCounter("expArithMod", operation);
						}
					} else {
						CtElement parent = binaryOperator.getParent(filter);
						if (parent != null && parent instanceof CtIf) {
							CtIf parentIf = (CtIf) parent;
							if (parentIf.getMetadata("isMoved") == null) {
								// repairPatterns.incrementFeatureCounter(EXP_LOGIC_MOD, operation);
							}
						} else {
							CtBinaryOperator parentBinaryOperator = binaryOperator;
							while (parentBinaryOperator.getParent() instanceof CtBinaryOperator) {
								parentBinaryOperator = (CtBinaryOperator) parentBinaryOperator.getParent();
							}
							if (hasMetaInIt(parentBinaryOperator, "update")) {
								// repairPatterns.incrementFeatureCounter(EXP_LOGIC_MOD, operation);
							}
						}
					}
				}
				CtUnaryOperator unaryOperator = dstNode instanceof CtUnaryOperator ? (CtUnaryOperator) dstNode
						: dstNode.getParent(CtUnaryOperator.class);
				if (unaryOperator != null) {
					if (unaryOperators.contains(unaryOperator.getKind())) {
						repairPatterns.incrementFeatureCounter("expArithMod", operation);
					}
				}
				if (dstNode.getParent() instanceof CtIf && dstNode.getRoleInParent() == CtRole.CONDITION) {
					// repairPatterns.incrementFeatureCounter(EXP_LOGIC_MOD, operation);//Chart-1
				}
			} else {
				CtElement srcNode = operation.getSrcNode();
				if (srcNode.isImplicit() && srcNode instanceof CtBlock
						&& ((CtBlock) srcNode).getStatements().size() == 1) {
					srcNode = ((CtBlock) srcNode).getLastStatement();
				}

				boolean isExpLogicExOrRed = false;
				if (srcNode instanceof CtBinaryOperator) {
					if (RepairPatternUtils.isMovedCondition((CtBinaryOperator) srcNode)) {
						continue;
					}

					if (hasMetaInIt(srcNode, "isMoved")) {
						if (mathematicOperator.contains(((CtBinaryOperator) srcNode).getKind())) {
							if (!RepairPatternUtils.isStringInvolvedInBinaryOperator((CtBinaryOperator) srcNode)) {
								// repairPatterns.incrementFeatureCounter("expArithMod", operation);
							}
						}
					}

					CtBinaryOperator ctBinaryOperator = (CtBinaryOperator) srcNode;
					CtBinaryOperator parentBinaryOperator = ctBinaryOperator.getParent(CtBinaryOperator.class);
					if (parentBinaryOperator == null) {
						parentBinaryOperator = ctBinaryOperator;
					}
					boolean isThereOldCondition = false;
					boolean isThereNewCondition = false;
					boolean isThereDeletedCondition = false;
					boolean isThereChangedCondition = false;

					List<CtBinaryOperator> binaryOperatorList = parentBinaryOperator
							.getElements(new TypeFilter<>(CtBinaryOperator.class));
					for (CtBinaryOperator binaryOperator : binaryOperatorList) {
						if (conditionalOperators.contains(ctBinaryOperator.getKind())) {
							if (RepairPatternUtils.isNewBinaryOperator(binaryOperator)
									&& !RepairPatternUtils.isDeletedBinaryOperator(binaryOperator)
									&& RepairPatternUtils.isNewConditionInBinaryOperator(binaryOperator)) {
								isThereNewCondition = true;
							}
							if (RepairPatternUtils.isExistingConditionInBinaryOperator(binaryOperator)) {
								isThereOldCondition = true;
							}
							if (RepairPatternUtils.isDeletedBinaryOperator(binaryOperator)
									&& RepairPatternUtils.isDeletedConditionInBinaryOperator(binaryOperator)) {
								isThereDeletedCondition = true;
							}
							if (RepairPatternUtils.isUpdatedConditionInBinaryOperator(binaryOperator)) {
								isThereChangedCondition = true;
							}
						}
					}
					List<CtUnaryOperator> unaryOperatorList = parentBinaryOperator
							.getElements(new TypeFilter<>(CtUnaryOperator.class));
					for (CtUnaryOperator unaryOperator : unaryOperatorList) {
						// if (logicOperators.contains(ctBinaryOperator.getKind()))
						if (RepairPatternUtils.isNewUnaryOperator(unaryOperator)
								&& !RepairPatternUtils.isDeletedUnaryOperator(unaryOperator)
								&& RepairPatternUtils.isNewConditionInUnaryOperator(unaryOperator)) {
							isThereNewCondition = true;
						}
						if (RepairPatternUtils.isExistingConditionInUnaryOperator(unaryOperator)) {
							isThereOldCondition = true;
						}
						if (RepairPatternUtils.isDeletedUnaryOperator(unaryOperator)) {
							isThereDeletedCondition = true;
						}
						if (RepairPatternUtils.isUpdatedConditionInUnaryOperator(unaryOperator)) {
							isThereChangedCondition = true;
						}
					}
					if (isThereChangedCondition) {
						// Discarded: not information about the change done in the
						// repairPatterns.incrementFeatureCounter(EXP_LOGIC_MOD, operation);
						isExpLogicExOrRed = true;
					}
					if (isThereOldCondition && isThereNewCondition) {

						// Get the nodes moved in the right
						List<CtElement> movesInRight = parentBinaryOperator
								.getElements(e -> e.getMetadata("isMoved") != null && e.getMetadata("root") != null);

						List<CtElement> suspLeft = new ArrayList();
						for (CtElement ctElement : movesInRight) {

							ITree mappedLeft = MappingAnalysis.getLeftFromRightNodeMapped(diff,
									(ITree) ctElement.getMetadata("gtnode"));
							if (mappedLeft != null) {
								suspLeft.add((CtElement) mappedLeft.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT));

							} else {
								return;
							}

						}

						CtElement parentLine = MappingAnalysis.getParentLine(filter, suspLeft.get(0));
						ITree lineTree = (ITree) ((parentLine.getMetadata("tree") != null)
								? parentLine.getMetadata("tree")
								: parentLine.getMetadata("gtnode"));
						///
						repairPatterns.incrementFeatureCounterInstance(EXP_LOGIC_EXPAND, new PatternInstance(
								EXP_LOGIC_EXPAND, operation, parentBinaryOperator, suspLeft, parentLine, lineTree));

						isExpLogicExOrRed = true;
					}
					if (isThereOldCondition && isThereDeletedCondition) {

						ITree parentAffectInRight = MappingAnalysis.getParentInRight(diff, operation.getAction());
						CtElement affected = (CtElement) parentAffectInRight
								.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

						//
						CtBinaryOperator binary = (CtBinaryOperator) srcNode;

						CtElement removedNode = (binary.getRightHandOperand().getMetadata("delete") != null)
								? binary.getRightHandOperand()
								: binary.getLeftHandOperand();

						CtElement parentLine = MappingAnalysis.getParentLine(filter, binary);

						ITree lineTree = (ITree) ((parentLine.getMetadata("tree") != null)
								? parentLine.getMetadata("tree")
								: parentLine.getMetadata("gtnode"));

						repairPatterns.incrementFeatureCounterInstance(EXP_LOGIC_REDUCE, new PatternInstance(
								EXP_LOGIC_REDUCE, operation, affected, removedNode, parentLine, lineTree));

						// repairPatterns.incrementFeatureCounter(EXP_LOGIC_REDUCE, operation);

						//
//
//						ITree solutionInRight = MappingAnalysis.getParentInRight(diff, operation.getAction());
//						CtElement solution = (CtElement) solutionInRight.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);
//
//						//
//						CtBinaryOperator binary = (CtBinaryOperator) srcNode;
//
//						// Get the nodes moved in the right
//						List<CtElement> notNovesInLeft = parentBinaryOperator
//								.getElements(e -> e.getMetadata("isMoved") == null && e.getMetadata("root") != null);
//
//						List<CtElement> suspLeft = new ArrayList();
//						for (CtElement ctElement : notNovesInLeft) {
//
//							ITree mappedLeft = (ITree) ctElement.getMetadata("gtnode");
//							if (mappedLeft != null) {
//								suspLeft.add((CtElement) mappedLeft.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT));
//
//							} else {
//								return;
//							}
//
//						}
//
//						CtElement parentLine = MappingAnalysis.getParentLine(filter, binary);
//
//						ITree lineTree = (ITree) ((parentLine.getMetadata("tree") != null)
//								? parentLine.getMetadata("tree")
//								: parentLine.getMetadata("gtnode"));
//
//						repairPatterns.incrementFeatureCounterInstance(EXP_LOGIC_REDUCE, new PatternInstance(
//								EXP_LOGIC_REDUCE, operation, solution, notNovesInLeft, parentLine, lineTree));

						isExpLogicExOrRed = true;
					}
				}

				if (srcNode instanceof CtUnaryOperator) {
					if (isInCondition(srcNode)) {
						if (srcNode.getParent().getMetadata("isMoved") == null) {
							if (operation instanceof InsertOperation) {
								if (hasMetaInIt(((InsertOperation) operation).getParent(), "delete")) {
									// Too general closure 104
									// repairPatterns.incrementFeatureCounter(EXP_LOGIC_MOD, operation);
								}
							}
						}
					}
				}

				if (srcNode instanceof CtCase) {
					CtCase ctCase = (CtCase) srcNode;
					if (ctCase.getParent().getMetadata("isMoved") == null) {
						if (ctCase.getStatements().size() == 0) {
							if (operation instanceof InsertOperation) {
								repairPatterns.incrementFeatureCounter(EXP_LOGIC_EXPAND, operation);
								isExpLogicExOrRed = true;
							} else {
								// cannot delete completely the condition
								if (!(srcNode.getParent() instanceof CtIf) || hasMetaInIt(srcNode, "isMoved")) {
									repairPatterns.incrementFeatureCounter(EXP_LOGIC_REDUCE, operation);
									isExpLogicExOrRed = true;
								}
							}
						} else {
							if (RepairPatternUtils.isThereOldStatementInStatementList(ctCase.getStatements())) {
								for (int j = 0; j < operations.size(); j++) {
									Operation operation2 = operations.get(j);
									if (operation2 instanceof MoveOperation) {
										CtElement movedSrcNode = operation2.getSrcNode();
										if (movedSrcNode.getParent() instanceof CtCase) {
											if (operation instanceof InsertOperation) {
												repairPatterns.incrementFeatureCounter(EXP_LOGIC_EXPAND, operation);
												isExpLogicExOrRed = true;
											}
										}
									}
								}
							}
						}
					}
				}
				if (srcNode instanceof CtExpression && operation instanceof InsertOperation && !isExpLogicExOrRed) {
					CtIf parent = srcNode.getParent(CtIf.class);
					CtBinaryOperator binary = srcNode.getParent(CtBinaryOperator.class);
					if (parent != null && isInCondition(binary) && parent.getMetadata("isMoved") == null) {
						// Discard
						// repairPatterns.incrementFeatureCounter(EXP_LOGIC_MOD, operation);//math 76
					}
				}
				CtBinaryOperator binaryOperator = srcNode.getParent(CtBinaryOperator.class);
				if (binaryOperator != null) {
					if (mathematicOperator.contains(binaryOperator.getKind())) {
						if (!RepairPatternUtils.isStringInvolvedInBinaryOperator(binaryOperator)) {
							// repairPatterns.incrementFeatureCounter("expArithMod", operation);
						}
					}
				}
			}
		}

	}

	private boolean idem(CtExpression rightHandOperand, CtExpression ctExpression) {
		if (rightHandOperand == null && ctExpression == null)
			return true;
		if (rightHandOperand == null || ctExpression == null)
			return false;
		return rightHandOperand.equals(ctExpression);
	}

	private boolean isInCondition(CtElement element) {
		if (element == null) {
			return false;
		}
		CtIf ifParent = element.getParent(CtIf.class);
		if (ifParent != null && (element.hasParent(ifParent.getCondition()) || element == ifParent.getCondition())) {
			return true;
		}
		CtFor forParent = element.getParent(CtFor.class);
		if (forParent != null
				&& (element.hasParent(forParent.getExpression()) || element == forParent.getExpression())) {
			return true;
		}
		CtWhile whileParent = element.getParent(CtWhile.class);
		if (whileParent != null && (element.hasParent(whileParent.getLoopingExpression())
				|| element == whileParent.getLoopingExpression())) {
			return true;
		}
		return false;
	}

	private boolean hasMetaInIt(CtElement element, String meta) {
		EarlyTerminatingScanner hasMeta = new EarlyTerminatingScanner() {
			@Override
			public void scan(CtElement element) {
				if (element != null && element.getMetadata(meta) != null) {
					setResult(element);
					terminate();
				}
				super.scan(element);
			}
		};
		hasMeta.scan(element);
		return hasMeta.getResult() != null;
	}
}
