package backend;

import java.util.HashMap;

public class BlockMerging {
  public void runOnModule(asm.Module module) {
    module.funcs.forEach(this::runOnFunc);
  }

  public void runOnFunc(asm.Function func) {
    blockMoveMap.clear();
    for (var pred : func.blocks) {
      pred = getAlias(pred);
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

  HashMap<asm.Block, asm.Block> blockMoveMap = new HashMap<>();

  asm.Block getAlias(asm.Block block) {
    var a = blockMoveMap.get(block);
    if (a == null)
      return block;
    a = getAlias(a);
    blockMoveMap.put(block, a);
    return a;
  }
}
