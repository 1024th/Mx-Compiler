package ir.inst;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.PointerType;

public class StoreInst extends BaseInst {
  public StoreInst(Value val, Value ptr, BasicBlock parent) {
    // value's type is determined by ptr's type, because value can be
    // nullptr constant which does not contain type info.
    super(((PointerType) ptr.type).elemType, "store", parent);
    addOperand(val);
    addOperand(ptr);
  }

  @Override
  public String toString() {
    var val = this.getOperand(0);
    var ptr = this.getOperand(1);
    return "store %s %s, %s, align %d".formatted(this.type, val.name(), ptr.typedName(), val.type.size());
  }

  @Override
  public void accept(IRVisitor visitor) {
    visitor.visit(this);
  }
}
