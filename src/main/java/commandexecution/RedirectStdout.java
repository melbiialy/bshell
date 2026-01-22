package commandexecution;

import java.nio.file.Files;
import java.nio.file.Path;

public class RedirectStdout implements Redirect{
    @Override
    public void redirect(RunResults results, String fileName) {
        if (!results.output().isEmpty()){
            Path path = BShell.path.getPath().resolve(fileName);
            try {
                Files.writeString(path,results.output());
            } catch (Exception e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }

        }
        if (!results.error().isEmpty()){
            System.err.println(results.error());
        }

    }
}
