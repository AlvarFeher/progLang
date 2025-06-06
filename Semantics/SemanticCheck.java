package Semantics;

import Symbols.SymbolsTable;
import Tree.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class SemanticCheck {
    private SymbolsTable symbolsTable;
    private List<String> errors;
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public SemanticCheck(SymbolsTable symbolsTable) {
        this.symbolsTable = symbolsTable;
        errors = new ArrayList<>();
    }

    public void analyze(TreeNode root) {
        checkNode(root);
    }
    public boolean hasErrors() {
        if (errors.size() > 0) {
            return true;
        }
        return false;
    }
    public void printErrors() {
        System.out.println(ANSI_RED+"SEMANTIC ERRORS:");

        for (String error : errors) {
            System.out.println(error);
        }
        System.out.println(ANSI_RESET);
    }

    // check if the node is an instruction and goes over its children, check if itu is a function or an assignment
    private void checkNode(TreeNode node) {
        if (node == null) return;
        if ("Inst".equals(node.label) && node.children.size() >= 2) {
            TreeNode first = node.children.get(0);
            TreeNode second = node.children.get(1);

            // Case 1: Function call
            if (!second.children.isEmpty() && "(".equals(second.children.get(0).label)) {
                checkFunctionCall(first.value, second);
            }

            // Case 2: Assignment
            else if (second.label.equals("InstTail") && second.children.size() >= 3 && "=".equals(second.children.get(0).label)) {
                checkAssignment(node);
            }

            // Case 3: Expression statement (e.g., y == 10;)
            else {
                TreeNode expr = second.find("Exp");
                if (expr != null) {
                    String type = checkExpression(expr, symbolsTable);
                    errors.add("Useless expression: '" + expr.toString() + "' evaluates to " + type + " but is not used.");
                }
            }
        }




        if(node.label.equals("ExpSimple")) {

        }
        for (TreeNode child : node.children) {
            checkNode(child);
        }
    }

    private void checkAssignment(TreeNode node) {
        if (node.children.size() < 2 || !node.children.get(1).label.equals("InstTail")) return;

        TreeNode idNode = node.children.get(0);
        TreeNode tail = node.children.get(1);
        if (tail.children.size() < 3) return;

        TreeNode expr = tail.children.get(1);  // = Exp ;

        String varName = idNode.value;
        String expectedType = symbolsTable.getType(varName);
        String exprType = checkExpression(expr, symbolsTable);

        if (!expectedType.equals(exprType)) {
            errors.add("Type mismatch: variable '" + varName + "' expects " + expectedType + " but got " + exprType);
        }
    }

    private void checkFunctionCall(String funcName, TreeNode instTailNode) {
        List<String> expectedParams = symbolsTable.getFunctionParams(funcName);

        if (expectedParams == null) {
            errors.add("Function '" + funcName + "' is not declared.");
            return;
        }

        TreeNode exprListNode = instTailNode.find("Llista_expressio");
        if (exprListNode == null) {
            errors.add("Cannot find parameter list in call to '" + funcName + "'");
            return;
        }

        List<String> actualParams = extractArgs(exprListNode);

        if (actualParams.size() != expectedParams.size()) {
            errors.add("Function '" + funcName + "' expects " + expectedParams.size() +
                    " argument(s), but got " + actualParams.size());
            return;
        }

        for (int i = 0; i < actualParams.size(); i++) {
            if (!actualParams.get(i).equals(expectedParams.get(i))) {
                errors.add("Function '" + funcName + "' argument " + (i + 1) +
                        " type mismatch: expected " + expectedParams.get(i) +
                        ", got " + actualParams.get(i));
            }
        }
    }

    private String findDataType(TreeNode node) {
        if (node == null) return "UNKNOWN";

        switch (node.label) {
            case "cte_entera": return "SENCER";
            case "cte_cadena": return "CADENA";
            case "CERT": return "LOGIC";
            case "FALS": return "LOGIC";
            case "ID": return symbolsTable.getType(node.value);
        }

        for (TreeNode child : node.children) {
            String childType = findDataType(child);
            if (!childType.equals("UNKNOWN")) return childType;
        }

        return "UNKNOWN";
    }

// get the list of arguments of a function
    private List<String> extractArgs(TreeNode node) {
        List<String> types = new ArrayList<>();
        if (node == null) return types;

        TreeNode current = node;
        while (current != null) {
            TreeNode exp = current.find("Exp");
            if (exp != null) {
                types.add(findDataType(exp));
            }

            current = current.find("Llista_expressio_tail");
            if (current != null && !current.children.isEmpty()) {
                current = current; // continue traversal
            } else {
                current = null;
            }
        }

        return types;
    }

    public String checkExpression(TreeNode node, SymbolsTable table) {
        if (node == null) return "UNKNOWN";

        switch (node.label) {
            case "ID":
                return table.getType(node.value);

            case "cte_entera":
                return "SENCER";

            case "cte_cadena":
                return "STRING";

            case "CERT":
            case "FALS":
                return "LOGIC";

            case "Exp":
                return checkExpression(node.find("ExpSimple"), table);

            case "ExpSimple": {
                TreeNode term = node.find("Term");
                TreeNode tail = node.find("ExpSimpleTail");

                String leftType = checkExpression(term, table);
                return checkBinaryTail(leftType, tail, table);
            }

            case "Term": {
                TreeNode factor = node.find("Factor");
                TreeNode tail = node.find("TermTail");

                String leftType = checkExpression(factor, table);
                return checkBinaryTail(leftType, tail, table);
            }

            case "Factor": {
                if (node.children.size() >= 3) {
                    TreeNode first = node.children.get(0);
                    TreeNode second = node.children.get(1);

                    // Detect function call pattern: ID ( Llista_expressio )
                    if ("ID".equals(first.label) && "(".equals(second.label)) {
                        String funcName = first.value;

                        String fullType = table.getType(funcName);
                        if (fullType != null && fullType.startsWith("FUNCIO(")) {
                            int start = fullType.indexOf('(');
                            int end = fullType.indexOf(')', start);
                            if (start != -1 && end != -1) {
                                String returnType = fullType.substring(start + 1, end).trim();
                                return returnType;
                            }
                        }

                        return "UNKNOWN"; // fallback if parsing fails
                    }
                }

                // fallback to single child evaluation
                for (TreeNode child : node.children) {
                    String type = checkExpression(child, table);
                    if (!type.equals("UNKNOWN")) return type;
                }

                return "UNKNOWN";
            }

            default:
                return "UNKNOWN";
        }
    }

    // check that when doing an operation both types match each other SENCER = SENCER + SENCER
    private String checkBinaryTail(String leftType, TreeNode tail, SymbolsTable table) {
        if (tail == null || tail.children.isEmpty()) return leftType;

        String op = tail.children.get(0).label;
        TreeNode rightNode = tail.children.get(1);
        String rightType = checkExpression(rightNode, table);

        System.out.println("Checking: " + leftType + " " + op + " " + rightType);

        if (!leftType.equals(rightType)) {
            errors.add("Type mismatch in operation: " + leftType + " " + op + " " + rightType);
            return "ERROR";
        }

        if (List.of("+", "-", "*", "/").contains(op)) {
            if (!leftType.equals("SENCER")) {
                errors.add("Invalid operand type for arithmetic op '" + op + "': " + leftType);
                return "ERROR";
            }
        }

        TreeNode nextTail = tail.children.size() > 2 ? tail.children.get(2) : null;
        return checkBinaryTail(leftType, nextTail, table);
    }

}
