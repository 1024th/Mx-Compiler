package asm.inst;

import java.util.HashSet;

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
    return "beqz %s, %s".formatted(rs, dest.label);
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
    return new HashSet<Reg>();
  }

  @Override
  public void replaceUse(Reg oldReg, Reg newReg) {
    if (rs == oldReg)
      rs = newReg;
  }

  @Override
  public void replaceDef(Reg oldReg, Reg newReg) {
  }
}
