package asm.inst;

import asm.Block;
import asm.operand.Imm;
import asm.operand.Reg;

public class StoreInst extends BaseInst {
  public Reg rs1, rs2;
  public Imm offset;
  public int size;

  public StoreInst(int size, Reg val, Reg addr, Imm offset, Block parent) {
    super(parent);
    this.rs1 = addr;
    this.rs2 = val;
    this.offset = offset;
    this.size = size;
  }

  @Override
  public String toString() {
    if (size == 1)
      return "sb %s, %s(%s)".formatted(rs2, offset, rs1);
    else
      return "sw %s, %s(%s)".formatted(rs2, offset, rs1);
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }
}
