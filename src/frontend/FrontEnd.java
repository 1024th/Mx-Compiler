package frontend;

import grammar.MxParser.ProgramContext;
import utils.scope.GlobalScope;

public class FrontEnd {
  public ast.ProgramNode astRootNode;
  public GlobalScope gScope;

  public FrontEnd(ProgramContext root) {
    var astBuilder = new ast.ASTBuilder();
    this.astRootNode = (ast.ProgramNode) astBuilder.visit(root);
    this.gScope = new GlobalScope();
    var symbolCollector = new SymbolCollector(gScope);
    symbolCollector.visit(astRootNode);
    // gScope.print();
    var semanticChecker = new SemanticChecker(gScope);
    semanticChecker.visit(astRootNode);
    // System.out.println(root.toStringTree());
  }
}
