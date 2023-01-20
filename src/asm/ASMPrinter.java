package asm;

import java.io.PrintStream;

public class ASMPrinter implements ModulePass, FuncPass, BlockPass {
  private PrintStream p;

  public ASMPrinter(PrintStream p) {
    this.p = p;
  }

  @Override
  public void runOnModule(Module module) {
    for (var v : module.globalVars) {
      p.printf("\t.data\n");
      p.printf("\t.globl %s\n", v.name);
      p.printf("%s:\n", v.name);
      if (v.size == 1)
        p.printf("\t.byte %d\n", v.initVal);
      else
        p.printf("\t.word %d\n", v.initVal);
    }

    for (var s : module.stringConsts) {
      p.printf("\t.rodata\n");
      p.printf("\t.globl %s\n", s.name);
      p.printf("%s:\n", s.name);
      p.printf("\t.asciz \"%s\"\n", s.escaped());
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
