package asm.inst;

import java.util.HashSet;

import asm.Block;
import asm.operand.PhysicalReg;
import asm.operand.Reg;

public class RetInst extends BaseInst {
  public RetInst(Block parent) {
    super(parent);
  }

  @Override
  public String toString() {
    return "ret";
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public HashSet<Reg> uses() {
    var ret = new HashSet<Reg>();
    ret.add(PhysicalReg.reg("ra"));
    return ret;
  }

  @Override
  public HashSet<Reg> defs() {
    return new HashSet<>();
  }

  @Override
  public void replaceUse(Reg oldReg, Reg newReg) {
  }

  @Override
  public void replaceDef(Reg oldReg, Reg newReg) {
  }
}
