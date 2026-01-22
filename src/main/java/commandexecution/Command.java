package commandexecution;

import java.util.List;

public class Command {
    private List<String > tokens;
    private List<String > redirectTokens;
    private Redirect redirect;

    public Command(List<String> tokens, List<String> redirectTokens, Redirect redirect) {
        this.tokens = tokens;
        this.redirectTokens = redirectTokens;
        this.redirect = redirect;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public List<String> getRedirectTokens() {
        return redirectTokens;
    }
    public void setRedirect(Redirect redirect) {
        this.redirect = redirect;
    }
    public void redirect(RunResults results) {
        this.redirect.redirect(results, !redirectTokens.isEmpty() ?redirectTokens.getFirst():"");
    }


}
