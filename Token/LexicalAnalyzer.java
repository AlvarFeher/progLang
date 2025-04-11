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
    private int pos;
    private int len;
    String content;

    public LexicalAnalyzer(String filepath) throws IOException {
        this.tokens = new ArrayList<>();
        this.filepath = filepath;
        content = new String(Files.readAllBytes(Paths.get(filepath)));
        pos = 0;
        len = content.length();
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
    public Token nextToken() throws IOException {

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
                StringBuilder sb = new StringBuilder();
                while(pos < len && content.charAt(pos) != '"'){
                    sb.append(content.charAt(pos));
                    pos++;
                }
                if(pos < len && content.charAt(pos) == '"'){ // skip closing "
                    pos++;
                }
                tokens.add(new Token(TokenType.STRING_CONSTANT, sb.toString()));
                return new Token(TokenType.STRING_CONSTANT, sb.toString());
            }

            // Identifier: "test7_Test" for example
            if(Character.isLetter(curr)){
                StringBuilder sb = new StringBuilder();
                while (pos < len && (Character.isLetterOrDigit(content.charAt(pos)) || content.charAt(pos) == '_')) {
                    sb.append(content.charAt(pos));
                    pos++;
                }

                String word = sb.toString();
                if (word.length() > 32) {
                    System.err.println("Identifier exceeds maximum allowed length of 32 characters: " + word);
                    word = word.substring(0, 32);
                }
                if(Keywords.isKeyword(word)){
                    tokens.add(new Token(TokenType.KEYWORD,word));
                    return new Token(TokenType.KEYWORD,word);
                } else if (isLogicalop(word)) {
                    tokens.add(new Token(TokenType.LOGICAL_OP,word));
                    return new Token(TokenType.LOGICAL_OP,word);
                } else if (word.equalsIgnoreCase("CERT") || word.equalsIgnoreCase("FALS")) {
                    tokens.add(new Token(TokenType.BOOL_CONSTANT,word));
                    return new Token(TokenType.BOOL_CONSTANT,word);
                }else{
                    tokens.add(new Token(TokenType.IDENTIFIER,word));
                    return new Token(TokenType.IDENTIFIER,word);
                }
            }

            // digits
            if(Character.isDigit(curr)){
                StringBuilder sb = new StringBuilder();
                while(pos < len && Character.isDigit(content.charAt(pos))){
                    sb.append(content.charAt(pos));
                    pos++;
                }
                tokens.add(new Token(TokenType.INT_CONSTANT,sb.toString()));
                return new Token(TokenType.INT_CONSTANT,sb.toString());

            }

            if(curr == '=' && (pos+1 < len) && content.charAt(pos+1) == '='){
                tokens.add(new Token(TokenType.RELATIONAL_OP,"=="));
                pos+=2; // skip ! and =
                return new Token(TokenType.RELATIONAL_OP,"==");


            }

            if(curr == '<' || curr == '>' ){
                tokens.add(new Token(TokenType.RELATIONAL_OP,String.valueOf(curr)));
                pos++;
                return new Token(TokenType.RELATIONAL_OP,String.valueOf(curr));


            }
            if(curr == '!' && (pos+1 < len) && content.charAt(pos+1) == '='){
                tokens.add(new Token(TokenType.RELATIONAL_OP,"!="));
                pos+=2; // skip ! and =
                return new Token(TokenType.RELATIONAL_OP,"!=");


            }

            if(curr == '<' && (pos+1 < len) && content.charAt(pos+1) == '='){
                tokens.add(new Token(TokenType.RELATIONAL_OP,"<="));
                pos+=2; // skip ! and =
                return new Token(TokenType.RELATIONAL_OP,"<=");


            }
            if(curr == '>' && (pos+1 < len) && content.charAt(pos+1) == '='){
                tokens.add(new Token(TokenType.RELATIONAL_OP,">="));
                pos+=2; // skip ! and =
                return new Token(TokenType.RELATIONAL_OP,">=");
            }

            //operators
            if(curr == '+' || curr == '-'  || curr == '/'|| curr == '*' || curr == '='){
                tokens.add(new Token(TokenType.ARITHMETIC_OP,String.valueOf(curr)));

                pos++;
                return new Token(TokenType.ARITHMETIC_OP,String.valueOf(curr));

            }
            if("(),;:[]{}".indexOf(curr) != -1){
                tokens.add(new Token(TokenType.SPECIAL_SYMBOL,String.valueOf(curr)));
                pos++;
                return new Token(TokenType.SPECIAL_SYMBOL,String.valueOf(curr));

            }

            pos++;
        }
        return null;
    }
}
