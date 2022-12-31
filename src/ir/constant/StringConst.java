package ir.constant;

import ir.type.ArrayType;
import ir.type.IntType;
import ir.type.PointerType;

public class StringConst extends Constant {
  public String val;

  public StringConst(String name, String val) {
    super(new PointerType(new ArrayType(new IntType(8), val.length() + 1)), name);
    this.val = val + "\0";
  }

  @Override
  public String toString() {
    return "%s = private unnamed_addr constant %s c\"%s\"".formatted(
        this.name(), ((PointerType) this.type).elemType, this.escaped());
  }

  public String escaped() {
    return val.replace("\\", "\\5C")
        .replace("\0", "\\00")
        .replace("\n", "\\0A")
        .replace("\t", "\\09")
        .replace("\"", "\\22");
  }
}
