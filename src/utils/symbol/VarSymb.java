package utils.symbol;

import utils.type.VarType;

public class VarSymb extends Symbol {
  public VarType type;

  public VarSymb(String name, VarType type) {
    super(name);
    this.type = type;
  }
}
