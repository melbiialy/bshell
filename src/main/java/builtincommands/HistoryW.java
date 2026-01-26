package builtincommands;

import commandexecution.dto.RunResults;
import history.HistoryManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HistoryW extends BuiltInCommand{
    @Override
    public void execute(String... args) throws IOException, InterruptedException {
        HistoryManager historyManager = HistoryManager.getInstance();
        if (args.length <1){
            this.getErrorStream().write("history: missing operand".getBytes());
        }
        String filePath = args[0];
        Path path = Path.of(filePath);
        Files.writeString(path, historyManager.getHistory(historyManager.getHistorySize())
                .stream()
                .map(s -> s.substring(s.indexOf(" ")+2))
                .reduce("",
                        (a, b) -> (a + b) + "\n"));
        historyManager.updatePublish(filePath, historyManager.getHistorySize());
    }
}
