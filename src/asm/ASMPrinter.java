package asm;

import java.io.PrintStream;

public class ASMPrinter implements ModulePass, FuncPass, BlockPass {
  private PrintStream p;

  public ASMPrinter(PrintStream p) {
    this.p = p;
  }

  @Override
  public void runOnModule(Module module) {
    if (module.globalVars.size() > 0)
      p.println("\t.data");
    for (var v : module.globalVars) {
      p.printf("\t.globl\t%s\n", v.name);
      p.printf("%s:\n", v.name);
      p.printf("\t%s\t%d\n", v.size == 4 ? ".word" : ".byte", v.initVal);
    }

    if (module.stringConsts.size() > 0)
      p.println("\t.rodata\n");
    for (var s : module.stringConsts) {
      p.printf("\t.globl\t%s\n", s.name);
      p.printf("%s:\n", s.name);
      p.printf("\t.asciz\t\"%s\"\n", s.escaped());
    }

    p.println("\t.text");
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
    block.insts.forEach(inst -> p.println("\t" + inst));
  }
}
