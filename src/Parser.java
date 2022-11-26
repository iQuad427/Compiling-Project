import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class Parser {
    private final LexicalAnalyzer lexer;
    private Symbol currentToken;
    private TreeMap<String, Symbol> variableTable = new TreeMap<>();

    public Parser(LexicalAnalyzer lexer){
        this.lexer = lexer;
    }

    public TreeMap<String, Symbol> getVariableTable() {
        return variableTable;
    }

    /**
     * Calls the first function of the recursive parser
     *
     * @return the ParseTree corresponding to the parsing of the language read by the lexical analyser given when
     * parser was created.
     */
    public ParseTree startParsing() {
        nextToken();
        return Program();
    }

    private ParseTree Program() {
        System.out.print("1 ");
        List<ParseTree> leaves = new ArrayList<>();
        leaves.add(match(LexicalUnit.BEGIN));
        leaves.add(match(LexicalUnit.PROGNAME));
        leaves.add(Code());
        leaves.add(match(LexicalUnit.END));
        System.out.print("\n");
        return new ParseTree(new Symbol(LexicalUnit.Program, "Program"), leaves);
    }

    private ParseTree Code() {
        List<ParseTree> leaves = new ArrayList<>();
        switch (currentToken.getType()) {
            case END, ELSE -> {
                System.out.print("3 ");
                return new ParseTree(new Symbol(LexicalUnit.EPSILON, "$\\epsilon$"));
            }
            case IF, WHILE, PRINT, READ, VARNAME -> {
                System.out.print("2 ");
                leaves.add(Instruction());
                leaves.add(match(LexicalUnit.COMMA));
                leaves.add(Code());
                return new ParseTree(new Symbol(LexicalUnit.Code, "Code"), leaves);
            }
            default -> throw new RuntimeException("\nParsing Error, Unexpected token : " + currentToken.getType() + " at line " + currentToken.getLine());
        }
    }

    private ParseTree Instruction() {
        List<ParseTree> leaves = new ArrayList<>();
        switch (currentToken.getType()) {
            case IF -> {
                System.out.print("5 ");
                leaves.add(If());
            }
            case WHILE -> {
                System.out.print("6 ");
                leaves.add(While());
            }
            case PRINT -> {
                System.out.print("7 ");
                leaves.add(Print());
            }
            case READ -> {
                System.out.print("8 ");
                leaves.add(Read());
            }
            case VARNAME -> {
                System.out.print("4 ");
                leaves.add(Assign());
            }
            default -> throw new RuntimeException();
        }
        return new ParseTree(new Symbol(LexicalUnit.Instruction, "Instruction"), leaves);
    }

    private ParseTree Assign() {
        System.out.print("9 ");
        List<ParseTree> leaves = new ArrayList<>();
        leaves.add(match(LexicalUnit.VARNAME));
        leaves.add(match(LexicalUnit.ASSIGN));
        leaves.add(Expression());
        return new ParseTree(new Symbol(LexicalUnit.Assign, "Assign"), leaves);
    }

    private ParseTree If() {
        System.out.print("22 ");
        List<ParseTree> leaves = new ArrayList<>();
        leaves.add(match(LexicalUnit.IF));
        leaves.add(match(LexicalUnit.LPAREN));
        leaves.add(Cond());
        leaves.add(match(LexicalUnit.RPAREN));
        leaves.add(match(LexicalUnit.THEN));
        leaves.add(Code());
        leaves.add(EndIf());
        return new ParseTree(new Symbol(LexicalUnit.If, "If"), leaves);
    }

    private ParseTree EndIf() {
        List<ParseTree> leaves = new ArrayList<>();
        switch (currentToken.getType()) {
            case END -> {
                System.out.print("23 ");
                leaves.add(match(LexicalUnit.END));
            }
            case ELSE -> {
                System.out.print("24 ");
                leaves.add(match(LexicalUnit.ELSE));
                leaves.add(Code());
                leaves.add(match(LexicalUnit.END));
            }
            default -> throw new RuntimeException();
        }
        return new ParseTree(new Symbol(LexicalUnit.EndIf, "EndIf"), leaves);
    }

    private ParseTree While() {
        System.out.print("29 ");
        List<ParseTree> leaves = new ArrayList<>();
        leaves.add(match(LexicalUnit.WHILE));
        leaves.add(match(LexicalUnit.LPAREN));
        leaves.add(Cond());
        leaves.add(match(LexicalUnit.RPAREN));
        leaves.add(match(LexicalUnit.DO));
        leaves.add(Code());
        leaves.add(match(LexicalUnit.END));
        return new ParseTree(new Symbol(LexicalUnit.While, "While"), leaves);
    }

    private ParseTree Print() {
        System.out.print("30 ");
        List<ParseTree> leaves = new ArrayList<>();
        leaves.add(match(LexicalUnit.PRINT));
        leaves.add(match(LexicalUnit.LPAREN));
        leaves.add(match(LexicalUnit.VARNAME));
        leaves.add(match(LexicalUnit.RPAREN));
        return new ParseTree(new Symbol(LexicalUnit.Print, "Print"), leaves);
    }

    private ParseTree Read() {
        System.out.print("32 ");
        List<ParseTree> leaves = new ArrayList<>();
        leaves.add(match(LexicalUnit.READ));
        leaves.add(match(LexicalUnit.LPAREN));
        leaves.add(match(LexicalUnit.VARNAME));
        leaves.add(match(LexicalUnit.RPAREN));
        return new ParseTree(new Symbol(LexicalUnit.Read, "Read"), leaves);
    }

    private ParseTree Cond() {
        System.out.print("25 ");
        List<ParseTree> leaves = new ArrayList<>();
        leaves.add(Expression());
        leaves.add(Comp());
        leaves.add(Expression());
        return new ParseTree(new Symbol(LexicalUnit.Cond, "Cond"), leaves);
    }

    private ParseTree Comp() {
        System.out.print("5 ");
        List<ParseTree> leaves = new ArrayList<>();
        switch (currentToken.getType()) {
            case EQUAL -> {
                System.out.print("26 ");
                leaves.add(match(LexicalUnit.EQUAL));
            }
            case GREATER -> {
                System.out.print("27 ");
                leaves.add(match(LexicalUnit.GREATER));
            }
            case SMALLER -> {
                System.out.print("28 ");
                leaves.add(match(LexicalUnit.SMALLER));
            }
            default -> throw new RuntimeException("\nParsing Error, Unexpected token : " + currentToken.getType() + " at line " + currentToken.getLine());
        }
        return new ParseTree(new Symbol(LexicalUnit.Comp, "Comp"), leaves);
    }

    private ParseTree Expression() {
        System.out.print("10 ");
        List<ParseTree> leaves = new ArrayList<>();
        leaves.add(Product());
        leaves.add(Expression2());
        return new ParseTree(new Symbol(LexicalUnit.Expression, "Expression"), leaves);
    }

    private ParseTree Expression2() {
        List<ParseTree> leaves = new ArrayList<>();
        switch (currentToken.getType()) {
            case PLUS -> {
                System.out.print("11 ");
                leaves.add(match(LexicalUnit.PLUS));
                leaves.add(Product());
                leaves.add(Expression2());
            }
            case MINUS -> {
                System.out.print("12 ");
                leaves.add(match(LexicalUnit.MINUS));
                leaves.add(Product());
                leaves.add(Expression2());
            }
            case COMMA, RPAREN, EQUAL, GREATER, SMALLER -> {
                System.out.print("13 ");
                return new ParseTree(new Symbol(LexicalUnit.EPSILON, "$\\epsilon$"));
            }
        }
        return new ParseTree(new Symbol(LexicalUnit.Expression2, "Expression'"), leaves);
    }

    private ParseTree Product() {
        System.out.print("14 ");
        List<ParseTree> leaves = new ArrayList<>();
        leaves.add(Atom());
        leaves.add(Product2());
        return new ParseTree(new Symbol(LexicalUnit.Product, "Product"), leaves);
    }

    private ParseTree Product2() {
        List<ParseTree> leaves = new ArrayList<>();
        switch (currentToken.getType()) {
            case TIMES -> {
                System.out.print("15 ");
                leaves.add(match(LexicalUnit.TIMES));
                leaves.add(Atom());
                leaves.add(Product2());
            }
            case DIVIDE -> {
                System.out.print("16 ");
                leaves.add(match(LexicalUnit.DIVIDE));
                leaves.add(Atom());
                leaves.add(Product2());
            }
            case COMMA, PLUS, MINUS, RPAREN, EQUAL, GREATER, SMALLER -> {
                System.out.print("17 ");
                return new ParseTree(new Symbol(LexicalUnit.EPSILON, "$\\epsilon$"));
            }
        }
        return new ParseTree(new Symbol(LexicalUnit.Product2, "Product'"), leaves);
    }

    private ParseTree Atom() {
        List<ParseTree> leaves = new ArrayList<>();
        switch (currentToken.getType()) {
            case MINUS -> {
                System.out.print("18 ");
                leaves.add(match(LexicalUnit.MINUS));
                leaves.add(Atom());
            }
            case NUMBER -> {
                System.out.print("19 ");
                leaves.add(match(LexicalUnit.NUMBER));
            }
            case VARNAME -> {
                System.out.print("20 ");
                leaves.add(match(LexicalUnit.VARNAME));
            }
            case LPAREN -> {
                System.out.print("21 ");
                leaves.add(match(LexicalUnit.LPAREN));
                leaves.add(Expression());
                leaves.add(match(LexicalUnit.RPAREN));
            }
            default -> throw new RuntimeException("\nParsing Error, Unexpected token : " + currentToken.getType() + " at line " + currentToken.getLine());
        }
        return new ParseTree(new Symbol(LexicalUnit.Atom, "Atom"), leaves);
    }

    /**
     * Allows for interfacing the nextToken function of the LexicalAnalyser
     */
    private void nextToken() {
        try {
            currentToken = lexer.nextToken();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't get the next token from lexical analyser", e);
        }

    }

    /**
     * Checks if the look-ahead correspond to the symbol that should be on the stack while reducing a grammar rule
     *
     * @param expectedToken : token expected as look-ahead in this configuration of the parser
     * @return ParseTree node corresponding to the symbol that was matched
     */
    private ParseTree match(LexicalUnit expectedToken) {
        ParseTree leaf;
        // System.out.println("Expected : " + expectedToken + ", Actual : " + currentToken.getType());
        if (!currentToken.getType().equals(expectedToken)) {
            throw new RuntimeException("\nParsing Error, Expected : " + expectedToken + ", Actual : " + currentToken.getType());
        } else {
            leaf = new ParseTree(currentToken);
            nextToken();

            // Add the token to the variable table if it is a variable
            if (currentToken != null) {
                if (currentToken.getType().equals(LexicalUnit.VARNAME)) {
                    if (!variableTable.containsKey(currentToken.getValue().toString())) {
                        variableTable.put(currentToken.getValue().toString(), currentToken);
                    }
                }
            }

        }
        return leaf;
    }
}
