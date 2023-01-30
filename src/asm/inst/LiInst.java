package asm.inst;

import java.util.HashSet;

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

  @Override
  public HashSet<Reg> uses() {
    return new HashSet<Reg>();
  }

  @Override
  public HashSet<Reg> defs() {
    var ret = new HashSet<Reg>();
    ret.add(rd);
    return ret;
  }

  @Override
  public void replaceUse(Reg oldReg, Reg newReg) {
  }

  @Override
  public void replaceDef(Reg oldReg, Reg newReg) {
    if (rd == oldReg)
      rd = newReg;
  }
}
