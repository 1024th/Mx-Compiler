package utils.type;

public abstract class BaseType {
  public enum TypeKind {
    NULL, INT, BOOL, STRING, VOID, CLASS, FUNC
  }

  public TypeKind kind;

  public BaseType(TypeKind kind) {
    this.kind = kind;
  }
}