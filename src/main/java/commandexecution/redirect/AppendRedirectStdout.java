package commandexecution.redirect;

import commandexecution.BPath;
import commandexecution.BShell;
import commandexecution.dto.RunResults;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class AppendRedirectStdout implements Redirect {

    @Override
    public void redirect(RunResults results, String fileName) {
        BPath bPath = BPath.getInstance();

            Path path = bPath.getPath().resolve(fileName);
            String output = results.output();
            if (!results.output().isEmpty()){
                output = "\n" + results.output();
            }
            try {
                Files.writeString(path,output,StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (Exception e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }

        if (!results.error().isEmpty()){
            System.out.println(results.error());
        }
    }
}
