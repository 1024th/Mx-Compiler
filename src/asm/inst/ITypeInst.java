package asm.inst;

import asm.Block;
import asm.operand.Imm;
import asm.operand.Reg;

public class ITypeInst extends BaseInst {
  public String op;
  public Reg rd, rs;
  public Imm imm;

  public ITypeInst(String op, Reg rd, Reg rs, Imm imm, Block parent) {
    super(parent);
    this.rd = rd;
    this.rs = rs;
    this.imm = imm;
    this.op = op;
  }

  @Override
  public String toString() {
    if (this.imm == null) // pseudo inst
      return "%s %s, %s".formatted(op, rd, rs);
    else
      return "%s %s, %s, %s".formatted(op, rd, rs, imm);
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }
}
