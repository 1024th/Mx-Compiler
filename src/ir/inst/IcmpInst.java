package ir.inst;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.IntType;

public class IcmpInst extends BaseInst {
  public String op;

  public IcmpInst(String op, Value op1, Value op2, String name, BasicBlock parent) {
    super(new IntType(1), name, parent);
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
    return "%s = icmp %s %s, %s".formatted(name(), op, op1().typedName(), op2().name());
  }

  @Override
  public void accept(IRVisitor visitor) {
    visitor.visit(this);
  }
}
