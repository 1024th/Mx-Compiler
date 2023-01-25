package asm;

import java.util.ArrayList;
import java.util.HashSet;

import asm.inst.BaseInst;
import asm.operand.Reg;

public class Block extends Operand {
  public String label;
  public ArrayList<BaseInst> insts = new ArrayList<>();

  /** for control flow graph */
  public ArrayList<Block> prevs = new ArrayList<>(), nexts = new ArrayList<>();

  /** for liveness analysis */
  public HashSet<Reg> liveIn = new HashSet<>(), liveOut = new HashSet<>();

  public Block(String label) {
    this.label = label;
  }

  public void addInst(BaseInst inst) {
    this.insts.add(inst);
  }
}
