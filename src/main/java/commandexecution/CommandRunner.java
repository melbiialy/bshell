package commandexecution;

import builtincommands.CommandRegistry;
import exception.CommandNotFound;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CommandRunner {
    private final CommandRegistry commandRegistry;
    public CommandRunner(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    public RunResults run(List<String > tokens) throws IOException, InterruptedException {
        if (CommandRegistry.containsCommand(tokens.getFirst())) {
            String [] args = tokens.stream().skip(1).toArray(String[]::new);
            return commandRegistry.getCommand(tokens.getFirst()).operate(args);
        }
        try {
            ProcessBuilder pb = new ProcessBuilder(tokens);
            pb.directory(BShell.path.getPath().toFile());

            Process process = pb.start();
            process.waitFor();

            String out = new String(
                    process.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            String err = new String(
                    process.getErrorStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            return new RunResults(out, err);


        }catch (IOException e) {
            throw new CommandNotFound(tokens.getFirst()+ ": command not found");
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
