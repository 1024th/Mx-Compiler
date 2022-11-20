package ir.type;

import java.util.ArrayList;

public class StructType extends BaseType {
  public String className;
  public ArrayList<BaseType> typeList = new ArrayList<>();

  public StructType(String className) {
    this.className = className;
  }

  @Override
  public int size() {
    int ret = 0;
    for (var t : this.typeList)
      ret += t.size();
    return ret;
  }

  @Override
  public String toString() {
    return "%class." + this.className;
  }
}
