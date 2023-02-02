package backend;

import java.util.HashMap;

public class BlockMerging {
  public void runOnModule(asm.Module module) {
    module.funcs.forEach(this::runOnFunc);
  }

  public void runOnFunc(asm.Function func) {
    var blockMoveMap = new HashMap<asm.Block, asm.Block>();
    for (var pred : func.blocks) {
      var block = blockMoveMap.get(pred);
      if (block != null)
        pred = block;
      if (pred.nexts.size() != 1)
        continue;
      var succ = pred.nexts.get(0);
      if (succ.prevs.size() != 1)
        continue;
      pred.insts.remove(pred.insts.size() - 1);
      pred.insts.addAll(succ.insts);
      pred.nexts.clear();
      pred.nexts.addAll(succ.nexts);
      blockMoveMap.put(succ, pred);
    }
    var iter = func.blocks.iterator();
    while (iter.hasNext()) {
      var block = iter.next();
      if (blockMoveMap.containsKey(block))
        iter.remove();
    }
  }
}
