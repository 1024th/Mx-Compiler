package ast.expr;

import java.util.ArrayList;

import ast.ASTVisitor;
import utils.Position;

public class FuncCallExprNode extends ExprNode {
  public ExprNode function;
  public ArrayList<ExprNode> args = new ArrayList<>();

  public FuncCallExprNode(ExprNode function, Position pos) {
    super(null, false, pos);
    this.function = function;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
