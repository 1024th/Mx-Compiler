package middleend;

import java.util.ArrayList;

import ir.BasicBlock;
import ir.IRBuilder;
import ir.inst.BrInst;

/** Loop Invariant Code Motion */
public class LICM {
  IRBuilder irBuilder;

  public LICM(IRBuilder irBuilder) {
    this.irBuilder = irBuilder;
  }

  public void runOnModule(ir.Module module) {
    module.funcs.forEach(this::runOnFunc);
  }

  public void runOnFunc(ir.Function func) {
    new CFGBuilder().runOnFunc(func);
    new LoopAnalyzer().runOnFunc(func);
    func.rootLoops.forEach(this::runOnLoop);
  }

  void createPreHeader(Loop loop) {
    var preHeader = new BasicBlock(
        irBuilder.rename("preheader"), loop.header.parent);
    loop.preHeader = preHeader;

    // to avoid ConcurrentModificationException
    var headerPrevs = new ArrayList<BasicBlock>(loop.header.prevs);
    for (var pre : headerPrevs) {
      if (loop.tailers.contains(pre))
        continue;

      preHeader.prevs.add(pre);
      loop.header.prevs.remove(pre);

      pre.redirectSucc(loop.header, preHeader);
      loop.header.redirectPred(pre, preHeader);
    }

    new BrInst(loop.header, preHeader);
    preHeader.nexts.add(loop.header);

    if (loop.header.parent.entryBlock == loop.header) {
      loop.header.parent.entryBlock = preHeader;
    }

    var outerloop = loop.outerLoop;
    while (outerloop != null) {
      outerloop.blocks.add(loop.preHeader);
      outerloop = outerloop.outerLoop;
    }
  }

  public void runOnLoop(Loop loop) {
    loop.innerLoops.forEach(this::runOnLoop);

    createPreHeader(loop);
    boolean changed = true;
    while (changed) {
      changed = false;
      for (var block : loop.blocks) {
        var iter = block.insts.listIterator();
        while (iter.hasNext()) {
          var inst = iter.next();
          if (loop.isInvariant(inst)) {
            changed = true;
            iter.remove();
            loop.preHeader.addInstBeforeTerminator(inst);
          }
        }
      }
    }
  }
}
