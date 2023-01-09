package asm.inst;

import asm.Block;
import asm.operand.Imm;
import asm.operand.Reg;

public class StoreInst extends BaseInst {
  public Reg rs1, rs2;
  public Imm offset;

  public StoreInst(Reg rs1, Reg rs2, Imm offset, Block parent) {
    super(parent);
    this.rs1 = rs1;
    this.rs2 = rs2;
    this.offset = offset;
  }

  @Override
  public String toString() {
    return "sw %s, %s(%s)".formatted(rs2, offset, rs1);
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }
}
