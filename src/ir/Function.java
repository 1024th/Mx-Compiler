package ir;

import java.util.ArrayList;
import java.util.HashSet;

import ir.constant.Constant;
import ir.type.FuncType;
import middleend.Loop;

public class Function extends Constant {
  public ArrayList<BasicBlock> blocks = new ArrayList<>();

  public BasicBlock entryBlock, exitBlock;
  public Value retValPtr;

  public boolean isMember;

  public HashSet<Loop> rootLoops = new HashSet<>();

  public Function(FuncType type, String name, boolean isMember) {
    super(type, name);
    this.isMember = isMember;
  }

  public FuncType type() {
    return (FuncType) type;
  }

  public void addArg(Value arg) {
    this.addOperand(arg);
  }

  public Value getArg(int i) {
    return this.getOperand(i);
  }

  public void accept(IRVisitor visitor) {
    visitor.visit(this);
  }
}
