import Grammar.Grammar;
import IntCode.IntermediateCode;
import IntCode.Quadruple;
import Parser.FirstFollowCalculator;
import Parser.InputEntry;
import Parser.ParsingTableBuilder;
import Parser.LL1Parser;
import Semantics.SemanticCheck;
import Symbols.SymbolsTable;
import Token.LexicalAnalyzer;
import Token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
// pasar solo 1 TOKEN, no tofo el code, el parser solo pilla 1 token
// el lexical anal va TOKEN A TOKEN
// crear el first and follow de forma automatica y crear la symbols table con el stack crear el tabular parser
// check ejemplo eStudy
// guardar la grammar en un file, xml json para el first and follow

public class Main {
    public static String filepath = "Input/input2.txt";

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
        List<InputEntry> input = new ArrayList<>();

        for(Token t : tokens){
            input.add(new InputEntry(t.getToken(),parser.mapTokenToGrammarTerminal(t)));
            System.out.println(input.getLast());
        }

        parser.parse(input);
        SymbolsTable symbolsTable = new SymbolsTable();
        symbolsTable = parser.buildSymbolTable(parser.getRoot(), symbolsTable);
        symbolsTable.printSymbolsTable();

        // semantics
        SemanticCheck semanticCheck = new SemanticCheck(symbolsTable);
        semanticCheck.analyze(parser.getRoot());
        semanticCheck.printErrors();

        IntermediateCode intermediateCode = new IntermediateCode();
        intermediateCode.generate(parser.getRoot());
        System.out.println("INTERMEDIATE CODE");
        for(Quadruple q : intermediateCode.getCode()){
            System.out.println(q);
        }
        System.out.println();

    }
}
