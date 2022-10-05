// Generated from java-escape by ANTLR 4.11.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MxParser}.
 */
public interface MxParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MxParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MxParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MxParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#funcDef}.
	 * @param ctx the parse tree
	 */
	void enterFuncDef(MxParser.FuncDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#funcDef}.
	 * @param ctx the parse tree
	 */
	void exitFuncDef(MxParser.FuncDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#returnType}.
	 * @param ctx the parse tree
	 */
	void enterReturnType(MxParser.ReturnTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#returnType}.
	 * @param ctx the parse tree
	 */
	void exitReturnType(MxParser.ReturnTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(MxParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(MxParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#classDef}.
	 * @param ctx the parse tree
	 */
	void enterClassDef(MxParser.ClassDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#classDef}.
	 * @param ctx the parse tree
	 */
	void exitClassDef(MxParser.ClassDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#classConstructorDef}.
	 * @param ctx the parse tree
	 */
	void enterClassConstructorDef(MxParser.ClassConstructorDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#classConstructorDef}.
	 * @param ctx the parse tree
	 */
	void exitClassConstructorDef(MxParser.ClassConstructorDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#varDef}.
	 * @param ctx the parse tree
	 */
	void enterVarDef(MxParser.VarDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#varDef}.
	 * @param ctx the parse tree
	 */
	void exitVarDef(MxParser.VarDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#singleVarDef}.
	 * @param ctx the parse tree
	 */
	void enterSingleVarDef(MxParser.SingleVarDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#singleVarDef}.
	 * @param ctx the parse tree
	 */
	void exitSingleVarDef(MxParser.SingleVarDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(MxParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(MxParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#nonArrayType}.
	 * @param ctx the parse tree
	 */
	void enterNonArrayType(MxParser.NonArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#nonArrayType}.
	 * @param ctx the parse tree
	 */
	void exitNonArrayType(MxParser.NonArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(MxParser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(MxParser.PrimitiveTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#suite}.
	 * @param ctx the parse tree
	 */
	void enterSuite(MxParser.SuiteContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#suite}.
	 * @param ctx the parse tree
	 */
	void exitSuite(MxParser.SuiteContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(MxParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(MxParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#ifStmt}.
	 * @param ctx the parse tree
	 */
	void enterIfStmt(MxParser.IfStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#ifStmt}.
	 * @param ctx the parse tree
	 */
	void exitIfStmt(MxParser.IfStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#whileStmt}.
	 * @param ctx the parse tree
	 */
	void enterWhileStmt(MxParser.WhileStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#whileStmt}.
	 * @param ctx the parse tree
	 */
	void exitWhileStmt(MxParser.WhileStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#forStmt}.
	 * @param ctx the parse tree
	 */
	void enterForStmt(MxParser.ForStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#forStmt}.
	 * @param ctx the parse tree
	 */
	void exitForStmt(MxParser.ForStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#forInitStmt}.
	 * @param ctx the parse tree
	 */
	void enterForInitStmt(MxParser.ForInitStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#forInitStmt}.
	 * @param ctx the parse tree
	 */
	void exitForInitStmt(MxParser.ForInitStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#breakStmt}.
	 * @param ctx the parse tree
	 */
	void enterBreakStmt(MxParser.BreakStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#breakStmt}.
	 * @param ctx the parse tree
	 */
	void exitBreakStmt(MxParser.BreakStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#continueStmt}.
	 * @param ctx the parse tree
	 */
	void enterContinueStmt(MxParser.ContinueStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#continueStmt}.
	 * @param ctx the parse tree
	 */
	void exitContinueStmt(MxParser.ContinueStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#returnStmt}.
	 * @param ctx the parse tree
	 */
	void enterReturnStmt(MxParser.ReturnStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#returnStmt}.
	 * @param ctx the parse tree
	 */
	void exitReturnStmt(MxParser.ReturnStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#exprStmt}.
	 * @param ctx the parse tree
	 */
	void enterExprStmt(MxParser.ExprStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#exprStmt}.
	 * @param ctx the parse tree
	 */
	void exitExprStmt(MxParser.ExprStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#unaryOps}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOps(MxParser.UnaryOpsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#unaryOps}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOps(MxParser.UnaryOpsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#shiftOps}.
	 * @param ctx the parse tree
	 */
	void enterShiftOps(MxParser.ShiftOpsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#shiftOps}.
	 * @param ctx the parse tree
	 */
	void exitShiftOps(MxParser.ShiftOpsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#mulLevelOps}.
	 * @param ctx the parse tree
	 */
	void enterMulLevelOps(MxParser.MulLevelOpsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#mulLevelOps}.
	 * @param ctx the parse tree
	 */
	void exitMulLevelOps(MxParser.MulLevelOpsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#addLevelOps}.
	 * @param ctx the parse tree
	 */
	void enterAddLevelOps(MxParser.AddLevelOpsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#addLevelOps}.
	 * @param ctx the parse tree
	 */
	void exitAddLevelOps(MxParser.AddLevelOpsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#compareOps}.
	 * @param ctx the parse tree
	 */
	void enterCompareOps(MxParser.CompareOpsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#compareOps}.
	 * @param ctx the parse tree
	 */
	void exitCompareOps(MxParser.CompareOpsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#equalOps}.
	 * @param ctx the parse tree
	 */
	void enterEqualOps(MxParser.EqualOpsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#equalOps}.
	 * @param ctx the parse tree
	 */
	void exitEqualOps(MxParser.EqualOpsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentList(MxParser.ArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentList(MxParser.ArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(MxParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(MxParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#atomExpr}.
	 * @param ctx the parse tree
	 */
	void enterAtomExpr(MxParser.AtomExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#atomExpr}.
	 * @param ctx the parse tree
	 */
	void exitAtomExpr(MxParser.AtomExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#lambdaExpr}.
	 * @param ctx the parse tree
	 */
	void enterLambdaExpr(MxParser.LambdaExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#lambdaExpr}.
	 * @param ctx the parse tree
	 */
	void exitLambdaExpr(MxParser.LambdaExprContext ctx);
}