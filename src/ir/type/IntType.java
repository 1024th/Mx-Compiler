package ir.type;

public class IntType extends BaseType {
  public int bitWidth;

  public IntType(int bitWidth) {
    this.bitWidth = bitWidth;
  }

  @Override
  public int size() {
    return (this.bitWidth - 1) / 4 + 1;
  }

  @Override
  public String toString() {
    return "i%d".formatted(this.bitWidth);
  }
}
