package commandexecution.redirect;

import commandexecution.BShell;
import commandexecution.dto.RunResults;

import java.nio.file.Files;
import java.nio.file.Path;

public class RedirectStdout implements Redirect {
    @Override
    public void redirect(RunResults results, String fileName) {
        String output = results.output();
        if (!output.isEmpty()){
            output = "\n" + output;
        }
            Path path = BShell.path.getPath().resolve(fileName);
            try {
                Files.writeString(path,output);
            } catch (Exception e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }


        if (!results.error().isEmpty()){
            System.out.println(results.error());
        }

    }
}
