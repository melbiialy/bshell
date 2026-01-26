package commandexecution.redirect;

import commandexecution.BPath;
import commandexecution.BShell;
import commandexecution.dto.RunResults;

import java.nio.file.Files;
import java.nio.file.Path;

public class RedirectStderr implements Redirect {
    @Override
    public void redirect(RunResults results, String fileName) {
        BPath bPath = BPath.getInstance();
        if (!results.output().isEmpty()){
            System.err.println(results.output());
        }
        String errorOutput = results.error();
        if (!errorOutput.isEmpty()){
            errorOutput = "\n" + errorOutput;
        }
            Path path = bPath.getPath().resolve(fileName);
            try {
                Files.writeString(path,errorOutput);
            } catch (Exception e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }



    }
}
