package ir.constant;

import ir.Value;
import ir.type.BaseType;
import ir.type.PointerType;

public class GlobalVariable extends Constant {
  public Value initVal;

  public GlobalVariable(BaseType type, String name) {
    super(new PointerType(type), name);
  }

  public String toString() {
    var elemType = ((PointerType) this.type).elemType;
    var init = this.initVal == null ? "zeroinitializer" : this.initVal.toString();
    return "%s = global %s %s, align %d".formatted(this.name(), elemType, init, this.type.size());
  }
}
