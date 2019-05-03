package diffson;

import java.util.List;

import com.github.gumtreediff.tree.ITree;
import com.google.gson.JsonObject;

import add.features.detector.spoon.LogicalExpressionAnalyzer;
import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.builder.jsonsupport.NodePainter;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ReturnTypePainter implements NodePainter {
	
	List<CtExpression> allrootlogicalexpers;
	
	public ReturnTypePainter(CtElement faultyLine) {
		allrootlogicalexpers = LogicalExpressionAnalyzer.getAllRootLogicalExpressions(faultyLine);
	}

	@Override
	public void paint(ITree tree, JsonObject jsontree) {

		CtElement ctelement = (CtElement) tree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);
		if (ctelement != null && ctelement instanceof CtExpression) {
			String type = "java.lang.Object";
			CtExpression exp = (CtExpression) ctelement;
			if (LogicalExpressionAnalyzer.isBooleanExpression(exp)) {
				 type = Boolean.class.getCanonicalName();
			}
			else
				if (exp.getType() != null)
					type = exp.getType().getQualifiedName();
			// Let's try to infer the type
//			if (exp instanceof CtBinaryOperator) {
//				// let's check if it;s logical operator
//				CtBinaryOperator binOp = (CtBinaryOperator) exp;
//				if (binOp.getKind().equals(BinaryOperatorKind.AND) || binOp.getKind().equals(BinaryOperatorKind.OR)
//						|| binOp.getKind().equals(BinaryOperatorKind.EQ)
//						|| binOp.getKind().equals(BinaryOperatorKind.GE)
//						|| binOp.getKind().equals(BinaryOperatorKind.GT)
//						|| binOp.getKind().equals(BinaryOperatorKind.INSTANCEOF)
//						|| binOp.getKind().equals(BinaryOperatorKind.LE)
//						|| binOp.getKind().equals(BinaryOperatorKind.LT)
//						|| binOp.getKind().equals(BinaryOperatorKind.NE))
//				type = Boolean.class.getCanonicalName();
//
//			}
			
			jsontree.addProperty("return_type", type);
			
			if(type.toLowerCase().equals("boolean") || type.toLowerCase().equals("java.lang.boolean")) {
				
				for(int index=0; index<allrootlogicalexpers.size(); index++) {
					CtExpression specificlogicalexper=allrootlogicalexpers.get(index);
					if(specificlogicalexper.equals(exp)) {
						jsontree.addProperty("index_of_logical_exper", "logical_expression_"+Integer.toString(index));
						break;
					}
				}
			}
		}

	}
}
