package asm.inst;

import asm.Block;
import asm.operand.Imm;
import asm.operand.Reg;

public class LoadInst extends BaseInst {
  public Reg rd, rs;
  public Imm offset;
  public int size;

  public LoadInst(int size, Reg rd, Reg rs, Imm offset, Block parent) {
    super(parent);
    this.rd = rd;
    this.rs = rs;
    this.offset = offset;
    this.size = size;
  }

  @Override
  public String toString() {
    if (size == 1)
      return "lb %s, %s(%s)".formatted(rd, offset, rs);
    else
      return "lw %s, %s(%s)".formatted(rd, offset, rs);
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }
}
