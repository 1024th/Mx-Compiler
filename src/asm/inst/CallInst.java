package asm.inst;

import java.util.HashSet;

import asm.Block;
import asm.Function;
import asm.InstVisitor;
import asm.operand.PhysicalReg;
import asm.operand.Reg;

public class CallInst extends BaseInst {
  public Function func;

  public CallInst(Function func, Block parent) {
    super(parent);
    this.func = func;
  }

  @Override
  public String toString() {
    return "call " + func.label;
  }

  @Override
  public void accept(InstVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public HashSet<Reg> uses() {
    var ret = new HashSet<Reg>();
    for (int i = 0; i < func.args.size() && i < 8; ++i) {
      ret.add(PhysicalReg.regA(i));
    }
    return ret;
  }

  @Override
  public HashSet<Reg> defs() {
    return new HashSet<>(PhysicalReg.callerSaved);
  }

  @Override
  public void replaceUse(Reg oldReg, Reg newReg) {
  }

  @Override
  public void replaceDef(Reg oldReg, Reg newReg) {
  }
}
