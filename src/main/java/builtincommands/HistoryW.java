package builtincommands;

import commandexecution.dto.RunResults;
import history.HistoryManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HistoryW implements BuiltInCommand{
    @Override
    public RunResults operate(String... args) throws IOException, InterruptedException {
        if (args.length <1){
            return new RunResults("", "history: missing operand");
        }
        String filePath = args[0];
        Path path = Path.of(filePath);
        Files.writeString(path, HistoryManager.getHistory(HistoryManager.getHistorySize())
                .stream()
                .map(s -> s.substring(s.indexOf(" ")+2))
                .reduce("",
                        (a, b) -> (a + b) + "\n"));
        HistoryManager.commandCount.put(filePath, HistoryManager.getHistorySize());
        return new RunResults("", "");
    }
}
