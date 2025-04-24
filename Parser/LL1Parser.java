package Parser;


import Symbols.Symbol;
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
                 //FUNCIO, CONST, SI, MENTRE, INICI, FI...
            case IDENTIFIER:
                return "ID";
            case INT_CONSTANT:
                return "cte_entera";
            case STRING_CONSTANT:
                return "cte_cadena";
            case BOOL_CONSTANT:
                return lexeme.equals("CERT") ? "CERT" : "FALS";
            case RELATIONAL_OP:
                return "oper_rel";
            case LOGICAL_OP:
            case ARITHMETIC_OP:
                return lexeme; // +, -, *, /
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

        // set up initial stack and tree root
        stack.push("$");
        TreeNode programRoot = new TreeNode("Program");
        treeStack.push(programRoot);
        stack.push("Program");

        tokens.add(new InputEntry("","$")); // end marker
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
               // terminal value
                index++;
            } else if (terminals.contains(top)) {
                error("Unexpected token: " + currentToken + " (expected: " + top + ")");
                return;
            } else if (parsingTable.containsKey(top) && parsingTable.get(top).containsKey(currentToken)) {
                stack.pop();

                List<String> production = parsingTable.get(top).get(currentToken);
                System.out.println(top + " -> " + production);

                TreeNode parent = treeStack.pop(); // non-terminal node

                List<TreeNode> children = new ArrayList<>();
                for (String symbol : production) {
                    if (!symbol.equals("ε")) {
                        TreeNode child = new TreeNode(symbol);
                        parent.children.add(child);
                        children.add(child);
                    }
                }

                // reverse and push children to treeStack and parse stack
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
        System.out.println("\nParse Tree:");
        printTree(root, 0);

        if (!treeStack.isEmpty()) {
            root = programRoot;
            System.out.println("\nParse Tree:");
            printTree(root, 0);
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
        //System.out.println(node.label+": "+node.value);


        // variable or constant declaration
        if (node.label.equals("Dec_Cte_Var")) {
            String type = null;
            String id = null;
            String value = null;

            for (TreeNode child : node.children) {
                if (child.label.equals("Tipus")) {
                    type = extractType(child);
                }
                if (child.label.equals("Const")) {
                    type = "CONST";
                }
                if (child.label.equals("ID")) {
                    id = child.value;
                }
                if (child.label.equals("Exp")) {
                    value = extractExpression(child);
                }
            }

            if (id != null && type != null) {
                table.addEntry(id, type, value);
            }
        }


        // function declaration
        if (node.label.equals("Dec_Fun")) {
            String functionName = null;
            String returnType = null;
            List<String> params = new ArrayList<>();

            for (TreeNode child : node.children) {
                if (child.label.equals("ID")) {
                    functionName = child.value;
                } else if (child.label.equals("tipus_simple")) {
                    returnType = extractExpression(child);
                } else if (child.label.equals("Llista_Param")) {
                    params.addAll(extractParams(child));
                }
            }

            if (functionName != null && returnType != null) {
                String paramString = String.join(", ", params);
                table.addEntry(functionName, "FUNCIO(" + returnType + ")", "(" + paramString + ")");
            }
        }

        //  constants
        if (node.label.equals("Const")) {
            String id = null;

            for (TreeNode child : node.children) {
                if (child.label.equals("ID")) {
                    id = child.value;
                }
            }

            if (id != null) {
                table.addEntry(id, "CONST", null);
            }
        }

        // assignments and operations
        if (node.label.equals("Inst") && node.children.size() >= 2) {
            TreeNode idNode = node.children.get(0);
            TreeNode instTail = node.children.get(1);

            if (idNode.label.equals("ID") && instTail.label.equals("InstTail")) {
                TreeNode assignOp = instTail.children.isEmpty() ? null : instTail.children.get(0);
                TreeNode expNode = findChild(instTail, "Exp");

                if (assignOp != null && assignOp.label.equals("=") && expNode != null) {
                    String id = idNode.value;
                    String value = extractExpression(expNode);  // Use this to show `a + 1` instead of just numbers
                    Symbol sym = table.getEntry(id);
                    System.out.println(">> Updating variable '" + id + "' with value: " + value);

                    if (sym != null) {
                        sym.setValue(value);  // update the value in the symbol table
                    }
                }
            }
        }

        // traverse to all children
        for (TreeNode child : node.children) {
            buildSymbolTable(child, table);
        }

        return table;
    }

    private List<String> extractParams(TreeNode node) {
        List<String> params = new ArrayList<>();
        if (node == null) return params;

        for (TreeNode child : node.children) {
            if (child.label.equals("Parameter")) {
                for (TreeNode grandchild : child.children) {
                    if (grandchild.label.equals("Tipus")) {
                        params.add(extractType(grandchild));
                    }
                }
            }
            params.addAll(extractParams(child));
        }

        return params;
    }

    private TreeNode findChild(TreeNode node, String label) {
        for (TreeNode child : node.children) {
            if (child.label.equals(label)) return child;
        }
        return null;
    }

    private String extractExpression(TreeNode node) {
        if (node == null) return "";

        // Leaf node with an actual token
        if (node.children.isEmpty()) {
            return node.value != null ? node.value : "";
        }

        // go through children and ignore grammar rules like FactorTail, TermTail, etc..
        StringBuilder sb = new StringBuilder();
        for (TreeNode child : node.children) {
            String part = extractExpression(child);
            if (!part.isEmpty()) {
                sb.append(part).append(" ");
            }
        }

        return sb.toString().trim();
    }



}
