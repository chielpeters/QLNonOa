package org.uva.sea.ql.gui.questionaire;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.uva.sea.ql.ast.Expression;
import org.uva.sea.ql.ast.StatementVisitor;
import org.uva.sea.ql.ast.literal.StringLiteral;
import org.uva.sea.ql.ast.statement.ExpressionQuestion;
import org.uva.sea.ql.ast.statement.Form;
import org.uva.sea.ql.ast.statement.IfElseStatement;
import org.uva.sea.ql.ast.statement.IfStatement;
import org.uva.sea.ql.ast.statement.Question;
import org.uva.sea.ql.ast.statement.Questions;
import org.uva.sea.ql.ast.statement.Statement;
import org.uva.sea.ql.gui.widget.Widget;

public class Renderer implements StatementVisitor {

	private final JPanel mainpanel;
	private final State state;
		
	public static JPanel render(Statement statement, State state){
		Renderer renderer = new Renderer(state);
		statement.accept(renderer);
		return renderer.getPanel();
	}
	
	public Renderer(State state){
		this.state = state;
		this.mainpanel = new JPanel();
		mainpanel.setLayout(new MigLayout("wrap 2"));
	}

	public JPanel getPanel() {
		return mainpanel;
	}

	private void addLabel(StringLiteral label){
		JLabel questionLabel = new JLabel(label.toString());
		mainpanel.add(questionLabel, " grow");
	}
	
	private void addWidget(Widget widget){	
		mainpanel.add(widget.UIElement(), "width 50%, grow");
	}
	
	private Widget typeToWidget(Question question, boolean isEnabled){
		Widget widget = question.getType().accept( new TypeToWidget(question.getIdentifier(), state) );
		widget.setEnabled(isEnabled);
		return widget;
	}

	private void registerHandler(Question question, Widget widget){
		state.addObservable(question.getIdentifier(), widget);
	}
	
	private void registerExprQuestion(ExpressionQuestion exprquestion, Widget widget){
		ExpressionObserver exprObserver = new ExpressionObserver(widget, state, exprquestion);
		state.addGlobalObservers(exprObserver);
		state.addObservable(exprquestion.getIdentifier(), widget);
	}
	
	public void visit(ExpressionQuestion exprquestion) {
		addLabel(exprquestion.getLabel());
		Widget widget = typeToWidget(exprquestion, false);
		registerExprQuestion(exprquestion, widget);
		addWidget(widget);
	}

	public void visit(Question question) {
		addLabel(question.getLabel());
		Widget widget = typeToWidget(question, true);
		registerHandler(question,widget);
		addWidget(widget);
	}

	public void visit(IfStatement ifconditional) {
		JPanel trueIf = render(ifconditional.getIfBody(), state);
		registerConditionDeps(ifconditional.getConditional(), trueIf, null);
		trueIf.setVisible(false);
		this.mainpanel.add(trueIf, "span");
	}

	public void visit(IfElseStatement ifelseconditional) {
		JPanel trueIf = render(ifelseconditional.getIfBody(), state);
		JPanel falseIf = render(ifelseconditional.getElseBody(),state);
		registerConditionDeps(ifelseconditional.getConditional(), trueIf, falseIf);
		trueIf.setVisible(false);
		falseIf.setVisible(true);
		
		this.mainpanel.add(trueIf, "span");
		this.mainpanel.add(falseIf, "span");
	}

	private void registerConditionDeps(Expression conditional, JPanel trueIf,
			JPanel falseIf) {
		ConditionalObserver conditionalObserver = new ConditionalObserver(conditional,state,trueIf,falseIf);
		state.addGlobalObservers(conditionalObserver);
	}

	public void visit(Form form) {
		form.getBody().accept(this);
	}

	public void visit(Questions body) {
		for(Statement s : body){
			s.accept(this);
		}
	}
}
