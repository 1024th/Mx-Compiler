package backend;

import asm.inst.ITypeInst;
import asm.inst.JumpInst;

public class RemoveUselessInst {
  public void runOnModule(asm.Module module) {
    module.funcs.forEach(this::runOnFunc);
  }

  public void runOnFunc(asm.Function func) {
    for (int i = 0; i < func.blocks.size(); ++i) {
      var block = func.blocks.get(i);
      var nxtBlock = i + 1 < func.blocks.size() ? func.blocks.get(i + 1) : null;
      var iter = block.insts.listIterator();
      while (iter.hasNext()) {
        var inst = iter.next();
        if (inst instanceof ITypeInst iinst
            && (iinst.op.equals("addi") || iinst.op.equals("subi"))
            && iinst.rd == iinst.rs && iinst.imm.val == 0) {
          iter.remove();
        }
      }
      var t = block.insts.getLast();
      if (t instanceof JumpInst j && j.dest == nxtBlock)
        block.insts.remove(block.insts.size() - 1);
    }
  }
}
