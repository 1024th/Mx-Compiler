package asm.operand;

public class VirtualReg extends Reg {
  public int size;

  public VirtualReg(int size) {
    this.size = size;
  }

  public VirtualReg() {
    this(4);
  }
}
