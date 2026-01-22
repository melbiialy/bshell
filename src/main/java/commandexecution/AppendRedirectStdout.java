package commandexecution;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class AppendRedirectStdout implements Redirect{

    @Override
    public void redirect(RunResults results, String fileName) {

            Path path = BShell.path.getPath().resolve(fileName);
            try {
                Files.writeString(path,"\n"+results.output(),StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (Exception e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }

        if (!results.error().isEmpty()){
            System.out.println(results.error());
        }
    }
}
