package ir.inst;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.BaseType;
import ir.type.PointerType;
import utils.TextUtils;

public class GetElementPtrInst extends BaseInst {
  public GetElementPtrInst(String name, BaseType retType, Value ptr, BasicBlock parent, Value... idx) {
    super(retType, name, parent);
    addOperand(ptr);
    for (var i : idx)
      addOperand(i);
  }

  public Value ptr() {
    return this.getOperand(0);
  }

  @Override
  public String toString() {
    return "%s = getelementptr inbounds %s, %s".formatted(
        name(), ((PointerType) ptr().type).elemType,
        TextUtils.join(operands, x -> x.typedName()));
  }

  @Override
  public void accept(IRVisitor visitor) {
    visitor.visit(this);
  }
}
