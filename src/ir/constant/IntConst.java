package ir.constant;

import ir.type.IntType;

public class IntConst extends Constant {
  public int val;

  public IntConst(int val, int bitWidth) {
    super(new IntType(bitWidth), null);
    this.val = val;
  }

  public String name() {
    return "%d".formatted(this.val);
  }
}
