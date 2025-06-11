package IntCode;

import Tree.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class IntermediateCode {
    private List<Quadruple> code = new ArrayList<>();
    private LabelGenerator gen = new LabelGenerator();

    public List<Quadruple> getCode() {
        return code;
    }

    public void generate(TreeNode node) {
        if (node == null) return;
        switch (node.label) {
            case "Inst":
                 handleInstruction(node);
                 break;
            case "Exp":
                 handleExpression(node);
                 break;
            case "Dec_Fun":
                String funcName = node.children.get(1).value;
                addLabel(funcName);
                generate(node.find("Llista_inst"));
                add("END_FUNC", funcName, null, null);
                return; // important: do not descend into children again

            default:
                     for (TreeNode child : node.children) {
                         generate(child);
                     }
        }


    }

    private void handleInstruction(TreeNode node) {
        TreeNode first = node.children.get(0);
        if (first.label.equals("ID") && node.children.size() > 1 && node.children.get(1).label.equals("InstTail")) {
            TreeNode tail = node.children.get(1);
            if (tail.children.size() > 0 && tail.children.get(0).label.equals("=")) {
                // assign ID = Exp;
                String id = first.value;
                TreeNode exp = tail.children.get(1);

                //function call or expression
                String value = evalExp(exp);
                code.add(new Quadruple("=", value, null, id));
            } else if (tail.children.size() > 0 && tail.children.get(0).label.equals("(")) {
                //  function call
                TreeNode args = tail.find("Llista_expressio");
                handleFunctionCall(first.value, args); // dont assign result
            }

        }

        else if (first.label.equals("ESCRIURE")) {
            TreeNode list = node.find("Llista_expressio");
            handlePrint(list);
        }

        else if (first.label.equals("SI")) {
            TreeNode cond = node.find("Exp");
            TreeNode thenBlock = node.find("Llista_inst");
            TreeNode fisi = node.find("FISI");
            TreeNode elseBlock = findElseBlock(fisi);

            String condTemp = evalExp(cond);
            String labelElse = gen.newLabel();
            String labelEnd = gen.newLabel();

            add("IFZ", condTemp, null, labelElse);
            generate(thenBlock);

            if (elseBlock != null) {
                add("GOTO", null, null, labelEnd); // skip else block
                addLabel(labelElse);
                generate(elseBlock);
                addLabel(labelEnd);
            } else {
                addLabel(labelElse); // no else, jump here directly
            }
        }

        else if (first.label.equals("MENTRE")) {
            String labelStart = gen.newLabel();
            String labelEnd = gen.newLabel();

            addLabel(labelStart);

            TreeNode cond = node.find("Exp");
            String condTemp = evalExp(cond);
            add("IFZ", condTemp, null, labelEnd);

            TreeNode body = node.find("Llista_inst");
            generate(body);

            add("GOTO", null, null, labelStart);
            addLabel(labelEnd);
        }
        else if (first.label.equals("RETORNAR")) {
            TreeNode exp = node.find("Exp");
            String value = evalExp(exp);
            add("RETURN", value, null, null);
        }

    }

    private void add(String op, String arg1, String arg2, String result) {
        code.add(new Quadruple(op, arg1, arg2, result));
    }

    private void addLabel(String label) {
        code.add(new Quadruple("LABEL", label, null, null));
    }



    private void handlePrint(TreeNode list) {
        if (list == null) return;
        for (TreeNode expNode : list.children) {
            if (expNode.label.equals("Exp")) {
                String val = evalExp(expNode);
                code.add(new Quadruple("PRINT", val, null, null));
            }
        }
    }

    private TreeNode findElseBlock(TreeNode fisi) {
        if (fisi == null) return null;
        for (TreeNode child : fisi.children) {
            if ("ID".equals(child.label) && "SINO".equals(child.value)) {
                int index = fisi.children.indexOf(child);
                if (index + 1 < fisi.children.size()) {
                    return fisi.children.get(index + 1);
                }
            }
        }
        return null;
    }

    private String evalExp(TreeNode node) {
        if (node == null) return null;

        if (node.label.equals("ID") || node.label.equals("cte_entera") || node.label.equals("cte_cadena"))
            return node.value;

        if (node.label.equals("Exp")) {
            TreeNode expSimple = node.find("ExpSimple");
            TreeNode expRelTail = node.find("ExpRelTail");

            String left = evalExp(expSimple);

            if (expRelTail != null && !expRelTail.children.isEmpty()) {
                String relOp = expRelTail.children.get(0).value; // like >, ==...
                TreeNode rightNode = expRelTail.children.get(1);
                String right = evalExp(rightNode);
                String temp = gen.newTemp();
                code.add(new Quadruple(relOp, left, right, temp));
                return temp;
            }

            return left;
        }

        if (node.label.equals("ExpSimple")) {
            TreeNode term = node.find("Term");
            TreeNode tail = node.find("ExpSimpleTail");
            String left = evalExp(term);
            return evalBinaryTail(left, tail);
        }

        if (node.label.equals("Term")) {
            TreeNode factor = node.find("Factor");
            TreeNode tail = node.find("TermTail");
            String left = evalExp(factor);
            return evalBinaryTail(left, tail);
        }

        if (node.label.equals("Factor")) {
            TreeNode first = node.children.get(0);

            if (first.label.equals("ID")) {
                // check if its a function call
                if (node.children.size() > 1 && node.children.get(1).label.equals("FactorTail")) {
                    TreeNode tail = node.children.get(1);
                    if (tail.children.size() > 0 && tail.children.get(0).label.equals("(")) {
                        TreeNode args = tail.find("Llista_expressio");
                        return handleFunctionCall(first.value, args);
                    }
                }
                return first.value; // just an ID (variable)
            }

            return evalExp(first);
        }

        return null;
    }

    private String handleFunctionCall(String funcName, TreeNode args) {
        int argCount = 0;
        if (args != null) {
            for (TreeNode child : args.children) {
                if (child.label.equals("Exp")) {
                    String val = evalExp(child);
                    code.add(new Quadruple("PARAM", val, null, null));
                    argCount++;
                }
            }
        }

        String temp = gen.newTemp();
        code.add(new Quadruple("CALL", funcName, String.valueOf(argCount), temp));
        return temp;
    }

    private String evalBinaryTail(String left, TreeNode tail) {
        if (tail == null || tail.children.isEmpty()) return left;

        String op = tail.children.get(0).label;
        TreeNode rightNode = tail.children.get(1);
        String right = evalExp(rightNode);
        String t = gen.newTemp();
        code.add(new Quadruple(op, left, right, t));

        TreeNode nextTail = tail.children.size() > 2 ? tail.children.get(2) : null;
        return evalBinaryTail(t, nextTail);
    }


    private String handleExpression(TreeNode node) {
        if (node.children.isEmpty()) return null;

        TreeNode expSimple = node.children.get(0);
        return handleExpSimple(expSimple);
    }

    private String handleExpSimple(TreeNode node) {
        if (node.children.isEmpty()) return null;

        TreeNode term = node.children.get(0);
        String left = handleTerm(term);

        if (node.children.size() > 1) {
            TreeNode expSimpleTail = node.children.get(1);
            if (!expSimpleTail.children.isEmpty()) {
                TreeNode operator = expSimpleTail.children.get(0); // + o -
                TreeNode rightTerm = expSimpleTail.children.get(1);
                String right = handleTerm(rightTerm);
                String temp = gen.newTemp();
                code.add(new Quadruple(operator.value, left, right, temp));
                return temp;
            }
        }
        return left;
    }

    private String handleTerm(TreeNode node) {
        if (node.children.isEmpty()) return null;

        TreeNode factor = node.children.get(0);
        return handleFactor(factor);
    }

    private String handleFactor(TreeNode node) {
        if (node.children.isEmpty()) return null;

        TreeNode first = node.children.get(0);
        if (first.label.equals("ID") || first.label.equals("cte_entera")) {
            return first.value;
        }
        return null;
    }
}
