package utils.type;

public class VarType extends BaseType {
  public int dimension; // for array type

  public VarType(TypeKind kind) {
    super(kind);
    this.dimension = 0;
  }

  public VarType(VarType other) {
    super(other.kind);
    this.dimension = other.dimension;
  }
}
