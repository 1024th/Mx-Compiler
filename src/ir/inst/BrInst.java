package ir.inst;

import ir.Value;
import ir.structure.BasicBlock;
import ir.type.VoidType;

public class BrInst extends BaseInst {
  public BrInst(BasicBlock dst, BasicBlock parent) {
    super(new VoidType(), "br", parent);
    addOperand(dst);
  }

  public BrInst(Value condition, BasicBlock ifTrue, BaseInst ifFalse, BasicBlock parent) {
    super(new VoidType(), "br", parent);
    addOperand(condition);
    addOperand(ifTrue);
    addOperand(ifFalse);
  }

  @Override
  public String toString() {
    if (this.operands.size() == 1) {
      var dest = this.operands.get(0);
      return "br " + dest.typedName();
    }
    var cond = this.operands.get(0);
    var ifTrue = this.operands.get(1);
    var ifFalse = this.operands.get(2);
    return "br %s, %s, %s".formatted(cond.typedName(), ifTrue.typedName(), ifFalse.typedName());
  }
}
