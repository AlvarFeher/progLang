import Parser.ParseNode;
import Parser.Parser;
import Token.LexicalAnalyzer;
import Token.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
// pasar solo 1 TOKEN, no tofo el code, el parser solo pilla 1 token
// el lexical anal va TOKEN A TOKEN
// crear el first and follow de forma automatica y crear la symbols table con el stack crear el tabular parser
// check ejemplo eStudy
// guardar la grammar en un file, xml json para el first and follow

public class Main {
    public static String filepath = "Input/input1.txt";

    public static void main(String[] args) throws IOException {
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(filepath);
        Parser parser = new Parser();
        List<Token> tokens = new ArrayList<>();
        Token token;

        while((token = lexicalAnalyzer.nextToken()) != null){
            tokens.add(token);
            parser.getFirst(token);
            parser.getFollow(token);
            parser.generateParseTree(token);
        }

        ParseNode root = parser.getRoot();

    }
}
