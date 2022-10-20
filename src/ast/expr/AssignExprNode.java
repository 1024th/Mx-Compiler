package ast.expr;

import ast.ASTVisitor;
import utils.Position;

public class AssignExprNode extends ExprNode {
  public ExprNode lhs, rhs;

  public AssignExprNode(ExprNode lhs, ExprNode rhs, Position pos) {
    // TODO: check type?
    super(lhs.type, false, pos);
    this.lhs = lhs;
    this.rhs = rhs;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
