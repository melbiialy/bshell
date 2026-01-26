package commandexecution;

import builtincommands.CommandRegistry;
import commandexecution.dto.RunResults;
import commandexecution.dto.Token;

import java.io.IOException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CommandExecutor {
    private final CommandRunner commandRunner;
    private final RedirectHandler redirectHandler;

    public CommandExecutor() {
        this.commandRunner = new CommandRunner(new CommandRegistry());
        this.redirectHandler = new RedirectHandler();
    }

    public void execute(List<Token> tokens) throws IOException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Command command = redirectHandler.handle(tokens,0);

        RunResults output = null;
        List<Command> commands = new ArrayList<>();
        commands.add(command);
        while (command.getChild() != null) {

            command = command.getChild();
            commands.add(command);
        }
        output = commandRunner.run(commands);

        command.redirect(output);
    }

}
