package backend;

import ir.inst.*;
import ir.inst.RetInst;

import java.util.ArrayList;

import asm.Block;
import asm.inst.LoadInst;
import asm.operand.*;
import asm.inst.*;

public class InstSelector implements ir.IRVisitor {
  asm.Module module;
  asm.Function curFunc;
  asm.Block curBlock;

  private final PhysicalReg sp = PhysicalReg.regMap.get("sp");
  private final PhysicalReg ra = PhysicalReg.regMap.get("ra");
  private final PhysicalReg a0 = PhysicalReg.regMap.get("a0");

  private PhysicalReg RegA(int i) {
    return PhysicalReg.regMap.get("a" + i);
  }

  private Reg getReg(ir.Value val) {
    if (val.asm != null) {
      return (Reg) val.asm;
    }

    Integer constVal = null;
    if (val instanceof ir.constant.IntConst x)
      constVal = x.val;
    else if (val instanceof ir.constant.NullptrConst)
      constVal = 0;

    if (constVal != null) {
      if (constVal == 0) {
        val.asm = PhysicalReg.regMap.get("zero");
      } else {
        val.asm = new VirtualReg(val.type.size());
        new LiInst((Reg) val.asm, new Imm(constVal), curBlock);
      }
    } else {
      val.asm = new VirtualReg(val.type.size());
    }
    return (Reg) val.asm;
  }

  public InstSelector(asm.Module module) {
    this.module = module;
  }

  @Override
  public void visit(ir.Module module) {
    // TODO Auto-generated method stub
    for (var i : module.funcs) {
      i.accept(this);
    }
  }

  @Override
  public void visit(ir.Function func) {
    // remove '@' in front of the function name
    curFunc = new asm.Function(func.name.substring(1));
    module.funcs.add(curFunc);
    VirtualReg.cnt = 0;

    for (var i : func.blocks) {
      i.asm = new Block(i.name);
      curFunc.blocks.add((Block) i.asm);
    }
    curBlock = (Block) func.entryBlock.asm;

    // decrease sp
    new ITypeInst("addi", sp, sp, new StackOffset(0, StackOffset.Type.decSp), curBlock);

    // save ra
    VirtualReg savedRa = new VirtualReg();
    new MvInst(savedRa, ra, curBlock);
    // save registers
    var savedRegs = new ArrayList<Reg>();
    for (var reg : PhysicalReg.calleeSaved) {
      var rd = new VirtualReg();
      savedRegs.add(rd);
      new MvInst(rd, reg, curBlock);
    }

    // arguments
    for (int i = 0; i < func.operands.size(); ++i) {
      var arg = func.operands.get(i);
      if (i < 8) {
        arg.asm = RegA(i);
      } else {
        var reg = new VirtualReg(4);
        arg.asm = reg;
        new LoadInst(reg, sp, new StackOffset(i - 8, StackOffset.Type.getArg), curBlock);
      }
    }

    func.blocks.forEach(x -> x.accept(this));

    curBlock = (Block) func.exitBlock.asm;
    // restore saved regs
    int i = 0;
    for (var reg : PhysicalReg.calleeSaved) {
      new MvInst(reg, savedRegs.get(i), curBlock);
      i++;
    }
    // restore ra
    new MvInst(ra, savedRa, curBlock);

    // increase sp
    new ITypeInst("addi", sp, sp, new StackOffset(0, StackOffset.Type.incSp), curBlock);

    new asm.inst.RetInst(curBlock);
  }

  @Override
  public void visit(ir.BasicBlock block) {
    curBlock = (Block) block.asm;
    block.insts.forEach(x -> x.accept(this));
  }

  @Override
  public void visit(AllocaInst inst) {
    // TODO Auto-generated method stub
    inst.asm = new StackOffset(curFunc.allocaCnt, StackOffset.Type.alloca);
    curFunc.allocaCnt++;
  }

  @Override
  public void visit(BinaryInst inst) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(BitCastInst inst) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(BrInst inst) {
    // TODO Auto-generated method stub
    if (inst.operands.size() == 1) {
      new JumpInst((Block) inst.dest().asm, curBlock);
    } else {
      new BeqzInst(getReg(inst.cond()), (Block) inst.ifElse().asm, curBlock);
      new JumpInst((Block) inst.ifThen().asm, curBlock);
    }
  }

  @Override
  public void visit(CallInst inst) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(GetElementPtrInst inst) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(IcmpInst inst) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ir.inst.LoadInst inst) {
    // TODO Auto-generated method stub
    var ptr = inst.ptr();
    if (ptr instanceof ir.constant.GlobalVariable) {
      // TODO
    } else {
      if (ptr.asm instanceof StackOffset x)
        new LoadInst(getReg(inst), sp, x, curBlock);
      else {
        // TODO
      }
    }
  }

  @Override
  public void visit(ir.inst.RetInst inst) {
    if (inst.operands.size() != 0) {
      new MvInst(a0, getReg(inst.val()), curBlock);
    }
  }

  @Override
  public void visit(ir.inst.StoreInst inst) {
    // TODO Auto-generated method stub
    var ptr = inst.ptr();
    if (ptr instanceof ir.constant.GlobalVariable) {
      // TODO
    } else {
      if (ptr.asm instanceof StackOffset x)
        new asm.inst.StoreInst(getReg(inst.val()), sp, x, curBlock);
      else {
        // TODO
      }
    }
  }

  @Override
  public void visit(TruncInst inst) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ZextInst inst) {
    // TODO Auto-generated method stub

  }

}
