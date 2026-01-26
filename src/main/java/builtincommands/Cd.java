package builtincommands;

import commandexecution.BPath;
import commandexecution.BShell;
import commandexecution.dto.RunResults;

import java.io.IOException;
import java.util.Arrays;

public class Cd extends BuiltInCommand {

    @Override
    public void execute(String... args) throws IOException, InterruptedException {
        BPath path = BPath.getInstance();
        String targetPath;
        if (args.length == 0) {
            targetPath = this.getInputStream().toString();
            path.moveTo(targetPath);
        }
        if (args.length == 1) {
            targetPath = args[0];
            path.moveTo(targetPath);
        } else {
            String errorMessage = "cd: too many arguments";
            this.getErrorStream().write(errorMessage.getBytes());
        }

    }
}
