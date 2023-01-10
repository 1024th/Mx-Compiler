package asm.operand;

public class StringConst extends GlobalObj {
  public String val;

  public StringConst(String name, String val) {
    this.name = name;
    this.val = val;
  }

  public String escaped() {
    return val.replace("\\", "\\\\")
        .replace("\n", "\\n")
        .replace("\t", "\\t")
        .replace("\"", "\\\"")
        .replace("\0", "");
  }

}
