package ir.structure;

import java.util.ArrayList;

import ir.Constant;
import ir.type.BaseType;

public class Function extends Constant {
  public ArrayList<BasicBlock> blocks = new ArrayList<>();

  public Function(BaseType type, String name) {
    super(type, name);
  }
}
