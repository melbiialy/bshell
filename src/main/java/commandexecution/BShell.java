package commandexecution;



import commandexecution.dto.Token;
import commandexecution.lineinputhandler.LineInputHandler;
import history.HistoryManager;

import java.util.List;


public class BShell {
    public static BPath path;
    public final CommandParser parser;
    private final CommandExecutor commandRunner;
    private final LineInputHandler lineInputHandler;
    private final HistoryManager historyManager;

    public BShell(CommandParser parser, LineInputHandler lineInputHandler) {
        this.lineInputHandler = lineInputHandler;
        path = new BPath();
        this.parser = parser;
        this.commandRunner = new CommandExecutor();
        this.historyManager = new HistoryManager();
    }

    public void start() {

        while (true) {
            String input = lineInputHandler.handle();
            if (input.isBlank()) continue;
            List<Token> tokens = parser.parse(input);
            historyManager.add(input);
            try {
                commandRunner.execute(tokens);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}



