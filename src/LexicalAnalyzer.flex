import java.util.TreeMap;
import java.util.Map;

%%

%class LexicalAnalyzer
%unicode
%line
%column
%standalone

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
        System.out.println(sym);
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
		"BEGIN" 	{bothPrintAndReturn(LexicalUnit.BEGIN, yyline, yycolumn, yytext()); yybegin(PROGNAME);}

		"%%"		{yybegin(LONGCOMMENT);}
		"::"		{yybegin(SHORTCOMMENT);}
		
		"IF"		{bothPrintAndReturn(LexicalUnit.IF, yyline, yycolumn, yytext());}
		"THEN"		{bothPrintAndReturn(LexicalUnit.THEN, yyline, yycolumn, yytext());}
		"ELSE"		{bothPrintAndReturn(LexicalUnit.ELSE, yyline, yycolumn, yytext());}
		"ENDIF"		{bothPrintAndReturn(LexicalUnit.ENDIF, yyline, yycolumn, yytext());}
		"WHILE"		{bothPrintAndReturn(LexicalUnit.WHILE, yyline, yycolumn, yytext());}
		"DO"		{bothPrintAndReturn(LexicalUnit.DO, yyline, yycolumn, yytext());}
		"END"		{bothPrintAndReturn(LexicalUnit.END, yyline, yycolumn, yytext());}

		"PRINT"		{bothPrintAndReturn(LexicalUnit.PRINT, yyline, yycolumn, yytext());}
		"READ"		{bothPrintAndReturn(LexicalUnit.READ, yyline, yycolumn, yytext());}

		":="	    {bothPrintAndReturn(LexicalUnit.ASSIGN, yyline, yycolumn, yytext());}
		"-"			{bothPrintAndReturn(LexicalUnit.MINUS, yyline, yycolumn, yytext());}
		"+"			{bothPrintAndReturn(LexicalUnit.PLUS, yyline, yycolumn, yytext());}
		"*"			{bothPrintAndReturn(LexicalUnit.TIMES, yyline, yycolumn, yytext());}
		"/"			{bothPrintAndReturn(LexicalUnit.DIVIDE, yyline, yycolumn, yytext());}
		"="			{bothPrintAndReturn(LexicalUnit.EQUAL, yyline, yycolumn, yytext());}
		">"			{bothPrintAndReturn(LexicalUnit.GREATER, yyline, yycolumn, yytext());}
		"<"			{bothPrintAndReturn(LexicalUnit.SMALLER, yyline, yycolumn, yytext());}

		","			{bothPrintAndReturn(LexicalUnit.COMMA, yyline, yycolumn, yytext());}

		{VarName}	{
			addVariable(yytext(), yyline);
		    bothPrintAndReturn(LexicalUnit.VARNAME, yyline, yycolumn, yytext());
		}		
		{Number}	{bothPrintAndReturn(LexicalUnit.NUMBER, yyline, yycolumn, yytext());}
        {BadNum}    {System.out.println("Error : number with leading zeroes"); yybegin(STOP);}

		"("		    {bothPrintAndReturn(LexicalUnit.LPAREN, yyline, yycolumn, yytext());}
		")"		    {bothPrintAndReturn(LexicalUnit.RPAREN, yyline, yycolumn, yytext());}

		"\0"		{bothPrintAndReturn(LexicalUnit.EOS, yyline, yycolumn, yytext());}

		{Space}     {}
		.           {System.out.println("Error : wrong token at (" + (yyline + 1) + ", " + yycolumn + ")"); yybegin(STOP);}
		{EndOfLine} {}
	}
	<PROGNAME>		{
		{ProgramName}	{bothPrintAndReturn(LexicalUnit.PROGNAME, yyline, yycolumn, yytext()); yybegin(YYINITIAL);}
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
























