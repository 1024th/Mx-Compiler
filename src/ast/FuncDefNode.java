package ast;

import java.util.ArrayList;

import utils.Position;

public class FuncDefNode extends BaseNode {
  public final String funcName;
  public final TypeNode returnType;
  public ArrayList<TypeNode> paramTypes;
  public ArrayList<String> paramNames;

  public FuncDefNode(String funcName, TypeNode returnType, Position pos) {
    super(pos);
    this.funcName = funcName;
    this.returnType = returnType;
  }

  public void addParam(TypeNode type, String name) {
    paramTypes.add(type);
    paramNames.add(name);
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
