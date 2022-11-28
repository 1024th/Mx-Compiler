package frontend;

import ast.*;
import ast.expr.*;
import ast.stmt.*;
import utils.error.SemanticError;
import utils.scope.ClassScope;
import utils.scope.GlobalScope;

// collects functions, classes, and member functions & variables
// in classes
public class SymbolCollector implements ASTVisitor {
  public GlobalScope gScope;
  public ClassScope clsScope;

  public SymbolCollector(GlobalScope gScope) {
    this.gScope = gScope;
  }

  @Override
  public void visit(ProgramNode node) {
    for (var i : node.defs) {
      if (i instanceof VarDefNode)
        continue;
      i.accept(this);
    }
  }

  @Override
  public void visit(ClassDefNode node) {
    ClassScope cls = new ClassScope(node.className, gScope, node.pos);
    gScope.addClassScope(cls);
    this.clsScope = cls;
    for (var i : node.defs) {
      i.accept(this);
    }
    this.clsScope = null;
  }

  @Override
  public void visit(ClassCtorDefNode node) {
    this.clsScope.addCtor(node);
  }

  @Override
  public void visit(FuncDefNode node) {
    if (this.clsScope != null) {
      this.clsScope.addFuncDef(node);
    } else {
      this.gScope.addFuncDef(node);
    }
  }

  @Override
  public void visit(VarDefNode node) {
    for (var i : node.vars) {
      i.accept(this);
    }
  }

  @Override
  public void visit(SingleVarDefNode node) {
    if (node.initExpr != null) {
      throw new SemanticError("initialization expression is not allowed for member variable definition",
          node.initExpr.pos);
    }
    this.clsScope.addVarDef(node);
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

}
