package ir.inst;

import ir.Value;
import ir.structure.BasicBlock;
import ir.type.IntType;

public class IcmpInst extends BaseInst {
  public String op;

  public IcmpInst(String op, Value op1, Value op2, String name, BasicBlock parent) {
    super(new IntType(1), name, parent);
    this.op = op;
    addOperand(op1);
    addOperand(op2);
  }

  @Override
  public String toString() {
    var op1 = getOperand(0);
    var op2 = getOperand(1);
    return "%s = icmp %s %s %s, %s".formatted(name(), op, op1.type, op1.name(), op2.name());
  }
}
