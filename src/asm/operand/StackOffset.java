package asm.operand;

public class StackOffset extends Imm {
  public enum Type {
    getArg, alloca, spill, putArg, decSp, incSp
  };

  public Type type;

  public StackOffset(int offset, Type type) {
    super(offset);
    this.type = type;
  }
}
