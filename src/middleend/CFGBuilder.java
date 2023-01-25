package middleend;

/** Control Flow Graph Builder */
public class CFGBuilder {
  public void runOnFunc(ir.Function func) {
    // clear old graph
    for (var block : func.blocks) {
      block.prevs.clear();
      block.nexts.clear();
    }

    for (var block : func.blocks) {
      var terminator = block.insts.get(block.insts.size() - 1);
      if (terminator instanceof ir.inst.BrInst b) {
        if (b.operands.size() == 1) {
          link(block, b.dest());
        } else {
          link(block, b.ifThen());
          link(block, b.ifElse());
        }
      }
    }
  }

  private void link(ir.BasicBlock prev, ir.BasicBlock next) {
    prev.nexts.add(next);
    next.prevs.add(prev);
  }
}
