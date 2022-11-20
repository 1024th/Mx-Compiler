package ir.type;

public class PointerType extends BaseType {
  public BaseType elemType;

  public PointerType(BaseType elemType) {
    this.elemType = elemType;
  }

  @Override
  public int size() {
    return 4; // target: 32-bit
  }

  @Override
  public String toString() {
    return this.elemType.toString() + "*";
  }
}
