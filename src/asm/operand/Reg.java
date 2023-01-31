package asm.operand;

import asm.Operand;
import backend.InterferenceGraph.Node;

public class Reg extends Operand {
  /** Register Allocation */
  public PhysicalReg color;
  /** stack offset of spilled register */
  public StackOffset stackOffset;
  /** interference graph node */
  public Node node = new Node();

  @Override
  public String toString() {
    if (color == null)
      return this.getClass().getName() + "@" + Integer.toHexString(this.hashCode());
    return color.toString();
  }
}
