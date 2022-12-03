import java.rmi.UnexpectedException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class ASTCreator {

    /**
     * @param parseTree the node of the tree that we want to cast to AST
     * @param context the context of that node, i.e. what it needs from the parent
     * @return the AST
     */
    public ParseTree castParseTreeToAST(ParseTree parseTree, ParseTree context) {

        Symbol currentSymbol = parseTree.getLabel();

        List<ParseTree> children = new ArrayList<>();

        switch(currentSymbol.getType()) {
            case Program -> {
                children.add(castParseTreeToAST(parseTree.getChildren().get(2), null));
                parseTree.setChildren(children);
            }
            case Code -> {
                children.add(castParseTreeToAST(parseTree.getChildren().get(0), null));
                if (parseTree.getChildren().get(2).getLabel().getType() != LexicalUnit.EPSILON) {
                    children.add(castParseTreeToAST(parseTree.getChildren().get(2), null));
                }
                parseTree.setChildren(children);
            }
            case Instruction -> {
                parseTree = castParseTreeToAST(parseTree.getChildren().get(0), null);
            }
            case Assign -> {
                children.add(parseTree.getChildren().get(0));
                children.add(castParseTreeToAST(parseTree.getChildren().get(2), null));
                parseTree = parseTree.getChildren().get(1);
                parseTree.setChildren(children);
            }
            case Expression, Product -> {
                if (parseTree.getChildren().get(1).getLabel().getType() == LexicalUnit.EPSILON) {
                    parseTree = castParseTreeToAST(parseTree.getChildren().get(0), null);
                } else {
                    context = castParseTreeToAST(parseTree.getChildren().get(0), null);
                    parseTree = castParseTreeToAST(parseTree.getChildren().get(1), context);
                }
            }
            case Expression2, Product2 -> {;
                if (parseTree.getChildren().get(2).getLabel().getType() == LexicalUnit.EPSILON) {
                    children.add(context);
                    children.add(castParseTreeToAST(parseTree.getChildren().get(1), null));
                    parseTree = new ParseTree(parseTree.getChildren().get(0).getLabel(), children);
                } else {
                    children.add(context);
                    children.add(castParseTreeToAST(parseTree.getChildren().get(1), null));
                    context = new ParseTree(parseTree.getChildren().get(0).getLabel(), children);
                    parseTree = castParseTreeToAST(parseTree.getChildren().get(2), context);
                }
            }
            case Atom -> {
                if (parseTree.getChildren().size() == 1) {
                    parseTree = parseTree.getChildren().get(0);
                } else if (parseTree.getChildren().size() == 2) {
                    children.add(castParseTreeToAST(parseTree.getChildren().get(1), null));
                    parseTree.setLabel(parseTree.getChildren().get(0).getLabel());
                    parseTree.setChildren(children);
                } else if (parseTree.getChildren().size() == 3) {
                    parseTree = castParseTreeToAST(parseTree.getChildren().get(1), null);
                }
            }
            case If -> {
                children.add(castParseTreeToAST(parseTree.getChildren().get(2), null));
                children.add(castParseTreeToAST(parseTree.getChildren().get(5), null));
                if (parseTree.getChildren().get(6).getChildren().get(0).getLabel().getType() != LexicalUnit.END) {
                    children.add(castParseTreeToAST(parseTree.getChildren().get(6), null));
                }
                parseTree.setChildren(children);
            }
            case EndIf -> {
                parseTree = castParseTreeToAST(parseTree.getChildren().get(1), null);
            }
            case Cond -> {
                children.add(castParseTreeToAST(parseTree.getChildren().get(0), null));
                children.add(castParseTreeToAST(parseTree.getChildren().get(2), null));
                parseTree = castParseTreeToAST(parseTree.getChildren().get(1), null);
                parseTree.setChildren(children);
            }
            case Comp -> {
                parseTree = parseTree.getChildren().get(0);
            }
            case While -> {
                children.add(castParseTreeToAST(parseTree.getChildren().get(2), null));
                children.add(castParseTreeToAST(parseTree.getChildren().get(5), null));
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