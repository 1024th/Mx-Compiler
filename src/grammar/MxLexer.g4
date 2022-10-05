lexer grammar MxLexer;

Add: '+';
Sub: '-';
Mul: '*';
Div: '/';
Mod: '%';

GT: '>';
LT: '<';
GEq: '>=';
LEq: '<=';
NEq: '!=';
Eq: '==';

LAnd: '&&';
LOr: '||';
LNot: '!';

ShiftL: '<<';
ShiftR: '>>';
BitAnd: '&';
BitOr: '|';
BitXor: '^';
BitNot: '~';

Assign: '=';

Inc: '++';
Dec: '--';

Dot: '.';
Arrow: '->';

LBracket: '[';
RBracket: ']';
LParen: '(';
RParen: ')';
LBrace: '{';
RBrace: '}';

Semi: ';';
Comma: ',';

Null: 'null';
True: 'true';
False: 'false';
IntegerLiteral: '0' | [1-9][0-9]*;
Quote: '"';
StringLiteral: Quote ( '\\n' | '\\\\' | '\\"' | .)*? Quote;

Void: 'void';
Bool: 'bool';
Int: 'int';
String: 'string';
New: 'new';
Class: 'class';
This: 'this';
If: 'if';
Else: 'else';
For: 'for';
While: 'while';
Break: 'break';
Continue: 'continue';
Return: 'return';

Identifier: [a-zA-Z][a-zA-Z_0-9]*;

WhiteSpace: [ \t]+ -> skip;
NewLine: ('\r' '\n'? | '\n') -> skip;
BlockComment: '/*' .*? '*/' -> skip;
LineComment: '//' ~[\r\n]* -> skip;