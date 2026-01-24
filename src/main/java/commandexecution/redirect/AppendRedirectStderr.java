package commandexecution.redirect;

import commandexecution.BShell;
import commandexecution.dto.RunResults;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class AppendRedirectStderr implements Redirect {
    @Override
    public void redirect(RunResults results, String fileName) {
        if (!results.output().isEmpty()){
            System.out.println(results.output());
        }
        String output = results.error();
        if (!results.error().isEmpty()){
            output = "\n" + results.error();
        }
            Path path = BShell.path.getPath().resolve(fileName);
            try {
                Files.writeString(path,output,StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (Exception e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }


    }
}
