package Parser;

import Token.Token;

public class ParseNode {
    private Token token;
    private ParseNode[] children;

    public ParseNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public ParseNode[] getChildren() {
        return children;
    }

    public void setChildren(ParseNode[] children) {
        this.children = children;
    }
}
