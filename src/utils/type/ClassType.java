package utils.type;

import utils.scope.ClassScope;

public class ClassType extends BaseType {
  public final String name;
  public ClassScope scope;

  public ClassType(String name) {
    super(BaseType.TypeKind.CLASS);
    this.name = name;
  }
}
