package builtincommands;


import commandexecution.BPath;
import commandexecution.BShell;
import commandexecution.dto.RunResults;

import java.io.IOException;

public class Pwd implements BuiltInCommand {
    @Override
    public RunResults operate(String... args) throws IOException {
        BPath path = BPath.getInstance();
        return new RunResults(path.getPath().toString(), "");
    }
}
