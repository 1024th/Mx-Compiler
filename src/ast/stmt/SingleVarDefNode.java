package ast.stmt;

import ast.ASTVisitor;
import ast.TypeNode;
import ast.expr.ExprNode;
import utils.Position;

public class SingleVarDefNode extends StmtNode {
  public TypeNode type;
  public String name;
  public ExprNode initExpr;
  
  public SingleVarDefNode(TypeNode type, String name, ExprNode initExpr, Position pos) {
    super(pos);
    this.name = name;
    this.initExpr = initExpr;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
