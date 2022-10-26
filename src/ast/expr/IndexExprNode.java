package ast.expr;

import ast.ASTVisitor;
import utils.Position;

public class IndexExprNode extends ExprNode {
  public ExprNode array, index;

  public IndexExprNode(ExprNode array, ExprNode index, Position pos) {
    super(null, true, pos);
    this.array = array;
    this.index = index;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
