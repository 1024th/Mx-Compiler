import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Test {
  public static void main(String[] args) throws Exception {
    CharStream input = CharStreams.fromFileName("input");
    ArrayInitLexer lexer = new ArrayInitLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ArrayInitParser parser = new ArrayInitParser(tokens);
    
    ParseTree tree = parser.init();
    System.out.println(tree.toStringTree(parser));
  }
}
