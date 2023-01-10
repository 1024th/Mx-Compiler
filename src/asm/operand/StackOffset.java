package asm.operand;

public class StackOffset extends Imm {
  public enum StackOffsetType {
    getArg, alloca, spill, putArg, decSp, incSp
  };

  public StackOffsetType type;

  public StackOffset(int offset, StackOffsetType type) {
    super(offset);
    this.type = type;
  }
}
