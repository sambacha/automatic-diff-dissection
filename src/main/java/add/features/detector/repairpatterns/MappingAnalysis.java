package add.features.detector.repairpatterns;

import java.util.ArrayList;
import java.util.List;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Addition;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.ITree;

import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.diff.Diff;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.LineFilter;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MappingAnalysis {

	public static ITree getParentInSource(Diff diff, Action affectedAction) {
		ITree affected = null;
		if (affectedAction instanceof Addition) {

			ITree parentInRight = diff.getMappingsComp().firstMappedDstParent(affectedAction.getNode());
			if (parentInRight != null)
				return (diff).getMappingsComp().getSrc(parentInRight);
			else {
				return diff.getMappingsComp().firstMappedSrcParent(affectedAction.getNode());
			}

		} else {
			// We are in left
			affected = affectedAction.getNode().getParent();
		}

		return affected;
	}

	public static ITree getTreeInLeft(Diff diff, CtElement elementRight) {

		for (Mapping ms : diff.getMappingsComp().asSet()) {
			if (isIn(elementRight, ms.getSecond())) {
				return ms.getFirst();
			}
		}
		return null;
	}

	public static ITree getParentInRight(Diff diff, Action affectedAction) {
		// ITree affected = null;
		// if (affectedAction instanceof Addition) {

		ITree parentInLeft = diff.getMappingsComp().firstMappedSrcParent(affectedAction.getNode());
		if (parentInLeft != null)
			return diff.getMappingsComp().getDst(parentInLeft);
		else {
			return diff.getMappingsComp().firstMappedDstParent(affectedAction.getNode());
		}
		// }
		// We are in left
		// affected = affectedAction.getNode().getParent();

		// return affected;
	}

	public static ITree getCorrespondingInSourceTree(Diff diff, ITree affectedByOperator, CtElement faulty) {
		ITree nodeFaulty = null;
		for (ITree ctree : affectedByOperator.getDescendants()) {

			if (isIn(faulty, ctree)) {
				nodeFaulty = ctree;
				break;
			}
		}
		if (nodeFaulty == null) {
			for (Mapping ms : diff.getMappingsComp().asSet()) {
				if (isIn(faulty, ms.getFirst())) {
					nodeFaulty = ms.getFirst();
					break;
				}
			}
		}
		return nodeFaulty;
	}

	public static boolean isIn(CtElement faulty, ITree ctree) {
		Object metadata = ctree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

		boolean save = /* ctree.hasLabel() && */metadata != null && metadata.equals(faulty);
		if (save)
			return true;

		metadata = ctree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT_DEST);

		save = metadata != null && metadata.equals(faulty);

		return save;
	}

	public static CtElement getParentLine(LineFilter filter, CtElement parentCtElement) {
		CtElement parentLine = null;
		if (parentCtElement instanceof CtIf || parentCtElement instanceof CtLoop)
			parentLine = parentCtElement;
		else {
			parentLine = parentCtElement.getParent(filter);
			if (parentLine == null)
				parentLine = parentCtElement;
		}
		return parentLine;
	}

	public static List<CtElement> getAllStatementsOfParent(Addition maction) {
		List<CtElement> suspicious = new ArrayList();
		ITree treeparent = maction.getParent();
		List<ITree> s = treeparent.getChildren();
		for (ITree iTree : s) {
			CtElement e = (CtElement) iTree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);
			if (e instanceof CtStatement)
				suspicious.add(e);
		}
		return suspicious;
	}

	public static List<CtElement> getFollowStatements(Addition maction) {
		List<CtElement> suspicious = new ArrayList();
		ITree treeparent = maction.getParent();
		int possition = maction.getPosition();
		List<ITree> s = treeparent.getChildren().subList(possition, treeparent.getChildren().size());
		for (ITree iTree : s) {
			suspicious.add((CtElement) iTree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT));
		}
		return suspicious;
	}
}
