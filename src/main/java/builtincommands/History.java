package builtincommands;

import commandexecution.dto.RunResults;
import history.HistoryManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class History implements BuiltInCommand{
    private final Map<String, BuiltInCommand> commands;
    public History() {
        this.commands = new HashMap<>();
        commands.put("-r", new HistoryR());
        commands.put("-w", new HistoryW());
        commands.put("-a", new HistoryAppend());
    }

    @Override
    public RunResults operate(String... args) throws IOException, InterruptedException {
        HistoryManager historyManager = HistoryManager.getInstance();

        int limit = historyManager.getHistorySize();

        if (args.length>1){
            if (commands.containsKey(args[0])){
                return commands.get(args[0]).operate(args[1]);
            } else {
                return new RunResults("", "history: invalid option " + args[0]);
            }
        } else if (args.length==1){
            try {
                limit = Integer.parseInt(args[0]);
                if (limit < 0) {
                    return new RunResults("", "history: invalid number: " + args[0]);
                }
            } catch (NumberFormatException e) {
                return new RunResults("", "history: invalid number: " + args[0]);
            }
        }

        String history = historyManager.getHistory(limit)
                .stream()
                .reduce("",
                        (a, b) -> (a +"    "+ b) + "\n");
        history = history.trim();
        return new RunResults(history, "");
    }
}
