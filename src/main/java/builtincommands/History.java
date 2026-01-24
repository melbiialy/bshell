package builtincommands;

import commandexecution.dto.RunResults;
import history.HistoryManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;


public class History implements BuiltInCommand{

    @Override
    public RunResults operate(String... args) throws IOException, InterruptedException {
        AtomicInteger counter = new AtomicInteger(1);
        int limit = HistoryManager.getHistorySize();
        String filePath;
        if (args.length > 0) {
            if (args[0].equals("-r")){
                filePath = args[1];
                File file = new File(filePath);
                BufferedReader br = java.nio.file.Files.newBufferedReader(Path.of(file.getAbsolutePath()));
                String line;
                while ((line = br.readLine()) != null) {
                    HistoryManager.add(line);
                }
                return new RunResults("", "");
            }
            else {
                limit = Integer.parseInt(args[0]);
            }
        }
        String history = HistoryManager.getHistory(limit)
                .stream()
                .reduce("",
                        (a, b) -> (a +"    "+ b) + "\n");
        history = history.trim();
        return new RunResults(history, "");
    }
}
