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

        try {
            // Parse the input file
            Parser parser = new Parser(new LexicalAnalyzer(new FileReader(inputPath)));
            ParseTree parseTree = parser.startParsing();

            // Retrieve the variable table
            variableTable = parser.getVariableTable();
            if (requiresOutput) {
                // Write the parse tree in the LaTeX file
                FileWriter fileWriter = new FileWriter(outputPath);
                fileWriter.write(parseTree.toLaTeX());
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print the Variable Table
        if (!variableTable.isEmpty()) {
            // System.out.println("\nVariables");

            for (Map.Entry<String, Symbol> variable : variableTable.entrySet()) {
                // System.out.println(variable.getKey() + "\t" + variable.getValue().getLine());
            }
        }
    }
}
