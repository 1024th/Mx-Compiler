package ir.type;

public class ArrayType extends BaseType {
  public BaseType elemType;
  public int len;

  public ArrayType(BaseType elemType, int len) {
    this.elemType = elemType;
    this.len = len;
  }

  @Override
  public int size() {
    return this.len * this.elemType.size();
  }

  @Override
  public String toString() {
    return "[%d x %s]".formatted(len, elemType.toString());
  }
}
