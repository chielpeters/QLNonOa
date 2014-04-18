package org.uva.sea.ql.ast.form;

import org.uva.sea.ql.ast.expr.Ident;
import org.uva.sea.ql.ast.stmt.Block;

public class Form {
	private Ident ident;
	private Block stmts;
	
	public Form(Ident ident, Block stmts){
		this.setIdent(ident);
		this.stmts = stmts;
	}
	
	public Ident getIdent() {
		return ident;
	}

	public void setIdent(Ident ident) {
		this.ident = ident;
	}

	public Block getBlock(){
		return this.stmts;
	}
	
	public String toString(){
		String form = getIdent().getName();
		form += getBlock().toString();
		return form;
	}

}
