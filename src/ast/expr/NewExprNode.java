package ast.expr;

import java.util.ArrayList;

import ast.ASTVisitor;
import ast.TypeNode;
import utils.Position;

public class NewExprNode extends ExprNode {
  public ArrayList<ExprNode> sizeExprs = new ArrayList<>();

  public NewExprNode(TypeNode type, Position pos) {
    super(type, false, pos);
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
