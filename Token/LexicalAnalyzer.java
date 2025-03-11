package Token;

import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {
    private String filepath;
    private List<Token> tokens;

    public LexicalAnalyzer(String filepath) {
        this.tokens = new ArrayList<>();
        this.filepath = filepath;
    }


    public void tokenize(){

    }
}
