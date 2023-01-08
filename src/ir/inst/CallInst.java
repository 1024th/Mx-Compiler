package ir.inst;

import java.util.ArrayList;

import ir.BasicBlock;
import ir.Function;
import ir.IRVisitor;
import ir.Value;
import ir.type.VoidType;

public class CallInst extends BaseInst {
  public CallInst(String name, Function func, BasicBlock parent, Value... args) {
    super(func.type().retType, name, parent);
    addOperand(func);
    for (var i : args) {
      addOperand(i);
    }
  }

  public CallInst(String name, Function func, BasicBlock parent, ArrayList<Value> args) {
    super(func.type().retType, name, parent);
    addOperand(func);
    for (var i : args) {
      addOperand(i);
    }
  }

  public Function getFunc() {
    return (Function) this.getOperand(0);
  }

  @Override
  public String toString() {
    var func = getFunc();
    String s = "";
    if (!(this.type instanceof VoidType)) {
      s += "%s = ".formatted(name());
    }
    s += "call %s %s(".formatted(func.type().retType, func.name());
    for (int i = 1; i < this.operands.size(); ++i) {
      if (i > 1)
        s += ", ";
      s += this.getOperand(i).typedName();
    }
    s += ")";
    return s;
  }

  @Override
  public void accept(IRVisitor visitor) {
    visitor.visit(this);
  }
}
