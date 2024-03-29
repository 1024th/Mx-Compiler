package ir.inst;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.User;
import ir.type.BaseType;

public abstract class BaseInst extends User {
  public BasicBlock parent;

  public BaseInst(BaseType type, String name, BasicBlock parent) {
    super(type, name);
    setParent(parent);
  }

  public void setParent(BasicBlock parent) {
    if (this.parent != parent) {
      this.parent = parent;
      if (parent != null)
        parent.addInst(this);
    }
  }

  public abstract String toString();

  public boolean isTerminator() {
    return false;
  }

  public abstract void accept(IRVisitor visitor);
}
