package commandexecution;

import builtincommands.CommandRegistry;
import commandexecution.dto.RunResults;
import commandexecution.dto.Token;

import java.io.IOException;

import java.util.List;

public class CommandExecutor {
    private final CommandRunner commandRunner;
    private final RedirectHandler redirectHandler;

    public CommandExecutor() {
        this.commandRunner = new CommandRunner(new CommandRegistry());
        this.redirectHandler = new RedirectHandler();
    }

    public void execute(List<Token> tokens) throws IOException, InterruptedException {
        Command command = redirectHandler.handle(tokens);
        RunResults output = commandRunner.run(command.getTokens());
        command.redirect(output);
    }

}
