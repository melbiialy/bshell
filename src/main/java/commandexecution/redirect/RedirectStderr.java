package commandexecution.redirect;

import commandexecution.BShell;
import commandexecution.dto.RunResults;

import java.nio.file.Files;
import java.nio.file.Path;

public class RedirectStderr implements Redirect {
    @Override
    public void redirect(RunResults results, String fileName) {
        if (!results.output().isEmpty()){
            System.err.println(results.output());
        }
        String errorOutput = results.error();
        if (!errorOutput.isEmpty()){
            errorOutput = "\n" + errorOutput;
        }
            Path path = BShell.path.getPath().resolve(fileName);
            try {
                Files.writeString(path,errorOutput);
            } catch (Exception e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }



    }
}
