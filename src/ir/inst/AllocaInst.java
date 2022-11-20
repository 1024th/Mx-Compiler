package ir.inst;

import ir.structure.BasicBlock;
import ir.type.BaseType;
import ir.type.PointerType;

public class AllocaInst extends BaseInst {
  BaseType elemType;

  public AllocaInst(BaseType type, String name, BasicBlock parent) {
    super(new PointerType(type), name, parent);
    this.elemType = type;
  }

  @Override
  public String toString() {
    return "%s = alloca %s, align %d".formatted(name, elemType.toString(), elemType.size());
  }
}
