package utils.symbol;

import utils.Position;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class Symbol {
  public final String name;
  public final Position pos;

  Symbol(String name) {
    this.name = name;
    this.pos = null;
  }

  Symbol(String name, ParserRuleContext ctx) {
    this.name = name;
    this.pos = new Position(ctx);
  }
}
