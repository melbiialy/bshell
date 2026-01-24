package builtincommands;

import commandexecution.dto.RunResults;
import history.HistoryManager;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class History implements BuiltInCommand{

    @Override
    public RunResults operate(String... args) throws IOException, InterruptedException {
        AtomicInteger counter = new AtomicInteger(1);
        int limit = 0;
        if (args.length > 0) {
            limit = Integer.parseInt(args[0]);
        }
        String history = HistoryManager.getHistory(limit)
                .stream()
                .reduce("",
                        (a, b) -> (a +"    "+ b) + "\n");
        history = history.trim();
        return new RunResults(history, "");
    }
}
