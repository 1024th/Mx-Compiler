package ir.inst;

import ir.Value;
import ir.structure.BasicBlock;
import ir.type.BaseType;

public class ZextInst extends BaseInst {
  public ZextInst(Value val, BaseType toType, String name, BasicBlock parent) {
    super(toType, name, parent);
    addOperand(val);
  }

  @Override
  public String toString() {
    var val = this.getOperand(0);
    return "%s = zext %s to %s".formatted(name, val.typedName(), this.type);
  }
}
