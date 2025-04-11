import Grammar.Grammar;
import Parser.ParseNode;
import Parser.Parser;
import Parser.FirstFollowCalculator;
import Parser.ParsingTableBuilder;
import Parser.LL1Parser;
import Token.LexicalAnalyzer;
import Token.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
// pasar solo 1 TOKEN, no tofo el code, el parser solo pilla 1 token
// el lexical anal va TOKEN A TOKEN
// crear el first and follow de forma automatica y crear la symbols table con el stack crear el tabular parser
// check ejemplo eStudy
// guardar la grammar en un file, xml json para el first and follow

public class Main {
    public static String filepath = "Input/input1.txt";

    public static void main(String[] args) throws IOException {
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(filepath);
        List<Token> tokens = new ArrayList<>();
        Token token;
        Grammar g = new Grammar();
        FirstFollowCalculator calc = new FirstFollowCalculator(g.getGrammar());
        ParsingTableBuilder builder = new ParsingTableBuilder(
                g.getGrammar(),
                calc.firstSets,
                calc.followSets
        );
        while((token = lexicalAnalyzer.nextToken()) != null){
            tokens.add(token);
        }

        Map<String, Map<String, List<String>>> table = builder.buildParsingTable();
        LL1Parser parser = new LL1Parser(g.getGrammar(), table);
        List<String> input = new ArrayList<>();

        for(Token t : tokens){
            input.add(parser.mapTokenToGrammarTerminal(t));
            System.out.println(input.getLast());
        }

        parser.parse(input);

    }
}
