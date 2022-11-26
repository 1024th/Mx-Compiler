package utils.scope;

import java.util.HashMap;
import java.util.logging.Logger;

import ast.FuncDefNode;
import ir.constant.GlobalVariable;
import ir.structure.Function;
import ir.type.StructType;
import utils.error.SemanticError;

public class GlobalScope extends Scope {
  // AST
  public HashMap<String, ClassScope> classScopes = new HashMap<>();
  public HashMap<String, FuncDefNode> funcDefs = new HashMap<>();
  // IR
  public HashMap<String, StructType> classTypes = new HashMap<>();
  public HashMap<String, Function> funcs = new HashMap<>();
  public HashMap<String, GlobalVariable> globalVars = new HashMap<>();

  public GlobalScope() {
    super(null, false);
  }

  public void addFuncDef(FuncDefNode func) {
    if (funcDefs.containsKey(func.funcName)) { // Mx* does not support function overloading
      throw new SemanticError("redefinition of function '" + func.funcName + "'", func.pos);
    }
    if (classScopes.containsKey(func.funcName)) {
      throw new SemanticError("name '" + func.funcName + "' already used by class", func.pos);
    }
    funcDefs.put(func.funcName, func);
  }

  public void addFunc(Function func) {
    funcs.put(func.name, func);
  }

  public Function getFunc(String funcName) {
    return funcs.get(funcName);
  }

  public FuncDefNode getFuncDef(String name) {
    return this.funcDefs.get(name);
  }

  public ClassScope getClassScope(String name) {
    return this.classScopes.get(name);
  }

  public void addClassScope(ClassScope cls) {
    if (classScopes.containsKey(cls.className)) {
      throw new SemanticError("redefinition of class '" + cls.className + "'", cls.pos);
    }
    if (funcDefs.containsKey(cls.className)) {
      throw new SemanticError("name '" + cls.className + "' already used by function", cls.pos);
    }
    classScopes.put(cls.className, cls);
  }

  public void addClassType(StructType cls) {
    this.classTypes.put(cls.name, cls);
  }

  public StructType getClassType(String name) {
    return this.classTypes.get(name);
  }

  public void print() {
    Logger logger = Logger.getLogger("Scope");
    logger.info("print GlobalScope\nclasses:");
    this.classScopes.forEach((name, cls) -> {
      logger.info(name);
      cls.print();
    });
    logger.info("print GlobalScope\nfunctions:");
    this.funcDefs.forEach((name, func) -> {
      logger.info(name);
    });
  }
}
