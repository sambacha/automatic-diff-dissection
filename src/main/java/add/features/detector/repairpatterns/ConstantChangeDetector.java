package add.features.detector.repairpatterns;

import java.util.List;

import com.github.gumtreediff.tree.ITree;

import add.entities.PatternInstance;
import add.entities.RepairPatterns;
import add.features.detector.spoon.RepairPatternUtils;
import gumtree.spoon.diff.operations.DeleteOperation;
import gumtree.spoon.diff.operations.InsertOperation;
import gumtree.spoon.diff.operations.Operation;
import gumtree.spoon.diff.operations.UpdateOperation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.LineFilter;

/**
 * Created by tdurieux
 */
public class ConstantChangeDetector extends AbstractPatternDetector {

	private static final String CONST_CHANGE = "constChange";

	public ConstantChangeDetector(List<Operation> operations) {
		super(operations);
	}

	@Override
	public void detect(RepairPatterns repairPatterns) {
		for (int i = 0; i < operations.size(); i++) {
			Operation operation = operations.get(i);
			if ((operation instanceof UpdateOperation)) {
				CtElement srcNode = operation.getSrcNode();
				if (operation.getSrcNode().getParent().getMetadata("new") != null
						|| operation.getSrcNode().getParent().getMetadata("isMoved") != null) {
					continue;
				}
				CtElement parent = MappingAnalysis.getParentLine(new LineFilter(), srcNode);
				ITree lineTree = (ITree) ((parent.getMetadata("tree") != null) ? parent.getMetadata("tree")
						: parent.getMetadata("gtnode"));

				if (srcNode instanceof CtLiteral) {
					repairPatterns.incrementFeatureCounterInstance(CONST_CHANGE, new PatternInstance(CONST_CHANGE,
							operation, operation.getDstNode(), srcNode, parent, lineTree));
				}
				if (srcNode instanceof CtVariableAccess
						&& RepairPatternUtils.isConstantVariableAccess((CtVariableAccess) srcNode)) {
					// repairPatterns.incrementFeatureCounter(CONST_CHANGE, operation);
					repairPatterns.incrementFeatureCounterInstance(CONST_CHANGE, new PatternInstance(CONST_CHANGE,
							operation, operation.getDstNode(), srcNode, parent, lineTree));
				}
				if (srcNode instanceof CtTypeAccess
						&& RepairPatternUtils.isConstantTypeAccess((CtTypeAccess) srcNode)) {
					// repairPatterns.incrementFeatureCounter(CONST_CHANGE, operation);
					repairPatterns.incrementFeatureCounterInstance(CONST_CHANGE, new PatternInstance(CONST_CHANGE,
							operation, operation.getDstNode(), srcNode, parent, lineTree));
				}
			} else {
				if (operation instanceof DeleteOperation && operation.getSrcNode() instanceof CtLiteral) {
					CtLiteral ctLiteral = (CtLiteral) operation.getSrcNode();
					// try to search a replacement for the literal
					for (int j = 0; j < operations.size(); j++) {
						Operation operation2Insert = operations.get(j);
						if (operation2Insert instanceof InsertOperation) {
							CtElement ctElement = operation2Insert.getSrcNode();
							boolean isConstantVariable = false;
							if ((ctElement instanceof CtVariableAccess
									&& RepairPatternUtils.isConstantVariableAccess((CtVariableAccess) ctElement))
									|| (ctElement instanceof CtTypeAccess
											&& RepairPatternUtils.isConstantTypeAccess((CtTypeAccess) ctElement))) {
								isConstantVariable = true;
							}
							if (((InsertOperation) operation2Insert).getParent() == ctLiteral.getParent()
									&& isConstantVariable) {
								CtElement parent = MappingAnalysis.getParentLine(new LineFilter(), ctLiteral);
								ITree lineTree = (ITree) ((parent.getMetadata("tree") != null)
										? parent.getMetadata("tree")
										: parent.getMetadata("gtnode"));

								repairPatterns.incrementFeatureCounterInstance(CONST_CHANGE,
										new PatternInstance(CONST_CHANGE, operation2Insert,
												operation2Insert.getSrcNode(), ctLiteral, parent, lineTree));
							}
						}
					}
				}
			}
		}
	}

}
