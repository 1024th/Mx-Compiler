package asm.inst;

import java.util.HashSet;

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

  @Override
  public HashSet<Reg> uses() {
    var ret = new HashSet<Reg>();
    ret.add(rs1);
    ret.add(rs2);
    return ret;
  }

  @Override
  public HashSet<Reg> defs() {
    return new HashSet<>();
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
