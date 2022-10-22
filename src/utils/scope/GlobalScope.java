package utils.scope;

import java.util.HashMap;

import utils.error.SemanticError;
import utils.symbol.FuncSymb;

public class GlobalScope extends BaseScope {
  public HashMap<String, ClassScope> classes = new HashMap<>();
  public HashMap<String, FuncSymb> funcs = new HashMap<>();

  public GlobalScope() {
    super(null);
  }

  public void addFunc(FuncSymb func) {
    if (funcs.containsKey(func.name)) {  // Mx* does not support function overloading
      throw new SemanticError("redefinition of function '" + func.name + "'", func.pos);
    }
    funcs.put(func.name, func);
  }

  public void addClass(ClassScope cls) {
    if (funcs.containsKey(cls.className)) {
      throw new SemanticError("redefinition of class '" + cls.className + "'", cls.pos);
    }
    classes.put(cls.className, cls);
  }
}
