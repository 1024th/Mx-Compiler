package utils.scope;

import ir.BasicBlock;

public class LoopScope extends Scope {
  public BasicBlock continueBlock, breakBlock;

  public LoopScope(Scope parentScope) {
    super(parentScope);
  }
}
