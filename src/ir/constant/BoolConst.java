package ir.constant;

import ir.type.IntType;

public class BoolConst extends Constant {
  public boolean val;

  public BoolConst(boolean val) {
    super(new IntType(1), null);
    this.val = val;
  }

  public String name() {
    return this.val ? "true" : "false";
  }
}
