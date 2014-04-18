package org.uva.sea.ql.ast.expr;

import org.uva.sea.ql.ast.type.Bool;
import org.uva.sea.ql.ast.type.Type;
import org.uva.sea.ql.checker.visitor.IExprVisitor;

public class Not extends Unary {

	public Not(Expr arg) {
		super(arg);
	}

	@Override
	public <T> T accept(IExprVisitor<T> ev) {
		return ev.visit(this);
	}

	@Override
	public Type hasType() {
		return new Bool();
	}

	public String toString(){
		return "!"+this.getArg().toString();
	}

}
