package ast.stmt;

import ast.ASTVisitor;
import ast.expr.ExprNode;
import utils.Position;

public class ReturnStmtNode extends StmtNode {
  public ExprNode expr;

  public ReturnStmtNode(ExprNode expr, Position pos) {
    super(pos);
    this.expr = expr;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
