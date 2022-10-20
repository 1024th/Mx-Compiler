package ast.stmt;

import java.util.ArrayList;

import ast.ASTVisitor;
import utils.Position;

public class SuiteNode extends StmtNode {
  public ArrayList<StmtNode> stmts;
  
  public SuiteNode(Position pos) {
    super(pos);
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
