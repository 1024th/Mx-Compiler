package backend;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Block Placement: the idea is to put sequentially executed blocks together
 * and hopefully increase the number of fall-through branches.
 * This pass basically orders blocks in depth-first order.
 */
public class BlockPlacement {
  public void runOnModule(asm.Module module) {
    module.funcs.forEach(this::runOnFunc);
  }

  public void runOnFunc(asm.Function func) {
    visited.clear();
    reordered = new ArrayList<>();
    dfs(func.entryBlock);
    func.blocks = reordered;
  }

  HashSet<asm.Block> visited = new HashSet<>();
  ArrayList<asm.Block> reordered;

  void dfs(asm.Block block) {
    if (visited.contains(block))
      return;
    visited.add(block);
    reordered.add(block);
    for (var succ : block.nexts) {
      dfs(succ);
    }
  }
}
