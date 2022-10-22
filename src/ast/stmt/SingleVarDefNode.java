package ast.stmt;

import ast.ASTVisitor;
import ast.expr.ExprNode;
import utils.Position;

public class SingleVarDefNode extends StmtNode {
  public String name;
  public ExprNode initExpr;
  
  public SingleVarDefNode(String name, ExprNode initExpr, Position pos) {
    super(pos);
    this.name = name;
    this.initExpr = initExpr;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
