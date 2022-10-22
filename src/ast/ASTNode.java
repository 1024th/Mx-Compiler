package ast;

import utils.Position;

public abstract class ASTNode {
  public Position pos;

  public ASTNode(Position pos) {
    this.pos = pos;
  }

  public abstract void accept(ASTVisitor visitor);
}
