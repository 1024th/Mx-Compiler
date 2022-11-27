package ir.constant;

import ir.type.IntType;

public class IntConst extends Constant {
  public int val;

  public IntConst(int val, int bitWidth) {
    super(new IntType(bitWidth), null);
    this.val = val;
  }

  public String toString() {
    return "%s %d".formatted(this.type, this.val);
  }
}
