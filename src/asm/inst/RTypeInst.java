package asm.inst;

import java.util.HashSet;

import asm.Block;
import asm.operand.Reg;

public class RTypeInst extends BaseInst {
  public String op;
  public Reg rd, rs1, rs2;

  public RTypeInst(String op, Reg rd, Reg rs1, Reg rs2, Block parent) {
    super(parent);
    this.rd = rd;
    this.rs1 = rs1;
    this.rs2 = rs2;
    this.op = op;
  }

  @Override
  public String toString() {
    return "%s %s, %s, %s".formatted(op, rd, rs1, rs2);
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
    var ret = new HashSet<Reg>();
    ret.add(rd);
    return ret;
  }
}
