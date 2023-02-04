package backend;

import java.io.FileOutputStream;
import java.io.PrintStream;

import asm.ASMPrinter;
import middleend.MiddleEnd;
import utils.BuiltinAsmPrinter;

public class BackEnd {
  MiddleEnd middleEnd;
  boolean debug;

  public BackEnd(MiddleEnd middleEnd, boolean debug) throws Exception {
    this.middleEnd = middleEnd;
    this.debug = debug;

    var asmModule = new asm.Module();
    new InstSelector(asmModule).visit(middleEnd.irModule);
    if (debug) {
      var asmFile = new FileOutputStream("output.s.no.regalloc");
      var asm = new PrintStream(asmFile);
      new ASMPrinter(asm).runOnModule(asmModule);
      asmFile.close();
    }
    new RegAllocator(debug).runOnModule(asmModule);
    // new StupidRegAllocator().runOnModule(asmModule);
    new StackAllocator().runOnModule(asmModule);
    new BlockMerging().runOnModule(asmModule);
    new BlockPlacement().runOnModule(asmModule);
    new RemoveUselessInst().runOnModule(asmModule);

    var asmFile = new FileOutputStream("output.s");
    var asm = new PrintStream(asmFile);
    new ASMPrinter(asm).runOnModule(asmModule);
    asmFile.close();

    new BuiltinAsmPrinter("builtin.s");
  }
}
