package ir.structure;

import java.util.ArrayList;

import ir.constant.GlobalVariable;
import ir.type.StructType;

public class Module {
  public ArrayList<GlobalVariable> globalVars = new ArrayList<>();
  public ArrayList<Function> funcs = new ArrayList<>();
  public ArrayList<StructType> classes = new ArrayList<>();

  public ArrayList<Function> funcDecls = new ArrayList<>();
}
