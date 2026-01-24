package commandexecution.redirect;

import commandexecution.dto.RunResults;

public interface Redirect {
    void redirect(RunResults results, String output);
}
