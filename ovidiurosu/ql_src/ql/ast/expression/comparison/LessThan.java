package ql.ast.expression.comparison;

import ql.ast.expression.BinaryExpression;
import ql.ast.expression.IExpression;
import ql.ast.type.BooleanType;
import ql.ast.visitor_elements.IExpressionElementVisitor;

/**
 * @author orosu
 */
public class LessThan extends BinaryExpression
{
    public LessThan(IExpression leftSubExpression, IExpression rightSubExpression)
    {
        super(leftSubExpression, rightSubExpression);
    }

    @Override
    public BooleanType getType()
    {
        return new BooleanType();
    }

    @Override
    public String toString()
    {
        return "(expression " + this.getLeftSubExpression().toString() +
               " < " + this.getRightSubExpression().toString() + ")";
    }

    @Override
    public <T> T accept(IExpressionElementVisitor<T> visitor)
    {
        return visitor.visit(this);
    }
}
