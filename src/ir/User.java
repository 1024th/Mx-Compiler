package ir;

import java.util.ArrayList;

import ir.type.BaseType;

public class User extends Value {
  public ArrayList<Value> operands = new ArrayList<>();

  public User(BaseType type, String name) {
    super(type, name);
  }

  public void addOperand(Value v) {
    this.operands.add(v);
  }
}
