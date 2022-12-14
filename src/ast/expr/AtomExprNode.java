package ast.expr;

import ast.ASTVisitor;
import ast.TypeNode;
import ast.stmt.SingleVarDefNode;
import utils.Position;

public class AtomExprNode extends ExprNode {
  public String text;
  public SingleVarDefNode varDef;

  public AtomExprNode(String text, TypeNode type, boolean isLeftVal, Position pos) {
    super(type, isLeftVal, pos);
    this.text = text;
  }

  public String unescape() {
    return text.substring(1, text.length() - 1)
        .replace("\\\"", "\"")
        .replace("\\n", "\n")
        .replace("\\t", "\t")
        .replace("\\\\", "\\");
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
