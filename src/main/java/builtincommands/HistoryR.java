package builtincommands;

import commandexecution.BPath;
import commandexecution.BShell;
import commandexecution.dto.RunResults;
import history.HistoryManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class HistoryR extends BuiltInCommand{

    @Override
    public void execute(PipedOutputStream outputStream,String... args) throws IOException, InterruptedException {
        HistoryManager historyManager = HistoryManager.getInstance();
        BPath path = BPath.getInstance();
        if (args.length < 1) {
            this.getErrorStream().write("history: missing operand".getBytes());
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
        reader.close();
    }
}
