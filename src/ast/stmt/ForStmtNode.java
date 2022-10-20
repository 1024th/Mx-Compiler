package ast.stmt;

import ast.ASTVisitor;
import ast.expr.ExprNode;
import utils.Position;

public class ForStmtNode extends StmtNode {
  public VarDefNode initVar;
  public ExprNode initExpr, condition, increase;

  public ForStmtNode(VarDefNode initVar, ExprNode initExpr, ExprNode condition, ExprNode increase, Position pos) {
    super(pos);
    this.initVar = initVar;
    this.initExpr = initExpr;
    this.condition = condition;
    this.increase = increase;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
