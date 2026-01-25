package builtincommands;

import commandexecution.dto.RunResults;
import history.HistoryManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HistoryAppend implements BuiltInCommand{
    @Override
    public RunResults operate(String... args) throws IOException, InterruptedException {
        if (args.length < 1) {
            return new RunResults("", "history: missing operand");
        }
        String filePath = args[0];
        int startIndex = 0;
        if (HistoryManager.commandCount.containsKey(filePath)) {
            startIndex = HistoryManager.commandCount.get(filePath);
        }
        Path file = Paths.get(filePath);
        java.nio.file.Files.writeString(file,
                history.HistoryManager.getHistory(history.HistoryManager.getHistorySize()-startIndex)
                        .stream()
                        .map(s -> s.substring(s.indexOf(" ") + 2))
                        .reduce("",
                                (a, b) -> (a + b) + "\n"),
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND);
        HistoryManager.commandCount.put(filePath, HistoryManager.getHistorySize());
        return new RunResults("", "");
    }
}
