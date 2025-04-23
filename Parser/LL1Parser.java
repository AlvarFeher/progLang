package Parser;


import Symbols.SymbolsTable;
import Token.Token;
import Tree.TreeNode;

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

    public TreeNode getRoot() {
        return root;
    }

    public void parse(List<InputEntry> tokens) {
        Stack<String> stack = new Stack<>();
        treeStack.clear();

        // Set up initial stack and tree root
        stack.push("$");
        TreeNode programRoot = new TreeNode("Program");
        treeStack.push(programRoot);
        stack.push("Program");

        tokens.add(new InputEntry("","$")); // End marker
        int index = 0;

        System.out.println("== Parsing Process ==");

        while (!stack.isEmpty()) {
            String top = stack.peek();
            String currentToken = tokens.get(index).getTerminal();

            System.out.println("STACK TOP: " + top + "   CURRENT TOKEN: " + currentToken);

            if (top.equals(currentToken)) {
                System.out.println("Matched: " + top);
                stack.pop();
                if(!treeStack.isEmpty()){
                    TreeNode matchedNode = treeStack.pop();
                    matchedNode.label = currentToken;
                    matchedNode.value = tokens.get(index).getValue();
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
            System.err.println("Tree stack was empty! Could not build parse tree.");
        }

        if (index == tokens.size()) {
            System.out.println("Input accepted!");
        } else {
            error("Unexpected end of input");
        }
    }


    private void error(String message) {
        System.err.println("Syntax Error: " + message);
    }
    private void printTree(TreeNode node, int indent) {
        for (int i = 0; i < indent; i++) System.out.print("  ");
        System.out.println(node.label + ": " + node.value);
        for (TreeNode child : node.children) {
            printTree(child, indent + 1);
        }
    }

    private String extractType(TreeNode node) {
        for(TreeNode child : node.children ) {
            if (child.label.equals("tipus_simple")) {
                return child.children.isEmpty() ? child.value : child.children.get(0).label;
            }
        }
        return null;
    }



    public SymbolsTable buildSymbolTable(TreeNode node, SymbolsTable table) {
        if (node == null) return table;
        System.out.println(node.label+": "+node.value);
        // Handle variable or constant declaration
        if (node.label.equals("Dec_Cte_Var")) {
            String type = null;
            String id = null;

            for (TreeNode child : node.children) {
                if (child.label.equals("Tipus")) {
                    type = extractType(child);
                }
                if (child.label.equals("ID")) {
                    id = child.value;
                }
            }

            if (id != null && type != null) {
                table.addEntry(id, type, null);
            }
        }

        // Handle function declaration
        if (node.label.equals("Dec_Fun")) {
            String functionName = null;
            String returnType = null;

            for (TreeNode child : node.children) {
                if (child.label.equals("ID")) {
                    functionName = child.value;
                } else if (child.label.equals("tipus_simple")) {
                    returnType = child.children.isEmpty() ? child.value : child.children.get(0).label;
                }
            }

            if (functionName != null && returnType != null) {
                table.addEntry(functionName, "FUNCIO(" + returnType + ")", null);
            }
        }

        if (node.label.equals("Const")) {
            String id = null;

            for (TreeNode child : node.children) {
                if (child.label.equals("ID")) {
                    id = child.value;
                }
            }

            if (id != null) {
                table.addEntry(id, "CONST", null);  // Type unknown until resolved
            }
        }

        // Handle assignments and operations
        if (node.label.equals("Inst")) {
            String id = null;
            TreeNode instTail = null;

            for (TreeNode child : node.children) {
                if (child.label.equals("ID")) {
                    id = child.value;
                } else if (child.label.equals("InstTail")) {
                    instTail = child;
                }
            }

            if (id != null && instTail != null) {
                TreeNode firstChild = instTail.children.isEmpty() ? null : instTail.children.get(0);

                if (firstChild != null) {
                    // Assignment
                    if (firstChild.label.equals("=")) {
                        TreeNode exp = findChild(instTail, "Exp");
                        String value = extractExpressionValue(exp);
                        table.updateValue(id, value);  // Add value to previously declared variable
                    }

                    // Function call
                    if (firstChild.label.equals("(")) {
                        table.updateValue(id, "CALL"); // or you can expand this to store function name, etc.
                    }
                }
            }
        }


        // Recurse to all children
        for (TreeNode child : node.children) {
            buildSymbolTable(child, table);
        }

        return table;
    }


    private TreeNode findChild(TreeNode node, String label) {
        for (TreeNode child : node.children) {
            if (child.label.equals(label)) return child;
        }
        return null;
    }

    private String extractExpressionValue(TreeNode node) {
        if (node == null) return "";

        // Simple constant or identifier
        if (node.label.equals("cte_entera") || node.label.equals("cte_cadena") || node.label.equals("ID")) {
            return node.value;
        }

        // If it's an expression subtree, concatenate its children
        StringBuilder sb = new StringBuilder();
        for (TreeNode child : node.children) {
            sb.append(extractExpressionValue(child)).append(" ");
        }
        return sb.toString().trim();
    }


}
