import Token.LexicalAnalyzer;
import Token.Token;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer("Input/input1.txt");
        List<Token> tokens =  lexicalAnalyzer.tokenize();
        for(Token t: tokens){
            System.out.println(t.getToken());
        }
    }
}
