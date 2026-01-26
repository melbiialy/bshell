package builtincommands;

import commandexecution.BPath;
import commandexecution.BShell;
import commandexecution.dto.RunResults;
import history.HistoryManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HistoryR implements BuiltInCommand{

    @Override
    public RunResults operate(String... args) throws IOException, InterruptedException {
        HistoryManager historyManager = HistoryManager.getInstance();
        BPath path = BPath.getInstance();
        if (args.length < 1) {
            return new RunResults("", "history: missing operand");
        }
        Path filePath = path.getPath().resolve(args[0]);
        String line;
        BufferedReader reader = Files.newBufferedReader(filePath);
        int limit = 0;
        while ((line = reader.readLine())!=null){
            historyManager.addCommand(line);
            limit++;
        }
        historyManager.updatePublish(args[0], limit);
        return new RunResults("", "");
    }
}
