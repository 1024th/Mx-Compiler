package ir;

import ir.inst.*;

public interface IRVisitor {
  // @formatter:off
  public void visit(ir.Module module);
  public void visit(Function func);
  public void visit(BasicBlock block);

  public void visit(AllocaInst inst);
  public void visit(BinaryInst inst);
  public void visit(BitCastInst inst);
  public void visit(BrInst inst);
  public void visit(CallInst inst);
  public void visit(GetElementPtrInst inst);
  public void visit(IcmpInst inst);
  public void visit(LoadInst inst);
  public void visit(MoveInst inst);
  public void visit(PhiInst inst);
  public void visit(RetInst inst);
  public void visit(StoreInst inst);
  public void visit(TruncInst inst);
  public void visit(ZextInst inst);
  // @formatter:on
}
