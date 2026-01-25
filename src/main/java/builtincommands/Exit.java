package builtincommands;

import commandexecution.dto.RunResults;

import java.io.IOException;

public class Exit implements BuiltInCommand{
    @Override
    public RunResults operate(String... args) throws IOException, InterruptedException {
        if (System.getenv("HISTFILE") != null) {
            new HistoryAppend().operate(System.getenv("HISTFILE"));
        }
        System.exit(0);
        return null;
    }
}
