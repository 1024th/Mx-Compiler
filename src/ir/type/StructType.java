package ir.type;

import java.util.ArrayList;

public class StructType extends BaseType {
  public String name;
  public ArrayList<BaseType> typeList = new ArrayList<>();

  public StructType(String name) {
    this.name = name;
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
    return "%class." + this.name;
  }
}
