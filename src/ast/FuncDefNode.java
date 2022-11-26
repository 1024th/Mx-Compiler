package ast;

import ast.stmt.SuiteNode;
import utils.Position;
import utils.scope.FuncScope;

public class FuncDefNode extends ASTNode {
  public final String funcName;
  public final TypeNode returnType;
  public ParamListNode params;
  public SuiteNode body;

  public FuncScope scope;

  public FuncDefNode(String funcName, TypeNode returnType, SuiteNode suite, ParamListNode params, Position pos) {
    super(pos);
    this.funcName = funcName;
    this.returnType = returnType;
    this.body = suite;
    this.params = params;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
