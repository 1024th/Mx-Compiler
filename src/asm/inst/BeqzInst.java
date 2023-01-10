package asm.inst;

import asm.Block;
import asm.operand.Reg;

public class BeqzInst extends BaseInst {
  public Reg rs;
  public Block dest;

  public BeqzInst(Reg rs, Block dest, Block parent) {
    super(parent);
    this.rs = rs;
    this.dest = dest;
  }

  @Override
  public String toString() {
    return "beqz %s, %s".formatted(rs, dest);
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }
}
