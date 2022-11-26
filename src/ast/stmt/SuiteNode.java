package ast.stmt;

import java.util.ArrayList;

import ast.ASTVisitor;
import utils.Position;
import utils.scope.Scope;

public class SuiteNode extends StmtNode {
  public ArrayList<StmtNode> stmts = new ArrayList<>();

  public Scope scope;

  public SuiteNode(Position pos) {
    super(pos);
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
