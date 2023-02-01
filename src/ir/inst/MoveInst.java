package ir.inst;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;

/** This is NOT an LLVM IR instruction. Used in Phi Elimination. */
public class MoveInst extends BaseInst {
  public MoveInst(Value dest, Value src, BasicBlock parent) {
    super(null, null, parent);
    addOperand(dest);
    addOperand(src);
  }

  public Value dest() {
    return this.getOperand(0);
  }

  public Value src() {
    return this.getOperand(1);
  }

  @Override
  public String toString() {
    return "move %s %s".formatted(dest().name(), src().name());
  }

  @Override
  public void accept(IRVisitor visitor) {
    visitor.visit(this);
  }
}
