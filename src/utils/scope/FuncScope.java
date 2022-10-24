package utils.scope;

import ast.TypeNode;

public class FuncScope extends Scope {
  public TypeNode returnType;
  public boolean hasReturn = false;
  public boolean isLambda;

  public FuncScope(TypeNode returnType, boolean isLambda, Scope parentScope) {
    super(parentScope, false);
    this.returnType = returnType;
    this.isLambda = isLambda;
  }

  public TypeNode getReturnType() {
    return this.returnType;
  }
}
