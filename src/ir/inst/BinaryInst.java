package ir.inst;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;

public class BinaryInst extends BaseInst {
  public String op;

  public BinaryInst(String op, Value op1, Value op2, String name, BasicBlock parent) {
    super(op1.type, name, parent);
    this.op = op;
    addOperand(op1);
    addOperand(op2);
  }

  public Value op1() {
    return this.getOperand(0);
  }

  public Value op2() {
    return this.getOperand(1);
  }

  @Override
  public String toString() {
    return "%s = %s %s %s, %s".formatted(name(), op, type, op1().name(), op2().name());
  }

  @Override
  public void accept(IRVisitor visitor) {
    visitor.visit(this);
  }
}
