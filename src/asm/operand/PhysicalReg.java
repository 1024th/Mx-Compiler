package asm.operand;

import java.util.ArrayList;
import java.util.HashMap;

public class PhysicalReg extends Reg {
  public String name;

  public PhysicalReg(String name) {
    this.name = name;
  }

  public static HashMap<String, PhysicalReg> regMap = new HashMap<>();
  public static ArrayList<PhysicalReg> callerSaved = new ArrayList<>();
  public static ArrayList<PhysicalReg> calleeSaved = new ArrayList<>();
  static {
    regMap.put("zero", new PhysicalReg("zero"));
    regMap.put("ra", new PhysicalReg("ra"));
    regMap.put("sp", new PhysicalReg("sp"));
    for (int i = 0; i < 7; ++i) {
      var reg = new PhysicalReg("t" + i);
      regMap.put("t" + i, reg);
      callerSaved.add(reg);
    }
    for (int i = 0; i < 8; ++i) {
      var reg = new PhysicalReg("a" + i);
      regMap.put("a" + i, reg);
      callerSaved.add(reg);
    }
    for (int i = 0; i < 12; ++i) {
      var reg = new PhysicalReg("s" + i);
      regMap.put("s" + i, reg);
      calleeSaved.add(reg);
    }
  }

  public String toString() {
    return name;
  }
}
