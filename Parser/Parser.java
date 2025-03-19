package Parser;

import Token.Token;

import java.io.FileNotFoundException;
import java.util.List;

public class Parser {
    private ParseNode root;
    private List<Token> tokens;
    private int currentToken =0;
    private Token token;
    LoadFirstFollow firstFollow;
    public Parser() throws FileNotFoundException {
        firstFollow = new LoadFirstFollow();
    }

    public void getFirst(Token token) throws FileNotFoundException {

        System.out.println(firstFollow.getFirst(token.getTokenType().toString()));
    }

    public ParseNode generateParseTree(){

        return root;
    }

}
