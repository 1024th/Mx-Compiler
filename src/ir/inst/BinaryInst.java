package ir.inst;

import ir.Value;
import ir.structure.BasicBlock;

public class BinaryInst extends BaseInst {
  public String op;

  public BinaryInst(String op, Value op1, Value op2, String name, BasicBlock parent) {
    super(op1.type, name, parent);
    this.op = op;
    addOperand(op1);
    addOperand(op2);
  }

  @Override
  public String toString() {
    var op1 = getOperand(0);
    var op2 = getOperand(1);
    return "%s = %s %s %s, %s".formatted(name(), op, type, op1.name(), op2.name());
  }
}
