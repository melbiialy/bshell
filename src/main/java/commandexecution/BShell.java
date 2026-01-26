package commandexecution;



import builtincommands.HistoryR;
import commandexecution.dto.Token;
import commandexecution.lineinputhandler.LineInputHandler;
import commandexecution.parser.Parser;
import history.HistoryManager;
import org.jline.terminal.Terminal;

import java.io.IOException;
import java.util.List;


public class BShell {
    private final BPath path;
    private final Parser parser;
    private final CommandExecutor commandRunner;
    private final LineInputHandler lineInputHandler;
    private final HistoryManager historyManager;

    public BShell(Parser parser, LineInputHandler lineInputHandler) {
        this.lineInputHandler = lineInputHandler;
        path = new BPath();
        this.parser = parser;
        this.commandRunner = new CommandExecutor();
        this.historyManager = HistoryManager.getInstance();
    }

    public void start() throws IOException, InterruptedException {
        banner(lineInputHandler.getTerminal());
        if (System.getenv("HISTFILE") != null) {
            new HistoryR().operate(System.getenv("HISTFILE"));
        }
        while (true) {
            String input = lineInputHandler.handle();
            if (input.isBlank()) continue;
            List<Token> tokens = parser.parse(input);
            historyManager.addCommand(input);
            try {
                commandRunner.execute(tokens);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void banner(Terminal terminal) {
        terminal.writer().println("\033[1;36m" +
                "  ____   _____ _          _ _ \n" +
                " |  _ \\ / ____| |        | | |\n" +
                " | |_) | (___ | |__   ___| | |\n" +
                " |  _ < \\___ \\| '_ \\ / _ \\ | |\n" +
                " | |_) |____) | | | |  __/ | |\n" +
                " |____/|_____/|_| |_|\\___|_|_|\n" +
                "\033[0m");
        terminal.writer().println("\033[1;33mWelcome to BShell v1.0\033[0m");
        terminal.writer().println("\033[0;37mType 'exit' to quit.\033[0m");
        terminal.writer().println();
        terminal.flush();
    }

}



