package ir.constant;

import ir.User;
import ir.type.BaseType;

// A constant is a value that is immutable at runtime.
// Functions are constants because their address is immutable.
// Same with global variables.
public class Constant extends User {
  public Constant(BaseType type, String name) {
    super(type, name);
  }
}
