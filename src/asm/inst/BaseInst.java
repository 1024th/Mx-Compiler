package asm.inst;

import asm.Block;
import asm.InstVisitor;

public abstract class BaseInst {
  public BaseInst(Block parent) {
    if (parent != null)
      parent.addInst(this);
  }

  @Override
  public abstract String toString();

  public abstract void accept(InstVisitor visitor);
}
