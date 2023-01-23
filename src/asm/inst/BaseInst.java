package asm.inst;

import java.util.HashSet;

import asm.Block;
import asm.InstVisitor;
import asm.operand.Reg;

public abstract class BaseInst {
  public BaseInst(Block parent) {
    if (parent != null)
      parent.addInst(this);
  }

  @Override
  public abstract String toString();

  public abstract HashSet<Reg> uses();

  public abstract HashSet<Reg> defs();

  public abstract void accept(InstVisitor visitor);
}
