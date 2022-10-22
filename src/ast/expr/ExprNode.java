package ast.expr;

import ast.ASTNode;
import ast.TypeNode;
import utils.Position;

public abstract class ExprNode extends ASTNode {
  public TypeNode type;
  public boolean isLeftVal;

  public ExprNode(TypeNode type, boolean isLeftVal, Position pos) {
    super(pos);
    this.type = type;
    this.isLeftVal = isLeftVal;
  }
}
