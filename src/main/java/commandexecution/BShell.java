package commandexecution;



import commandexecution.lineinputhandler.LineInputHandler;

import java.util.List;


public class BShell {
    public static BPath path;
    public final CommandParser parser;
    private final CommandExecutor commandRunner;
    private final LineInputHandler lineInputHandler;

    public BShell(CommandParser parser, LineInputHandler lineInputHandler) {
        this.lineInputHandler = lineInputHandler;
        path = new BPath();
        this.parser = parser;
        this.commandRunner = new CommandExecutor();
    }

    public void start() {

        while (true) {
            String input = lineInputHandler.handle();
            if (input.isBlank()) continue;
            List<Token> tokens = parser.parse(input);
            try {
                commandRunner.execute(tokens);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}



