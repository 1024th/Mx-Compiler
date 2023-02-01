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
}
