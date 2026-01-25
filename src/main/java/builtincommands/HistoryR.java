package builtincommands;

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
        if (args.length < 1) {
            return new RunResults("", "history: missing operand");
        }
        Path filePath = BShell.path.getPath().resolve(args[0]);
        String line;
        BufferedReader reader = Files.newBufferedReader(filePath);
        int limit = 0;
        while ((line = reader.readLine())!=null){
            HistoryManager.add(line);
            limit++;
        }
        HistoryManager.commandCount.put(args[0], limit);
        return new RunResults("", "");
    }
}
