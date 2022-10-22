package ast;

import ast.stmt.SuiteNode;
import utils.Position;

public class FuncDefNode extends ASTNode {
  public final String funcName;
  public final TypeNode returnType;
  public ParamListNode params;
  public SuiteNode suite;

  public FuncDefNode(String funcName, TypeNode returnType, SuiteNode suite, ParamListNode params, Position pos) {
    super(pos);
    this.funcName = funcName;
    this.returnType = returnType;
    this.suite = suite;
    this.params = params;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
