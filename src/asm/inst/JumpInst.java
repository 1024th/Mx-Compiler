package asm.inst;

import asm.Block;

public class JumpInst extends BaseInst {
  public Block dest;

  public JumpInst(Block dest, Block parent) {
    super(parent);
    this.dest = dest;
  }

  @Override
  public String toString() {
    return "j " + dest.label;
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }
}
