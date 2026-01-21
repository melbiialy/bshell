package commandexecution;

import builtincommands.CommandRegistry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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


        if (!command.getRedirectTokens().isEmpty()&&command.getReturnCode()!=2) {
            String fileName = command.getRedirectTokens().getFirst();
            Path filePath = BShell.path.getPath().resolve(fileName);
            Files.writeString(filePath, output.output());
            if (!output.error().isEmpty()) {
                System.out.println(output.error().trim());
            }
        }
        else if (command.getReturnCode()==2) {
            String fileName = command.getRedirectTokens().getFirst();
            Path filePath = BShell.path.getPath().resolve(fileName);
            Files.writeString(filePath, output.error());
            if (!output.output().isEmpty()) {
                System.out.println(output.output().trim());
            }

        }
        else if(command.getReturnCode()==3){
            String fileName = command.getRedirectTokens().getFirst();
            Path filePath = BShell.path.getPath().resolve(fileName);
            String content = Files.readString(filePath);
            Files.writeString(filePath, content + output.output());
        }
        else {
            if (!output.output().isEmpty()) {
                System.out.println(output.output().trim());
            }
        }

    }

}
