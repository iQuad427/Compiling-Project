import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String filePath;


        if (args.length == 0) {
             throw new IllegalArgumentException("missing input file");
        } else {
            filePath = args[0];
        }

        Symbol token;
        try {
            LexicalAnalyzer lexer = new LexicalAnalyzer(new FileReader(filePath));
            while ((token = lexer.nextToken()) != null) {
                System.out.println(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
