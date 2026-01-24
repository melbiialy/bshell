package commandexecution.redirect;

import commandexecution.dto.RunResults;

public class DefaultOutputRedirect implements Redirect {
    @Override
    public void redirect(RunResults results, String output) {
        if (!results.output().isEmpty()){
            System.out.println(results.output());
        }
        if (!results.error().isEmpty()){
            System.out.println(results.error());
        }

    }
}
