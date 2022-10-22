package ast.stmt;

import ast.ASTNode;
import utils.Position;

public abstract class StmtNode extends ASTNode {
  public StmtNode(Position pos) {
    super(pos);
  }
}
