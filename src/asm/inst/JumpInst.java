package asm.inst;

import java.util.HashSet;

import asm.Block;
import asm.operand.Reg;

public class JumpInst extends BaseInst {
  public Block dest;

  public JumpInst(Block dest, Block parent) {
    super(parent);
    this.dest = dest;
  }

  @Override
  public String toString() {
    return "j " + dest.label;
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public HashSet<Reg> uses() {
    return new HashSet<>();
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
