package ast.stmt;

import ast.BaseNode;
import utils.Position;

public abstract class StmtNode extends BaseNode {
  public StmtNode(Position pos) {
    super(pos);
  }
}
