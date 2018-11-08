package add.features.detector.repairpatterns;

import java.util.ArrayList;
import java.util.List;

import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.tree.ITree;

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
import spoon.reflect.visitor.filter.LineFilter;

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

			List susp = new ArrayList<>();

			CtElement lineP = null;
			ITree lineTree = null;

			List<CtElement> follow = MappingAnalysis.getFollowStatements(diff, maction);
			if (!follow.isEmpty()) {
				lineP = follow.get(0);

			} else {
				// in case of adding at the end
				lineP = MappingAnalysis.getParentLine(new LineFilter(), srcNode);
			}
			susp.add(lineP);

			lineTree = MappingAnalysis.getCorrespondingInSourceTree(diff, operation.getAction().getNode(), lineP);

			repairPatterns.incrementFeatureCounterInstance(ADD_ASSIGNMENT,
					new PatternInstance(ADD_ASSIGNMENT, operation, operation.getSrcNode(), follow, lineP, lineTree));
			// repairPatterns.incrementFeatureCounter("codeMove", operation);

			// }
		}
	}

}
