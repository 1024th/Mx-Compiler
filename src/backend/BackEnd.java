package backend;

import java.io.FileOutputStream;
import java.io.PrintStream;

import asm.ASMPrinter;
import middleend.MiddleEnd;
import utils.BuiltinAsmPrinter;

public class BackEnd {
  MiddleEnd middleEnd;
  asm.Module asmModule;
  boolean debug;

  public BackEnd(MiddleEnd middleEnd, boolean debug) throws Exception {
    this.middleEnd = middleEnd;
    this.debug = debug;

    this.asmModule = new asm.Module();
    new InstSelector(asmModule).visit(middleEnd.irModule);
    if (debug)
      printAsm("output.s.no.regalloc");
    new RegAllocator(debug).runOnModule(asmModule);
    // new StupidRegAllocator().runOnModule(asmModule);
    new StackAllocator().runOnModule(asmModule);
    new BlockMerging().runOnModule(asmModule);
    new BlockPlacement().runOnModule(asmModule);
    if (debug)
      printAsm("output-placement.s");
    new RemoveUselessInst().runOnModule(asmModule);

    printAsm("output.s");

    new BuiltinAsmPrinter("builtin.s");
  }

  void printAsm(String filename) throws Exception {
    var asmFile = new FileOutputStream(filename);
    var asm = new PrintStream(asmFile);
    new ASMPrinter(asm).runOnModule(asmModule);
    asmFile.close();
  }
}
