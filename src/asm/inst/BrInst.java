package asm.inst;

import java.util.HashSet;

import asm.Block;
import asm.operand.Reg;

public class BrInst extends BaseInst {
  public String op;
  public Reg rs1, rs2;
  public Block dest;

  public BrInst(String op, Reg rs1, Reg rs2, Block dest, Block parent) {
    super(parent);
    this.op = op;
    this.rs1 = rs1;
    this.rs2 = rs2;
    this.dest = dest;
  }

  @Override
  public String toString() {
    return "%s %s, %s, %s".formatted(op, rs1, rs2, dest.label);
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public HashSet<Reg> uses() {
    var ret = new HashSet<Reg>();
    ret.add(rs1);
    ret.add(rs2);
    return ret;
  }

  @Override
  public HashSet<Reg> defs() {
    return new HashSet<Reg>();
  }

  @Override
  public void replaceUse(Reg oldReg, Reg newReg) {
    if (rs1 == oldReg)
      rs1 = newReg;
    if (rs2 == oldReg)
      rs2 = newReg;
  }

  @Override
  public void replaceDef(Reg oldReg, Reg newReg) {
  }
}
