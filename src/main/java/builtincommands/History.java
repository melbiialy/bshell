package builtincommands;

import commandexecution.dto.RunResults;
import history.HistoryManager;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class History implements BuiltInCommand{

    @Override
    public RunResults operate(String... args) throws IOException, InterruptedException {
        AtomicInteger counter = new AtomicInteger(1);
        String history = HistoryManager.getHistory()
                .stream()
                .reduce("",
                        (a, b) -> (a +counter.getAndIncrement()+" "+ b) + "\n");
        history = history.trim();
        return new RunResults(history, "");
    }
}
