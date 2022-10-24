package ast;

import java.util.ArrayList;

import utils.Position;

public class ParamNode extends ASTNode {
  public TypeNode type;
  public String name;

  public ParamNode(TypeNode type, String name, Position pos) {
    super(pos);
    this.type = type;
    this.name = name;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
