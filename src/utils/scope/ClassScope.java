package utils.scope;

import java.util.HashMap;
import java.util.logging.Logger;

import ast.ClassCtorDefNode;
import ast.FuncDefNode;
import utils.Position;
import utils.error.SemanticError;

public class ClassScope extends Scope {
  public String className;
  public Position pos;
  public HashMap<String, FuncDefNode> funcs = new HashMap<>();
  public ClassCtorDefNode ctor;

  public ClassScope(String className, GlobalScope parent, Position pos) { // Mx* does not support nested class
                                                                          // definition
    super(parent, false);
    this.className = className;
    this.pos = pos;
  }

  public void addFunc(FuncDefNode func) {
    if (funcs.containsKey(func.funcName)) { // Mx* does not support function overloading
      throw new SemanticError("redefinition of function '" + func.funcName + "'", func.pos);
    }
    funcs.put(func.funcName, func);
  }

  public FuncDefNode getFunc(String name) {
    return this.funcs.get(name);
  }

  public void addCtor(ClassCtorDefNode ctor) {
    if (!ctor.name.equals(className))
      throw new SemanticError("class constructor's name does not match class name", ctor.pos);
    if (this.ctor != null) { // Mx* only allows one constructor in a class
      throw new SemanticError("redefinition of class constructor '" + ctor.name + "'", ctor.pos);
    }
    this.ctor = ctor;
  }

  public void print() {
    Logger logger = Logger.getLogger("Scope");
    logger.info("print ClassScope\nmember functions:");
    this.funcs.forEach((name, func) -> {
      logger.info(name);
    });
    logger.info("print ClassScope\nmember variables:");
    this.vars.forEach((name, var) -> {
      logger.info(name);
    });
  }
}
