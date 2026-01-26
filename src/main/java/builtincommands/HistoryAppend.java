package builtincommands;

import commandexecution.dto.RunResults;
import history.HistoryManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HistoryAppend extends BuiltInCommand{
    @Override
    public void execute(String... args) throws IOException, InterruptedException {
        HistoryManager historyManager = HistoryManager.getInstance();
        if (args.length < 1) {
            this.getErrorStream().write("history: missing operand".getBytes());
        }
        String filePath = args[0];
        int startIndex = 0;
        if (historyManager.isPublished(filePath)) {
            startIndex = historyManager.getPublishedNumber(filePath);
        }
        Path file = Paths.get(filePath);
        Files.writeString(file,
              historyManager.getHistory(historyManager.getHistorySize()-startIndex)
                        .stream()
                        .map(s -> s.substring(s.indexOf(" ") + 2))
                        .reduce("",
                                (a, b) -> (a + b) + "\n"),
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND);
        historyManager.updatePublish(filePath, historyManager.getHistorySize());
    }
}
