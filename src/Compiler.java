import java.io.FileOutputStream;
import java.io.PrintStream;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import backend.BackEnd;
import frontend.FrontEnd;
import grammar.MxLexer;
import grammar.MxParser;
import middleend.MiddleEnd;
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

    boolean debug = System.getProperty("user.name").equals("ltc");
    var middleEnd = new MiddleEnd(frontEnd, debug);

    var backEnd = new BackEnd(middleEnd, debug);
  }
}
