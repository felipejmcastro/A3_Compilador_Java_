import compilerTools.Token;

%%

%class Lexer
%type Token
%line
%column

BRANCO = [ \t\r]+
ID = [_a-zA-Z][_a-zA-Z0-9]*
NUMERO = 0|[1-9][0-9]*

%{
    private Token createToken(String lexeme, String lexicalComp, int line, int column){
        return new Token(lexeme, lexicalComp, line+1, column+1);
    }
%}

%%

{BRANCO} {/* Ignore */}
"//".* {/* Ignore */}
"\n" { return createToken(yytext(), "Linha", yyline, yycolumn); }

"texto" | "grande" { return createToken(yytext(), "Tipo_de_dado", yyline, yycolumn); }

"=" { return createToken(yytext(), "Op_atribuicao", yyline, yycolumn); }
"," { return createToken(yytext(), "Virgula", yyline, yycolumn); }
";" { return createToken(yytext(), "P_Final", yyline, yycolumn); }

"(" { return createToken(yytext(), "Parenteses_abertura", yyline, yycolumn); }
")" { return createToken(yytext(), "Parenteses_fechamento", yyline, yycolumn); }
"{" { return createToken(yytext(), "Chave_abertura", yyline, yycolumn); }
"}" { return createToken(yytext(), "Chave_fechamento", yyline, yycolumn); }

"+" { return createToken(yytext(), "Soma", yyline, yycolumn); }
"-" { return createToken(yytext(), "Subtrai", yyline, yycolumn); }
"*" { return createToken(yytext(), "Multiplicacao", yyline, yycolumn); }
"/" { return createToken(yytext(), "Divisao", yyline, yycolumn); }

">" { return createToken(yytext(), "Maior_que", yyline, yycolumn); }
"<" { return createToken(yytext(), "Menor_que", yyline, yycolumn); }
"&" { return createToken(yytext(), "Op_Logico", yyline, yycolumn); }
"|" { return createToken(yytext(), "Op_Logico", yyline, yycolumn); }

"captura" { return createToken(yytext(), "Captura", yyline, yycolumn); }
"enquanto" { return createToken(yytext(), "Enquanto", yyline, yycolumn); }
"sera" | "naosera" { return createToken(yytext(), "Est_Verificacao", yyline, yycolumn); }
"fim" { return createToken(yytext(), "Fim", yyline, yycolumn); }

{NUMERO} { return createToken(yytext(), "Numero", yyline, yycolumn); }
"@" {ID} { return createToken(yytext(), "Identificador", yyline, yycolumn); }
{ID} { return createToken(yytext(), "ID", yyline, yycolumn); }

. { return createToken(yytext(), "ERROR", yyline, yycolumn); }
