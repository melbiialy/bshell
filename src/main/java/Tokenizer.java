import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    public static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;
        boolean escaped = false;
        StringBuilder temp = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '\\' && !escaped) {
                escaped = true;
                continue;
            }

            if (!escaped) {
                if (c == '\'' && !inDoubleQuotes) {
                    inSingleQuotes = !inSingleQuotes;
                    temp.append(c);
                    continue;
                }

                if (c == '"' && !inSingleQuotes) {
                    inDoubleQuotes = !inDoubleQuotes;
                    temp.append(c);
                    continue;
                }

                if (c == ' ' && !inSingleQuotes && !inDoubleQuotes) {
                    if (!temp.isEmpty()) {
                        tokens.add(buildToken(temp.toString()));
                        temp = new StringBuilder();
                    }
                    continue;
                }
            }


            temp.append(c);
            escaped = false;
        }


        if (!temp.isEmpty()) {
            tokens.add(buildToken(temp.toString()));
        }

        return tokens;
    }

    private static Token buildToken(String tokenStr) {
        Token token = new Token();
        String value = tokenStr;

        if (value.startsWith("'") && value.endsWith("'") && value.length() > 1) {
            token.setQuoted(true);
            token.setDoubleQuoted(false);
            value = value.substring(1, value.length() - 1);

        } else if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
            token.setQuoted(false);
            token.setDoubleQuoted(true);
            value = value.substring(1, value.length() - 1);

        } else {
            token.setQuoted(false);
            token.setDoubleQuoted(false);

        }

        token.setToken(value);
        return token;
    }
}