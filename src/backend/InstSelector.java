package backend;

import ir.inst.*;

import java.util.ArrayList;

import asm.Block;
import asm.inst.LoadInst;
import asm.operand.*;
import asm.operand.StackOffset.StackOffsetType;
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
    new ITypeInst("addi", sp, sp, new StackOffset(0, StackOffsetType.decSp), curBlock);

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
        new LoadInst(reg, sp, new StackOffset(i - 8, StackOffsetType.getArg), curBlock);
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
    new ITypeInst("addi", sp, sp, new StackOffset(0, StackOffsetType.incSp), curBlock);

    new asm.inst.RetInst(curBlock);
  }

  @Override
  public void visit(ir.BasicBlock block) {
    curBlock = (Block) block.asm;
    block.insts.forEach(x -> x.accept(this));
  }

  @Override
  public void visit(AllocaInst inst) {
    inst.asm = new StackOffset(curFunc.allocaCnt, StackOffsetType.alloca);
    curFunc.allocaCnt++;
  }

  @Override
  public void visit(BinaryInst inst) {
    String op = switch (inst.op) {
      case "add" -> "add";
      case "sub" -> "sub";
      case "mul" -> "mul";
      case "and" -> "and";
      case "or" -> "or";
      case "xor" -> "xor";
      case "sdiv" -> "div";
      case "srem" -> "rem";
      case "shl" -> "sll";
      case "ashr" -> "sra";
      default -> null;
    };
    var op1 = inst.op1();
    var op2 = inst.op2();
    if (op1 instanceof ir.constant.IntConst) {
      var tmp = op1;
      op1 = op2;
      op2 = tmp;
    }
    if (op2 instanceof ir.constant.IntConst x) {
      String iop = op + "i";
      int val = x.val;
      if (op.equals("sub")) {
        iop = "addi";
        val = -val;
      }
      if (val < 1 << 11 && val >= -(1 << 11)) {
        new ITypeInst(iop, getReg(inst), getReg(op1), new Imm(val), curBlock);
        return;
      }
    }
    new RTypeInst(op, getReg(inst), getReg(op1), getReg(op2), curBlock);
  }

  @Override
  public void visit(BrInst inst) {
    if (inst.operands.size() == 1) {
      new JumpInst((Block) inst.dest().asm, curBlock);
    } else {
      new BeqzInst(getReg(inst.cond()), (Block) inst.ifElse().asm, curBlock);
      new JumpInst((Block) inst.ifThen().asm, curBlock);
    }
  }

  @Override
  public void visit(ir.inst.CallInst inst) {
    for (int i = 0; i + 1 < inst.operands.size(); ++i) {
      var arg = inst.operands.get(i + 1);
      if (i < 8) {
        new MvInst(RegA(i), getReg(arg), curBlock);
      } else {
        curFunc.spilledArg = Math.max(curFunc.spilledArg, i - 8);
        var offset = new StackOffset(i - 8, StackOffsetType.putArg);
        new asm.inst.StoreInst(getReg(arg), sp, offset, curBlock);
      }
    }

    new asm.inst.CallInst(inst.getFunc().name.substring(1), curBlock);

    if (!(inst.type instanceof ir.type.VoidType)) {
      new MvInst(getReg(inst), a0, curBlock);
    }
  }

  @Override
  public void visit(GetElementPtrInst inst) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(IcmpInst inst) {
    VirtualReg tmp = null;
    switch (inst.op) {
      case "slt":
        new RTypeInst("slt", getReg(inst), getReg(inst.op1()), getReg(inst.op2()), curBlock);
        break;
      case "sgt":
        new RTypeInst("slt", getReg(inst), getReg(inst.op2()), getReg(inst.op1()), curBlock);
        break;
      case "eq":
        tmp = new VirtualReg();
        new RTypeInst("sub", tmp, getReg(inst.op1()), getReg(inst.op2()), curBlock);
        new ITypeInst("seqz", getReg(inst), tmp, curBlock);
        break;
      case "ne":
        tmp = new VirtualReg();
        new RTypeInst("sub", tmp, getReg(inst.op1()), getReg(inst.op2()), curBlock);
        new ITypeInst("snez", getReg(inst), tmp, curBlock);
        break;
      case "sge": // a >= b -> !(a < b)
        tmp = new VirtualReg();
        new RTypeInst("slt", tmp, getReg(inst.op1()), getReg(inst.op2()), curBlock);
        new ITypeInst("xori", getReg(inst), tmp, new Imm(1), curBlock);
        break;
      case "sle": // a <= b -> !(b < a)
        tmp = new VirtualReg();
        new RTypeInst("slt", tmp, getReg(inst.op2()), getReg(inst.op1()), curBlock);
        new ITypeInst("xori", getReg(inst), tmp, new Imm(1), curBlock);
        break;
    }
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

  // TODO optimize following?
  @Override
  public void visit(BitCastInst inst) {
    new MvInst(getReg(inst), getReg(inst.getOperand(0)), curBlock);
  }

  @Override
  public void visit(TruncInst inst) {
    new MvInst(getReg(inst), getReg(inst.getOperand(0)), curBlock);
  }

  @Override
  public void visit(ZextInst inst) {
    new MvInst(getReg(inst), getReg(inst.getOperand(0)), curBlock);
  }

}
