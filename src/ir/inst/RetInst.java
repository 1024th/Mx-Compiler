package ir.inst;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.VoidType;

public class RetInst extends BaseInst {
  public RetInst(BasicBlock parent) {
    super(new VoidType(), "ret", parent);
  }

  public RetInst(Value val, BasicBlock parent) {
    super(val.type, "ret", parent);
    addOperand(val);
  }

  public Value val() {
    return this.getOperand(0);
  }

  @Override
  public String toString() {
    if (this.type instanceof VoidType) {
      return "ret void";
    }
    return "ret %s".formatted(val().typedName());
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
