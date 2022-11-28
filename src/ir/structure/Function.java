package ir.structure;

import java.util.ArrayList;

import ir.Value;
import ir.constant.Constant;
import ir.inst.AllocaInst;
import ir.type.FuncType;

public class Function extends Constant {
  public ArrayList<BasicBlock> blocks = new ArrayList<>();

  public BasicBlock entryBlock, exitBlock;
  public Value retValPtr;

  public Function(FuncType type, String name) {
    super(type, name);
  }

  public FuncType type() {
    return (FuncType) type;
  }

  public void addArg(Value arg) {
    this.addOperand(arg);
  }

  public Value getArg(int i) {
    return this.getOperand(i);
  }
}
