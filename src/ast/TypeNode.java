package ast;

import grammar.MxParser.NonArrayTypeContext;
import utils.Position;

public class TypeNode extends ASTNode {
  public String typename;
  public boolean isArrayType, isClass;
  public int dimension;

  public TypeNode(String typename, boolean isClass, Position pos) {
    super(pos);
    this.typename = typename;
    this.isClass = isClass;
    this.isArrayType = false;
    this.dimension = 0;
  }

  public TypeNode(TypeNode other) {
    super(other.pos);
    this.typename = other.typename;
    this.isClass = other.isClass;
    this.isArrayType = other.isArrayType;
    this.dimension = other.dimension;
  }

  public TypeNode(NonArrayTypeContext ctx) {
    this(ctx.getText(), ctx.Identifier() != null || ctx.getText().equals("string"), new Position(ctx));
  }

  public TypeNode(String typename, boolean isClass, int dimension, Position pos) {
    super(pos);
    this.typename = typename;
    this.isClass = isClass;
    this.isArrayType = true;
    this.dimension = dimension;
  }

  public boolean isBool() {
    return this.typename.equals("bool") && this.isArrayType == false;
  }

  public boolean isInt() {
    return this.typename.equals("int") && this.isArrayType == false;
  }

  public boolean isString() {
    return this.typename.equals("string") && this.isArrayType == false;
  }

  public boolean isVoid() {
    return this.typename.equals("void");
  }

  public boolean isNull() {
    return this.typename.equals("null");
  }

  public boolean match(TypeNode other) {
    return (this.typename.equals(other.typename) &&
        this.isArrayType == other.isArrayType &&
        this.dimension == other.dimension);
  }

  public boolean canAssignTo(TypeNode other) {
    if (this.isNull()) {
      return other.isClass || other.isArrayType;
    }
    return this.match(other);
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
