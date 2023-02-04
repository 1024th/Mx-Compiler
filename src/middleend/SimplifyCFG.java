package middleend;

import java.util.HashSet;

import ir.BasicBlock;

/**
 * <ul>
 * <li>Removes basic blocks with no predecessors.
 * <li>Eliminates PHI nodes for basic blocks with a single predecessor.
 */
public class SimplifyCFG {
  void runOnModule(ir.Module module) {
    module.funcs.forEach(this::runOnFunc);
  }

  void runOnFunc(ir.Function func) {
    boolean changed = true;
    while (changed) {
      new CFGBuilder().runOnFunc(func);
      changed = removeDeadBlock(func);
    }
  }

  /** @return {@code true} if the function contains dead block. */
  boolean removeDeadBlock(ir.Function func) {
    var deadBlocks = new HashSet<BasicBlock>();

    boolean changed = true;
    while (changed) {
      changed = false;
      for (var block : func.blocks) {
        if (block == func.entryBlock)
          continue;
        if (deadBlocks.contains(block))
          continue;
        if (block.prevs.isEmpty()) {
          deadBlocks.add(block);
          for (var suc : block.nexts) {
            suc.prevs.remove(block);
          }
          changed = true;
        }
      }
    }

    for (var block : deadBlocks) {
      func.blocks.remove(block);
      for (var suc : block.nexts) {
        suc.prevs.remove(block);
        removePhiBranchIn(suc, block);
      }
      block.prevs.clear();
      block.nexts.clear();
    }

    return deadBlocks.size() > 1;
  }

  public void removePhiBranchIn(BasicBlock block, BasicBlock removed) {
    var iter = block.phiInsts.iterator();
    while (iter.hasNext()) {
      var phi = iter.next();
      phi.removeBranch(removed);
      if (phi.operands.size() == 2) {
        iter.remove();
        phi.replaceAllUsesWith(phi.getOperand(0));
      }
    }
  }
}
