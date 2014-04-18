package nodeAST;

public abstract class BinaryExpr extends Expression {
	protected Expression leftHandOperand;
	protected Expression rightHandOperand;


	public BinaryExpr(Expression leftHandOperand, Expression rightHandOperand) {
		this.leftHandOperand=leftHandOperand;
		this.rightHandOperand=rightHandOperand;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BinaryExpr))
			return false;
		BinaryExpr binaryExpr=(BinaryExpr) obj;
		return this.leftHandOperand.equals(binaryExpr.leftHandOperand) &&
				this.rightHandOperand.equals(binaryExpr.rightHandOperand);
	}	

	@Override
	public boolean containsTreeNode(Expression e) {
		if(e.equals(this) || 
				this.leftHandOperand.containsTreeNode(e) ||
				this.rightHandOperand.containsTreeNode(e) )
			return true;
		return false;
	}
}
