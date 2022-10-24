package ast;

import utils.Position;

import java.util.ArrayList;

public class ProgramNode extends ASTNode {
  public ArrayList<ASTNode> defs = new ArrayList<>();

  public ProgramNode(Position pos) {
    super(pos);
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
