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

    public void printErrors() {
        System.out.println(ANSI_RED+"ERRORS:");

        for (String error : errors) {
            System.out.println(error);
        }
        System.out.println(ANSI_RESET);
    }

    // check if the node is an instruction and goes over its children, check if itu is a function or an assignment
    private void checkNode(TreeNode node) {
        if ("Inst".equals(node.label) && node.children.size() >= 2) {
            TreeNode idNode = node.children.get(0);
            TreeNode tailNode = node.children.get(1);

            if (!tailNode.children.isEmpty() && "(".equals(tailNode.children.get(0).label)) {
                checkFunctionCall(idNode.value, tailNode);
            } else {
                checkAssignment(node);
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
