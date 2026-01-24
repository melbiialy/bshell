package commandexecution;

import commandexecution.dto.RunResults;
import commandexecution.redirect.Redirect;

import java.util.List;

public class Command {
    private final List<String > tokens;
    private final List<String > redirectTokens;
    private final Redirect redirect;

    public Command(List<String> tokens, List<String> redirectTokens, Redirect redirect) {
        this.tokens = tokens;
        this.redirectTokens = redirectTokens;
        this.redirect = redirect;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void redirect(RunResults results) {
        this.redirect.redirect(results, !redirectTokens.isEmpty() ?redirectTokens.getFirst():"");
    }


}
