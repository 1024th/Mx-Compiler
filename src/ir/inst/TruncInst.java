package ir.inst;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.BaseType;

public class TruncInst extends BaseInst {
  public TruncInst(Value val, BaseType toType, String name, BasicBlock parent) {
    super(toType, name, parent);
    addOperand(val);
  }

  @Override
  public String toString() {
    var val = this.getOperand(0);
    return "%s = trunc %s to %s".formatted(name(), val.typedName(), this.type);
  }

  @Override
  public void accept(IRVisitor visitor) {
    visitor.visit(this);
  }
}
