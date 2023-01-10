package backend;

import asm.inst.*;
import asm.operand.Imm;
import asm.operand.StackOffset;

public class StackAllocator implements asm.ModulePass, asm.FuncPass, asm.BlockPass, asm.InstVisitor {
  /*
   * Stack Frame Layout
   * 
   * ------------
   * spilled regs
   * ------------
   * alloc
   * ------------
   * args
   * ------------ <-- sp
   */

  private Imm calcStackOff(StackOffset s) {
    int offset = switch (s.type) {
      case spill -> curFunc.spilledArg + curFunc.allocaCnt + s.val;
      case alloca -> curFunc.spilledArg + s.val;
      case putArg -> s.val;
      case getArg -> curFunc.totalStack + s.val;
      case decSp -> -curFunc.totalStack;
      case incSp -> curFunc.totalStack;
    };
    return new Imm(offset * 4);
  }

  asm.Function curFunc;

  @Override
  public void runOnModule(asm.Module module) {
    module.funcs.forEach(this::runOnFunc);
  }

  @Override
  public void runOnFunc(asm.Function func) {
    curFunc = func;
    func.totalStack = func.spilledReg + func.allocaCnt + func.spilledArg;
    func.blocks.forEach(this::runOnBlock);
  }

  @Override
  public void runOnBlock(asm.Block block) {
    block.insts.forEach(x -> x.accept(this));
  }

  @Override
  public void visit(LoadInst inst) {
    if (inst.offset instanceof StackOffset s) {
      inst.offset = calcStackOff(s);
    }
  }

  @Override
  public void visit(StoreInst inst) {
    if (inst.offset instanceof StackOffset s) {
      inst.offset = calcStackOff(s);
    }
  }

  @Override
  public void visit(ITypeInst inst) {
    if (inst.imm instanceof StackOffset s) {
      inst.imm = calcStackOff(s);
    }
  }

  @Override
  public void visit(BeqzInst inst) {
  }

  @Override
  public void visit(CallInst inst) {
  }

  @Override
  public void visit(JumpInst inst) {
  }

  @Override
  public void visit(LiInst inst) {
  }

  @Override
  public void visit(LuiInst inst) {
  }

  @Override
  public void visit(MvInst inst) {
  }

  @Override
  public void visit(RetInst inst) {
  }

  @Override
  public void visit(RTypeInst inst) {
  }

}
