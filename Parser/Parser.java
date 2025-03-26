package Parser;

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
    public Parser() throws FileNotFoundException {
        firstFollow = new LoadFirstFollow();
        stack = new Stack<>();
        stack.push("$"); // End marker
        stack.push("Program"); // Start symbol of grammar
        this.root = new ParseNode("Program");
    }

    private String mapTokenToNonTerminal(Token token) {
        String lexeme = token.getToken().toUpperCase();

        switch (token.getTokenType()) {
            case KEYWORD:
                switch (lexeme) {
                    case "FUNCIO": return "FunctionDeclaration";
                    case "SENCER": return "VariableDeclaration";
                    case "ESCRIURE": return "PrintStatement";
                    case "SI": return "IfStatement";
                    case "MENTRE": return "WhileStatement";
                    case "REPETIR": return "RepeatStatement";
                    case "RETORNAR": return "ReturnStatement";
                    case "INICI": return "MainBlock";
                    default: return "Statement";
                }

            case IDENTIFIER:
                return "Expression";

            case INT_CONSTANT:
            case STRING_CONSTANT:
            case BOOL_CONSTANT:
                return "Expression";

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


    }

    private boolean isTerminal(String symbol) {
        return !firstFollow.getFirst(symbol).isEmpty() ? false : !symbol.equals("Program"); // crude fallback
    }

    private void applyRule(String nonTerminal, Stack<String> stack) {
        String rule = getProduction(nonTerminal, tokens.get(currentToken).getToken());
        if (rule != null) {
            String[] symbols = rule.split(" ");
            for (int i = symbols.length - 1; i >= 0; i--) {
                stack.push(symbols[i]);
            }
        } else {
            System.out.println("No production found for " + nonTerminal);
        }
    }

    private String getProduction(String nonTerminal, String terminal) {
        // Implement LL(1) parsing table lookup
        return "Example_Production -> ..."; // Replace with actual logic
    }


}
