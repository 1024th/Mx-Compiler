package ast.stmt;

import ast.ASTVisitor;
import ast.expr.ExprNode;
import utils.Position;
import utils.scope.Scope;

public class ForStmtNode extends StmtNode {
  public VarDefNode initVar;
  public ExprNode initExpr, condition, increase;
  public StmtNode body;

  public Scope scope;

  public ForStmtNode(VarDefNode initVar, ExprNode initExpr, ExprNode condition, ExprNode increase, StmtNode body, Position pos) {
    super(pos);
    this.initVar = initVar;
    this.initExpr = initExpr;
    this.condition = condition;
    this.increase = increase;
    this.body = body;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
