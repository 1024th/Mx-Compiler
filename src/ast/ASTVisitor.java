package ast;

import ast.expr.*;
import ast.stmt.*;

public interface ASTVisitor {
  void visit(ProgramNode node);

  void visit(ClassDefNode node);
  void visit(ClassCtorDefNode node);
  void visit(FuncDefNode node);
  void visit(VarDefNode node);
  void visit(SingleVarDefNode node);
  void visit(ForStmtNode node);
  void visit(IfStmtNode node);
  void visit(WhileStmtNode node);
  void visit(BreakStmtNode node);
  void visit(ContinueStmtNode node);
  void visit(ReturnStmtNode node);
  void visit(ExprStmtNode node);
  void visit(SuiteNode node);

  void visit(TypeNode node);

  void visit(AssignExprNode node);
  void visit(AtomExprNode node);
  void visit(BinaryExprNode node);
  void visit(FuncCallExprNode node);
  void visit(ParamListNode node);
  void visit(ParamNode node);
  void visit(IndexExprNode node);
  void visit(MemberExprNode node);
  void visit(NewExprNode node);
  void visit(PostfixExprNode node);
  void visit(PrefixExprNode node);
  void visit(UnaryExprNode node);
  void visit(LambdaExprNode node);
}
