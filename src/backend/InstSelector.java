package backend;

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

  private final PhysicalReg sp = PhysicalReg.reg("sp");
  private final PhysicalReg ra = PhysicalReg.reg("ra");
  private final PhysicalReg a0 = PhysicalReg.reg("a0");

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
        val.asm = PhysicalReg.reg("zero");
      } else {
        var reg = new VirtualReg(val.type.size());
        new LiInst(reg, new Imm(constVal), curBlock);
        return reg;
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
    // remove '@' in front of the function name
    for (var func : irModule.funcs) {
      var asmFunc = new asm.Function(func.name.substring(1));
      func.asm = asmFunc;
      for (var arg : func.operands) {
        arg.asm = new VirtualReg();
        asmFunc.args.add((Reg) arg.asm);
      }
    }
    for (var i : irModule.funcDecls)
      i.asm = new asm.Function(i.name.substring(1));

    for (var i : irModule.funcs) {
      i.accept(this);
    }
  }

  @Override
  public void visit(ir.Function func) {
    curFunc = (asm.Function) func.asm;
    module.funcs.add(curFunc);

    for (var block : func.blocks) {
      block.asm = new Block(block.name, block.loopDepth);
      curFunc.blocks.add((Block) block.asm);
    }
    curFunc.entryBlock = (Block) func.entryBlock.asm;
    curFunc.exitBlock = (Block) func.exitBlock.asm;

    // build control flow graph for asm.Block
    for (var i : func.blocks) {
      var asmBlock = (asm.Block) i.asm;
      i.prevs.forEach(b -> asmBlock.prevs.add((asm.Block) b.asm));
      i.nexts.forEach(b -> asmBlock.nexts.add((asm.Block) b.asm));
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
      if (i < 8) {
        new MvInst(curFunc.args.get(i), PhysicalReg.regA(i), curBlock);
      } else {
        new LoadInst(4, curFunc.args.get(i), sp, new StackOffset(
            i - 8, StackOffsetType.getArg), curBlock);
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
    var iter = block.insts.listIterator(0);
    while (iter.hasNext()) {
      if (iter.nextIndex() >= block.insts.size() - 2)
        break;
      var inst = iter.next();
      inst.accept(this);
    }
    var inst1 = iter.hasNext() ? iter.next() : null;
    var inst2 = iter.hasNext() ? iter.next() : null;
    if (inst1 instanceof IcmpInst icmp && inst2 instanceof ir.inst.BrInst br
        && br.cond() == icmp && br.operands.size() > 1) {
      var op = switch (icmp.op) {
        case "eq" -> "bne";
        case "ne" -> "beq";
        case "sgt" -> "ble";
        case "sge" -> "blt";
        case "slt" -> "bge";
        case "sle" -> "bgt";
        default -> null;
      };
      new asm.inst.BrInst(op, getReg(icmp.op1()), getReg(icmp.op2()),
          (Block) br.ifElse().asm, curBlock);
      new JumpInst((Block) br.ifThen().asm, curBlock);
    } else {
      if (inst1 != null)
        inst1.accept(this);
      if (inst2 != null)
        inst2.accept(this);
    }
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
    boolean commutative = switch (op) {
      case "add", "and", "or", "xor", "mul" -> true;
      default -> false;
    };
    var op1 = inst.op1();
    var op2 = inst.op2();
    if (hasIType) {
      if (commutative && op1 instanceof ir.constant.IntConst) {
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
        if (inImmRange(val)) {
          new ITypeInst(iop, getReg(inst), getReg(op1), new Imm(val), curBlock);
          return;
        }
      }
    }
    new RTypeInst(op, getReg(inst), getReg(op1), getReg(op2), curBlock);
  }

  boolean inImmRange(int val) {
    return val < 1 << 11 && val >= -(1 << 11);
  }

  @Override
  public void visit(ir.inst.BrInst inst) {
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
        MvOrLi(PhysicalReg.regA(i), arg);
      } else {
        curFunc.spilledArg = Math.max(curFunc.spilledArg, i - 7);
        var offset = new StackOffset(i - 8, StackOffsetType.putArg);
        new asm.inst.StoreInst(4, getReg(arg), sp, offset, curBlock);
      }
    }

    new asm.inst.CallInst((asm.Function) inst.getFunc().asm, curBlock);

    if (!(inst.type instanceof ir.type.VoidType)) {
      new MvInst(getReg(inst), a0, curBlock);
    }
  }

  @Override
  public void visit(GetElementPtrInst inst) {
    var ptr = inst.ptr();
    var ptrElemType = ((PointerType) ptr.type).elemType;
    if (ptrElemType instanceof ir.type.ArrayType) {
      // string constant
      var reg = getReg(inst);
      var s = (StringConst) ptr.asm;
      new LuiInst(reg, new Relocation(s, RelocationType.hi), curBlock);
      new ITypeInst("addi", reg, reg, new Relocation(s, RelocationType.lo), curBlock);
    } else if (ptrElemType instanceof ir.type.StructType) {
      // class member
      // getelementptr inbounds %cls, %cls* %ptr, i32 0, i32 member_index
      var idx = (ir.constant.IntConst) inst.getOperand(2);
      new ITypeInst("addi", getReg(inst), getReg(ptr), new Imm(idx.val * 4), curBlock);
    } else {
      // array, element type will only be int/bool/pointer (will not be char)
      // getelementptr inbounds int, int* %arr, i32 index
      var idx = inst.getOperand(1);
      if (idx instanceof ir.constant.IntConst i) {
        int val = i.val * ptrElemType.size();
        if (inImmRange(val)) {
          new ITypeInst("addi", getReg(inst), getReg(ptr),
              new Imm(val), curBlock);
        } else {
          var tmp = new VirtualReg();
          new LiInst(tmp, new Imm(val), curBlock);
          new RTypeInst("add", getReg(inst), getReg(ptr), tmp, curBlock);
        }
        return;
      }
      Reg tmp;
      if (ptrElemType.size() < 4) {
        tmp = getReg(idx);
      } else {
        tmp = new VirtualReg();
        new ITypeInst("slli", tmp, getReg(idx), new Imm(2), curBlock);
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
    var size = ((PointerType) ptr.type).elemType.size();
    if (ptr instanceof ir.constant.Constant v) {
      // global variable or string constant
      var tmp = new VirtualReg();
      var obj = (GlobalObj) v.asm;
      new LuiInst(tmp, new Relocation(obj, RelocationType.hi), curBlock);
      new LoadInst(size, getReg(inst), tmp, new Relocation(obj, RelocationType.lo), curBlock);
    } else {
      if (ptr.asm instanceof StackOffset x)
        new LoadInst(size, getReg(inst), sp, x, curBlock);
      else
        new LoadInst(size, getReg(inst), getReg(ptr), new Imm(0), curBlock);
    }
  }

  @Override
  public void visit(ir.inst.RetInst inst) {
    if (inst.operands.size() != 0) {
      MvOrLi(a0, inst.val());
    }
  }

  @Override
  public void visit(ir.inst.StoreInst inst) {
    var ptr = inst.ptr();
    var val = inst.val();
    if (ptr instanceof ir.constant.Constant v) {
      // global variable or string constant
      var tmp = new VirtualReg();
      var obj = (GlobalObj) v.asm;
      new LuiInst(tmp, new Relocation(obj, RelocationType.hi), curBlock);
      new StoreInst(val.type.size(), getReg(val), tmp, new Relocation(obj, RelocationType.lo), curBlock);
    } else {
      if (ptr.asm instanceof StackOffset x)
        new StoreInst(val.type.size(), getReg(val), sp, x, curBlock);
      else
        new StoreInst(val.type.size(), getReg(val), getReg(ptr), new Imm(0), curBlock);
    }
  }

  @Override
  public void visit(BitCastInst inst) {
    MvOrLi(getReg(inst), inst.getOperand(0));
  }

  @Override
  public void visit(TruncInst inst) {
    MvOrLi(getReg(inst), inst.getOperand(0));
  }

  @Override
  public void visit(ZextInst inst) {
    MvOrLi(getReg(inst), inst.getOperand(0));
  }

  @Override
  public void visit(PhiInst inst) {
  }

  @Override
  public void visit(MoveInst inst) {
    MvOrLi(getReg(inst.dest()), inst.src());
  }

  void MvOrLi(Reg dest, ir.Value src) {
    var c = getConstVal(src);
    if (c == null)
      new MvInst(dest, getReg(src), curBlock);
    else
      new LiInst(dest, new Imm(c), curBlock);
  }
}
