package add.features.detector.spoon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtElement;

public class LogicalExpressionAnalyzer {
	
	public static List<CtExpression> getAllRootLogicalExpressions (CtElement parentline) {
		
		List<CtExpression> logicalExpressions = new ArrayList();

		CtElement elementToStudy = retrieveElementToStudy(parentline);

		List<CtExpression> expressionssFromFaultyLine = elementToStudy.getElements(e -> (e instanceof CtExpression)).stream()
				.map(CtExpression.class::cast).collect(Collectors.toList());
	    
	    for(int index=0; index<expressionssFromFaultyLine.size(); index++) {

	        if(isBooleanExpression(expressionssFromFaultyLine.get(index)) &&
	    			!whetherparentboolean(expressionssFromFaultyLine.get(index)) &&
	    			!logicalExpressions.contains(expressionssFromFaultyLine.get(index))) {
	            logicalExpressions.add(expressionssFromFaultyLine.get(index));
	        }
	    } 
		
		return logicalExpressions;
	}

	public static CtElement retrieveElementToStudy(CtElement element) {

		if (element instanceof CtIf) {
			return (((CtIf) element).getCondition());
		} else if (element instanceof CtWhile) {
			return (((CtWhile) element).getLoopingExpression());
		} else if (element instanceof CtFor) {
			return (((CtFor) element).getExpression());
		} else if (element instanceof CtDo) {
			return (((CtDo) element).getLoopingExpression());
//		} else if (element instanceof CtConditional) {
//			return (((CtConditional) element).getCondition());
		} else if (element instanceof CtForEach) {
			return (((CtForEach) element).getExpression());
		} else if (element instanceof CtSwitch) {
			return (((CtSwitch) element).getSelector());
		} else
			return (element);
	}
	
    public static boolean whetherparentboolean (CtExpression tostudy) {
		
		CtElement parent= tostudy;
		while(parent!=null) {		
			parent=parent.getParent();
			
			if(isBooleanExpression(parent))
				return true;
		}
		
		return false;
	}
	
    public static boolean isBooleanExpression(CtElement currentexpression) {
		
		if (currentexpression == null)
			return false;
		
		if (isLogicalExpression(currentexpression)) {
			return true;
		}
		
		if(currentexpression instanceof CtExpression) {
		   
		   CtExpression exper= (CtExpression) currentexpression;
		   try {
		      if (exper.getType() != null
				&& exper.getType().unbox().toString().equals("boolean")) {
			  return true;
		     }
		   } catch (Exception e) {
			   return false;
		   }
		}

		return false;
	}
    
    public static boolean isLogicalExpression (CtElement currentElement) {
    	
		if (currentElement == null)
			return false;
		
		if ((currentElement instanceof CtBinaryOperator)) {
			
			CtBinaryOperator binOp = (CtBinaryOperator) currentElement;
						
			if(binOp.getKind().equals(BinaryOperatorKind.AND) || binOp.getKind().equals(BinaryOperatorKind.OR)
				|| binOp.getKind().equals(BinaryOperatorKind.EQ)
				|| binOp.getKind().equals(BinaryOperatorKind.GE)
				|| binOp.getKind().equals(BinaryOperatorKind.GT)
				|| binOp.getKind().equals(BinaryOperatorKind.INSTANCEOF)
				|| binOp.getKind().equals(BinaryOperatorKind.LE)
				|| binOp.getKind().equals(BinaryOperatorKind.LT)
				|| binOp.getKind().equals(BinaryOperatorKind.NE)
				|| (binOp.getType() != null &&
                      binOp.getType().unbox().getSimpleName().equals("boolean")))
				
				   return true;
		}
		
		if(currentElement.getParent() instanceof CtConditional) {
			CtConditional cond = (CtConditional) currentElement.getParent();
			if(currentElement.equals(cond.getCondition()))
				return true;
		}
		
		if(currentElement.getParent() instanceof CtIf) {
			CtIf ifcond = (CtIf) currentElement.getParent();
			if(currentElement.equals(ifcond.getCondition()))
				return true;
		}
		
		if(currentElement.getParent() instanceof CtWhile) {
			CtWhile whilecond = (CtWhile) currentElement.getParent();
			if(currentElement.equals(whilecond.getLoopingExpression()))
				return true;
		}
		
		if(currentElement.getParent() instanceof CtDo) {
			CtDo docond = (CtDo) currentElement.getParent();
			if(currentElement.equals(docond.getLoopingExpression()))
				return true;
		}
		
		if(currentElement.getParent() instanceof CtFor) {
			CtFor forcond = (CtFor) currentElement.getParent();
			if(currentElement.equals(forcond.getExpression()))
				return true;
		}
		
		return false;
	}
}
