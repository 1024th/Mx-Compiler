package ast.expr;

import ast.BaseNode;
import ast.TypeNode;
import utils.Position;

public abstract class ExprNode extends BaseNode {
  public TypeNode type;
  public boolean isLeftVal;

  public ExprNode(TypeNode type, boolean isLeftVal, Position pos) {
    super(pos);
    this.type = type;
    this.isLeftVal = isLeftVal;
  }
}
