package ast;

import grammar.MxParser.*;
import utils.Position;
import utils.error.ASTBuildError;
import utils.error.SemanticError;
import ast.stmt.*;

import java.util.logging.Logger;

import ast.expr.*;
import grammar.MxParserBaseVisitor;

public class ASTBuilder extends MxParserBaseVisitor<ASTNode> {
  Logger logger = Logger.getLogger("ASTBuilder");

  @Override
  public ASTNode visitProgram(ProgramContext ctx) {
    ProgramNode root = new ProgramNode(new Position(ctx));
    boolean hasMain = false;
    for (var i : ctx.children) {
      if (!(i instanceof ClassDefContext) &&
          !(i instanceof FuncDefContext) &&
          !(i instanceof VarDefContext))
        continue;
      ASTNode define = visit(i);
      root.defs.add(define);
      if (define instanceof FuncDefNode) {
        logger.info(((FuncDefNode) define).funcName);
        if (((FuncDefNode) define).funcName.equals("main")) {
          hasMain = true;
        }
      }
    }
    if (!hasMain) {
      throw new SemanticError("no main function", new Position(ctx.getStop()));
    }
    return root;
  }

  @Override
  public ASTNode visitFuncDef(FuncDefContext ctx) {
    var returnType = (TypeNode) visit(ctx.returnType());
    var suite = (SuiteNode) visit(ctx.suite());
    ParamListNode params = null;
    if (ctx.parameterList() != null) {
      params = (ParamListNode) visit(ctx.parameterList());
    }
    var ret = new FuncDefNode(ctx.Identifier().getText(), returnType, suite, params, new Position(ctx));
    return ret;
  }

  @Override
  public ASTNode visitReturnType(ReturnTypeContext ctx) {
    if (ctx.Void() != null) {
      return new TypeNode("void", new Position(ctx));
    }
    return visit(ctx.type());
  }

  @Override
  public ASTNode visitParameterList(ParameterListContext ctx) {
    var ret = new ParamListNode(new Position(ctx));
    for (int i = 0; i < ctx.type().size(); ++i) {
      TypeNode type = (TypeNode) visit(ctx.type(i));
      ret.add(type, ctx.Identifier(i).getText());
    }
    return ret;
  }

  @Override
  public ASTNode visitClassDef(ClassDefContext ctx) {
    var ret = new ClassDefNode(ctx.Identifier().getText(), new Position(ctx));
    for (var i : ctx.varDef()) {
      ret.defs.add(visit(i));
    }
    for (var i : ctx.classConstructorDef()) {
      var node = (ClassCtorDefNode) visit(i);
      if (!node.name.equals(ret.className)) {
        throw new SemanticError("constructor name does not match class name", new Position(i));
      }
      ret.defs.add(node);
    }
    for (var i : ctx.varDef()) {
      ret.defs.add(visit(i));
    }
    for (var i : ctx.funcDef()) {
      ret.defs.add(visit(i));
    }
    return ret;
  }

  @Override
  public ASTNode visitClassConstructorDef(ClassConstructorDefContext ctx) {
    return new ClassCtorDefNode(ctx.Identifier().getText(), (SuiteNode) visit(ctx.suite()), new Position(ctx));
  }

  @Override
  public ASTNode visitVarDef(VarDefContext ctx) {
    TypeNode type = (TypeNode) visit(ctx.type());
    var ret = new VarDefNode(type, new Position(ctx));
    for (var i : ctx.singleVarDef()) {
      ExprNode initExpr = null;
      if (i.expr() != null) {
        initExpr = (ExprNode) visit(i.expr());
      }
      ret.vars.add(new SingleVarDefNode(type, i.Identifier().getText(), initExpr, new Position(ctx)));
    }
    return ret;
  }

  @Override
  public ASTNode visitSingleVarDef(SingleVarDefContext ctx) {
    // Not used
    return null;
  }

  @Override
  public ASTNode visitType(TypeContext ctx) {
    int dim = ctx.LBracket().size();
    if (dim == 0) {
      return new TypeNode(ctx.nonArrayType().getText(), new Position(ctx));
    }
    return new TypeNode(ctx.nonArrayType().getText(), dim, new Position(ctx));
  }

  @Override
  public ASTNode visitNonArrayType(NonArrayTypeContext ctx) {
    // Not used
    return null;
  }

  @Override
  public ASTNode visitPrimitiveType(PrimitiveTypeContext ctx) {
    // Not used
    return null;
  }

  @Override
  public ASTNode visitSuite(SuiteContext ctx) {
    var ret = new SuiteNode(new Position(ctx));
    for (var i : ctx.statement()) {
      ret.stmts.add((StmtNode) visit(i));
    }
    return ret;
  }

  @Override
  public ASTNode visitStatement(StatementContext ctx) {
    if (ctx.suite() != null) {
      return visit(ctx.suite());
    } else if (ctx.varDef() != null) {
      return visit(ctx.varDef());
    } else if (ctx.ifStmt() != null) {
      return visit(ctx.ifStmt());
    } else if (ctx.whileStmt() != null) {
      return visit(ctx.whileStmt());
    } else if (ctx.forStmt() != null) {
      return visit(ctx.forStmt());
    } else if (ctx.breakStmt() != null) {
      return visit(ctx.breakStmt());
    } else if (ctx.continueStmt() != null) {
      return visit(ctx.continueStmt());
    } else if (ctx.returnStmt() != null) {
      return visit(ctx.returnStmt());
    } else if (ctx.exprStmt() != null) {
      return visit(ctx.exprStmt());
    }
    throw new ASTBuildError("unknown type of statement", new Position(ctx));
  }

  @Override
  public ASTNode visitIfStmt(IfStmtContext ctx) {
    ExprNode condition = (ExprNode) visit(ctx.expr());
    StmtNode thenStmt, elseStmt = null;
    thenStmt = (StmtNode) visit(ctx.statement(0));
    if (ctx.statement().size() > 1) {
      elseStmt = (StmtNode) visit(ctx.statement(1));
    }
    return new IfStmtNode(condition, thenStmt, elseStmt, new Position(ctx));
  }

  @Override
  public ASTNode visitWhileStmt(WhileStmtContext ctx) {
    ExprNode condition = (ExprNode) visit(ctx.expr());
    StmtNode body = (StmtNode) visit(ctx.statement());
    return new WhileStmtNode(condition, body, new Position(ctx));
  }

  @Override
  public ASTNode visitForStmt(ForStmtContext ctx) {
    var init = ctx.forInitStmt();
    VarDefNode initVar = null;
    ExprNode initExpr = null, forCond = null, forInc = null;
    if (init.varDef() != null) {
      initVar = (VarDefNode) visit(init.varDef());
    } else if (init.expr() != null) {
      initExpr = (ExprNode) visit(init.expr());
    }
    if (ctx.forCond != null) {
      forCond = (ExprNode) visit(ctx.forCond);
    }
    if (ctx.forInc != null) {
      forInc = (ExprNode) visit(ctx.forInc);
    }
    return new ForStmtNode(initVar, initExpr, forCond, forInc, new Position(ctx));
  }

  @Override
  public ASTNode visitForInitStmt(ForInitStmtContext ctx) {
    // Not used
    return null;
  }

  @Override
  public ASTNode visitBreakStmt(BreakStmtContext ctx) {
    return new BreakStmtNode(new Position(ctx));
  }

  @Override
  public ASTNode visitContinueStmt(ContinueStmtContext ctx) {
    return new ContinueStmtNode(new Position(ctx));
  }

  @Override
  public ASTNode visitReturnStmt(ReturnStmtContext ctx) {
    ExprNode expr = null;
    if (ctx.expr() != null) {
      expr = (ExprNode) visit(ctx.expr());
    }
    return new ReturnStmtNode(expr, new Position(ctx));
  }

  @Override
  public ASTNode visitExprStmt(ExprStmtContext ctx) {
    return new ExprStmtNode((ExprNode) visit(ctx.expr()), new Position(ctx));
  }

  @Override
  public ASTNode visitArgumentList(ArgumentListContext ctx) {
    // Not used
    return null;
  }

  @Override
  public ASTNode visitNewExpr(NewExprContext ctx) {
    TypeNode type = new TypeNode(ctx.nonArrayType().getText(), new Position(ctx.nonArrayType()));
    if (ctx.LBracket().size() >= 0) {
      type.isArrayType = true;
      type.dimension = ctx.LBracket().size();
      type.typename += "[]".repeat(type.dimension);
    }
    var ret = new NewExprNode(type, new Position(ctx));
    for (var i : ctx.expr()) {
      ret.sizeExprs.add((ExprNode) visit(i));
    }
    return ret;
  }

  @Override
  public ASTNode visitIndexExpr(IndexExprContext ctx) {
    return new IndexExprNode((ExprNode) visit(ctx.expr(0)), (ExprNode) visit(ctx.expr(1)), new Position(ctx));
  }

  @Override
  public ASTNode visitPrefixExpr(PrefixExprContext ctx) {
    return new PrefixExprNode((ExprNode) visit(ctx.expr()), ctx.op.getText(), new Position(ctx));
  }

  @Override
  public ASTNode visitUnaryExpr(UnaryExprContext ctx) {
    return new UnaryExprNode((ExprNode) visit(ctx.expr()), ctx.op.getText(), new Position(ctx));
  }

  @Override
  public ASTNode visitLambdaExpr(LambdaExprContext ctx) {
    ParamListNode params = null;
    if (ctx.parameterList() != null) {
      params = (ParamListNode) visit(ctx.parameterList());
    }
    var ret = new LambdaExprNode(ctx.BitAnd() != null, params, (SuiteNode) visit(ctx.suite()), new Position(ctx));
    if (ctx.argumentList() != null) {
      for (var i : ctx.argumentList().expr()) {
        ret.args.add((ExprNode) visit(i));
      }
    }
    return ret;
  }

  @Override
  public ASTNode visitMemberExpr(MemberExprContext ctx) {
    return new MemberExprNode((ExprNode) visit(ctx.expr()), ctx.Identifier().getText(), new Position(ctx));
  }

  @Override
  public ASTNode visitAtomExpr(AtomExprContext ctx) {
    // TODO: consider how to store the type
    String typename = null;
    var atom = ctx.atom();
    if (atom.IntegerLiteral() != null) {
      typename = "int";
    } else if (atom.StringLiteral() != null) {
      typename = "string";
    } else if (atom.True() != null || atom.False() != null) {
      typename = "bool";
    } else if (atom.Null() != null) {
      typename = "null";
    } else if (atom.This() != null) {
      typename = "this";
    }
    TypeNode type = new TypeNode(typename, new Position(ctx));
    return new AtomExprNode(ctx.getText(), type, atom.Identifier() != null, new Position(ctx));
  }

  @Override
  public ASTNode visitBinaryExpr(BinaryExprContext ctx) {
    return new BinaryExprNode(
        (ExprNode) visit(ctx.expr(0)),
        (ExprNode) visit(ctx.expr(1)),
        ctx.op.getText(),
        new Position(ctx));
  }

  @Override
  public ASTNode visitPostfixExpr(PostfixExprContext ctx) {
    return new PostfixExprNode((ExprNode) visit(ctx.expr()), ctx.op.getText(), new Position(ctx));
  }

  @Override
  public ASTNode visitFuncCallExpr(FuncCallExprContext ctx) {
    var ret = new FuncCallExprNode((ExprNode) visit(ctx.expr()), new Position(ctx));
    if (ctx.argumentList() != null) {
      for (var i : ctx.argumentList().expr()) {
        ret.args.add((ExprNode) visit(i));
      }
    }
    return ret;
  }

  @Override
  public ASTNode visitAssignExpr(AssignExprContext ctx) {
    return new AssignExprNode((ExprNode) visit(ctx.expr(0)), (ExprNode) visit(ctx.expr(1)), new Position(ctx));
  }

  @Override
  public ASTNode visitParenExpr(ParenExprContext ctx) {
    return visit(ctx.expr());
  }

  @Override
  public ASTNode visitAtom(AtomContext ctx) {
    // Not used
    return null;
  }

}