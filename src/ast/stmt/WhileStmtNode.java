package ast.stmt;

import ast.ASTVisitor;
import ast.expr.ExprNode;
import utils.Position;
import utils.scope.Scope;

public class WhileStmtNode extends StmtNode {
  public ExprNode condition;
  public StmtNode body;

  public Scope scope;

  public WhileStmtNode(ExprNode condition, StmtNode body, Position pos) {
    super(pos);
    this.condition = condition;
    this.body = body;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
