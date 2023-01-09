package asm;

import java.util.ArrayList;

import asm.operand.Reg;

public class Function extends Operand {
  public String label;
  public ArrayList<Block> blocks = new ArrayList<>();
  public ArrayList<Reg> args = new ArrayList<>();

  /** stack usage */
  public int allocaCnt = 0,
      spilledReg = 0,
      spilledArg = 0,
      totalStack = 0;

  public Function(String label) {
    this.label = label;
  }

  public void addBlock(Block block) {
    this.blocks.add(block);
  }
}
