package ir.inst;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.VoidType;

public class BrInst extends BaseInst {
  public BrInst(BasicBlock dst, BasicBlock parent) {
    super(new VoidType(), "br", parent);
    addOperand(dst);
  }

  public BrInst(Value condition, BasicBlock ifThen, BasicBlock ifElse, BasicBlock parent) {
    super(new VoidType(), "br", parent);
    addOperand(condition);
    addOperand(ifThen);
    addOperand(ifElse);
  }

  @Override
  public String toString() {
    if (this.operands.size() == 1) {
      var dest = this.getOperand(0);
      return "br " + dest.typedName();
    }
    var cond = this.getOperand(0);
    var ifThen = this.getOperand(1);
    var ifElse = this.getOperand(2);
    return "br %s, %s, %s".formatted(cond.typedName(), ifThen.typedName(), ifElse.typedName());
  }

  @Override
  public boolean isTerminator() {
    return true;
  }

  @Override
  public void accept(IRVisitor visitor) {
    visitor.visit(this);
  }
}
