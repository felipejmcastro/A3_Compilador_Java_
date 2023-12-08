import compilerTools.Token;
import java.awt.Color;

%%
%class LexerColor
%type TextColor
%line
%column

// Aqui, mantemos as definições do arquivo original

BRANCO = [ \t\r]+
ID = [_a-zA-Z][_a-zA-Z0-9]*
INTEIRO = 0|[1-9][0-9]*

%{
    private Token createToken(String lexeme, String lexicalComp, int line, int column){
        return new Token(lexeme, lexicalComp, line+1, column+1);
    }

    // Adicionamos a função para colorir
    private Token textColorToken(String lexeme, String lexicalComp, int line, int column, Color color){
        Token token = createToken(lexeme, lexicalComp, line, column);
        token.setColor(color);
        return token;
    }
%}

%%

// Mantemos as definições dos tokens do arquivo original
// ...

texto { return textColorToken(yytext(), "Texto", yyline, yycolumn, Color.RED); }
grande { return textColorToken(yytext(), "Grande", yyline, yycolumn, Color.BLUE); }
sera { return textColorToken(yytext(), "Sera", yyline, yycolumn, Color.GREEN); }
naosera { return textColorToken(yytext(), "Naosera", yyline, yycolumn, Color.YELLOW); }

// Mantemos as regras de tokens do arquivo original
// ...

{ID} { return createToken(yytext(), "Identificador", yyline, yycolumn); }
((-{INTEIRO}+))|{INTEIRO}+ { return createToken(yytext(), "Numero", yyline, yycolumn); }

. { return createToken(yytext(), "ERROR", yyline, yycolumn); }
