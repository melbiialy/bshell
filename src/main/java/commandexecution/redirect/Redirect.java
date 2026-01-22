package commandexecution.redirect;

import commandexecution.RunResults;

public interface Redirect {
    void redirect(RunResults results, String output);
}
