import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        String filePath;

        if (args.length == 0) {
             throw new IllegalArgumentException("missing input file");
        } else {
            filePath = args[0];
        }

        TreeMap<String, Symbol> variableTable = new TreeMap<>();
        Symbol symbol;
        try {
            LexicalAnalyzer lexer = new LexicalAnalyzer(new FileReader(filePath));
            while (!(symbol = lexer.nextToken()).getValue().equals(LexicalUnit.EOS)) {
                System.out.println(symbol);
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
    }
}
