package asm.inst;

import asm.Block;

public class RetInst extends BaseInst {
  public RetInst(Block parent) {
    super(parent);
  }

  @Override
  public String toString() {
    return "ret";
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }
}
