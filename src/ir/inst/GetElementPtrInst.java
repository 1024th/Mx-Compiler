package ir.inst;

import ir.Value;
import ir.structure.BasicBlock;
import ir.type.BaseType;
import utils.TextUtils;

public class GetElementPtrInst extends BaseInst {
  public GetElementPtrInst(String name, BaseType retType, Value ptr, BasicBlock parent, Value... idx) {
    super(retType, name, parent);
    addOperand(ptr);
    for (var i : idx)
      addOperand(i);
  }

  @Override
  public String toString() {
    return "%s = getelementptr inbounds %s, %s".formatted(
        name(), type, TextUtils.join(operands, x -> x.typedName()));
  }
}
