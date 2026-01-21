package commandexecution;

import java.util.ArrayList;
import java.util.List;

public class RedirectHandler {

    public Command handle(List<Token> tokens) {
        List<String > executionTokens = new ArrayList<>();
        List<String > redirectTokens = new ArrayList<>();
        boolean flag = false;
        for (Token token : tokens) {
            if (token.getToken().contains("1>")){
                flag = true;
                String temp = token.getToken().substring(0, token.getToken().indexOf("1>"));
                temp = temp.trim();
                if (!temp.isEmpty()) {
                    executionTokens.add(temp);
                }
                String fileName = token.getToken().substring(token.getToken().indexOf("1>")+2);
                fileName = fileName.trim();
                if (fileName.isEmpty()) continue;
                redirectTokens.add(fileName);
                continue;
            }
            else if (token.getToken().contains(">")) {
                flag = true;
                String temp = token.getToken().substring(0, token.getToken().indexOf(">"));
                temp = temp.trim();
                if (!temp.isEmpty()) {
                    executionTokens.add(temp);
                }
                String fileName = token.getToken().substring(token.getToken().indexOf(">")+1);
                fileName = fileName.trim();
                if (fileName.isEmpty()) continue;
                redirectTokens.add(fileName);
                continue;
            }
            if (flag) redirectTokens.add(token.getToken());
            else{
            executionTokens.add(token.getToken());}
        }
        return new Command(executionTokens,redirectTokens);
    }
}
