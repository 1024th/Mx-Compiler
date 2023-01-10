package backend;

import ir.IRBuilder;
import ir.inst.*;
import ir.type.PointerType;

import java.util.ArrayList;

import asm.Block;
import asm.inst.LoadInst;
import asm.inst.StoreInst;
import asm.operand.*;
import asm.operand.Relocation.RelocationType;
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

  private Integer getConstVal(ir.Value v) {
    Integer constVal = null;
    if (v instanceof ir.constant.IntConst x)
      constVal = x.val;
    else if (v instanceof ir.constant.NullptrConst)
      constVal = 0;
    return constVal;
  }

  private Reg getReg(ir.Value val) {
    if (val.asm != null) {
      return (Reg) val.asm;
    }
    var constVal = getConstVal(val);
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
  public void visit(ir.Module irModule) {
    // global objects
    for (var v : irModule.globalVars) {
      var init = getConstVal(v.initVal);
      v.asm = new GlobalVariable(v.name.substring(1),
          init == null ? 0 : init, v.type.size());
      module.globalVars.add((GlobalVariable) v.asm);
    }
    for (var s : irModule.stringConsts) {
      s.asm = new StringConst(s.name.substring(1), s.val);
      module.stringConsts.add((StringConst) s.asm);
    }
    // functions
    for (var i : irModule.funcs) {
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
      case "and" -> "and";
      case "or" -> "or";
      case "xor" -> "xor";
      case "mul" -> "mul";
      case "sdiv" -> "div";
      case "srem" -> "rem";
      case "shl" -> "sll";
      case "ashr" -> "sra";
      default -> null;
    };
    boolean hasIType = switch (inst.op) {
      case "mul", "sdiv", "srem" -> false;
      default -> true;
    };
    var op1 = inst.op1();
    var op2 = inst.op2();
    if (hasIType) {
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
    var ptr = inst.ptr();
    var ptrElemType = ((PointerType) ptr.type).elemType;
    if (ptrElemType instanceof ir.type.ArrayType) {
      // string constant
      var reg = new VirtualReg();
      var s = (StringConst) ptr.asm;
      new LuiInst(reg, new Relocation(s, RelocationType.hi), curBlock);
      new ITypeInst("addi", reg, reg, new Relocation(s, RelocationType.lo), curBlock);
      inst.asm = reg;
    } else if (ptrElemType instanceof ir.type.StructType) {
      // class member
      // getelementptr inbounds %cls, %cls* %ptr, i32 0, i32 member_index
      var tmp = new VirtualReg();
      // TODO optimize
      new ITypeInst("slli", tmp, getReg(inst.getOperand(2)), new Imm(4), curBlock);
      new RTypeInst("add", getReg(inst), getReg(ptr), tmp, curBlock);
    } else {
      // array, element type will only be int/bool/pointer (will not be char)
      // getelementptr inbounds int, int* %arr, i32 index
      // TODO optimize
      Reg tmp;
      if (IRBuilder.isBool(ptrElemType)) {
        tmp = getReg(inst.getOperand(1));
      } else {
        tmp = new VirtualReg();
        new ITypeInst("slli", tmp, getReg(inst.getOperand(1)), new Imm(4), curBlock);
      }
      new RTypeInst("add", getReg(inst), getReg(ptr), tmp, curBlock);
    }
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
    var ptr = inst.ptr();
    if (ptr instanceof ir.constant.Constant v) {
      // global variable or string constant
      var tmp = new VirtualReg();
      var obj = (GlobalObj) v.asm;
      new LuiInst(tmp, new Relocation(obj, RelocationType.hi), curBlock);
      new LoadInst(getReg(inst), tmp, new Relocation(obj, RelocationType.lo), curBlock);
    } else {
      if (ptr.asm instanceof StackOffset x)
        new LoadInst(getReg(inst), sp, x, curBlock);
      else
        new LoadInst(getReg(inst), (Reg) ptr.asm, new Imm(0), curBlock);
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
    var ptr = inst.ptr();
    if (ptr instanceof ir.constant.Constant v) {
      // global variable or string constant
      var tmp = new VirtualReg();
      var obj = (GlobalObj) v.asm;
      new LuiInst(tmp, new Relocation(obj, RelocationType.hi), curBlock);
      new StoreInst(getReg(inst.val()), tmp, new Relocation(obj, RelocationType.lo), curBlock);
    } else {
      if (ptr.asm instanceof StackOffset x)
        new StoreInst(getReg(inst.val()), sp, x, curBlock);
      else
        new StoreInst(getReg(inst.val()), (Reg) ptr.asm, new Imm(0), curBlock);
    }
  }

  // TODO optimize following?
  @Override
  public void visit(BitCastInst inst) {
    new MvInst(getReg(inst), getReg(inst.getOperand(0)), curBlock);
  }

  @Override
  public void visit(TruncInst inst) {
    var tmp = new VirtualReg();
    new ITypeInst("andi", tmp, getReg(inst.getOperand(0)), new Imm(1), curBlock);
    new MvInst(getReg(inst), tmp, curBlock);
  }

  @Override
  public void visit(ZextInst inst) {
    new MvInst(getReg(inst), getReg(inst.getOperand(0)), curBlock);
  }

}
