import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    public static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        boolean inQuotes = false;
        boolean inDoubleQuotes = false;
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '\'') {
                inQuotes = !inQuotes;
            }
            if (c == '"') {
                inDoubleQuotes = !inDoubleQuotes;
            }
            if (c == ' ' && !(inQuotes || inDoubleQuotes)){
                Token tokenObj = buildToken(temp);
                tokens.add(tokenObj);
              temp = new StringBuilder();
            }else {temp.append(c);}

        }
        if (!temp.isEmpty()){
            Token tokenObj = buildToken(temp);
            tokens.add(tokenObj);
        }
        return tokens;
    }

    private static Token buildToken(StringBuilder temp) {
        String token = temp.toString();
        Token tokenObj = new Token();
        if (token.charAt(0) == '\''){
            tokenObj.setQuoted(true);
            tokenObj.setDoubleQuoted(false);
        }else if (token.charAt(0) == '"'){
            tokenObj.setQuoted(false);
            tokenObj.setDoubleQuoted(true);
        }
        else {
            tokenObj.setQuoted(false);
            tokenObj.setDoubleQuoted(false);
        }
        token = token.replaceAll("'", "");
        token = token.replaceAll("\"", "");
        tokenObj.setToken(token);
        return tokenObj;
    }
}
