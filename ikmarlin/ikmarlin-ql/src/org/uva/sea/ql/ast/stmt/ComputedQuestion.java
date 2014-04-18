package org.uva.sea.ql.ast.stmt;

import org.uva.sea.ql.ast.expr.Expr;
import org.uva.sea.ql.ast.expr.Ident;
import org.uva.sea.ql.ast.type.Type;
import org.uva.sea.ql.checker.visitor.IStmtVisitor;

public class ComputedQuestion extends Question {
	
	private Expr computation;

	public ComputedQuestion(Ident ident, String label, Type type, Expr computation) {
		super(ident, label, type);
		this.setComputation(computation);
	}

	public Expr getComputation() {
		return computation;
	}

	public void setComputation(Expr computation) {
		this.computation = computation;
	}

	@Override
	public void accept(IStmtVisitor sv) {
		sv.visit(this);
	}
	
	@Override
	public String toString(){
		String question = "[";
		question += getIdent().getName();
		question += " = ";
		question += getType().toString();
		question += "(";
		question += getComputation().toString();
		question += ")";
		question += "]";
		return question;
	}

}
