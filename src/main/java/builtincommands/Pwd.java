package builtincommands;


import commandexecution.BPath;
import commandexecution.BShell;
import commandexecution.dto.RunResults;

import java.io.IOException;

public class Pwd extends BuiltInCommand {
    @Override
    public void execute(String... args) throws IOException {
        BPath path = BPath.getInstance();
        this.getOutputStream().write((path.getPath().toString()+"\n").getBytes());
    }
}
