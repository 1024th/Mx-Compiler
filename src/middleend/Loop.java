package middleend;

import java.util.HashMap;
import java.util.HashSet;

import ir.BasicBlock;
import ir.Value;
import ir.constant.Constant;
import ir.inst.BaseInst;
import ir.inst.BrInst;
import ir.inst.CallInst;
import ir.inst.LoadInst;
import ir.inst.RetInst;
import ir.inst.StoreInst;

public class Loop {
  public BasicBlock preHeader;
  public BasicBlock header;
  public HashSet<BasicBlock> tailers = new HashSet<>();
  public HashSet<BasicBlock> blocks = new HashSet<>();

  public Loop outerLoop;
  public HashSet<Loop> innerLoops = new HashSet<>();

  public Loop(BasicBlock header) {
    this.header = header;
  }

  HashMap<BaseInst, Boolean> calcRecord = new HashMap<>();

  public boolean isInvariant(Value value) {
    if (value instanceof Constant)
      return true;
    if (value instanceof BaseInst inst)
      return isInvariant(inst);
    return false;
  }

  public boolean isInvariant(BaseInst inst) {
    var res = calcRecord.get(inst);
    if (res != null) {
      return res;
    }

    if (!blocks.contains(inst.parent)) {
      res = true;
    } else if (inst instanceof RetInst || inst instanceof BrInst ||
        inst instanceof LoadInst || inst instanceof StoreInst ||
        inst instanceof CallInst) {
      return false;
    } else {
      res = true;
      for (var operand : inst.operands) {
        if (!this.isInvariant(operand)) {
          res = false;
          break;
        }
      }
    }
    calcRecord.put(inst, res);
    return res;
  }
}
