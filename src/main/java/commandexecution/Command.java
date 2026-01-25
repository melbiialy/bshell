package commandexecution;

import commandexecution.dto.RunResults;
import commandexecution.redirect.Redirect;

import java.util.List;

public class Command {
    private  List<String > tokens;
    private  List<String > redirectTokens;
    private  Redirect redirect;
    private  Command child;

    public Command() {
    }

    public Command(List<String> tokens, List<String> redirectTokens, Redirect redirect, Command child) {
        this.tokens = tokens;
        this.redirectTokens = redirectTokens;
        this.redirect = redirect;
        this.child = child;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void redirect(RunResults results) {
        this.redirect.redirect(results, !redirectTokens.isEmpty() ?redirectTokens.getFirst():"");
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public void setRedirectTokens(List<String> redirectTokens) {
        this.redirectTokens = redirectTokens;
    }

    public void setRedirect(Redirect redirect) {
        this.redirect = redirect;
    }

    public void setChild(Command child) {
        this.child = child;
    }
    public Command getChild() {
        return child;
    }
}
