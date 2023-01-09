package asm.operand;

public class Relocation extends Imm {
  public enum FuncType {
    hi, lo
  };

  public FuncType func;
  public String symbol;

  public Relocation(String symbol) {
    super(0);
    this.symbol = symbol;
  }

  @Override
  public String toString() {
    return "%s(%s)".formatted(func, symbol);
  }
}
