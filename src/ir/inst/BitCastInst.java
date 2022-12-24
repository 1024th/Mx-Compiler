package ir.inst;

import ir.Value;
import ir.structure.BasicBlock;
import ir.type.BaseType;

public class BitCastInst extends BaseInst {
  public BitCastInst(String name, BaseType toType, Value val, BasicBlock parent) {
    super(toType, name, parent);
    addOperand(val);
  }

  @Override
  public String toString() {
    var val = this.getOperand(0);
    return "%s = bitcast %s to %s".formatted(name(), val.typedName(), this.type);
  }
}
