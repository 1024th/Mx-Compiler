package ir.inst;

import java.util.ArrayList;

import ir.Value;
import ir.structure.BasicBlock;
import ir.structure.Function;
import ir.type.VoidType;

public class CallInst extends BaseInst {
  public CallInst(Function func, BasicBlock parent, ArrayList<Value> args) {
    super(func.type().retType, func.name, parent);
    addOperand(func);
    for (var i : args) {
      addOperand(i);
    }
  }

  public Function getFunc() {
    return (Function) this.operands.get(0);
  }

  @Override
  public String toString() {
    var func = getFunc();
    String s = "";
    if (!(this.type instanceof VoidType)) {
      s += "%s = ".formatted(name);
    }
    s += "call void %s(".formatted(func.name);
    boolean first = true;
    for (var i : operands) {
      if (!first)
        s += ", ";
      first = false;
      s += i.typedName();
    }
    s += ")";
    return s;
  }
}
