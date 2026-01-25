package commandexecution;

import builtincommands.CommandRegistry;
import commandexecution.dto.RunResults;
import commandexecution.dto.Token;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class CommandExecutor {
    private final CommandRunner commandRunner;
    private final RedirectHandler redirectHandler;

    public CommandExecutor() {
        this.commandRunner = new CommandRunner(new CommandRegistry());
        this.redirectHandler = new RedirectHandler();
    }

    public void execute(List<Token> tokens) throws IOException, InterruptedException {
        Command command = redirectHandler.handle(tokens,0);

        RunResults output;
        List<Command> commands = new ArrayList<>();
        commands.add(command);
        while (command.getChild() != null) {

            command = command.getChild();
            commands.add(command);
        }
        if (commands.size() == 1){
            output = commandRunner.run(commands.getFirst().getTokens());
        } else if (commands.size() > 1) {
            output = commandRunner.runResults(commands);
        } else {
            output = commandRunner.run(command.getTokens());
        }

        command.redirect(output);
    }

}
