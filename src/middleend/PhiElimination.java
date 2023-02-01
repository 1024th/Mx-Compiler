package middleend;

import ir.BasicBlock;
import ir.inst.MoveInst;

public class PhiElimination {
  // TODO: Is this right?
  public void runOnFunc(ir.Function func) {
    for (var block : func.blocks) {
      for (var phi : block.phiInsts) {
        for (int i = 0; i < phi.operands.size(); i += 2) {
          var val = phi.getOperand(i);
          var fromBlock = (BasicBlock) phi.getOperand(i + 1);
          var mv = new MoveInst(phi, val, null);
          fromBlock.addInstBeforeTerminator(mv);
        }
      }
    }
  }

  public void runOnModule(ir.Module module) {
    module.funcs.forEach(this::runOnFunc);
  }
}
