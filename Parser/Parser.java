package Parser;

import Grammar.Grammar;
import Token.Token;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Stack;

public class Parser {
    private ParseNode root;
    private List<Token> tokens;
    private int currentToken =0;
    private Token token;
    Stack<String> stack;
    LoadFirstFollow firstFollow;
    Grammar grammar;
    ParsingTable parsingTable;

    public Parser() throws FileNotFoundException {
        firstFollow = new LoadFirstFollow();
        stack = new Stack<>();
        stack.push("$"); // End marker
        stack.push("Program"); // Start symbol of grammar
        grammar = new Grammar();
        this.root = new ParseNode("Program");
        parsingTable = new ParsingTable(grammar.getGrammar(),firstFollow);
    }

    private String mapTokenToNonTerminal(Token token) {
        String lexeme = token.getToken().toUpperCase();

        switch (token.getTokenType()) {
            case KEYWORD:
                // Mapea las palabras clave a los no terminales correspondientes en tu gramática
                if (lexeme.equals("FUNCIO")) {
                    return "Dec_Fun"; // Declaración de funciones
                } else if (lexeme.equals("CONST")) {
                    return "Dec_Cte_Var"; // Declaración de constantes/variables
                } else if (lexeme.equals("INICI")) {
                    return "Inici"; // Inicio del programa
                } else if (lexeme.equals("FI")) {
                    return "FI"; // Fin del programa
                } else if (lexeme.equals("ESCRIURE") || lexeme.equals("LLEGIR") ||
                        lexeme.equals("SI") || lexeme.equals("MENTRE") ||
                        lexeme.equals("REPETIR") || lexeme.equals("RETORNAR")) {
                    return "Inst"; // Instrucción
                } else {
                    // Para otras palabras clave, podrías retornar un no terminal general
                    return "Decl";
                }

            case IDENTIFIER:
                return null;
            case INT_CONSTANT:
            case STRING_CONSTANT:
            case BOOL_CONSTANT:
                return null;
            case RELATIONAL_OP:
            case LOGICAL_OP:
            case ARITHMETIC_OP:
            case SPECIAL_SYMBOL:
                return null;

            default:
                return null;
        }
    }


    public void getFollow(Token token) {
        String nonTerminal = mapTokenToNonTerminal(token);
        if (nonTerminal != null) {
            List<String> followSet = firstFollow.getFollow(nonTerminal);
            System.out.println("Token: " + token.getToken() + " → FOLLOW(" + nonTerminal + ") = " + followSet);
        } else {
            System.out.println("Token: " + token.getToken() + " → No FOLLOW (it's a terminal)");
        }
    }

    public ParseNode getRoot() {
        return root;
    }

    public void getFirst(Token token) throws FileNotFoundException {

        String nonTerminal = mapTokenToNonTerminal(token);
        if (nonTerminal != null) {
            System.out.println("FIRST(" + nonTerminal + "): " + firstFollow.getFirst(nonTerminal));
        } else {
            System.out.println("Token '" + token.getToken() + "' does not correspond to a nonterminal.");
        }
    }


    public void generateParseTree(Token token) {
        parsingTable.buildTable();
        System.out.println("");

    }








}
