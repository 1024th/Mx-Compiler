package ast.expr;

import ast.ASTVisitor;
import ast.FuncDefNode;
import ast.stmt.SingleVarDefNode;
import utils.Position;

public class MemberExprNode extends ExprNode {
  public ExprNode instance;
  public String member;
  public SingleVarDefNode varDef;
  public FuncDefNode funcDef;

  public MemberExprNode(ExprNode instance, String member, Position pos) {
    super(null, instance.isLeftVal, pos);
    this.instance = instance;
    this.member = member;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
