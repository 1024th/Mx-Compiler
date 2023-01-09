package asm.inst;

import asm.Block;
import asm.operand.Reg;

public class MvInst extends BaseInst {
  public Reg rd, rs1;

  public MvInst(Reg rd, Reg rs1, Block parent) {
    super(parent);
    this.rd = rd;
    this.rs1 = rs1;
  }

  @Override
  public String toString() {
    return "mv %s, %s".formatted(rd, rs1);
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }
}
