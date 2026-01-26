package builtincommands;

import commandexecution.dto.RunResults;

import java.io.IOException;

public class Exit extends BuiltInCommand{
    @Override
    public void execute(String... args) throws IOException, InterruptedException {
        if (System.getenv("HISTFILE") != null) {
            new HistoryAppend().execute(System.getenv("HISTFILE"));
        }
        System.exit(0);
    }
}
