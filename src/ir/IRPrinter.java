package ir;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import ir.structure.BasicBlock;
import ir.structure.Function;
import ir.structure.Module;

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
    p.println("target datalayout = \"e-m:e-p:32:32-i64:64-n32-S128\"");
    p.println("target triple = \"riscv32-unknown-unknown\"");

    // TODO function declares
    p.println();
    for (var cls : module.classes) {
      p.printf("%s = type {%s}\n", cls, join(cls.typeList));
    }
    p.println();
    for (var v : module.globalVars) {
      p.println(v);
    }
    p.println();
    for (var func : module.funcs) {
      this.print(func);
    }
  }

  public void print(Function func) {
    var type = func.type();
    p.printf("define %s %s(%s) {\n",
        type.retType, func.name,
        join(func.operands, x -> x.typedName()));
    for (var i: func.blocks) {
      this.print(i);
      p.println();
    }
    p.println("}\n");
  }

  public void print(BasicBlock block) {
    p.printf("%s:\n", block.name);
    for (var i: block.insts) {
      p.println("  " + i);
    }
  }

  // sep.join([str(x) for x in arr])
  public static <T> String join(ArrayList<T> arr, String sep, Formatter<T> fmt) {
    var it = arr.iterator();
    if (!it.hasNext())
      return "";
    StringBuilder sb = new StringBuilder();
    while (true) {
      T e = it.next();
      sb.append(fmt.getString(e));
      if (!it.hasNext())
        return sb.toString();
      sb.append(sep);
    }
  }

  public static <T> String join(ArrayList<T> arr, Formatter<T> fmt) {
    return join(arr, ", ", fmt);
  }

  public static <T> String join(ArrayList<T> arr) {
    return join(arr, ", ", x -> x.toString());
  }
}

interface Formatter<T> {
  String getString(T e);
}
