package utils;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Position {
  private final int row, column;

  public Position(int row, int col) {
    this.row = row;
    this.column = col;
  }

  public Position(Token token) {
    this.row = token.getLine();
    this.column = token.getCharPositionInLine();
  }

  public Position(TerminalNode terminal) {
    this(terminal.getSymbol());
  }

  public Position(ParserRuleContext ctx) {
    this(ctx.getStart());
  }

  public int row() {
    return row;
  }

  public int col() {
    return column;
  }

  public String toString() {
    return row + "," + column;
  }
}
