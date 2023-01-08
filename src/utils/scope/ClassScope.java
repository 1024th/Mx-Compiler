package utils.scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import ast.ClassCtorDefNode;
import ast.FuncDefNode;
import ast.stmt.SingleVarDefNode;
import ir.Function;
import utils.Position;
import utils.error.SemanticError;

public class ClassScope extends Scope {
  public String className;
  public Position pos;
  // member function definitions
  public HashMap<String, FuncDefNode> funcDefs = new HashMap<>();
  public ClassCtorDefNode ctor;
  // for IR getelementptr
  public HashMap<String, Integer> memberVarIndex = new HashMap<>();
  // IR member functions
  public HashMap<String, Function> funcs = new HashMap<>();

  // parent must be GlobalScope, because Mx* does not support nested class definition
  public ClassScope(String className, GlobalScope parent, Position pos) {
    super(parent);
    this.className = className;
    this.pos = pos;
  }

  @Override
  public void addVarDef(SingleVarDefNode v) {
    super.addVarDef(v);
    memberVarIndex.put(v.name, memberVarIndex.size());
  }

  public int getVarIndex(String name) {
    return memberVarIndex.get(name);
  }

  public void addFuncDef(FuncDefNode func) {
    if (funcDefs.containsKey(func.funcName)) { // Mx* does not support function overloading
      throw new SemanticError("redefinition of function '" + func.funcName + "'", func.pos);
    }
    funcDefs.put(func.funcName, func);
  }

  public FuncDefNode getFuncDef(String name) {
    return this.funcDefs.get(name);
  }

  public void addFunc(String funcName, Function func) {
    funcs.put(funcName, func);
  }

  public Function getFunc(String funcName) {
    return this.funcs.get(funcName);
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
    this.funcDefs.forEach((name, func) -> {
      logger.info(name);
    });
    logger.info("print ClassScope\nmember variables:");
    this.varDefs.forEach((name, var) -> {
      logger.info(name);
    });
  }
}
