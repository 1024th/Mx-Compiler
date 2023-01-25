import java.io.FileOutputStream;
import java.io.PrintStream;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import asm.ASMPrinter;
import backend.InstSelector;
import backend.RegAllocator;
import backend.StackAllocator;
import frontend.FrontEnd;
import grammar.MxLexer;
import grammar.MxParser;
import middleend.MiddleEnd;
import utils.BuiltinAsmPrinter;
import utils.MxErrorListener;

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

    var frontEnd = new FrontEnd(parser.program());

    var middleEnd = new MiddleEnd(frontEnd);

    var asmModule = new asm.Module();
    new InstSelector(asmModule).visit(middleEnd.irModule);
    new RegAllocator().runOnModule(asmModule);
    new StackAllocator().runOnModule(asmModule);

    var asmFile = new FileOutputStream("output.s");
    var asm = new PrintStream(asmFile);
    new ASMPrinter(asm).runOnModule(asmModule);
    asmFile.close();

    new BuiltinAsmPrinter("builtin.s");
  }
}
