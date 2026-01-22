package commandexecution;

import java.nio.file.Files;
import java.nio.file.Path;

public class RedirectStderr implements Redirect{
    @Override
    public void redirect(RunResults results,String fileName) {
        if (!results.output().isEmpty()){
            System.err.println(results.output());
        }
        if (!results.error().isEmpty()){
            Path path = BShell.path.getPath().resolve(fileName);
            try {
                Files.writeString(path,results.error());
            } catch (Exception e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        }


    }
}
