package ir;

import java.io.PrintStream;

import utils.TextUtils;

public class IRPrinter {
  private PrintStream p;
  private String fileName;

  public IRPrinter(PrintStream p, String fileName) {
    this.p = p;
    this.fileName = fileName;
  }

  public void print(Module module) {
    p.printf("; ModuleID = '%s'\n", fileName);
    p.printf("source_filename = \"%s\"\n", fileName);
    // p.println("target datalayout = \"e-m:e-p:32:32-i64:64-n32-S128\"");
    // p.println("target triple = \"riscv32-unknown-unknown\"");
    p.println("target datalayout = \"e-m:e-p:32:32-p270:32:32-p271:32:32-p272:64:64-f64:32:64-f80:32-n8:16:32-S128\"");
    p.println("target triple = \"i386-pc-linux-gnu\"");

    for (var func : module.funcDecls) {
      this.declare(func);
    }
    for (var v : module.globalVars) {
      p.println(v);
    }
    for (var s : module.stringConsts) {
      p.println(s);
    }
    for (var cls : module.classes) {
      p.printf("%s = type {%s}\n", cls, TextUtils.join(cls.typeList));
    }
    for (var func : module.funcs) {
      this.print(func);
    }
  }

  public void print(Function func) {
    var type = func.type();
    p.printf("define %s %s(%s) {\n",
        type.retType, func.name,
        TextUtils.join(func.operands, x -> x.typedName()));
    for (var i: func.blocks) {
      this.print(i);
      p.println();
    }
    p.println("}\n");
  }

  public void declare(Function func) {
    var type = func.type();
    p.printf("declare %s %s(%s)\n",
        type.retType, func.name,
        TextUtils.join(type.paramTypes));
  }

  public void print(BasicBlock block) {
    p.printf("%s:\n", block.name);
    for (var i: block.insts) {
      p.println("  " + i);
    }
  }
}
