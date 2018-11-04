package add.features.detector.repairpatterns;

import java.util.ArrayList;
import java.util.List;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Addition;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.ITree;

import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.diff.Diff;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
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
		List<ITree> nodes = new ArrayList<>();

		if (isIn(faulty, affectedByOperator))
			return affectedByOperator;

		for (ITree ctree : affectedByOperator.getDescendants()) {

			if (isIn(faulty, ctree)) {
				nodes.add(ctree);
				// nodeFaulty = ctree;
				// break;
			}
		}
		if (nodeFaulty == null && nodes.isEmpty()) {
			for (Mapping ms : diff.getMappingsComp().asSet()) {
				if (isIn(faulty, ms.getFirst())) {
					// nodeFaulty = ms.getFirst();
					// break;
					nodes.add(ms.getFirst());
				}
			}
		}
		if (nodes.isEmpty()) {
			// return null;
			ITree mappedsource = diff.getMappingsComp().firstMappedSrcParent(affectedByOperator);
			if (mappedsource != null)
				return mappedsource;
			else
				return diff.getMappingsComp().firstMappedDstParent(affectedByOperator);
		}

		if (nodes.size() == 1)
			return nodes.get(0);
		else {
			for (ITree iTree : nodes) {
				if (!iTree.getLabel().isEmpty())
					return iTree;
			}
			return nodes.get(0);
		}
		// return nodeFaulty;
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

	public static CtElement getParentLine(LineFilter filter, CtElement element) {
		if (element.getParent() instanceof CtBlock) {
			return element;
		}

		CtElement parentCondition = element.getParent(e -> e != null && e.getRoleInParent() != null
				&& (e.getRoleInParent().equals(CtRole.CONDITION) || e.getRoleInParent().equals(CtRole.EXPRESSION)));

		if (parentCondition != null) {

			CtElement parent = parentCondition.getParent();
			if (parent instanceof CtReturn)
				return parent;
			else
				return parentCondition;

		}
		// Not in if/loop condition
		CtElement parentLine = null;

		parentLine = element.getParent(filter);
		if (parentLine == null)
			parentLine = element;

//		if (parentCtElement instanceof CtIf || parentCtElement instanceof CtLoop)
//			parentLine = parentCtElement;
//		else {
//			parentLine = parentCtElement.getParent(filter);
//			if (parentLine == null)
//				parentLine = parentCtElement;
//		}
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

		if (treeparent.getChildren().isEmpty())
			return suspicious;

		if (possition >= treeparent.getChildren().size()) {
			// The last one
			suspicious.add((CtElement) treeparent.getChildren().get(treeparent.getChildren().size() - 1)
					.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT));
		} else {
			List<ITree> s = treeparent.getChildren().subList(possition, treeparent.getChildren().size());
			for (ITree iTree : s) {
				suspicious.add((CtElement) iTree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT));
			}
		}
		return suspicious;
	}
}
