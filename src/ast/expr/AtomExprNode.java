package ast.expr;

import ast.ASTVisitor;
import ast.TypeNode;
import utils.Position;

public class AtomExprNode extends ExprNode {
  public String text;

  public AtomExprNode(String text, TypeNode type, boolean isLeftVal, Position pos) {
    super(type, isLeftVal, pos);
    this.text = text;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
