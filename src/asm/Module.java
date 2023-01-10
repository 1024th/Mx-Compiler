package asm;

import java.util.ArrayList;

import asm.operand.GlobalVariable;
import asm.operand.StringConst;

public class Module {
  public ArrayList<Function> funcs = new ArrayList<>();

  public ArrayList<GlobalVariable> globalVars = new ArrayList<>();
  public ArrayList<StringConst> stringConsts = new ArrayList<>();
}
