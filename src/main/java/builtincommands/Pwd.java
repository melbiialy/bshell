package builtincommands;


import commandexecution.BPath;
import commandexecution.BShell;
import commandexecution.dto.RunResults;

import java.io.IOException;
import java.io.PipedOutputStream;

public class Pwd extends BuiltInCommand {
    @Override
    public void execute(PipedOutputStream outputStream,String... args) throws IOException {
        BPath path = BPath.getInstance();
        outputStream.write(path.getPath().toString().getBytes());
    }
}
