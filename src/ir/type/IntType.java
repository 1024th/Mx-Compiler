package ir.type;

public class IntType extends BaseType {
  public int bitWidth;

  public boolean isBool; // bool variables are stored as i8 type

  public IntType(int bitWidth, boolean isBool) {
    this.bitWidth = bitWidth;
    this.isBool = isBool;
  }

  public IntType(int bitWidth) {
    this(bitWidth, false);
  }

  @Override
  public int size() {
    // return (this.bitWidth - 1) / 8 + 1;
    return 4;
  }

  @Override
  public String toString() {
    return "i%d".formatted(this.bitWidth);
  }
}
