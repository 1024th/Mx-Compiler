package asm.inst;

import java.util.HashSet;

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

  public ITypeInst(String op, Reg rd, Reg rs, Block parent) {
    this(op, rd, rs, null, parent);
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
