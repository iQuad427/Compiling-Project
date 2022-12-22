import javax.crypto.spec.PSource;
import java.util.HashSet;
import java.util.Set;

public class Compiler {
    ParseTree AST;
    private int counter = 0;
    private int label = 0;
    private Set<String> variables = new HashSet<>();

    Compiler(ParseTree AST) {
        this.AST = AST;
    }

    public void compile() {
        ParseTree parseTree = compileNode(this.AST);
    }

    private ParseTree compileNode(ParseTree parseTree) {
        Symbol currentNode = parseTree.getLabel();

        switch (currentNode.getType()) {
            case Program -> {
                // Import Print and Read functions
                System.out.println("""
                @.strP = private unnamed_addr constant [4 x i8] c"%d\\0A\\00", align 1

                define void @println(i32 %x) {
                    %1 = alloca i32, align 4
                    store i32 %x, i32* %1, align 4
                    %2 = load i32, i32* %1, align 4
                    %3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)
                    ret void
                }
                                
                @.str = private unnamed_addr constant [3 x i8] c"%d\\00", align 1
                        
                define i32 @readInt() #0 {
                    %1 = alloca i32, align 4
                    %2 = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str, i32 0, i32 0), i32* %1)
                    %3 = load i32, i32* %1, align 4
                    ret i32 %3
                }
                                        
                declare i32 @scanf(i8*, ...) #1
                declare i32 @printf(i8*, ...)
                """);

                // Starts main function
                System.out.println("""
                define i32 @main() {
                    entry:""");

                // Compile the code
                compileNode(parseTree.getChildren().get(0));

                // Ends the main function
                System.out.println("""
                        ret i32 0
                }
                """);
            }
            case Code -> {
                compileNode(parseTree.getChildren().get(0));
                if (parseTree.getChildren().size() > 1) {
                    compileNode(parseTree.getChildren().get(1));
                }
            }
            case ASSIGN -> {
                String variable = parseTree.getChildren().get(0).getLabel().getValue().toString();

                // Verify that the variable exists, else generates it
                if (variables.add(variable)) {
                    System.out.printf("%%%s = alloca i32\n", variable);
                }

                // Compute the expression on the right of the assign
                compileNode(parseTree.getChildren().get(1));

                // End of compilation (assign)
                System.out.printf("store i32 %%%d, i32* %%%s\n", counter - 1, variable);
            }
            case NUMBER -> System.out.printf("%%%d = add i32 0, %s\n", counter++, parseTree.getLabel().getValue().toString());
            case VARNAME -> {
                String variable = parseTree.getLabel().getValue().toString();

                // Verify that the variable exists
                if (!variables.add(variable)) {
                    // Variable exists, load content
                    System.out.printf("%%%d = load i32, i32* %%%s\n", counter++, variable);
                } else {
                    // Variable does not exist, throw an error
                    throw new RuntimeException("Variable : '" + variable + "' was not defined");
                }
            }
            case PLUS, TIMES, DIVIDE -> {
                compileNode(parseTree.getChildren().get(0));
                int leftExpr = counter - 1;

                compileNode(parseTree.getChildren().get(1));
                int rightExpr = counter - 1;

                int result = counter++;

                switch (parseTree.getLabel().getType()) {
                    case PLUS -> System.out.printf("%%%d = add i32 %%%d, %%%d\n", result, leftExpr, rightExpr);
                    case TIMES -> System.out.printf("%%%d = mul i32 %%%d, %%%d\n", result, leftExpr, rightExpr);
                    case DIVIDE -> System.out.printf("%%%d = sdiv i32 %%%d, %%%d\n", result, leftExpr, rightExpr);
                }
            }
            case MINUS -> {
                if (parseTree.getChildren().size() == 1) {
                    compileNode(parseTree.getChildren().get(0));
                    int expression = counter - 1;

                    int result = counter++;

                    System.out.printf("%%%d = sub i32 0, %%%d\n", result, expression);
                } else {
                    compileNode(parseTree.getChildren().get(0));
                    int leftExpr = counter - 1;

                    compileNode(parseTree.getChildren().get(1));
                    int rightExpr = counter - 1;

                    int result = counter++;

                    System.out.printf("%%%d = sub i32 %%%d, %%%d\n", result, leftExpr, rightExpr);
                }
            }
            case GREATER, SMALLER, EQUAL -> {
                compileNode(parseTree.getChildren().get(0));
                int left = this.counter - 1;

                compileNode(parseTree.getChildren().get(1));
                int right = this.counter - 1;

                switch (parseTree.getLabel().getType()) {
                    case GREATER -> System.out.printf("%%%d = icmp sgt i32 %%%d, %%%d\n", counter++, left, right);
                    case SMALLER -> System.out.printf("%%%d = icmp slt i32 %%%d, %%%d\n", counter++, left, right);
                    case EQUAL -> System.out.printf("%%%d = icmp eq i32 %%%d, %%%d\n", counter++, left, right);
                }
            }
            case If -> {
                compileNode(parseTree.getChildren().get(0));
                int comparison = this.counter - 1;

                int ifCode = label++;
                int elseCode = label++;
                int endIf = this.label++;

                System.out.printf("br i1 %%%d, label %%label%d, label %%label%d\n", comparison, ifCode, elseCode);

                System.out.printf("label%d:\n", ifCode);
                compileNode(parseTree.getChildren().get(1));
                System.out.printf("br label %%label%d\n", endIf);

                System.out.printf("label%d:\n", elseCode);
                // If there is an ELSE statement, there is code in the label
                if (parseTree.getChildren().size() == 3) {
                    compileNode(parseTree.getChildren().get(2));
                }
                System.out.printf("br label %%label%d\n", endIf);

                // Label for the rest of the code
                System.out.printf("label%d:\n", endIf);
            }
            case While -> {
                int whileCond = label++;
                int whileCode = label++;
                int endWhile = label++;

                // Jump to the While statement
                System.out.printf("br label %%label%d\n", whileCond);

                // Creates the condition part of the While
                System.out.printf("label%d:\n", whileCond);
                compileNode(parseTree.getChildren().get(0));
                int comparison = counter - 1;

                // Check the condition and goes either to the code or jump at the end of the While
                System.out.printf("br i1 %%%d, label %%label%d, label %%label%d\n", comparison, whileCode, endWhile);

                // Creates the code part of the While
                System.out.printf("label%d:\n", whileCode);
                compileNode(parseTree.getChildren().get(1));

                // Jumps back to condition after executing the code in the While
                System.out.printf("br label %%label%d\n", whileCond);

                // Allows for more code to come after the while
                System.out.printf("label%d:\n", endWhile);

            }
            case Print -> {
                String variable = parseTree.getChildren().get(0).getLabel().getValue().toString();
                if (!variables.add(variable)) {
                    // Variable does exist, we can keep compiling
                    System.out.printf("%%%d = load i32, i32* %%%s\n", counter++, variable);
                    System.out.printf("call void @println(i32 %%%d)\n", counter - 1);
                } else {
                    // Variable does not exist, throw an error
                    throw new RuntimeException("Variable : " + variable + " not defined");
                }
            }
            case Read -> {
                String variable = parseTree.getChildren().get(0).getLabel().getValue().toString();
                if (variables.add(variable)) {
                    System.out.printf("%%%s = alloca i32\n", variable);
                }
                System.out.printf("%%%d = call i32 @readInt()\n", counter++);
                System.out.printf("store i32 %%%d, i32* %%%s\n", counter - 1, variable);
            }
        }

        return parseTree;
    }

}
