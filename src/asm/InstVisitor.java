package asm;

import asm.inst.*;

public interface InstVisitor {
  // @formatter:off
  public void visit(BeqzInst inst);
  public void visit(ITypeInst inst);
  public void visit(JumpInst inst);
  public void visit(LiInst inst);
  public void visit(LoadInst inst);
  public void visit(MvInst inst);
  public void visit(RetInst inst);
  public void visit(RTypeInst inst);
  public void visit(StoreInst inst);
  // @formatter:on
}
