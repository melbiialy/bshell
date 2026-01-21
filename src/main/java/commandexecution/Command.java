import java.util.List;

public class Command {
    private List<String > tokens;
    private List<String > redirectTokens;

    public Command(List<String> tokens, List<String> redirectTokens) {
        this.tokens = tokens;
        this.redirectTokens = redirectTokens;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public List<String> getRedirectTokens() {
        return redirectTokens;
    }
}
