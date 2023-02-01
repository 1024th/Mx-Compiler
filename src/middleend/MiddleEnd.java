package middleend;

import java.io.FileOutputStream;
import java.io.PrintStream;

import frontend.FrontEnd;
import ir.IRBuilder;
import ir.IRPrinter;

public class MiddleEnd {
  public IRBuilder irBuilder;
  public ir.Module irModule;

  public MiddleEnd(FrontEnd frontEnd) throws Exception {
    this.irBuilder = new IRBuilder(frontEnd.gScope);
    this.irBuilder.visit(frontEnd.astRootNode);
    this.irModule = this.irBuilder.module;

    var cfg = new CFGBuilder();
    this.irModule.funcs.forEach(func -> cfg.runOnFunc(func));

    var llFile = new FileOutputStream("out.ll");
    var ll = new PrintStream(llFile);
    var irPrinter = new IRPrinter(ll, "input.mx");
    irPrinter.print(this.irModule);
    llFile.close();

    var mem2reg = new Mem2Reg(irBuilder);
    this.irModule.funcs.forEach(func -> mem2reg.runOnFunc(func));

    llFile = new FileOutputStream("out-mem2reg.ll");
    irPrinter.p = ll = new PrintStream(llFile);
    irPrinter.print(this.irModule);
    llFile.close();

    new PhiElimination().runOnModule(irModule);
    llFile = new FileOutputStream("out-phi-elim.ll");
    irPrinter.p = ll = new PrintStream(llFile);
    irPrinter.print(this.irModule);
    llFile.close();
  }
}
