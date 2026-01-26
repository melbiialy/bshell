package commandexecution;

import commandexecution.dto.Token;
import commandexecution.redirect.*;

import java.util.ArrayList;
import java.util.List;

public class RedirectHandler {

    public Command handle(List<Token> tokens, int index) {
        List<String> executionTokens = new ArrayList<>();
        List<String> redirectTokens = new ArrayList<>();
        boolean flag = false;
        Redirect redirect = null;
        Command command = new Command();

        for (int j = index; j < tokens.size(); j++) {
            Token token = tokens.get(j);

            if (token.getToken().equals("|")) {
                command.setChild(this.handle(tokens, j + 1));
                break;
            }


            if (token.getToken().contains(">>")) {
                flag = true;
                redirect = new AppendRedirectStdout();
                String temp = token.getToken().substring(0, token.getToken().indexOf(">>"));
                if (token.getToken().contains("1>>") || token.getToken().contains("2>>")) {
                    temp = token.getToken().substring(0, token.getToken().indexOf(">>") - 1);
                }
                if (token.getToken().contains("2>>")) redirect = new AppendRedirectStderr();
                temp = temp.trim();
                if (!temp.isEmpty()) {
                    executionTokens.add(temp);
                }
                String fileName = token.getToken().substring(token.getToken().indexOf(">>") + 2);
                fileName = fileName.trim();
                if (fileName.isEmpty()) continue;
                redirectTokens.add(fileName);
                continue;
            }

            if (token.getToken().contains(">")) {
                flag = true;
                redirect = new RedirectStdout();
                String temp = token.getToken().substring(0, token.getToken().indexOf(">"));
                if (token.getToken().contains("1>") || token.getToken().contains("2>")) {
                    temp = token.getToken().substring(0, token.getToken().indexOf(">") - 1);
                }
                if (token.getToken().contains("2>")) redirect = new RedirectStderr();
                temp = temp.trim();
                if (!temp.isEmpty()) {
                    executionTokens.add(temp);
                }
                String fileName = token.getToken().substring(token.getToken().indexOf(">") + 1);
                fileName = fileName.trim();
                if (fileName.isEmpty()) continue;
                redirectTokens.add(fileName);
                continue;
            }

            if (flag) {
                redirectTokens.add(token.getToken());
            } else {
                executionTokens.add(token.getToken());
            }
        }

        if (redirect == null) redirect = new DefaultOutputRedirect();
        command.setRedirect(redirect);
        command.setTokens(executionTokens);
        command.setRedirectTokens(redirectTokens);
        return command;
    }
}