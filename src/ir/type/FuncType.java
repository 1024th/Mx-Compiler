package ir.type;

import java.util.ArrayList;

public class FuncType extends BaseType {
  public BaseType retType;
  public ArrayList<BaseType> paramTypes = new ArrayList<>();

  public FuncType(BaseType retType) {
    this.retType = retType;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public String toString() {
    return null;
  }
}
