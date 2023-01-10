package asm.operand;

import asm.Operand;

public class GlobalObj extends Operand {
  public String name;

  @Override
  public String toString() {
    return name;
  }
}
