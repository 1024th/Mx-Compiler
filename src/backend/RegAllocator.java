package backend;

import java.util.ArrayList;

import asm.Block;
import asm.Function;
import asm.Module;
import asm.inst.*;
import asm.operand.PhysicalReg;
import asm.operand.Reg;
import asm.operand.StackOffset;
import asm.operand.VirtualReg;

public class RegAllocator implements asm.ModulePass, asm.FuncPass, asm.BlockPass, asm.InstVisitor {
  asm.Module module;
  asm.Function curFunc;
  asm.Block curBlock;

  private final PhysicalReg sp = PhysicalReg.regMap.get("sp");
  private final PhysicalReg t0 = PhysicalReg.regMap.get("t0");
  private final PhysicalReg t1 = PhysicalReg.regMap.get("t1");

  private PhysicalReg regAllocRead(Reg src, PhysicalReg reg) {
    if (src instanceof VirtualReg v) {
      curFunc.spilledReg = Math.max(curFunc.spilledReg, v.index + 1);
      var o = new StackOffset(v.index, StackOffset.StackOffsetType.spill);
      // TODO long offset lui
      new asm.inst.LoadInst(4, reg, sp, o, curBlock);
      return reg;
    }
    return (PhysicalReg) src;
  }

  private PhysicalReg regAllocWrite(Reg dest, PhysicalReg reg) {
    if (dest instanceof VirtualReg v) {
      curFunc.spilledReg = Math.max(curFunc.spilledReg, v.index + 1);
      var o = new StackOffset(v.index, StackOffset.StackOffsetType.spill);
      // TODO long offset lui
      new asm.inst.StoreInst(4, reg, sp, o, curBlock);
      return reg;
    }
    return (PhysicalReg) dest;
  }

  @Override
  public void runOnModule(Module module) {
    module.funcs.forEach(this::runOnFunc);
  }

  @Override
  public void runOnFunc(Function func) {
    curFunc = func;
    func.blocks.forEach(this::runOnBlock);
  }

  @Override
  public void runOnBlock(Block block) {
    curBlock = block;
    var oldInsts = block.insts;
    block.insts = new ArrayList<>();
    oldInsts.forEach(x -> x.accept(this));
  }

  @Override
  public void visit(BeqzInst inst) {
    inst.rs = regAllocRead(inst.rs, t0);
    curBlock.addInst(inst);
  }

  @Override
  public void visit(CallInst inst) {
    curBlock.addInst(inst);
  }

  @Override
  public void visit(ITypeInst inst) {
    inst.rs = regAllocRead(inst.rs, t0);
    curBlock.addInst(inst);
    inst.rd = regAllocWrite(inst.rd, t0);
  }

  @Override
  public void visit(JumpInst inst) {
    curBlock.addInst(inst);
  }

  @Override
  public void visit(LiInst inst) {
    curBlock.addInst(inst);
    inst.rd = regAllocWrite(inst.rd, t0);
  }

  @Override
  public void visit(LuiInst inst) {
    curBlock.addInst(inst);
    inst.rd = regAllocWrite(inst.rd, t0);
  }

  @Override
  public void visit(LoadInst inst) {
    inst.rs = regAllocRead(inst.rs, t0);
    curBlock.addInst(inst);
    inst.rd = regAllocWrite(inst.rd, t0);
  }

  @Override
  public void visit(MvInst inst) {
    inst.rs = regAllocRead(inst.rs, t0);
    curBlock.addInst(inst);
    inst.rd = regAllocWrite(inst.rd, t0);
  }

  @Override
  public void visit(RetInst inst) {
    curBlock.addInst(inst);
  }

  @Override
  public void visit(RTypeInst inst) {
    inst.rs1 = regAllocRead(inst.rs1, t0);
    inst.rs2 = regAllocRead(inst.rs2, t1);
    curBlock.addInst(inst);
    inst.rd = regAllocWrite(inst.rd, t0);
  }

  @Override
  public void visit(StoreInst inst) {
    inst.rs1 = regAllocRead(inst.rs1, t0);
    inst.rs2 = regAllocRead(inst.rs2, t1);
    curBlock.addInst(inst);
  }

}