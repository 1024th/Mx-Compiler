
package ast.expr;

import ast.ASTNode;
import ast.FuncDefNode;
import ast.TypeNode;
import ir.Value;
import utils.Position;

public abstract class ExprNode extends ASTNode {
  public TypeNode type;
  public boolean isLeftVal;
  public boolean isFunc = false;  // variable and function can have the same name
  public FuncDefNode funcDef;
  // IR
  public Value val, ptr;

  public ExprNode(TypeNode type, boolean isLeftVal, Position pos) {
    super(pos);
    this.type = type;
    this.isLeftVal = isLeftVal;
  }
}
