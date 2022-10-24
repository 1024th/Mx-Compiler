package ast;

import java.util.ArrayList;

import utils.Position;

public class ParamListNode extends ASTNode {
  public ArrayList<ParamNode> params = new ArrayList<>();

  public ParamListNode(Position pos) {
    super(pos);
  }

  public void add(ParamNode param) {
    this.params.add(param);
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
