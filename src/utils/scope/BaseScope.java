package utils.scope;

import java.util.HashMap;

import ast.stmt.SingleVarDefNode;
import utils.error.SemanticError;

public abstract class BaseScope {
  public final BaseScope parent;
  public HashMap<String, SingleVarDefNode> vars = new HashMap<>();

  public BaseScope(BaseScope parent) {
    this.parent = parent;
  }

  public void addVar(SingleVarDefNode v) {
    if (vars.containsKey(v.name)) {
      throw new SemanticError("redefinition of variable '" + v.name + "'", v.pos);
    }
    vars.put(v.name, v);
  }
}
