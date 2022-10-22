package ast.expr;

import java.util.ArrayList;

import ast.ASTVisitor;
import ast.ParamListNode;
import ast.stmt.SuiteNode;
import utils.Position;

public class LambdaExprNode extends ExprNode {
  public boolean capture;
  public ParamListNode params;
  public SuiteNode body;
  public ArrayList<ExprNode> args = new ArrayList<>();

  public LambdaExprNode(boolean capture, ParamListNode params, SuiteNode body, Position pos) {
    super(null, false, pos);
    this.capture = capture;
    this.body = body;
    this.params = params;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
