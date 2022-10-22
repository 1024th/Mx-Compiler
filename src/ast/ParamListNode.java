package ast;

import java.util.ArrayList;

import ast.stmt.SuiteNode;
import utils.Position;

public class ParamListNode extends ASTNode {
  public ArrayList<TypeNode> types;
  public ArrayList<String> names;

  public ParamListNode(Position pos) {
    super(pos);
  }

  public void add(TypeNode type, String name) {
    types.add(type);
    names.add(name);
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
