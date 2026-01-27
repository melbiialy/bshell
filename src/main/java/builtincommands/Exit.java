package builtincommands;

import commandexecution.dto.RunResults;

import java.io.IOException;
import java.io.PipedOutputStream;

public class Exit extends BuiltInCommand{
    @Override
    public void execute(PipedOutputStream outputStream,String... args) throws IOException, InterruptedException {
        if (System.getenv("HISTFILE") != null) {
            new HistoryAppend().execute(new PipedOutputStream(),System.getenv("HISTFILE"));
        }
        System.exit(0);
    }
}
