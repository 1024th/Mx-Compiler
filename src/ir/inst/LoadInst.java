package ir.inst;

import ir.Value;
import ir.structure.BasicBlock;
import ir.type.PointerType;

public class LoadInst extends BaseInst {
  public LoadInst(String name, Value ptr, BasicBlock parent) {
    super(((PointerType) ptr.type).elemType, name, parent);
    addOperand(ptr);
  }

  @Override
  public String toString() {
    var ptr = this.getOperand(0);
    return "%s = load %s, %s, align %d".formatted(this.name, this.type, ptr.typedName(), ptr.type.size());
  }
}
