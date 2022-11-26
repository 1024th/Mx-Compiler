package ir.structure;

import java.util.ArrayList;

import ir.constant.Constant;
import ir.type.FuncType;

public class Function extends Constant {
  public ArrayList<BasicBlock> blocks = new ArrayList<>();
  public ArrayList<Argument> args = new ArrayList<>();

  public Function(FuncType type, String name) {
    super(type, name);
  }

  public FuncType type() {
    return (FuncType) type;
  }
}
