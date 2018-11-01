package add.entities;

import java.util.ArrayList;
import java.util.List;

import gumtree.spoon.diff.operations.Operation;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class PatternInstance {

	String patternName;
	Operation op = null;
	CtElement nodeAffectedOp;
	CtElement faultyLine;
	List<CtElement> faultyStatements = new ArrayList<>();

	public PatternInstance(String patternName, Operation op, CtElement nodeAffectedOp) {
		super();
		this.patternName = patternName;
		this.op = op;
		this.nodeAffectedOp = nodeAffectedOp;
	}

	public PatternInstance(String patternName, Operation op, CtElement nodeAffectedOp, CtElement other) {
		super();
		this.patternName = patternName;
		this.op = op;
		this.nodeAffectedOp = nodeAffectedOp;
		this.faultyStatements.add(other);
	}

	public PatternInstance(String patternName, Operation op, CtElement nodeAffectedOp, CtElement other,
			CtElement suspiciousElement) {
		super();
		this.patternName = patternName;
		this.op = op;
		this.nodeAffectedOp = nodeAffectedOp;
		this.faultyStatements.add(other);
		this.faultyLine = suspiciousElement;
	}

	public PatternInstance(String patternName, Operation op, CtElement nodeAffectedOp,
			List<CtElement> faultyStatements) {
		super();
		this.patternName = patternName;
		this.op = op;
		this.nodeAffectedOp = nodeAffectedOp;
		this.faultyStatements = faultyStatements;
	}

	public PatternInstance(String patternName, Operation op, CtElement nodeAffectedOp, List<CtElement> faultyStatements,
			CtElement faultyLine) {
		super();
		this.patternName = patternName;
		this.op = op;
		this.nodeAffectedOp = nodeAffectedOp;
		this.faultyStatements = faultyStatements;
		this.faultyLine = faultyLine;
	}

	public String getPatternName() {
		return patternName;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}

	public Operation getOp() {
		return op;
	}

	public void setOp(Operation op) {
		this.op = op;
	}

	public CtElement getNodeAffectedOp() {
		return nodeAffectedOp;
	}

	public void setNodeAffectedOp(CtElement nodeAffectedOp) {
		this.nodeAffectedOp = nodeAffectedOp;
	}

	public List<CtElement> getFaulty() {
		return faultyStatements;
	}

	public void setFautlyStatements(List<CtElement> otherNodes) {
		this.faultyStatements = otherNodes;
	}

	@Override
	public String toString() {
		return "PatternInstance [\npatternName=" + patternName + ",\n op=" + op + ",\n nodeAffectedOp=" + nodeAffectedOp
				+ ",\n suspNodes=" + faultyStatements + "\n pattern line= " + faultyLine + "\n]";
	}

	public CtElement getFaultyLine() {
		return faultyLine;
	}

	public void setSuspiciousElement(CtElement suspiciousElement) {
		this.faultyLine = suspiciousElement;
	}

}
