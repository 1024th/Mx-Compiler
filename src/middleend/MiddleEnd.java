package middleend;

import java.io.FileOutputStream;
import java.io.PrintStream;

import frontend.FrontEnd;
import ir.IRBuilder;
import ir.IRPrinter;

public class MiddleEnd {
  public IRBuilder irBuilder;
  public ir.Module irModule;
  private boolean debug;

  public MiddleEnd(FrontEnd frontEnd, boolean debug) throws Exception {
    this.debug = debug;
    this.irBuilder = new IRBuilder(frontEnd.gScope);
    this.irBuilder.visit(frontEnd.astRootNode);
    this.irModule = this.irBuilder.module;

    var cfg = new CFGBuilder();
    this.irModule.funcs.forEach(func -> cfg.runOnFunc(func));
    debugPrint("out.ll");

    var mem2reg = new Mem2Reg(irBuilder);
    this.irModule.funcs.forEach(func -> mem2reg.runOnFunc(func));
    debugPrint("out-mem2reg.ll");

    new PhiElimination().runOnModule(irModule);
    debugPrint("out-phi-elim.ll");
  }

  void debugPrint(String filename) throws Exception {
    if (this.debug == false)
      return;
    var llFile = new FileOutputStream(filename);
    var ll = new PrintStream(llFile);
    var irPrinter = new IRPrinter(ll, "input.mx");
    irPrinter.print(this.irModule);
    llFile.close();
  }
}
