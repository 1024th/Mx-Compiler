package ir.structure;

import java.util.ArrayList;

import ir.Value;
import ir.inst.BaseInst;
import ir.type.LabelType;

// Basic blocks are Values because they are referenced by instructions such as
// branches and switch tables. The type of a BasicBlock is "LabelType" because
// the basic block represents a label to which a branch can jump.
public class BasicBlock extends Value {
  public ArrayList<BaseInst> insts = new ArrayList<>();
  public Function parent;

  public boolean terminated = false;

  public BasicBlock(String name, Function parent) {
    super(new LabelType(), name);
    this.parent = parent;
    if (parent != null) {
      parent.blocks.add(this);
    }
  }

  public void addInst(BaseInst inst) {
    insts.add(inst);
    if (inst.isTerminator())
      this.terminated = true;
  }
}
