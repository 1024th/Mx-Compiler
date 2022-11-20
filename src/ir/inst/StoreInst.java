package ir.inst;

import ir.Value;
import ir.structure.BasicBlock;
import ir.type.PointerType;

public class StoreInst extends BaseInst {
  public StoreInst(Value val, Value ptr, BasicBlock parent) {
    super(((PointerType) ptr.type).elemType, "store", parent);
    addOperand(val);
    addOperand(ptr);
  }

  @Override
  public String toString() {
    var val = this.operands.get(0);
    var ptr = this.operands.get(1);
    return "store %s %s, %s, align %d".formatted(this.type, val.name, ptr.typedName(), val.type.size());
  }
}
