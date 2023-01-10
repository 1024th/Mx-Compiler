package asm.operand;

public class Relocation extends Imm {
  public enum RelocationType {
    hi, lo
  };

  public RelocationType type;
  public String symbol;

  public Relocation(String symbol) {
    super(0);
    this.symbol = symbol;
  }

  @Override
  public String toString() {
    return "%s(%s)".formatted(type, symbol);
  }
}
