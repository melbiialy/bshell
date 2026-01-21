import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CommandExecutor {
    private final CommandRunner commandRunner;
    private final RedirectHandler redirectHandler;

    public CommandExecutor() {
        this.commandRunner = new CommandRunner(CommandRegistry.registerBuiltinCommands());
        this.redirectHandler = new RedirectHandler();
    }

    public void execute(List<Token> tokens) throws IOException {
        Command command = redirectHandler.handle(tokens);
        RunResults output = commandRunner.run(command.getTokens());
        if (!command.getRedirectTokens().isEmpty()) {
            String fileName = command.getRedirectTokens().getFirst();
            Path filePath = BShell.path.getPath().resolve(fileName);
            Files.writeString(filePath, output.output());
            if (!output.error().isEmpty()) {
                System.out.println(output.error().trim());
            }
        }
        else {
            if (!output.output().isEmpty()) {
                System.out.println(output.output().trim());
            }
        }

    }

}
