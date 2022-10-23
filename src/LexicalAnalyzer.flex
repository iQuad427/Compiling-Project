import java.util.TreeMap;
import java.util.Map;

%%

%class LexicalAnalyzer
%unicode
%line
%column
%function nextToken
%type Symbol

// States
%xstate YYINITIAL, PROGNAME, SHORTCOMMENT, LONGCOMMENT, STOP

// Initialisation

%init{
    // to do before reading
%init}

// Functions

%{
	static TreeMap<String, Integer> variableTable = new TreeMap<>();

	// add entry to the symbol table
	void addVariable(String variable, int line) {
		if (!variableTable.containsKey(variable)) {
			variableTable.put(variable, line);
		}
	}

    // print and return the Symbol
    Symbol bothPrintAndReturn(LexicalUnit token, int line, int column, String value) {
        Symbol sym = new Symbol(token, line, column, value);
        // System.out.println(sym);
        return sym;
    }
%}

// Output of the program

%eof{
	// to do after reading
	if (!variableTable.isEmpty()) {
		System.out.println("\nVariables");

		for (Map.Entry<String, Integer> value : variableTable.entrySet()) {
			String variable = value.getKey();
			int line = value.getValue();

            System.out.println(variable + "	 " + (line + 1));
       	}
	}
%eof}


// ERE

Space		= "\t" | " "
EndOfLine	= "\r"?"\n"

Alpha          = [A-Z]|[a-z]
Numeric        = [0-9]
AlphaNumeric   = [A-Z]|[a-z]|[0-9]

Number         = (([1-9][0-9]*)|0)
BadNum         = 0+[0-9]+
VarName        = [a-z]([a-z]|[0-9])*
ProgramName    = [A-Z]([A-Z]|[a-z]|[0-9])*[a-z]([A-Z]|[a-z]|[0-9])*

%%

	<YYINITIAL>		{
		"BEGIN" 	{yybegin(PROGNAME); return bothPrintAndReturn(LexicalUnit.BEGIN, yyline, yycolumn, yytext());}

		"%%"		{yybegin(LONGCOMMENT);}
		"::"		{yybegin(SHORTCOMMENT);}
		
		"IF"		{return bothPrintAndReturn(LexicalUnit.IF, yyline, yycolumn, yytext());}
		"THEN"		{return bothPrintAndReturn(LexicalUnit.THEN, yyline, yycolumn, yytext());}
		"ELSE"		{return bothPrintAndReturn(LexicalUnit.ELSE, yyline, yycolumn, yytext());}
		"ENDIF"		{return bothPrintAndReturn(LexicalUnit.ENDIF, yyline, yycolumn, yytext());}
		"WHILE"		{return bothPrintAndReturn(LexicalUnit.WHILE, yyline, yycolumn, yytext());}
		"DO"		{return bothPrintAndReturn(LexicalUnit.DO, yyline, yycolumn, yytext());}
		"END"		{return bothPrintAndReturn(LexicalUnit.END, yyline, yycolumn, yytext());}

		"PRINT"		{return bothPrintAndReturn(LexicalUnit.PRINT, yyline, yycolumn, yytext());}
		"READ"		{return bothPrintAndReturn(LexicalUnit.READ, yyline, yycolumn, yytext());}

		":="	    {return bothPrintAndReturn(LexicalUnit.ASSIGN, yyline, yycolumn, yytext());}
		"-"			{return bothPrintAndReturn(LexicalUnit.MINUS, yyline, yycolumn, yytext());}
		"+"			{return bothPrintAndReturn(LexicalUnit.PLUS, yyline, yycolumn, yytext());}
		"*"			{return bothPrintAndReturn(LexicalUnit.TIMES, yyline, yycolumn, yytext());}
		"/"			{return bothPrintAndReturn(LexicalUnit.DIVIDE, yyline, yycolumn, yytext());}
		"="			{return bothPrintAndReturn(LexicalUnit.EQUAL, yyline, yycolumn, yytext());}
		">"			{return bothPrintAndReturn(LexicalUnit.GREATER, yyline, yycolumn, yytext());}
		"<"			{return bothPrintAndReturn(LexicalUnit.SMALLER, yyline, yycolumn, yytext());}

		","			{return bothPrintAndReturn(LexicalUnit.COMMA, yyline, yycolumn, yytext());}

		{VarName}	{
			addVariable(yytext(), yyline);
		    return bothPrintAndReturn(LexicalUnit.VARNAME, yyline, yycolumn, yytext());
		}		
		{Number}	{return bothPrintAndReturn(LexicalUnit.NUMBER, yyline, yycolumn, yytext());}
        {BadNum}    {System.out.println("Error : number with leading zeroes"); yybegin(STOP);}

		"("		    {return bothPrintAndReturn(LexicalUnit.LPAREN, yyline, yycolumn, yytext());}
		")"		    {return bothPrintAndReturn(LexicalUnit.RPAREN, yyline, yycolumn, yytext());}

		"\0"		{return bothPrintAndReturn(LexicalUnit.EOS, yyline, yycolumn, yytext());}

		{Space}     {}
		.           {yybegin(STOP); System.out.println("Error : wrong token at (" + (yyline + 1) + ", " + yycolumn + ")");}
		{EndOfLine} {}
	}
	<PROGNAME>		{
		{ProgramName}	{yybegin(YYINITIAL); return bothPrintAndReturn(LexicalUnit.PROGNAME, yyline, yycolumn, yytext());}
		{AlphaNumeric}*	{System.out.println("Error : invalid ProgName : " + yytext()); yybegin(STOP);}
        {Space}         {}
		{EndOfLine}    	{}
		<<EOF>>		    {System.out.println("Error : no ProgName specified"); yybegin(STOP);}
	}
	<LONGCOMMENT>	{
		"%%"			{yybegin(YYINITIAL);}
		{AlphaNumeric}	{}
		{Space}		    {}
		.              	{}
		{EndOfLine}    	{}
		<<EOF>>		    {System.out.println("Error : long comment not closed"); yybegin(STOP);}
		
	}
	
	<SHORTCOMMENT>	{
		{AlphaNumeric}	{}
		{Space}		    {}
		.              	{}
		{EndOfLine}    	{yybegin(YYINITIAL);}
	}
	<STOP> {
		.              	{}
		{EndOfLine}    	{}
	}
























