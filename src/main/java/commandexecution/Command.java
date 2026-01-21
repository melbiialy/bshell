package commandexecution;

import java.util.List;

public class Command {
    private List<String > tokens;
    private List<String > redirectTokens;
    private int returnCode;

    public Command(List<String> tokens, List<String> redirectTokens, int returnCode) {
        this.tokens = tokens;
        this.redirectTokens = redirectTokens;
        this.returnCode = returnCode;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public List<String> getRedirectTokens() {
        return redirectTokens;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
}
