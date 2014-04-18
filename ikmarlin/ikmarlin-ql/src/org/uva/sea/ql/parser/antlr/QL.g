grammar QL;
options {backtrack=false; memoize=true; language = Java;}

@parser::header
{
package org.uva.sea.ql.parser.antlr;
import org.uva.sea.ql.ast.expr.*;
import org.uva.sea.ql.ast.form.*;
import org.uva.sea.ql.ast.type.*;
import org.uva.sea.ql.ast.stmt.*;
import antlr.ANTLRException;
import org.uva.sea.ql.checker.exception.QLException;
import org.uva.sea.ql.parser.test.ParseError;
}

@lexer::header
{
package org.uva.sea.ql.parser.antlr;
}

@parser::members {
	private List<QLException> errors = new ArrayList <QLException> ();
	 
	public List<QLException> getAllErrors() {
	  return new ArrayList<QLException>(errors);
	}
	
	public boolean hasErrors() {
	  return !errors.isEmpty();
	}
	 
	public void reportError(RecognitionException e) {
		if ( state.errorRecovery ) {
		  return;
		}
		state.syntaxErrors++;
		state.errorRecovery = true;
    String hdr = getErrorHeader(e);
    String msg = getErrorMessage(e, tokenNames);
		msg = null;
		if ( e instanceof NoViableAltException ) {
			NoViableAltException nvae = (NoViableAltException)e;
			msg = " unexpected token: '"+e.token.getText()+"'";
		} else {
			msg = getErrorMessage(e, tokenNames);
		}
    errors.add(new ParseError(hdr+" "+msg));
	}
}

primary returns [Expr result]
    : Bool {
        if($Bool.text.equals("true")){
          $result = new BoolLiteral(true);
        }else{
          $result = new BoolLiteral(false);
        }
      }
    | Int { $result = new IntLiteral(Integer.parseInt($Int.text)); }
    | Str { $result = new StrLiteral($Str.text); }
    | Ident { $result = new Ident($Ident.text); }
    | '(' x = orExpr ')' { $result = $x.result; }
    ;
    
unExpr returns [Expr result]
    :  '+' x=unExpr { $result = new Pos($x.result); }
    |  '-' x=unExpr { $result = new Neg($x.result); }
    |  '!' x=unExpr { $result = new Not($x.result); }
    |  x=primary    { $result = $x.result; }
    ;    
    
mulExpr returns [Expr result]
    :   lhs=unExpr { $result=$lhs.result; } ( op=( '*' | '/' ) rhs=unExpr 
    { 
      if ($op.text.equals("*")) {
        $result = new Mul($result, rhs);
      }
      if ($op.text.equals("/")) {
        $result = new Div($result, rhs);      
      }
    })*
    ;
    
addExpr returns [Expr result]
    :   lhs=mulExpr { $result=$lhs.result; } ( op=('+' | '-') rhs=mulExpr
    { 
      if ($op.text.equals("+")) {
        $result = new Add($result, rhs);
      }
      if ($op.text.equals("-")) {
        $result = new Sub($result, rhs);      
      }
    })*
    ;
  
relExpr returns [Expr result]
    :   lhs=addExpr { $result=$lhs.result; } ( op=('<'|'<='|'>'|'>='|'=='|'!=') rhs=addExpr 
    { 
      if ($op.text.equals("<")) {
        $result = new LT($result, rhs);
      }
      if ($op.text.equals("<=")) {
        $result = new LEq($result, rhs);      
      }
      if ($op.text.equals(">")) {
        $result = new GT($result, rhs);
      }
      if ($op.text.equals(">=")) {
        $result = new GEq($result, rhs);      
      }
      if ($op.text.equals("==")) {
        $result = new Eq($result, rhs);
      }
      if ($op.text.equals("!=")) {
        $result = new NEq($result, rhs);
      }
    })*
    ;
    
andExpr returns [Expr result]
    :   lhs=relExpr { $result=$lhs.result; } ( '&&' rhs=relExpr { $result = new And($result, rhs); } )*
    ;
    
orExpr returns [Expr result]
    :   lhs=andExpr { $result = $lhs.result; } ( '||' rhs=andExpr { $result = new Or($result, rhs); } )*
    ;
    
type returns [Type result]
    : 'boolean' {$result = new Bool(); }
    | 'string' {$result = new Str(); }
    | 'integer' {$result = new Int(); }
    ;
    
stmt returns [Stmt result]
    : question { $result = $question.result; }
    | conditionalQuestion { $result = $conditionalQuestion.result; }
    ;
    
ifThenStatement returns [Stmt result]
    : 'if' '(' condition=orExpr ')' '{' ifBlock = block '}' { $result = new IfThenStatement($condition.result, $ifBlock.result); }
    ;
    
conditionalQuestion returns [Stmt result]
    : 'if' '(' condition=orExpr ')' '{' ifBlock = block '}' { $result = new IfThenStatement($condition.result, $ifBlock.result); }
      ('else' '{' elseBlock = block '}' { $result = new IfThenElseStatement($condition.result, $ifBlock.result, $elseBlock.result); })?
    ;
    
question returns [Stmt result]
    @init
    {
      Question q = null;
    }
    : answerableQuestion { $result = q = $answerableQuestion.result; }
      ('(' computation = orExpr ')' 
				{
				  $result = new ComputedQuestion(q.getIdent(), q.getLabel(), q.getType(), $computation.result); 
				}
      )?
    ;
    
answerableQuestion returns [Question result]
    : Ident ':' label = Str type { $result = new AnswerableQuestion(new Ident($Ident.text), $label.text, $type.result); }
    ;
    
block returns [Block result]
    @init
    {
      $result = new Block();
    }
    : (stmt { $result.addStmt($stmt.result); })*
    ;
    
form returns [Form result]
    : 'form' Ident '{' block '}' { $result = new Form(new Ident($Ident.text), $block.result); }
    ;
    
// Tokens
WS  :	(' ' | '\t' | '\n' | '\r') { $channel=HIDDEN; }
    ;

SLComment
    : '//'(.)*('\r')*'\n' {$channel=HIDDEN;}
    ;
  
 MLCOMMENT 
    : '/*' .* '*/' {$channel=HIDDEN;}
    ;

Bool
    : 'true'
    | 'false'
    ;
    
Int
    : ('0'..'9')+
    ;
    
Str
    : '"' .* '"'
    ;
    
Ident
    :   ('_')* ('a'..'z'|'A'..'Z'|'0'..'9') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;