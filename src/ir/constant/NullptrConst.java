package ir.constant;

import ir.type.IntType;
import ir.type.PointerType;

public class NullptrConst extends Constant {
  public NullptrConst() {
    super(new PointerType(new IntType(32)), null);
  }

  public String toString() {
    return "null";
  }
}
