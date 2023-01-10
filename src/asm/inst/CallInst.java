package asm.inst;

import asm.Block;
import asm.InstVisitor;

public class CallInst extends BaseInst {
  public String funcName;

  public CallInst(String funcName, Block parent) {
    super(parent);
    this.funcName = funcName;
  }

  @Override
  public String toString() {
    return "call " + funcName;
  }

  @Override
  public void accept(InstVisitor visitor) {
    visitor.visit(this);
  }
}
