package utils.scope;

import java.util.HashMap;

import utils.Position;
import utils.error.SemanticError;
import utils.symbol.FuncSymb;

public class ClassScope extends BaseScope {
  public String className;
  public Position pos;
  public HashMap<String, FuncSymb> funcs = new HashMap<>();

  public ClassScope(GlobalScope parent, Position pos) {  // Mx* does not support nested class definition
    super(parent);
    this.pos = pos;
  }

  public void addFunc(FuncSymb func) {
    if (funcs.containsKey(func.name)) {  // Mx* does not support function overloading
      throw new SemanticError("redefinition of function '" + func.name + "'", func.pos);
    }
    funcs.put(func.name, func);
  }
}
