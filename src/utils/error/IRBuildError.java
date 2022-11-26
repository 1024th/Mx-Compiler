package utils.error;

public class IRBuildError extends RuntimeException {
  public IRBuildError(String msg) {
    super("IR Build Error: " + msg);
  }
}
