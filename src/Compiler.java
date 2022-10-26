import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import ast.ASTBuilder;
import ast.ProgramNode;
import frontend.SemanticChecker;
import frontend.SymbolCollector;
import grammar.MxLexer;
import grammar.MxParser;
import utils.MxErrorListener;
import utils.scope.GlobalScope;

public class Compiler {
  public static void main(String[] args) throws Exception {
    // CharStream input = CharStreams.fromFileName("input");
    CharStream input = CharStreams.fromStream(System.in);
    MxLexer lexer = new MxLexer(input);
    lexer.removeErrorListeners();
    lexer.addErrorListener(new MxErrorListener());
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    MxParser parser = new MxParser(tokens);
    parser.removeErrorListeners();
    parser.addErrorListener(new MxErrorListener());

    ParseTree root = parser.program();
    ASTBuilder astBuilder = new ASTBuilder();
    ProgramNode rootNode = (ProgramNode) astBuilder.visit(root);
    GlobalScope gScope = new GlobalScope();
    SymbolCollector symbolCollector = new SymbolCollector(gScope);
    symbolCollector.visit(rootNode);
    // gScope.print();
    SemanticChecker semanticChecker = new SemanticChecker(gScope);
    semanticChecker.visit(rootNode);
    // System.out.println(root.toStringTree());
  }
}
