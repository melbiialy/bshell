package builtincommands;


import commandexecution.BShell;
import commandexecution.dto.RunResults;

import java.io.IOException;

public class Pwd implements BuiltInCommand {
    @Override
    public RunResults operate(String... args) throws IOException {
        return new RunResults(BShell.path.getPath().toString(), "");
    }
}
