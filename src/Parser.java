import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final LexicalAnalyzer lexer;
    private Symbol currentToken;

    public Parser(LexicalAnalyzer lexer){
        this.lexer = lexer;
    }

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
            default -> throw new RuntimeException();
        }
    }

    private ParseTree Instruction() {
        List<ParseTree> leaves = new ArrayList<>();
        switch (currentToken.getType()) {
            case IF -> {
                leaves.add(If());
            }
            case WHILE -> {
                leaves.add(While());
            }
            case PRINT -> {
                leaves.add(Print());
            }
            case READ -> {
                leaves.add(Read());
            }
            case VARNAME -> {
                leaves.add(Assign());
            }
            default -> throw new RuntimeException();
        }
        return new ParseTree(new Symbol(LexicalUnit.Instruction, "Instruction"), leaves);
    }

    private ParseTree Assign() {
        List<ParseTree> leaves = new ArrayList<>();
        leaves.add(match(LexicalUnit.VARNAME));
        leaves.add(match(LexicalUnit.ASSIGN));
        leaves.add(Expression());
        return new ParseTree(new Symbol(LexicalUnit.Assign, "Assign"), leaves);
    }

    private ParseTree If() {
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
            case END -> leaves.add(match(LexicalUnit.END));
            case ELSE -> {
                leaves.add(match(LexicalUnit.ELSE));
                leaves.add(Code());
                leaves.add(match(LexicalUnit.END));
            }
            default -> throw new RuntimeException();
        }
        return new ParseTree(new Symbol(LexicalUnit.EndIf, "EndIf"), leaves);
    }

    private ParseTree While() {
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
        List<ParseTree> leaves = new ArrayList<>();
        leaves.add(match(LexicalUnit.PRINT));
        leaves.add(match(LexicalUnit.LPAREN));
        leaves.add(match(LexicalUnit.VARNAME));
        leaves.add(match(LexicalUnit.RPAREN));
        return new ParseTree(new Symbol(LexicalUnit.Print, "Print"), leaves);
    }

    private ParseTree Read() {
        List<ParseTree> leaves = new ArrayList<>();
        leaves.add(match(LexicalUnit.READ));
        leaves.add(match(LexicalUnit.LPAREN));
        leaves.add(match(LexicalUnit.VARNAME));
        leaves.add(match(LexicalUnit.RPAREN));
        return new ParseTree(new Symbol(LexicalUnit.Read, "Read"), leaves);
    }

    private ParseTree Cond() {
        List<ParseTree> leaves = new ArrayList<>();
        leaves.add(Expression());
        leaves.add(Comp());
        leaves.add(Expression());
        return new ParseTree(new Symbol(LexicalUnit.Cond, "Cond"), leaves);
    }

    private ParseTree Comp() {
        List<ParseTree> leaves = new ArrayList<>();
        switch (currentToken.getType()) {
            case EQUAL -> leaves.add(match(LexicalUnit.EQUAL));
            case SMALLER -> leaves.add(match(LexicalUnit.SMALLER));
            case GREATER -> leaves.add(match(LexicalUnit.GREATER));
        }
        return new ParseTree(new Symbol(LexicalUnit.Comp, "Comp"), leaves);
    }

    private ParseTree Expression() {
        List<ParseTree> leaves = new ArrayList<>();
        leaves.add(Product());
        leaves.add(Expression2());
        return new ParseTree(new Symbol(LexicalUnit.Expression, "Expression"), leaves);
    }

    private ParseTree Expression2() {
        List<ParseTree> leaves = new ArrayList<>();
        switch (currentToken.getType()) {
            case PLUS -> {
                leaves.add(match(LexicalUnit.PLUS));
                leaves.add(Product());
                leaves.add(Expression2());
            }
            case MINUS -> {
                leaves.add(match(LexicalUnit.MINUS));
                leaves.add(Product());
                leaves.add(Expression2());
            }
            case COMMA, RPAREN, EQUAL, GREATER, SMALLER -> {return new ParseTree(new Symbol(LexicalUnit.EPSILON, "$\\epsilon$"));}
        }
        return new ParseTree(new Symbol(LexicalUnit.Expression2, "Expression'"), leaves);
    }

    private ParseTree Product() {
        List<ParseTree> leaves = new ArrayList<>();
        leaves.add(Atom());
        leaves.add(Product2());
        return new ParseTree(new Symbol(LexicalUnit.Product, "Product"), leaves);
    }

    private ParseTree Product2() {
        List<ParseTree> leaves = new ArrayList<>();
        switch (currentToken.getType()) {
            case TIMES -> {
                leaves.add(match(LexicalUnit.TIMES));
                leaves.add(Atom());
                leaves.add(Product2());
            }
            case DIVIDE -> {
                leaves.add(match(LexicalUnit.DIVIDE));
                leaves.add(Atom());
                leaves.add(Product2());
            }
            case COMMA, PLUS, MINUS, RPAREN, EQUAL, GREATER, SMALLER -> {return new ParseTree(new Symbol(LexicalUnit.EPSILON, "$\\epsilon$"));}
        }
        return new ParseTree(new Symbol(LexicalUnit.Product2, "Product'"), leaves);
    }

    private ParseTree Atom() {
        List<ParseTree> leaves = new ArrayList<>();
        switch (currentToken.getType()) {
            case MINUS -> {
                leaves.add(match(LexicalUnit.MINUS));
                leaves.add(Atom());
            }
            case NUMBER -> leaves.add(match(LexicalUnit.NUMBER));
            case VARNAME -> leaves.add(match(LexicalUnit.VARNAME));
            case LPAREN -> {
                leaves.add(match(LexicalUnit.LPAREN));
                leaves.add(Expression());
                leaves.add(match(LexicalUnit.RPAREN));
            }
        }
        return new ParseTree(new Symbol(LexicalUnit.Atom, "Atom"), leaves);
    }

    private void nextToken() {
        try {
            currentToken = lexer.nextToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private ParseTree match(LexicalUnit expectedToken) {
        ParseTree leaf;
        // System.out.println("Expected : " + expectedToken + ", Actual : " + currentToken.getType());
        if (!currentToken.getType().equals(expectedToken)) {
            throw new RuntimeException();
        } else {
            leaf = new ParseTree(currentToken);
            nextToken();
        }
        return leaf;
    }
}
