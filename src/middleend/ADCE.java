package middleend;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import ir.BasicBlock;
import ir.inst.BaseInst;
import ir.inst.BrInst;
import ir.inst.CallInst;
import ir.inst.RetInst;
import ir.inst.StoreInst;
import utils.TextUtils;

/**
 * Aggressive Dead Code Elimination
 */
public class ADCE {
  boolean debug;

  public ADCE(boolean debug) {
    this.debug = debug;
  }

  void runOnModule(ir.Module module) {
    module.funcs.forEach(this::runOnFunc);
  }

  void runOnFunc(ir.Function func) {
    new CFGBuilder().runOnFunc(func);
    new DomTreeBuilder(true).runOnFunc(func);
    debugPrint(func);
    markLive(func);

    for (var block : func.blocks) {
      var iter1 = block.phiInsts.iterator();
      while (iter1.hasNext()) {
        var inst = iter1.next();
        if (liveInst.contains(inst))
          continue;
        iter1.remove();
      }
      var iter2 = block.insts.listIterator();
      while (iter2.hasNext()) {
        var inst = iter2.next();
        if (liveInst.contains(inst))
          continue;
        iter2.remove();
        if (inst.isTerminator()) {
          var dest = getLivePDom(block);
          iter2.add(new BrInst(dest, null));
        }
      }
    }
  }

  void debugPrint(ir.Function func) {
    if (!debug)
      return;
    ;
    var p = System.out;
    for (var block : func.blocks) {
      p.printf("%s:  ; preds = %s\n", block.name, TextUtils.join(block.prevs));
      var node = block.dtNode;
      p.printf("; cd: %s\n", TextUtils.join(node.domFrontier));
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

  /**
   * Gets the live post dominator (in the control flow graph)
   * of the block.
   */
  BasicBlock getLivePDom(BasicBlock block) {
    var dom = block.dtNode.idom;
    while (!liveBlock.contains(dom.origin)) {
      dom = dom.idom;
    }
    return dom.origin;
  }
}
