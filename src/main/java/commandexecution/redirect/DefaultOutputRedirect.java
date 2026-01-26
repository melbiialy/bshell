package commandexecution.redirect;

import commandexecution.dto.RunResults;

public class DefaultOutputRedirect implements Redirect {
    @Override
    public void redirect(RunResults results, String output) {
        if (!results.output().isEmpty()){
           String outputStr = results.output();
           while (outputStr.endsWith("\n")){
               outputStr = outputStr.substring(0,outputStr.length()-1);
           }
            System.out.println(outputStr);
        }
        if (!results.error().isEmpty()){
            String errorStr = results.error();
            while (errorStr.endsWith("\n")){
                errorStr = errorStr.substring(0,errorStr.length()-1);
            }
            System.out.println(errorStr);
        }

    }
}
