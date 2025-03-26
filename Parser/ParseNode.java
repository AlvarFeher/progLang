package Parser;

import Token.Token;

import java.util.List;

public class ParseNode {
    private String label;
    private List<ParseNode> children;

    public ParseNode(String label) {
        this.label = label;
    }

    public void addChildren(ParseNode newChild){
        children.add(newChild);
    }
}
