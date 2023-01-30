package asm.operand;

import java.util.HashMap;
import java.util.HashSet;

public class PhysicalReg extends Reg {
  public String name;

  public PhysicalReg(String name) {
    this.name = name;
  }

  public static PhysicalReg zero, ra, sp;
  public static HashMap<String, PhysicalReg> regMap = new HashMap<>();
  public static HashSet<PhysicalReg> callerSaved = new HashSet<>();
  public static HashSet<PhysicalReg> calleeSaved = new HashSet<>();
  public static HashSet<PhysicalReg> assignable = new HashSet<>();

  // @formatter:off
  public static PhysicalReg reg(String name) {
    return regMap.get(name);
  }
  public static PhysicalReg regA(int i) { return reg("a" + i); }
  public static PhysicalReg regS(int i) { return reg("s" + i); }
  public static PhysicalReg regT(int i) { return reg("t" + i); }
  // @formatter:on

  static {
    zero = new PhysicalReg("zero");
    ra = new PhysicalReg("ra");
    sp = new PhysicalReg("sp");
    regMap.put("zero", zero);
    regMap.put("ra", ra);
    regMap.put("sp", sp);
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
    assignable.addAll(callerSaved);
    assignable.addAll(calleeSaved);
  }

  public String toString() {
    return name;
  }
}
