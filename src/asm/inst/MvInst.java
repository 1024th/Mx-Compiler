package asm.inst;

import java.util.HashSet;

import asm.Block;
import asm.operand.Reg;

public class MvInst extends BaseInst {
  public Reg rd, rs;

  public MvInst(Reg rd, Reg rs, Block parent) {
    super(parent);
    this.rd = rd;
    this.rs = rs;
  }

  @Override
  public String toString() {
    return "mv %s, %s".formatted(rd, rs);
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public HashSet<Reg> uses() {
    var ret = new HashSet<Reg>();
    ret.add(rs);
    return ret;
  }

  @Override
  public HashSet<Reg> defs() {
    var ret = new HashSet<Reg>();
    ret.add(rd);
    return ret;
  }

  @Override
  public void replaceUse(Reg oldReg, Reg newReg) {
    if (rs == oldReg)
      rs = newReg;
  }

  @Override
  public void replaceDef(Reg oldReg, Reg newReg) {
    if (rd == oldReg)
      rd = newReg;
  }
}
