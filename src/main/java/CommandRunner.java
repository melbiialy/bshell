import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CommandRunner {
    private final CommandRegistry commandRegistry;
    public CommandRunner(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    public String run(List<String > tokens) throws IOException {
        if (commandRegistry.contains(tokens.getFirst())) {
            String [] args = tokens.stream().skip(1).toArray(String[]::new);
            return commandRegistry.getCommand(tokens.getFirst()).execute(args);
        }
        try {
            ProcessBuilder pb = new ProcessBuilder(tokens.toArray(String[]::new));
            pb.directory(BShell.path.getPath().toFile());
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
            return Arrays.toString(process.getInputStream().readAllBytes());


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
