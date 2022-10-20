parser grammar MxParser;

@header {
package grammar;
}

options {
	tokenVocab = MxLexer;
}

program: (funcDef | classDef | varDef)* EOF;

funcDef: returnType Identifier '(' parameterList? ')' suite;
returnType: type | Void;
parameterList: (type Identifier) (',' type Identifier)*;

classDef:
	Class Identifier '{' (varDef | classConstructorDef | funcDef)* '}' Semi;
classConstructorDef: Identifier '(' ')' suite;

varDef: type singleVarDef (',' singleVarDef)* Semi;
singleVarDef: Identifier (Assign expr)?;
type: nonArrayType ('[' ']')*;
nonArrayType: primitiveType | Identifier;
primitiveType: Int | Bool | String;

suite: '{' statement* '}';

statement:
	suite
	| varDef
	| ifStmt
	| whileStmt
	| forStmt
	| breakStmt
	| continueStmt
	| returnStmt
	| exprStmt
	| Semi;

ifStmt: If '(' expr ')' statement (Else statement)?;
whileStmt: While '(' expr ')' statement;
forStmt: For '(' forInitStmt expr? Semi expr? ')' statement;
forInitStmt: varDef | (expr? Semi);

breakStmt: Break Semi;
continueStmt: Continue Semi;
returnStmt: Return expr? Semi;

exprStmt: expr Semi;

argumentList: expr (',' expr)*;

// $antlr-format reflowComments false
expr:
	atom  #atomExpr
	| '(' expr ')'  #parenExpr
	| New nonArrayType (('[' expr ']')+ ('[' ']')*)?  #newExpr
	// Precedence 1, Left-to-right
	// ++ -- postfix increment and decrement
	// () function call
	// [] Array subscripting
	// . Structure and union member access
	| expr op = (Inc | Dec)  #postfixExpr
	| expr '(' argumentList? ')'  #funcCallExpr
	| expr '[' expr ']'  #indexExpr
	| expr Dot Identifier  #memberExpr
	// Precedence 2, Right-to-left
	// ++ -- 	Prefix increment and decrement
	// + - 	Unary plus and Minus
	// ! ~ 	Logical NOT and bitwise NOT
	| <assoc = right> op = (Inc | Dec) expr  #prefixExpr
	| <assoc = right> op = (BitNot | LNot | Add | Sub) expr  #unaryExpr
	// Precedence 3, Left-to-right
	// * / % 	Multiplication, division, and remainder
	| expr op = (Mul | Div | Mod) expr  #binaryExpr
	// Precedence 4, Left-to-right
	// + - 	Addition and subtraction
	| expr op = (Add | Sub) expr  #binaryExpr
	// Precedence 5, Left-to-right
	// << >> 	Bitwise left shift and right shift
	| expr op = (ShiftL | ShiftR) expr  #binaryExpr
	// Precedence 6, Left-to-right
	// < <= 	For relational operators < and ≤ respectively
	// > >= 	For relational operators > and ≥ respectively
	| expr op = (GT | GEq | LT | LEq) expr  #binaryExpr
	// Precedence 7, Left-to-right
	// == != 	For relational = and ≠ respectively
	| expr op = (Eq | NEq) expr  #binaryExpr
	// Precedence 8, Left-to-right
	// & 	Bitwise AND
	| expr op = BitAnd expr  #binaryExpr
	// Precedence 9, Left-to-right
	// ^ 	Bitwise XOR (exclusive or)
	| expr op = BitXor expr  #binaryExpr
	// Precedence 10, Left-to-right
	// | 	Bitwise OR (inclusive or)
	| expr op = BitOr expr  #binaryExpr
	// Precedence 11, Left-to-right
	// && 	Logical AND
	| expr op = LAnd expr  #binaryExpr
	// Precedence 12, Left-to-right
	// || 	Logical OR
	| expr op = LOr expr  #binaryExpr
	// Precedence 13, Right-to-left
	// Assignment
	| <assoc = right> expr Assign expr  #assignExpr
	// Precedence ?
	// Lambda
	| '[' '&'? ']' ('(' parameterList? ')')? Arrow suite '(' argumentList? ')'  #lambdaExpr;

atom:
	IntegerLiteral
	| StringLiteral
	| True
	| False
	| Null
	| This
	| Identifier;
