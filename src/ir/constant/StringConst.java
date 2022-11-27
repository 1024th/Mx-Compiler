package ir.constant;

import ir.type.ArrayType;
import ir.type.IntType;

public class StringConst extends Constant {
  public String val;

  public StringConst(String val) {
    super(new ArrayType(new IntType(8), val.length() + 1), null);
    this.val = val + "\0";
  }

  public String toString() {
    return "\"%s\"".formatted(this.escaped());
  }

  public String escaped() {
    return val.replace("\\", "\\5C")
        .replace("\0", "\\00")
        .replace("\n", "\\0A")
        .replace("\t", "\\09")
        .replace("\"", "\\22");
  }
}
