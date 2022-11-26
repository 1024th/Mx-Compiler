package ir.structure;

import ir.Value;
import ir.type.BaseType;

public class Argument extends Value {
  public int argNo;

  public Argument(BaseType type, String name, int argNo) {
    super(type, name);
    this.argNo = argNo;
  }
}
