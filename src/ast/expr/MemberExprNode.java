package ast.expr;

import ast.ASTVisitor;
import ast.stmt.SingleVarDefNode;
import utils.Position;

public class MemberExprNode extends ExprNode {
  public ExprNode instance;
  public String member;
  public SingleVarDefNode varDef;

  public MemberExprNode(ExprNode instance, String member, Position pos) {
    super(null, true, pos);
    this.instance = instance;
    this.member = member;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
