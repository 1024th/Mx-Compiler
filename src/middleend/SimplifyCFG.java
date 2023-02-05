package middleend;

import java.util.HashMap;
import java.util.HashSet;

import ir.BasicBlock;

/**
 * <ul>
 * <li>Merges a basic block into its predecessor if there is only one and the
 * predecessor only has one successor.
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
      changed = mergeBlock(func);
    }
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

  boolean mergeBlock(ir.Function func) {
    blockMoveMap.clear();
    for (var pred : func.blocks) {
      pred = getAlias(pred);
      if (pred.nexts.size() != 1)
        continue;
      var succ = pred.nexts.get(0);
      if (succ.prevs.size() != 1)
        continue;
      if (func.exitBlock == succ)
        func.exitBlock = pred;
      pred.insts.remove(pred.insts.size() - 1);
      assert succ.phiInsts.isEmpty();
      for (var sucInst : succ.insts) {
        sucInst.parent = pred;
        pred.insts.add(sucInst);
      }
      pred.nexts.clear();
      pred.nexts.addAll(succ.nexts);
      for (var suc : succ.nexts) {
        suc.redirectPred(succ, pred);
      }
      blockMoveMap.put(succ, pred);
    }
    var iter = func.blocks.iterator();
    while (iter.hasNext()) {
      var block = iter.next();
      if (blockMoveMap.containsKey(block))
        iter.remove();
    }
    return blockMoveMap.size() != 0;
  }

  HashMap<BasicBlock, BasicBlock> blockMoveMap = new HashMap<>();

  BasicBlock getAlias(BasicBlock block) {
    var a = blockMoveMap.get(block);
    if (a == null)
      return block;
    a = getAlias(a);
    blockMoveMap.put(block, a);
    return a;
  }
}
