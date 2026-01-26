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


public class History extends BuiltInCommand{
    private final Map<String, BuiltInCommand> commands;
    public History() {
        this.commands = new HashMap<>();
        commands.put("-r", new HistoryR());
        commands.put("-w", new HistoryW());
        commands.put("-a", new HistoryAppend());
    }

    @Override
    public void execute(String... args) throws IOException, InterruptedException {
        HistoryManager historyManager = HistoryManager.getInstance();

        int limit = historyManager.getHistorySize();

        if (args.length>1){
            if (commands.containsKey(args[0])){
                 commands.get(args[0]).execute(args[1]);
            } else {
                this.getErrorStream().write(("history: unknown option: " + args[0]).getBytes());
            }
        } else if (args.length==1){
            try {
                limit = Integer.parseInt(args[0]);
                if (limit < 0) {
                    this.getErrorStream().write(("history: invalid number: " + args[0]).getBytes());
                }
            } catch (NumberFormatException e) {
                this.getErrorStream().write(("history: invalid number: " + args[0]).getBytes());
            }
        }

        String history = historyManager.getHistory(limit)
                .stream()
                .reduce("",
                        (a, b) -> (a +"    "+ b) + "\n");
        history = history.trim();
        this.getOutputStream().write(history.getBytes());
    }
}
