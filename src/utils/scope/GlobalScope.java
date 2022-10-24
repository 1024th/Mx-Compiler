package utils.scope;

import java.util.HashMap;
import java.util.logging.Logger;

import ast.FuncDefNode;
import utils.error.SemanticError;

public class GlobalScope extends Scope {
  public HashMap<String, ClassScope> classes = new HashMap<>();
  public HashMap<String, FuncDefNode> funcs = new HashMap<>();

  public GlobalScope() {
    super(null, false);
  }

  public void addFunc(FuncDefNode func) {
    if (funcs.containsKey(func.funcName)) {  // Mx* does not support function overloading
      throw new SemanticError("redefinition of function '" + func.funcName + "'", func.pos);
    }
    funcs.put(func.funcName, func);
  }

  public FuncDefNode getFunc(String name) {
    return this.funcs.get(name);
  }

  public ClassScope getClass(String name) {
    return this.classes.get(name);
  }

  public void addClass(ClassScope cls) {
    if (funcs.containsKey(cls.className)) {
      throw new SemanticError("redefinition of class '" + cls.className + "'", cls.pos);
    }
    classes.put(cls.className, cls);
  }

  public void print() {
    Logger logger = Logger.getLogger("Scope");
    logger.info("print GlobalScope\nclasses:");
    this.classes.forEach((name, cls) -> {
      logger.info(name);
      cls.print();
    });
    logger.info("print GlobalScope\nfunctions:");
    this.funcs.forEach((name, func) -> {
      logger.info(name);
    });
  }
}
