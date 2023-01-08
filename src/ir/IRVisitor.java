package ir;

import ir.inst.*;

public interface IRVisitor {
  // @formatter:off
  public void visit(ir.Module node);
  public void visit(Function node);
  public void visit(BasicBlock node);

  public void visit(AllocaInst node);
  public void visit(BinaryInst node);
  public void visit(BitCastInst node);
  public void visit(BrInst node);
  public void visit(CallInst node);
  public void visit(GetElementPtrInst node);
  public void visit(IcmpInst node);
  public void visit(LoadInst node);
  public void visit(RetInst node);
  public void visit(StoreInst node);
  public void visit(TruncInst node);
  public void visit(ZextInst node);
  // @formatter:on
}
