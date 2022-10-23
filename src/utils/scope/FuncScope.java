package utils.scope;

import utils.type.VarType;

public class FuncScope extends BaseScope {
  public VarType returnType;
  public boolean isLambda = false;

  public FuncScope(VarType returnType, BaseScope parentScope) {
      super(parentScope);
      this.returnType = returnType;
  }

}
