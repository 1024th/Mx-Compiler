package middleend;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import ir.BasicBlock;
import ir.inst.BaseInst;
import ir.inst.CallInst;
import ir.inst.RetInst;
import ir.inst.StoreInst;

/**
 * Aggressive Dead Code Elimination
 */
public class ADCE {
  void runOnModule(ir.Module module) {
    module.funcs.forEach(this::runOnFunc);
  }

  void runOnFunc(ir.Function func) {
    new CFGBuilder().runOnFunc(func);
    new DomTreeBuilder(true).runOnFunc(func);
    markLive(func);

    for (var block : func.blocks) {
      var iter = block.insts.iterator();
      while (iter.hasNext()) {
        var inst = iter.next();
        if (liveInst.contains(inst))
          continue;
        if (inst.isTerminator()) {
          continue;
        }
        iter.remove();
      }
    }
  }

  Queue<BaseInst> worklist = new LinkedList<>();

  HashSet<BaseInst> liveInst = new HashSet<>();
  HashSet<BasicBlock> liveBlock = new HashSet<>();

  void markLive(ir.Function func) {
    for (var block : func.blocks) {
      for (var inst : block.insts)
        if (isAlwaysLive(inst))
          markInstLive(inst);
    }

    while (!worklist.isEmpty()) {
      var inst = worklist.poll();
      markInstLive(inst);
      markBlockLive(inst.parent);

      for (var operand : inst.operands) {
        if (operand instanceof BaseInst i) {
          markInstLive(i);
        } else if (operand instanceof BasicBlock b) {
          markTerminatorLive(b);
        }
      }
    }
  }

  /**
   * We do not perform function-level side effect analysis, i.e.,
   * we assume every function call to be live.
   */
  boolean isAlwaysLive(BaseInst inst) {
    if (inst instanceof RetInst)
      return true;
    if (inst instanceof StoreInst)
      return true;
    if (inst instanceof CallInst)
      return true;
    return false;
  }

  void markInstLive(BaseInst inst) {
    if (liveInst.contains(inst))
      return;
    liveInst.add(inst);
    worklist.offer(inst);
  }

  void markTerminatorLive(BasicBlock block) {
    markInstLive(block.getTerminator());
  }

  void markBlockLive(BasicBlock block) {
    if (liveBlock.contains(block))
      return;
    liveBlock.add(block);
    for (var dependence : block.dtNode.domFrontier) {
      markTerminatorLive(dependence.origin);
    }
  }
}
