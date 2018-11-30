package add.features.detector.repairpatterns;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gumtreediff.tree.ITree;

import add.entities.PatternInstance;
import add.entities.RepairPatterns;
import add.features.detector.spoon.RepairPatternUtils;
import add.features.detector.spoon.SpoonHelper;
import add.features.detector.spoon.filter.NullCheckFilter;
import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.diff.operations.InsertOperation;
import gumtree.spoon.diff.operations.Operation;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.filter.LineFilter;

/**
 * Created by fermadeiral
 */
public class MissingNullCheckDetector extends AbstractPatternDetector {
	public static final String MISS_NULL_CHECK_N = "missNullCheckN";
	public static final String MISS_NULL_CHECK_P = "missNullCheckP";
	private static Logger LOGGER = LoggerFactory.getLogger(MissingNullCheckDetector.class);

	public MissingNullCheckDetector(List<Operation> operations) {
		super(operations);
	}

	@Override
	public void detect(RepairPatterns repairPatterns) {
		for (Operation operation : this.operations) {
			if (operation instanceof InsertOperation) {

				CtElement srcNode = operation.getSrcNode();

				if (srcNode instanceof spoon.reflect.declaration.CtMethod)
					continue;

				SpoonHelper.printInsertOrDeleteOperation(srcNode.getFactory().getEnvironment(), srcNode, operation);

				List<CtBinaryOperator> binaryOperatorList = srcNode.getElements(new NullCheckFilter());
				for (CtBinaryOperator binaryOperator : binaryOperatorList) {
					if (RepairPatternUtils.isNewBinaryOperator(binaryOperator)) {
						if (RepairPatternUtils.isNewConditionInBinaryOperator(binaryOperator)) {
							LOGGER.debug("-New null check: " + binaryOperator.toString());

							final CtElement referenceExpression;
							if (binaryOperator.getRightHandOperand().toString().equals("null")) {
								referenceExpression = binaryOperator.getLeftHandOperand();
							} else {
								referenceExpression = binaryOperator.getRightHandOperand();
							}
							LOGGER.debug("-Reference expression: " + referenceExpression.toString());

							boolean wasPatternFound = false;

							List soldt = null;
							List soldelse = null;

							CtVariable variable = RepairPatternUtils
									.getVariableFromReferenceExpression(referenceExpression);
							if (variable == null || (variable != null && !RepairPatternUtils.isNewVariable(variable))) {
								wasPatternFound = true;
								//
								if (binaryOperator.getParent() instanceof CtConditional) {
									CtConditional c = (CtConditional) binaryOperator.getParent();
									CtElement thenExpr = c.getThenExpression();
									CtElement elseExp = c.getElseExpression();

									if (thenExpr != null) {
										soldt = new ArrayList<>();
										if (thenExpr.getMetadata("new") == null)
											soldt.add(thenExpr);

									}
									if (elseExp != null) {
										soldelse = new ArrayList<>();
										if (elseExp.getMetadata("new") == null)
											soldelse.add(elseExp);
									}

								}

							} // else {
							CtElement parent = binaryOperator.getParent(new LineFilter());

							if (parent instanceof CtIf) {
								CtBlock thenBlock = ((CtIf) parent).getThenStatement();
								CtBlock elseBlock = ((CtIf) parent).getElseStatement();

								if (thenBlock != null) {
									soldt = RepairPatternUtils.getIsThereOldStatementInStatementList(diff,
											thenBlock.getStatements());
									if (!soldt.isEmpty())
										wasPatternFound = true;

								} // else
								if (elseBlock != null) {
									soldelse = RepairPatternUtils.getIsThereOldStatementInStatementList(diff,
											elseBlock.getStatements());
									if (!soldelse.isEmpty())
										wasPatternFound = true;
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

									lineTree = MappingAnalysis.getTree(lineP);

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
									lineTree = treeInLeft.get(0);

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
