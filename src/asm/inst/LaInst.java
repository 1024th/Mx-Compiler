package asm.inst;

import asm.Block;
import asm.operand.GlobalObj;
import asm.operand.Reg;

public class LaInst extends BaseInst {
  public Reg rd;
  public GlobalObj symbol;

  public LaInst(Reg rd, GlobalObj symbol, Block parent) {
    super(parent);
    this.rd = rd;
    this.symbol = symbol;
  }

  @Override
  public String toString() {
    return "la %s, %s".formatted(rd, symbol.name);
  }

  @Override
  public void accept(asm.InstVisitor visitor) {
    visitor.visit(this);
  }
}
