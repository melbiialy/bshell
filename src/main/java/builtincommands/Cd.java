package builtincommands;

import commandexecution.BPath;
import commandexecution.BShell;
import commandexecution.dto.RunResults;

import java.io.IOException;

public class Cd implements BuiltInCommand {
    @Override
    public RunResults operate(String... args) throws IOException {
        BPath path = BPath.getInstance();
        if (args.length < 1) {
            return new RunResults("cd: missing operand","");
        }
        path.moveTo(args[0]);
        return new RunResults("","");
    }
}
