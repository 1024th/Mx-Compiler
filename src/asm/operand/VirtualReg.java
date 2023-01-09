package asm.operand;

public class VirtualReg extends Reg {
  public static int cnt = 0;
  public int index, size;

  public VirtualReg(int size) {
    this.index = cnt;
    this.size = size;
    cnt++;
  }

  public VirtualReg() {
    this(4);
  }
}
