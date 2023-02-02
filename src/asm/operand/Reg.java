package asm.operand;

import asm.Operand;
import backend.InterferenceGraph.Node;

public class Reg extends Operand {
  /** Register Allocation */
  public Reg color;
  /** stack offset of spilled register */
  public StackOffset stackOffset;
  /** interference graph node */
  public Node node = new Node(this);
  /** node in the interference graph of spilled registers */
  public Node nodeSpilled = new Node(this);

  @Override
  public String toString() {
    if (color == null || color == this)
      return this.getClass().getName() + "@" + Integer.toHexString(this.hashCode());
    return color.toString();
  }
}
