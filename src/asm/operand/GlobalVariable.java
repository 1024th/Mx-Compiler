package asm.operand;

public class GlobalVariable extends GlobalObj {
  public int initVal, size;

  public GlobalVariable(String name, int initVal, int size) {
    this.name = name;
    this.initVal = initVal;
    this.size = size;
  }
}
