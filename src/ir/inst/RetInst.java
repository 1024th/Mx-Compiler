package ir.inst;

import ir.Value;
import ir.structure.BasicBlock;
import ir.type.VoidType;

public class RetInst extends BaseInst {
  public RetInst(BasicBlock parent) {
    super(new VoidType(), "ret", parent);
  }

  public RetInst(Value val, BasicBlock parent) {
    super(val.type, "ret", parent);
    addOperand(val);
  }

  @Override
  public String toString() {
    if (this.type instanceof VoidType) {
      return "ret void";
    }
    var val = this.operands.get(0);
    return "ret %s".formatted(val);
  }
}
