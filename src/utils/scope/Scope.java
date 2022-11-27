package utils.scope;

import java.util.HashMap;

import ast.TypeNode;
import ast.stmt.SingleVarDefNode;
import ir.inst.AllocaInst;
import utils.error.SemanticError;

// Base scope class, only supports variable definition (which is the common 
// part of all scopes).
public class Scope {
  public final Scope parent;
  public boolean isLoop; // for break and continue
  public HashMap<String, SingleVarDefNode> varDefs = new HashMap<>();
  // IR
  public HashMap<String, AllocaInst> vars = new HashMap<>();

  public Scope(Scope parent, boolean isLoop) {
    this.parent = parent;
    this.isLoop = isLoop;
  }

  public void addVarDef(SingleVarDefNode v) {
    if (varDefs.containsKey(v.name)) {
      throw new SemanticError("redefinition of variable '" + v.name + "'", v.pos);
    }
    varDefs.put(v.name, v);
  }

  public SingleVarDefNode getVarDef(String name, boolean recursive) {
    var i = this.varDefs.get(name);
    if (i != null)
      return i;
    if (recursive && this.parent != null)
      return this.parent.getVarDef(name, recursive);
    return null;
  }

  public void addVar(String name, AllocaInst v) {
    vars.put(name, v);
  }

  public AllocaInst getVar(String name, boolean recursive) {
    var i = vars.get(name);
    if (i != null)
      return i;
    if (recursive && this.parent != null)
      return this.parent.getVar(name, recursive);
    return null;
  }

  // check if current scope is inside a loop scope
  public boolean insideLoop() {
    if (this.isLoop)
      return true;
    if (!(this instanceof FuncScope) && !(this instanceof ClassScope) && this.parent != null)
      return this.parent.insideLoop();
    return false;
  }

  // recursively find the ClassScope from this scope and its ancestors,
  // return null if no ClassScope is found.
  public ClassScope getClassScope() {
    if (this instanceof ClassScope)
      return (ClassScope) this;
    if (this.parent != null)
      return this.parent.getClassScope();
    return null;
  }

  // recursively find the FuncScope from this scope and its ancestors,
  // return null if no FuncScope is found.
  public FuncScope getFuncScope() {
    if (this instanceof FuncScope)
      return (FuncScope) this;
    if (this.parent != null)
      return this.parent.getFuncScope();
    return null;
  }

  // remember to override this function in FuncScope
  public TypeNode getReturnType() {
    return this.parent.getReturnType();
  }
}
