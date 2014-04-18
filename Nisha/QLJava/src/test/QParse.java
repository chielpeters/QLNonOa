package test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

import antlr4.QLLexer;
import antlr4.QLParser;
import ast.form.Form;
import expr.Expr;

public class QParse{

	//form
	private QLParser fparser(String src) throws IOException {
		InputStream is = new FileInputStream(src);
		ANTLRInputStream input = new ANTLRInputStream(is);
		QLLexer lexer = new QLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		return new QLParser(tokens);
	}

	//expression
	private QLParser eparser(String src) throws IOException {
		ANTLRInputStream input = new ANTLRInputStream(src);
		QLLexer lexer = new QLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		return new QLParser(tokens);
	}

	public Form parseF(String src) throws ParseError, IOException {
		QLParser parser = fparser(src);
		try {
			return parser.forms().result;
		} catch (RecognitionException e) {
			throw new ParseError(e.getMessage());
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
	}

	public Expr parseE(String src) throws ParseError, IOException {
		QLParser parser = eparser(src);
		try {
			return parser.orExpr().result;
		} catch (RecognitionException e) {
			throw new ParseError(e.getMessage());
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
	}
}