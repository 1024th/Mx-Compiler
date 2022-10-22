package ast;

import ast.stmt.SuiteNode;
import utils.Position;

public class ClassCtorDefNode extends ASTNode {
  public String name;
  public SuiteNode body;

  public ClassCtorDefNode(String name, SuiteNode body, Position pos) {
    super(pos);
    this.name = name;
    this.body = body;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
