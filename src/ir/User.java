package ir;

import java.util.ArrayList;

import ir.type.BaseType;

public class User extends Value {
  public ArrayList<Value> operands = new ArrayList<>();

  public User(BaseType type, String name) {
    super(type, name);
  }

  public void addOperand(Value v) {
    v.addUser(this);
    this.operands.add(v);
  }

  public Value getOperand(int i) {
    return this.operands.get(i);
  }

  public void replaceOperand(Value oldVal, Value newVal) {
    for (int i = 0; i < operands.size(); ++i) {
      var op = getOperand(i);
      if (op != oldVal)
        continue;
      oldVal.users.remove(this);
      operands.set(i, newVal);
      newVal.addUser(this);
    }
  }
}
