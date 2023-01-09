package asm.inst;

import asm.Block;
import asm.operand.Imm;
import asm.operand.Reg;

public class LoadInst extends BaseInst {
  public Reg rd, rs;
  public Imm offset;

  public LoadInst(Reg rd, Reg rs, Imm offset, Block parent) {
    super(parent);
    this.rd = rd;
    this.rs = rs;
    this.offset = offset;
  }

  @Override
  public String toString() {
    return "lw %s, %s(%s)".formatted(rd, offset, rs);
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }
}
