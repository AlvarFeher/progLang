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

    private String inferType(TreeNode node) {
        if (node == null) return "UNKNOWN";

        if (node.label.equals("cte_entera")) return "SENCER";
        if (node.label.equals("cte_cadena")) return "CADENA";  // define your types
        if (node.label.equals("CERT") || node.label.equals("FALS")) return "LOGIC";
        if (node.label.equals("ID")) return symbolsTable.getType(node.value);

        // recursively walk Exp -> ExpSimple -> Term -> Factor
        TreeNode childExp = node.find("ExpSimple");
        if (childExp != null) return inferType(childExp);

        TreeNode term = node.find("Term");
        if (term != null) return inferType(term);

        TreeNode factor = node.find("Factor");
        if (factor != null) return inferType(factor.children.get(0));

        return "UNKNOWN";
    }

    private void checkNode(TreeNode node) {
        switch(node.label){
            case "Inst":
                //checkAssignment(node);
                break;
            case "Exp":
                //checkExpression(node, symbolsTable);
                break;
            case "FuncCall":
                //checkFunctionCall(node);
                break;

        }
        if ("Inst".equals(node.label) && node.children.size() >= 2) {
            TreeNode idNode = node.children.get(0);
            TreeNode tailNode = node.children.get(1);
            if ("(".equals(tailNode.children.get(0).label)) {
                checkFunctionCall(idNode.value, tailNode);
            } else {
                checkAssignment(node);
            }
        }
        for (TreeNode child : node.children) {
            checkNode(child);
        }
    }
    private void checkFunctionCall(String funcName, TreeNode instTailNode) {
        List<String> expectedParams = symbolsTable.getFunctionParams(funcName);

        if (expectedParams == null) {
            errors.add("Function '" + funcName + "' is not declared.");
            return;
        }

        // Extract actual arguments from instTailNode (should be: (, Llista_expressio, ), ;)
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
    private List<String> extractArgs(TreeNode llistaNode) {
        List<String> types = new ArrayList<>();
        if (llistaNode == null) return types;

        TreeNode current = llistaNode;
        while (current != null) {
            TreeNode exp = current.find("Exp");
            if (exp != null) {
                types.add(inferType(exp));
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


    private void checkAssignment(TreeNode node) {
        if (node.children.size() < 2 || !node.children.get(1).label.equals("InstTail")) return;

        TreeNode idNode = node.children.get(0);
        TreeNode tail = node.children.get(1);
        if (tail.children.size() < 3) return;

        TreeNode expr = tail.children.get(1);

        String varName = idNode.value;
        String expectedType = symbolsTable.getType(varName); // from symbol table
        String exprType = inferType(expr); // your own method to infer type

        if (!expectedType.equals(exprType)) {
            errors.add("Type mismatch: variable '" + varName + "' expects " + expectedType + " but got " + exprType);
        }
    }

    private String checkBinaryTail(String leftType, TreeNode tail, SymbolsTable table) {
        if (tail == null || tail.children.isEmpty()) return leftType;

        String op = tail.children.get(0).label;
        TreeNode rightNode = tail.children.get(1);
        String rightType = checkExpression(rightNode, table);

        if (!leftType.equals(rightType)) {
            System.err.printf("❌ Type mismatch in operation %s: %s %s %s\n", op, leftType, op, rightType);
            return "ERROR";
        }

        // Example: +, -, *, / → allowed only for SENCER
        if ("+".equals(op) || "-".equals(op) || "*".equals(op) || "/".equals(op)) {
            if (!leftType.equals("SENCER")) {
                System.err.printf("❌ Invalid operand type for arithmetic op '%s': %s\n", op, leftType);
                return "ERROR";
            }
        }

        // Proceed to next part of the chain
        TreeNode nextTail = tail.children.size() > 2 ? tail.children.get(2) : null;
        return checkBinaryTail(leftType, nextTail, table);
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
                if (node.children.size() == 1) {
                    return checkExpression(node.children.get(0), table);
                }
                return "UNKNOWN";
            }

            default:
                return "UNKNOWN";
        }
    }

}
