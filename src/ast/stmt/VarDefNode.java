package ast.stmt;

import java.util.ArrayList;

import ast.ASTVisitor;
import ast.TypeNode;
import utils.Position;

public class VarDefNode extends StmtNode {
  public TypeNode type;
  public ArrayList<SingleVarDefNode> vars = new ArrayList<>();

  public VarDefNode(TypeNode type, Position pos) {
    super(pos);
    this.type = type;
  }

  public void addVar(SingleVarDefNode varDef) {
    vars.add(varDef);
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
