package ir;

import java.util.ArrayList;

import ir.type.BaseType;

public class Value {
  public BaseType type;
  public String name;
  public ArrayList<User> users = new ArrayList<>();

  // corresponding assembly operand of this IR value
  public asm.Operand asm = null;

  public Value(BaseType type, String name) {
    this.type = type;
    this.name = name;
  }

  public void addUser(User user) {
    users.add(user);
  }

  // Change all uses of this to point to a new Value
  public void replaceAllUsesWith(Value v) {
    for (var user : users) {
      var operands = user.operands;
      for (int i = 0; i < operands.size(); ++i) {
        if (operands.get(i) == this) {
          operands.set(i, v);
        }
      }
      v.users.add(user);
    }
    users.clear();
  }

  public String name() {
    return this.name;
  }

  public String typedName() {
    return this.type + " " + this.name();
  }
}
