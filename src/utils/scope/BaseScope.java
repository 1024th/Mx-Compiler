package utils.scope;

import utils.error.SemanticError;
import utils.symbol.VarSymb;

import java.util.HashMap;

public abstract class BaseScope {
  public final BaseScope parent;
  public HashMap<String, VarSymb> vars = new HashMap<>();

  public BaseScope(BaseScope parent) {
    this.parent = parent;
  }

  public void addVar(VarSymb var) {
    if (vars.containsKey(var.name)) {
      throw new SemanticError("redefinition of variable '" + var.name + "'", var.pos);
    }
    vars.put(var.name, var);
  }
}
