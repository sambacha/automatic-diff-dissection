package diffson;

import com.github.gumtreediff.tree.ITree;
import com.google.gson.JsonObject;

import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.builder.jsonsupport.NodePainter;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ReturnTypePainter implements NodePainter {

	@Override
	public void paint(ITree tree, JsonObject jsontree) {

		CtElement ctelement = (CtElement) tree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);
		if (ctelement != null && ctelement instanceof CtExpression) {
			String type = "java.lang.Object";
			CtExpression exp = (CtExpression) ctelement;
			if (exp.getType() != null)
				type = exp.getType().getQualifiedName();
			else
			// Let's try to infer the type
			if (exp instanceof CtBinaryOperator) {
				// let's check if it;s logical operator
				CtBinaryOperator binOp = (CtBinaryOperator) exp;
				if (binOp.getKind().equals(BinaryOperatorKind.AND) || binOp.getKind().equals(BinaryOperatorKind.OR)
						|| binOp.getKind().equals(BinaryOperatorKind.EQ)
						|| binOp.getKind().equals(BinaryOperatorKind.GE)
						|| binOp.getKind().equals(BinaryOperatorKind.GT)
						|| binOp.getKind().equals(BinaryOperatorKind.INSTANCEOF)
						|| binOp.getKind().equals(BinaryOperatorKind.LE)
						|| binOp.getKind().equals(BinaryOperatorKind.LT)
						|| binOp.getKind().equals(BinaryOperatorKind.NE))
					;
				type = Boolean.class.getCanonicalName();

			}
			jsontree.addProperty("return_type", type);

		}

	}

}
