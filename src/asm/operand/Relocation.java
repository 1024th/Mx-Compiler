package asm.operand;

public class Relocation extends Imm {
  public enum RelocationType {
    hi, lo
  };

  public RelocationType type;
  public GlobalObj obj;

  public Relocation(GlobalObj obj) {
    super(0);
    this.obj = obj;
  }

  @Override
  public String toString() {
    return "%s(%s)".formatted(type, obj);
  }
}
