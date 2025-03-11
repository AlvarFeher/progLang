package Token;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {
    private String filepath;
    private List<Token> tokens;

    public LexicalAnalyzer(String filepath) {
        this.tokens = new ArrayList<>();
        this.filepath = filepath;
    }

    private boolean isLogicalop(String word){
        String[] ops = {"AND","OR", "NOT"};
        for (String op : ops){
            if(word.equals(op)){
                return true;
            }
        }
        return false;
    }

    /**
     * Given the input file, generates all the tokens and clears the input (removes comments and white spaces)
     * @return list of Tokens
     * @throws IOException
     */
    public List<Token> tokenize() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filepath)));
        int pos=0;
        int len=content.length();
        StringBuilder sb = new StringBuilder();

        // iterate through the entire content string
        while(pos < len){
            char curr = content.charAt(pos);

            // skip whitespace
            if(Character.isWhitespace(curr)){
                pos++;
                continue;
            }

            // skip comments
            if(curr == '/' && pos+1<len && content.charAt(pos+1)=='/'){
                pos+=2;
                while(content.charAt(pos) != '\n' && pos <len){
                    pos++;
                }
                continue;
            }

            // Constant cadena: "khsdbvfks" (only to print!!)
            if(curr == '"'){
                pos++;

                while(pos < len && content.charAt(pos) != '"'){
                    sb.append(content.charAt(pos));
                    pos++;
                }
                if(pos < len && content.charAt(pos) == '"'){ // skip closing "
                    pos++;
                }
                tokens.add(new Token(TokenType.STRING_CONSTANT, sb.toString()));
            }

            // Identifier: "test7_Test" for example
            if(Character.isLetter(curr)){
                while(pos < len && Character.isLetterOrDigit(content.charAt(pos)) || content.charAt(pos) == '_'){
                    sb.append(content.charAt(pos));
                    pos++;
                }
                String word = sb.toString();
                if (word.length() > 32) {
                    System.err.println("Identifier exceeds maximum allowed length of 32 characters: " + word);
                }
                if(Keywords.isKeyword(word)){
                    tokens.add(new Token(TokenType.KEYWORD,word));
                } else if (isLogicalop(word)) {
                    tokens.add(new Token(TokenType.LOGICAL_OP,word));
                }
            }
            pos++;
        }

        return tokens;
    }
}
