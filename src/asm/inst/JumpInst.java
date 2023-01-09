package asm.inst;

import asm.Block;
import asm.Function;

public class JumpInst extends BaseInst {
  public Function dest;

  public JumpInst(Function dest, Block parent) {
    super(parent);
    this.dest = dest;
  }

  @Override
  public String toString() {
    return "j " + dest;
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }
}
