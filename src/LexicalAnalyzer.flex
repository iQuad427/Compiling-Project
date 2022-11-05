import java.util.regex.PatternSyntaxException;
import java.util.TreeMap;
import java.util.Map;

%%

%class LexicalAnalyzer
%unicode
%line
%column
%function nextToken
%type Symbol
%yylexthrow PatternSyntaxException

// States
%xstate YYINITIAL, PROGNAME, SHORTCOMMENT, LONGCOMMENT

// Initialisation

%init{
    // to do before reading
%init}

// Functions

%{
    // implement java code
%}

// Output of the program

%eof{
    // last code to execute
%eof}

%eofval{
    return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}


// ERE

Space		= "\t" | "\f" | " "
EndOfLine	= ("\r"?"\n")|("\r""\n"?)

Alpha          = [A-Z]|[a-z]
Numeric        = [0-9]
AlphaNumeric   = [A-Z]|[a-z]|[0-9]

Number         = ([1-9][0-9]*)|0
BadNum         = (0[0-9]+)
VarName        = [a-z]([a-z]|[0-9])*
ProgramName    = [A-Z]((([A-Z]|[a-z]|[0-9])*[a-z]([A-Z]|[a-z]|[0-9])*)|([0-9]*))
BadProgName    = [A-Z]+

%%

	<YYINITIAL>		{
		"BEGIN" 	{yybegin(PROGNAME); return new Symbol(LexicalUnit.BEGIN, yyline, yycolumn, yytext());}

		"%%"		{yybegin(LONGCOMMENT);}
		"::"		{yybegin(SHORTCOMMENT);}
		
		"IF"		{return new Symbol(LexicalUnit.IF, yyline, yycolumn, yytext());}
		"THEN"		{return new Symbol(LexicalUnit.THEN, yyline, yycolumn, yytext());}
		"ELSE"		{return new Symbol(LexicalUnit.ELSE, yyline, yycolumn, yytext());}
		"ENDIF"		{return new Symbol(LexicalUnit.ENDIF, yyline, yycolumn, yytext());}
		"WHILE"		{return new Symbol(LexicalUnit.WHILE, yyline, yycolumn, yytext());}
		"DO"		{return new Symbol(LexicalUnit.DO, yyline, yycolumn, yytext());}
		"END"		{return new Symbol(LexicalUnit.END, yyline, yycolumn, yytext());}

		"PRINT"		{return new Symbol(LexicalUnit.PRINT, yyline, yycolumn, yytext());}
		"READ"		{return new Symbol(LexicalUnit.READ, yyline, yycolumn, yytext());}

		":="	    {return new Symbol(LexicalUnit.ASSIGN, yyline, yycolumn, yytext());}
		"-"			{return new Symbol(LexicalUnit.MINUS, yyline, yycolumn, yytext());}
		"+"			{return new Symbol(LexicalUnit.PLUS, yyline, yycolumn, yytext());}
		"*"			{return new Symbol(LexicalUnit.TIMES, yyline, yycolumn, yytext());}
		"/"			{return new Symbol(LexicalUnit.DIVIDE, yyline, yycolumn, yytext());}
		"="			{return new Symbol(LexicalUnit.EQUAL, yyline, yycolumn, yytext());}
		">"			{return new Symbol(LexicalUnit.GREATER, yyline, yycolumn, yytext());}
		"<"			{return new Symbol(LexicalUnit.SMALLER, yyline, yycolumn, yytext());}

		","			{return new Symbol(LexicalUnit.COMMA, yyline, yycolumn, yytext());}

        "("		    {return new Symbol(LexicalUnit.LPAREN, yyline, yycolumn, yytext());}
      	")"		    {return new Symbol(LexicalUnit.RPAREN, yyline, yycolumn, yytext());}


		{VarName}	{return new Symbol(LexicalUnit.VARNAME, yyline, yycolumn, yytext());}

		{BadNum}    {
            System.out.println("Warning : numbers with leading zeroes are not allowed");
            return new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, yytext());
        }
		{Number}	{return new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, yytext());}

		{Space}     {}
        {EndOfLine} {}
		[^]         {throw new PatternSyntaxException("unmatched symbol(s) found", yytext(), yyline);}
	}
	<PROGNAME>		{
        {BadProgName}   {
                            yybegin(YYINITIAL);
                            System.out.println("Warning : all uppercases ProgName are not allowed : " + yytext());
                            return new Symbol(LexicalUnit.PROGNAME, yyline, yycolumn, yytext());
                        }
        {ProgramName}	{
                            yybegin(YYINITIAL);
                            return new Symbol(LexicalUnit.PROGNAME, yyline, yycolumn, yytext());
                        }
        {Space}         {}
		{EndOfLine}    	{}
		<<EOF>>		    {throw new PatternSyntaxException("no ProgName specified", yytext(), yyline);}
        [^]             {throw new PatternSyntaxException("unexpected symbol(s) found", yytext(), yyline);}
	}
	<LONGCOMMENT>	{
		"%%"			{yybegin(YYINITIAL);}
		<<EOF>>		    {throw new PatternSyntaxException("long comment not closed", yytext(), yyline);}
        [^]            	{}
	}
	<SHORTCOMMENT>	{
		{EndOfLine}    	{yybegin(YYINITIAL);}
        .              	{}
	}
























