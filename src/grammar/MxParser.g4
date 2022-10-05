parser grammar MxParser;

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

unaryOps: (BitNot | LNot | Add | Sub);
shiftOps: (ShiftL | ShiftR);
mulLevelOps: (Mul | Div | Mod);
addLevelOps: (Add | Sub);
compareOps: (GT | GEq | LT | LEq);
equalOps: (Eq | NEq);

argumentList: expr (',' expr)*;

// $antlr-format reflowComments false
expr:
	atomExpr
	| '(' expr ')'
	| New nonArrayType ('[' expr? ']')*
	// Precedence 1, Left-to-right
	// ++ -- postfix increment and decrement
	// () function call
	// [] Array subscripting
	// . Structure and union member access
	| expr (Inc | Dec)
	| expr '(' argumentList? ')'
	| expr '[' expr ']'
	| expr Dot Identifier
	// Precedence 2, Right-to-left
	// ++ -- 	Prefix increment and decrement
	// + - 	Unary plus and Minus
	// ! ~ 	Logical NOT and bitwise NOT
	| <assoc = right> (Inc | Dec) expr
	| <assoc = right> unaryOps expr
	// Precedence 3, Left-to-right
	// * / % 	Multiplication, division, and remainder
	| expr mulLevelOps expr
	// Precedence 4, Left-to-right
	// + - 	Addition and subtraction
	| expr addLevelOps expr
	// Precedence 5, Left-to-right
	// << >> 	Bitwise left shift and right shift
	| expr shiftOps expr
	// Precedence 6, Left-to-right
	// < <= 	For relational operators < and ≤ respectively
	// > >= 	For relational operators > and ≥ respectively
	| expr compareOps expr
	// Precedence 7, Left-to-right
	// == != 	For relational = and ≠ respectively
	| expr equalOps expr
	// Precedence 8, Left-to-right
	// & 	Bitwise AND
	| expr BitAnd expr
	// Precedence 9, Left-to-right
	// ^ 	Bitwise XOR (exclusive or)
	| expr BitXor expr
	// Precedence 10, Left-to-right
	// | 	Bitwise OR (inclusive or)
	| expr BitOr expr
	// Precedence 11, Left-to-right
	// && 	Logical AND
	| expr LAnd expr
	// Precedence 12, Left-to-right
	// || 	Logical OR
	| expr LOr expr
	// Precedence 13, Right-to-left
	// Assignment
	| <assoc = right> expr Assign expr
	// Precedence ?
	// Lambda
	| lambdaExpr;

atomExpr:
	IntegerLiteral
	| StringLiteral
	| True
	| False
	| Null
	| This
	| Identifier
	| This;

lambdaExpr:
	'[' '&'? ']' ('(' parameterList? ')')? Arrow suite '(' argumentList? ')';
