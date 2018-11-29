package add.features.detector.repairpatterns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Addition;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.ITree;

import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.MoveOperation;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;
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

	public static boolean isIn(CtElement faulty, ITree ctree) {
		Object metadata = ctree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

		boolean save = /* ctree.hasLabel() && */metadata != null && metadata.equals(faulty);
		if (save)
			return true;

		metadata = ctree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT_DEST);

		save = metadata != null && metadata.equals(faulty);

		return save;
	}

	public static CtElement getParentLineOld(LineFilter filter, CtElement element) {

		if (element.getParent() instanceof CtBlock) {
			return element;
		}

		if (element.getRoleInParent().equals(CtRole.CONDITION) ||
		//
				(element.getRoleInParent().equals(CtRole.EXPRESSION) && !(element.getParent() instanceof CtReturn))) {
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

		return parentLine;
	}

	public static CtElement getParentLine(LineFilter filter, CtElement element) {

		CtElement parentLine = null;

		parentLine = element.getParent(filter);
		if (parentLine == null)
			parentLine = element;

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

	@SuppressWarnings("unused")
	public static ITree getTree(CtElement parentLine) {

		ITree lineTree = (ITree) ((parentLine.getMetadata("tree") != null) ? parentLine.getMetadata("tree")
				: parentLine.getMetadata("gtnode"));

		if (parentLine instanceof CtIf) {

			ITree copiedIfTree = lineTree.deepCopy();
			// We keep only the first child (the condition)

			// We remove the else
			if (copiedIfTree.getChildren().size() == 3) {

				ITree elseTree = copiedIfTree.getChildren().get(2);
				copiedIfTree.getChildren().remove(2);
			} else
			// we remove the then
			if (copiedIfTree.getChildren().size() == 2) {

				ITree thenTree = copiedIfTree.getChildren().get(1);
				copiedIfTree.getChildren().remove(1);
			} else {
				System.err.println("ERRORIf with  More than 3 children");
			}

			return copiedIfTree;

		} else// todo DOUntil
		if (parentLine instanceof CtWhile || parentLine instanceof CtFor || parentLine instanceof CtForEach) {
			ITree copiedIfTree = lineTree.deepCopy();

			// we remove the then
			if (copiedIfTree.getChildren().size() <= 2) {

				ITree thenTree = copiedIfTree.getChildren().get(1);
				copiedIfTree.getChildren().remove(1);
			}

			return copiedIfTree;
		}

		return lineTree;
	}

	/**
	 * 
	 * @param diff
	 * @param maction
	 * @return
	 */
	public static List<ITree> getFollowStatementsInLeft(Diff diff, Addition maction) {

		List<ITree> followingInLeft = new ArrayList();

		ITree parentRight = null;
		if (maction instanceof Insert) {

			// Node at right
			ITree affectedRight = maction.getNode();
			// Parent at right
			parentRight = affectedRight.getParent();
			int position = getPositionInParent(parentRight, affectedRight);
			if (position >= 0) {

				int nrSiblings = parentRight.getChildren().size();
				if (position == nrSiblings - 1) {
					// The last element, let's suppose suspicious
					List<ITree> followingSiblingsInRing = new ArrayList((parentRight.getChildren()));
					Collections.reverse(followingSiblingsInRing);
					computeLeftFromRight(diff, followingInLeft, followingSiblingsInRing);

				} else {
					List<ITree> followingSiblingsInRing = parentRight.getChildren().subList(position + 1, nrSiblings);
					computeLeftFromRight(diff, followingInLeft, followingSiblingsInRing);

					if (followingInLeft.isEmpty()) {
						// all the following are inserted, let's find the last one
						// The last element, let's suppose suspicious
						followingSiblingsInRing = new ArrayList((parentRight.getChildren()));
						Collections.reverse(followingSiblingsInRing);
						computeLeftFromRight(diff, followingInLeft, followingSiblingsInRing);
					}
				}

			} else {
				System.out.println("Inserted node Not found in parent");
			}
		}

		return followingInLeft;
	}

	public static void computeLeftFromRight(Diff diff, List<ITree> followingInLeft,
			List<ITree> followingSiblingsInRing) {
		for (ITree siblingRight : followingSiblingsInRing) {
			// The mapped at the left
			ITree mappedSiblingLeft = getLeftFromRightNodeMapped(diff, siblingRight);

			if (mappedSiblingLeft != null) {
				// lets check if it's null
				boolean affectedByMoved = diff.getRootOperations().stream()
						.filter(e -> (e instanceof MoveOperation && mappedSiblingLeft.equals(e.getAction().getNode())))
						.findFirst().isPresent();
				// If mapped left is not moved
				if (!affectedByMoved) {
					followingInLeft.add(mappedSiblingLeft);
				}
			}

		}
	}

	public static int getPositionInParent(ITree parent, ITree element) {
		int i = 0;
		for (ITree child : parent.getChildren()) {
			if (child == element)
				return i;
			i++;
		}
		return -1;
	}

	private static boolean isRightNodeMapped(Diff diff, ITree iTree) {

		for (Mapping map : diff.getMappingsComp().asSet()) {
			if (map.getSecond().equals(iTree)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isRightNodeMappedANDallChildren(Diff diff, ITree iTree) {

		for (Mapping map : diff.getMappingsComp().asSet()) {
			if (map.getSecond().equals(iTree)) {

				for (ITree tc : iTree.getChildren()) {
					if (!isRightNodeMappedANDallChildren(diff, tc))
						return false;
				}
				return true;
			}
		}

		return false;
	}

	public static ITree getLeftFromRightNodeMapped(Diff diff, ITree iTree) {

		for (Mapping map : diff.getMappingsComp().asSet()) {
			if (map.getSecond().equals(iTree)) {
				return map.getFirst();
			}
		}

		return null;
	}

	public static ITree getRightFromLeftNodeMapped(Diff diff, ITree iTree) {

		for (Mapping map : diff.getMappingsComp().asSet()) {
			if (map.getFirst().equals(iTree)) {
				return map.getSecond();
			}
		}

		return null;
	}

	public static List<CtElement> getTreeLeftMovedFromRight(Diff diff, CtElement element) {
		// Get the nodes moved in the right
		List<CtElement> movesInRight = element
				.getElements(e -> e.getMetadata("isMoved") != null && e.getMetadata("root") != null);

		List<CtElement> suspLeft = new ArrayList();
		for (CtElement ctElement : movesInRight) {

			ITree mappedLeft = MappingAnalysis.getLeftFromRightNodeMapped(diff,
					(ITree) ctElement.getMetadata("gtnode"));
			if (mappedLeft != null) {
				suspLeft.add((CtElement) mappedLeft.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT));

			} else {
				return null;
			}

		}
		return suspLeft;
	}
}
