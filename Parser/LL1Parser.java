package Parser;


import Grammar.Grammar;
import Token.LexicalAnalyzer;
import Token.Token;
import Tree.TreeNode;

import java.io.IOException;
import java.util.*;

public class LL1Parser {

    private final Map<String, List<List<String>>> grammar;
    private final Map<String, Map<String, List<String>>> parsingTable;
    private final Set<String> terminals;

    // Tree Stack
    private final Stack<TreeNode> treeStack;
    private TreeNode root = null;

    public LL1Parser(Map<String, List<List<String>>> grammar,
                     Map<String, Map<String, List<String>>> parsingTable) {
        this.grammar = grammar;
        this.parsingTable = parsingTable;
        this.terminals = computeTerminals();
        treeStack = new Stack<>();
    }

    private Set<String> computeTerminals() {
        Set<String> terminals = new HashSet<>();
        for (List<List<String>> rules : grammar.values()) {
            for (List<String> rule : rules) {
                for (String symbol : rule) {
                    if (!grammar.containsKey(symbol) && !symbol.equals("ε")) {
                        terminals.add(symbol);
                    }
                }
            }
        }
        terminals.add("$");
        return terminals;
    }
    public String mapTokenToGrammarTerminal(Token token) {
        String lexeme = token.getToken().toUpperCase();

        switch (token.getTokenType()) {
            case KEYWORD:
                if (lexeme.equals("SENCER") || lexeme.equals("LOGIC")) {
                    return "tipus_simple";
                }
                return lexeme;
                 // e.g., FUNCIO, CONST, SI, MENTRE, INICI, FI...
            case IDENTIFIER:
                return "ID";
            case INT_CONSTANT:
                return "cte_entera";
            case STRING_CONSTANT:
                return "cte_cadena";
            case BOOL_CONSTANT:
                return lexeme.equals("CERT") ? "CERT" : "FALS";
            case RELATIONAL_OP:
                return "oper_rel"; // generalize all relational ops
            case LOGICAL_OP:
            case ARITHMETIC_OP:
                return lexeme; // e.g., +, -, *, /
            case SPECIAL_SYMBOL:
                return token.getToken(); // (, ), {, }, ;, :
            default:
                return null;
        }
    }


    public void parse(List<String> tokens) {
        Stack<String> stack = new Stack<>();
        treeStack.clear();

        // Set up initial stack and tree root
        stack.push("$");
        TreeNode programRoot = new TreeNode("Program");
        treeStack.push(programRoot);
        stack.push("Program");

        tokens.add("$"); // End marker
        int index = 0;

        System.out.println("== Parsing Process ==");

        while (!stack.isEmpty()) {
            String top = stack.peek();
            String currentToken = tokens.get(index);

            System.out.println("STACK TOP: " + top + "   CURRENT TOKEN: " + currentToken);

            if (top.equals(currentToken)) {
                System.out.println("Matched: " + top);
                stack.pop();
                if(!treeStack.isEmpty()){
                    TreeNode matchedNode = treeStack.pop();
                    matchedNode.label = currentToken;
                }
               // Terminal value
                index++;
            } else if (terminals.contains(top)) {
                error("Unexpected token: " + currentToken + " (expected: " + top + ")");
                return;
            } else if (parsingTable.containsKey(top) && parsingTable.get(top).containsKey(currentToken)) {
                stack.pop();

                List<String> production = parsingTable.get(top).get(currentToken);
                System.out.println(top + " -> " + production);

                TreeNode parent = treeStack.pop(); // Non-terminal node

                List<TreeNode> children = new ArrayList<>();
                for (String symbol : production) {
                    if (!symbol.equals("ε")) {
                        TreeNode child = new TreeNode(symbol);
                        parent.children.add(child);
                        children.add(child);
                    }
                }

                // Reverse and push children to the treeStack and parse stack
                Collections.reverse(children);
                for (TreeNode child : children) {
                    treeStack.push(child);
                    stack.push(child.label);
                }
            } else {
                error("No rule for [" + top + ", " + currentToken + "]");
                return;
            }
        }
        root = programRoot;
        System.out.println("\n📦 Parse Tree:");
        printTree(root, 0);

        // ✅ Final root assignment
        if (!treeStack.isEmpty()) {
            root = programRoot;
            System.out.println("\n📦 Parse Tree:");
            printTree(root, 0);
        } else {
            System.err.println("⚠️ Tree stack was empty! Could not build parse tree.");
        }

        if (index == tokens.size()) {
            System.out.println("✅ Input accepted!");
        } else {
            error("Unexpected end of input");
        }
    }


    private void error(String message) {
        System.err.println("❌ Syntax Error: " + message);
    }
    private void printTree(TreeNode node, int indent) {
        for (int i = 0; i < indent; i++) System.out.print("  ");
        System.out.println(node.label + ": " + node.value);
        for (TreeNode child : node.children) {
            printTree(child, indent + 1);
        }
    }


}
