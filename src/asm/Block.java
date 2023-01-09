package asm;

import java.util.ArrayList;

import asm.inst.BaseInst;

public class Block extends Operand {
  public String label;
  public ArrayList<BaseInst> insts = new ArrayList<>();

  public Block(String label) {
    this.label = label;
  }

  public void addInst(BaseInst inst) {
    this.insts.add(inst);
  }
}
