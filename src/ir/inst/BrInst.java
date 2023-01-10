package ir.inst;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.VoidType;

public class BrInst extends BaseInst {
  public BrInst(BasicBlock dest, BasicBlock parent) {
    super(new VoidType(), "br", parent);
    addOperand(dest);
  }

  public BasicBlock dest() {
    return (BasicBlock) this.getOperand(0);
  }

  public BrInst(Value condition, BasicBlock ifThen, BasicBlock ifElse, BasicBlock parent) {
    super(new VoidType(), "br", parent);
    addOperand(condition);
    addOperand(ifThen);
    addOperand(ifElse);
  }

  public Value cond() {
    return this.getOperand(0);
  }

  public BasicBlock ifThen() {
    return (BasicBlock) this.getOperand(1);
  }

  public BasicBlock ifElse() {
    return (BasicBlock) this.getOperand(2);
  }

  @Override
  public String toString() {
    if (this.operands.size() == 1) {
      return "br " + dest().typedName();
    }
    return "br %s, %s, %s".formatted(cond().typedName(), ifThen().typedName(), ifElse().typedName());
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
