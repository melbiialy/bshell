package commandexecution;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class AppendRedirectStderr implements Redirect{
    @Override
    public void redirect(RunResults results, String output) {
        if (!results.output().isEmpty()){
            System.out.println(results.output());
        }
        if (!results.error().isEmpty()){
            Path path = BShell.path.getPath().resolve(output);
            try {
                Files.writeString(path,"\n"+results.error(),StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (Exception e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }
        }

    }
}
