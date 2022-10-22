package ast;

import java.util.ArrayList;

import utils.Position;

public class ClassDefNode extends ASTNode {
  public final String className;
  public ArrayList<ASTNode> defs = new ArrayList<>();

  public ClassDefNode(String className, Position pos) {
    super(pos);
    this.className = className;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
