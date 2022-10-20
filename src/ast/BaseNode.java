package ast;

import utils.Position;

public abstract class BaseNode {
  public Position pos;

  public BaseNode(Position pos) {
    this.pos = pos;
  }

  public abstract void accept(ASTVisitor visitor);
}
