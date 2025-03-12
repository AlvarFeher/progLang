package Parser;

import Token.Token;

import java.util.List;

public class Parser {
    private ParseNode root;
    private List<Token> tokens;
    private int currentToken =0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public ParseNode generateParseTree(){

        return root;
    }

}
