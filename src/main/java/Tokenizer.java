import javax.swing.plaf.IconUIResource;
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
            if (c==' '&& temp.isEmpty()) continue;
            if (c == '\''&&!inDoubleQuotes) {
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
            token = token.substring(1);
            token = token.substring(0, token.length()-1);
        }else if (token.charAt(0) == '"'){
            tokenObj.setQuoted(false);
            tokenObj.setDoubleQuoted(true);
            token = token.substring(1);
            token = token.substring(0, token.length()-1);
        }
        else {
            tokenObj.setQuoted(false);
            tokenObj.setDoubleQuoted(false);
            token = token.replaceAll("\"", "");

        }

        tokenObj.setToken(token);
        return tokenObj;
    }
}
