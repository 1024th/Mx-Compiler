import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import asm.ASMPrinter;
import ast.ASTBuilder;
import ast.ProgramNode;
import backend.InstSelector;
import backend.RegAllocator;
import backend.StackAllocator;
import frontend.SemanticChecker;
import frontend.SymbolCollector;
import grammar.MxLexer;
import grammar.MxParser;
import ir.IRBuilder;
import ir.IRPrinter;
import utils.MxErrorListener;
import utils.scope.GlobalScope;

public class Compiler {
  public static void main(String[] args) throws Exception {
    // CharStream input = CharStreams.fromFileName("input");
    var input = CharStreams.fromStream(System.in);
    var lexer = new MxLexer(input);
    lexer.removeErrorListeners();
    lexer.addErrorListener(new MxErrorListener());
    var tokens = new CommonTokenStream(lexer);
    var parser = new MxParser(tokens);
    parser.removeErrorListeners();
    parser.addErrorListener(new MxErrorListener());

    var root = parser.program();
    var astBuilder = new ASTBuilder();
    var rootNode = (ProgramNode) astBuilder.visit(root);
    var gScope = new GlobalScope();
    var symbolCollector = new SymbolCollector(gScope);
    symbolCollector.visit(rootNode);
    // gScope.print();
    var semanticChecker = new SemanticChecker(gScope);
    semanticChecker.visit(rootNode);
    // System.out.println(root.toStringTree());

    var irBuilder = new IRBuilder(gScope);
    irBuilder.visit(rootNode);
    var irPrinter = new IRPrinter(System.out, "input.mx");
    irPrinter.print(irBuilder.module);

    var asmModule = new asm.Module();
    new InstSelector(asmModule).visit(irBuilder.module);
    new RegAllocator().runOnModule(asmModule);
    new StackAllocator().runOnModule(asmModule);
    new ASMPrinter(System.out).runOnModule(asmModule);
  }
}
