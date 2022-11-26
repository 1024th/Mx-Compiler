package ir;

import ast.*;
import ast.expr.*;
import ast.stmt.*;
import ir.constant.GlobalVariable;
import ir.structure.Function;
import ir.structure.Module;
import ir.type.*;
import utils.error.IRBuildError;
import utils.scope.ClassScope;
import utils.scope.GlobalScope;

public class IRBuilder implements ASTVisitor {
  public GlobalScope gScope;
  public ClassScope clsScope;
  public Module module;

  public IRBuilder(GlobalScope gScope) {
    this.gScope = gScope;
  }

  @Override
  public void visit(ProgramNode node) {
    for (var i : node.defs) {
      if (i instanceof ClassDefNode) {
        declareClass((ClassDefNode) i);
      }
      if (i instanceof VarDefNode) {
        for (var j : node.defs)
          declareGlobalVar((SingleVarDefNode) j);
      }
    }
    for (var i : node.defs) {
      if (i instanceof ClassDefNode)
        declareMemberFunc((ClassDefNode) i);
      if (i instanceof FuncDefNode)
        declareFunc((FuncDefNode) i);
    }
    // TODO Auto-generated method stub
    for (var i : node.defs) {
      if (i instanceof ClassDefNode)
        i.accept(this);
    }
    for (var i : node.defs) {
      if (i instanceof VarDefNode)
        i.accept(this);
    }
    for (var i : node.defs) {
      if (i instanceof FuncDefNode)
        i.accept(this);
    }
  }

  @Override
  public void visit(ClassDefNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(ClassCtorDefNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(FuncDefNode node) {
    // TODO Auto-generated method stub
    var func = gScope.getFunc(node.funcName);
  }

  @Override
  public void visit(VarDefNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(SingleVarDefNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(ForStmtNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(IfStmtNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(WhileStmtNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(BreakStmtNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(ContinueStmtNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(ReturnStmtNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(ExprStmtNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(SuiteNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(TypeNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(AssignExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(AtomExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(BinaryExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(FuncCallExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(ParamListNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(IndexExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(MemberExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(NewExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(PostfixExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(PrefixExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(UnaryExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(LambdaExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(ParamNode node) {
    // TODO Auto-generated method stub
  }

  private void declareClass(ClassDefNode node) {
    StructType cls = new StructType("%class." + node.className);
    for (var i : node.defs) {
      if (i instanceof VarDefNode) {
        cls.typeList.add(getType(((VarDefNode) i).type));
      }
    }
    module.classes.add(cls);
    gScope.addClassType(cls);
  }

  private void declareMemberFunc(ClassDefNode node) {
    var cls = gScope.getClassType(node.className);
    for (var i : node.defs) {
      if (i instanceof FuncDefNode) {
        var funcDef = (FuncDefNode) i;
        var funcType = new FuncType(getType(funcDef.returnType));
        funcType.paramTypes.add(new PointerType(cls)); // "this" pointer
        for (var j : funcDef.params.params) {
          funcType.paramTypes.add(getType(j.type));
        }
        var func = new Function(funcType, "@%s.%s".formatted(node.className, funcDef.funcName));
        module.funcs.add(func);
        gScope.addFunc(func);
      }
    }
  }

  private void declareFunc(FuncDefNode node) {
    var funcType = new FuncType(getType(node.returnType));
    for (var j : node.params.params) {
      funcType.paramTypes.add(getType(j.type));
    }
    var func = new Function(funcType, "@" + node.funcName);
    module.funcs.add(func);
    gScope.addFunc(func);
  }

  private void declareGlobalVar(SingleVarDefNode node) {
    var name = "@" + node.name;
    var v = new GlobalVariable(getType(node.type), name);
    if (node.initExpr != null) {
      // TODO
      // v.initVal = ;
    }
    module.globalVars.add(v);
    gScope.globalVars.put(name, v);
  }

  private static BaseType getElemType(String typename) {
    if (typename.equals("int")) {
      return new IntType(32);
    } else if (typename.equals("bool")) {
      return new IntType(1);
    } else if (typename.equals("string")) {
      return new PointerType(new IntType(8));
    } else if (typename.equals("void")) {
      return new VoidType();
    } else {
      throw new IRBuildError("unknown elementary type");
    }
  }

  private BaseType getType(TypeNode type) {
    if (type.isArrayType) {
      if (type.dimension == 1)
        return new PointerType(getElemType(type.typename));
      return new PointerType(getType(type));
    }
    if (type.isClass) {
      return new PointerType(gScope.getClassType(type.typename));
    }
    return getElemType(type.typename);
  }
}
