package utils.scope;

import java.util.ArrayList;

import utils.symbol.VarSymb;
import utils.type.VarType;

public class FuncScope extends BaseScope {
  public final ArrayList<VarSymb> params = new ArrayList<>();
  public VarType returnType;
  public boolean isLambda = false;

  public FuncScope(VarType returnType, BaseScope parentScope) {
      super(parentScope);
      this.returnType = returnType;
  }

}
