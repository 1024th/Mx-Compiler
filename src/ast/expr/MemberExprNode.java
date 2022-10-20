package ast.expr;

import ast.ASTVisitor;
import utils.Position;

public class MemberExprNode extends ExprNode {
  public ExprNode instance, member;

  public MemberExprNode(ExprNode instance, ExprNode member, Position pos) {
    super(null, instance.isLeftVal, pos);
    this.instance = instance;
    this.member = member;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
