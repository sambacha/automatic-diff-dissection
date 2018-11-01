package add.features.detector.repairpatterns;

import java.util.List;

import com.github.gumtreediff.actions.model.Insert;

import add.entities.PatternInstance;
import add.entities.RepairPatterns;
import add.features.detector.spoon.RepairPatternUtils;
import gumtree.spoon.diff.operations.InsertOperation;
import gumtree.spoon.diff.operations.Operation;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

/**
 * 
 */
public class AssigmentDetector extends AbstractPatternDetector {

	private static final String ADD_ASSIGNMENT = "addassignment";

	public AssigmentDetector(List<Operation> operations) {
		super(operations);
	}

	@Override
	public void detect(RepairPatterns repairPatterns) {
		for (int i = 0; i < operations.size(); i++) {
			Operation operation = operations.get(i);
			if (!(operation instanceof InsertOperation) || !(operation.getSrcNode() instanceof CtAssignment)) {
				continue;
			}
			InsertOperation opInsert = (InsertOperation) operation;
			// CtElement dstNode = operation.getDstNode();
			CtElement dstParent = opInsert.getParent();
			if (dstParent instanceof CtBlock) {
				if (dstParent.getMetadata("new") != null) {
					continue;
				}
				dstParent = dstParent.getParent();
			}
			CtElement srcNode = operation.getSrcNode();
			CtElement srcParent = srcNode.getParent();
			if (srcParent instanceof CtBlock) {
				if (srcParent.getMetadata("new") != null) {
					continue;
				}
				srcParent = srcParent.getParent();
				if (srcParent instanceof CtIf) {
					if (RepairPatternUtils.wasConditionChangedInIf(((CtIf) srcParent))) {
						continue;
					}
				}
			}

			if (!(srcNode instanceof CtStatement)) {
				continue;
			}

			// if (!RepairPatternUtils.isThereChangesInChildren(srcNode)) {

			Insert maction = (Insert) operation.getAction();
			List<CtElement> suspicious = MappingAnalysis.getAllStatementsOfParent(maction);

			repairPatterns.incrementFeatureCounterInstance(ADD_ASSIGNMENT,
					new PatternInstance(ADD_ASSIGNMENT, operation, operation.getSrcNode(), suspicious,
							(suspicious.size() > 0) ? suspicious.get(0) : dstParent, maction.getParent()));
			// repairPatterns.incrementFeatureCounter("codeMove", operation);

			// }
		}
	}

}
