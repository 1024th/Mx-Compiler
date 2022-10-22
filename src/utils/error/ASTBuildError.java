package utils.error;

import utils.Position;

public class ASTBuildError extends Error {
  public ASTBuildError(String msg, Position pos) {
    super("AST Build Error: " + msg, pos);
  }
}
