import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        String inputPath = null;
        String outputPath = null;
        boolean requiresOutput = false;

        if (args.length == 1) {
            inputPath = args[0];
        } else if (args.length == 3) {
            if (Objects.equals(args[0], "-wt")) {
                requiresOutput = true;
                inputPath = args[2];
                outputPath = args[1];
            }
        } else {
            throw new IllegalArgumentException("wrong arguments");
        }

        TreeMap<String, Symbol> variableTable = new TreeMap<>();
        Symbol symbol;
        try {
            LexicalAnalyzer lexer = new LexicalAnalyzer(new FileReader(inputPath));
            while ((symbol = lexer.nextToken()).getValue() != null) {
                // System.out.println(symbol);
                if (symbol.getType().equals(LexicalUnit.VARNAME)) {
                    if (!variableTable.containsKey(symbol.getValue().toString())) {
                        variableTable.put(symbol.getValue().toString(), symbol);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // to do after reading
        if (!variableTable.isEmpty()) {
            System.out.println("\nVariables");

            for (Map.Entry<String, Symbol> variable : variableTable.entrySet()) {
                System.out.println(variable.getKey() + "\t" + variable.getValue().getLine());
            }
        }

        try {
            Parser parser = new Parser(new LexicalAnalyzer(new FileReader(inputPath)));
            ParseTree parseTree = parser.startParsing();
            if (requiresOutput) {
                FileWriter fileWriter = new FileWriter(outputPath);
                fileWriter.write(parseTree.toLaTeX());
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
