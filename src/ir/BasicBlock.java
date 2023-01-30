package ir;

import java.util.ArrayList;

import ir.inst.AllocaInst;
import ir.inst.BaseInst;
import ir.type.LabelType;

// Basic blocks are Values because they are referenced by instructions such as
// branches and switch tables. The type of a BasicBlock is "LabelType" because
// the basic block represents a label to which a branch can jump.
public class BasicBlock extends Value {
  public ArrayList<BaseInst> insts = new ArrayList<>();
  public Function parent;

  public boolean terminated = false;

  /** for control flow graph */
  public ArrayList<BasicBlock> prevs = new ArrayList<>(), nexts = new ArrayList<>();

  /** for register allocation and optimize */
  public int loopDepth;

  public BasicBlock(String name, Function parent, int loopDepth) {
    super(new LabelType(), name);
    this.parent = parent;
    if (parent != null) {
      parent.blocks.add(this);
    }
    this.loopDepth = loopDepth;
  }

  public void addInst(BaseInst inst) {
    if (inst instanceof AllocaInst i) {
      addAlloca(i);
      return;
    }
    if (this.terminated)
      return;
    insts.add(inst);
    if (inst.isTerminator())
      this.terminated = true;
  }

  public void addAlloca(AllocaInst allocaInst) {
    for (int i = 0; i < insts.size(); ++i) {
      var inst = insts.get(i);
      if (!(inst instanceof AllocaInst)) {
        insts.add(i, allocaInst);
        return;
      }
    }
    insts.add(allocaInst);
  }

  @Override
  public String name() {
    return "%" + this.name;
  }

  public void accept(IRVisitor visitor) {
    visitor.visit(this);
  }
}
