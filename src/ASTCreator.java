import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;

public class ASTCreator {

    public ParseTree castParseTreeToAST(ParseTree parseTree) {

        Symbol currentSymbol = parseTree.getLabel();

        List<ParseTree> children = new ArrayList<>();

        switch(currentSymbol.getType()) {
            case Program -> {
                children.add(castParseTreeToAST(parseTree.getChildren().get(2)));
                parseTree.setChildren(children);
            }
            case Code -> {
                children.add(castParseTreeToAST(parseTree.getChildren().get(0)));
                if (parseTree.getChildren().get(2).getLabel().getType() != LexicalUnit.EPSILON) {
                    children.add(castParseTreeToAST(parseTree.getChildren().get(2)));
                }
                parseTree.setChildren(children);
            }
            case Instruction -> {
                parseTree = castParseTreeToAST(parseTree.getChildren().get(0));
            }
            case Assign -> {
                children.add(parseTree.getChildren().get(0));
                children.add(castParseTreeToAST(parseTree.getChildren().get(2)));
                parseTree = parseTree.getChildren().get(1);
                parseTree.setChildren(children);
            }
            case Expression, Product -> {
                Symbol symbol = parseTree.getChildren().get(1).getLabel();
                if (symbol.getType() == LexicalUnit.EPSILON) {
                    parseTree = castParseTreeToAST(parseTree.getChildren().get(0));
                } else {
                    children.add(castParseTreeToAST(parseTree.getChildren().get(0)));
                    parseTree.setLabel(parseTree.getChildren().get(1).getChildren().get(0).getLabel());
                    children.add(castParseTreeToAST(parseTree.getChildren().get(1)));
                    parseTree.setChildren(children);
                }
            }
            case Expression2, Product2 -> {
                Symbol symbol = parseTree.getChildren().get(2).getLabel();
                if (symbol.getType() == LexicalUnit.EPSILON) {
                    parseTree = castParseTreeToAST(parseTree.getChildren().get(1));
                } else {
                    children.add(castParseTreeToAST(parseTree.getChildren().get(1)));
                    parseTree.setLabel(parseTree.getChildren().get(2).getChildren().get(0).getLabel());
                    children.add(castParseTreeToAST(parseTree.getChildren().get(2)));
                    parseTree.setChildren(children);
                }
            }
            case Atom -> {
                if (parseTree.getChildren().size() == 1) {
                    parseTree = parseTree.getChildren().get(0);
                } else if (parseTree.getChildren().size() == 2) {
                    children.add(castParseTreeToAST(parseTree.getChildren().get(1)));
                    parseTree.setLabel(parseTree.getChildren().get(0).getLabel());
                    parseTree.setChildren(children);
                } else if (parseTree.getChildren().size() == 3) {
                    parseTree = castParseTreeToAST(parseTree.getChildren().get(1));
                }
            }
            case If -> {
                children.add(castParseTreeToAST(parseTree.getChildren().get(2)));
                children.add(castParseTreeToAST(parseTree.getChildren().get(5)));
                if (parseTree.getChildren().get(6).getChildren().get(0).getLabel().getType() != LexicalUnit.END) {
                    children.add(castParseTreeToAST(parseTree.getChildren().get(6)));
                }
                parseTree.setChildren(children);
            }
            case EndIf -> {
                parseTree = castParseTreeToAST(parseTree.getChildren().get(1));
            }
            case Cond -> {
                children.add(castParseTreeToAST(parseTree.getChildren().get(0)));
                children.add(castParseTreeToAST(parseTree.getChildren().get(2)));
                parseTree = castParseTreeToAST(parseTree.getChildren().get(1));
                parseTree.setChildren(children);
            }
            case Comp -> {
                parseTree = parseTree.getChildren().get(0);
            }
            case While -> {
                children.add(castParseTreeToAST(parseTree.getChildren().get(2)));
                children.add(castParseTreeToAST(parseTree.getChildren().get(5)));
                parseTree.setChildren(children);
            }
            case Print, Read -> {
                children.add(parseTree.getChildren().get(2));
                parseTree.setChildren(children);
            }
        }

        return parseTree;
    }
}