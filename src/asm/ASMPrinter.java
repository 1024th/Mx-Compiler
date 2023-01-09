package asm;

import java.io.PrintStream;

public class ASMPrinter implements ModulePass, FuncPass, BlockPass {
  private PrintStream p;

  public ASMPrinter(PrintStream p) {
    this.p = p;
  }

  @Override
  public void runOnModule(Module module) {
    // TODO global
    p.print("\t.text\n");
    module.funcs.forEach(this::runOnFunc);
  }

  @Override
  public void runOnFunc(Function func) {
    p.printf("\t.globl\t%s\n", func.label);
    p.printf("%s:\n", func.label);
    func.blocks.forEach(this::runOnBlock);
  }

  @Override
  public void runOnBlock(Block block) {
    p.print(block.label + ":\n");
    block.insts.forEach(inst -> p.print(inst));
  }
}
