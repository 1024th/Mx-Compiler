package middleend;

import java.util.ArrayDeque;
import java.util.HashSet;

import ir.BasicBlock;
import ir.Value;
import ir.constant.IntConst;
import ir.inst.BaseInst;
import ir.inst.BinaryInst;
import ir.inst.GetElementPtrInst;

/**
 * Naive Common Subexpression Elimination.
 * This is just a peephole optimazation that finds out the same instruction
 * in the peephole and replace the latter with the former.
 */
public class CSE {
  int peepholeSize = 10;

  public void runOnModule(ir.Module module) {
    module.funcs.forEach(this::runOnFunc);
  }

  public void runOnFunc(ir.Function func) {
    func.blocks.forEach(this::runOnBlock);
    func.blocks.forEach(this::runOnBlock);
    func.blocks.forEach(this::runOnBlock);
  }

  public void runOnBlock(BasicBlock block) {
    var peephole = new ArrayDeque<BaseInst>();
    var removed = new HashSet<BaseInst>();
    for (var inst : block.insts) {
      for (var prev : peephole) {
        if (isSame(prev, inst)) {
          inst.replaceAllUsesWith(prev);
          removed.add(inst);
        }
      }
      if (!removed.contains(inst))
        peephole.add(inst);
      if (peephole.size() > peepholeSize) {
        peephole.pop();
      }
    }
    var iter = block.insts.listIterator();
    while (iter.hasNext()) {
      var inst = iter.next();
      if (removed.contains(inst))
        iter.remove();
    }
  }

  boolean eq(Value a, Value b) {
    if (a == b)
      return true;
    if (a instanceof IntConst c1 && b instanceof IntConst c2) {
      return c1.val == c2.val;
    }
    return false;
  }

  boolean isSame(BaseInst prev, BaseInst next) {
    if (prev instanceof BinaryInst b1 && next instanceof BinaryInst b2) {
      if (b1.op.equals(b2.op) && eq(b1.op1(), b2.op1()) && eq(b1.op2(), b2.op2()))
        return true;
    }
    if (prev instanceof GetElementPtrInst g1 &&
        next instanceof GetElementPtrInst g2) {
      if (g1.operands.size() != g2.operands.size())
        return false;
      for (int i = 0; i < g1.operands.size(); ++i) {
        if (!eq(g1.getOperand(i), g2.getOperand(i)))
          return false;
      }
      return true;
    }
    return false;
  }
}
