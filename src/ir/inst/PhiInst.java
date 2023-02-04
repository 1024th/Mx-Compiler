package ir.inst;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.BaseType;

public class PhiInst extends BaseInst {
  public PhiInst(BaseType type, String name, BasicBlock parent) {
    super(type, name, null);
    setParent(parent);
  }

  @Override
  public void setParent(BasicBlock parent) {
    if (this.parent != parent) {
      this.parent = parent;
      if (parent != null)
        parent.phiInsts.add(this);
    }
  }

  public void addBranch(Value val, BasicBlock block) {
    addOperand(val);
    addOperand(block);
  }

  public void removeBranch(BasicBlock removedBlock) {
    for (int i = 1; i < operands.size(); i += 2) {
      if (getOperand(i) == removedBlock) {
        operands.remove(i);
        operands.remove(i - 1);
        return;
      }
    }
  }

  @Override
  public String toString() {
    var ret = "%s = phi %s ".formatted(name, type);
    for (int i = 0; i < operands.size(); i += 2) {
      ret += "[%s, %s]".formatted(getOperand(i).name(), getOperand(i + 1).name());
      if (i < operands.size() - 2)
        ret += ", ";
    }
    return ret;
  }

  @Override
  public void accept(IRVisitor visitor) {
    visitor.visit(this);
  }
}
