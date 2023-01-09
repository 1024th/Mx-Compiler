package asm.inst;

import asm.Block;
import asm.operand.Imm;
import asm.operand.Reg;

public class LiInst extends BaseInst {
  public Reg rd;
  public Imm imm;

  public LiInst(Reg rd, Imm imm, Block parent) {
    super(parent);
    this.rd = rd;
    this.imm = imm;
  }

  @Override
  public String toString() {
    return "li %s, %s".formatted(rd, imm);
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }
}
