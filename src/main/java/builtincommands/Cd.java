package builtincommands;

import commandexecution.BShell;
import commandexecution.dto.RunResults;

import java.io.IOException;

public class Cd implements BuiltInCommand {
    @Override
    public RunResults operate(String... args) throws IOException {
        if (args.length < 1) {
            return new RunResults("cd: missing operand","");
        }
        BShell.path.moveTo(args[0]);
        return new RunResults("","");
    }
}
