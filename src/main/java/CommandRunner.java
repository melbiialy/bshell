import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CommandRunner {
    private final CommandRegistry commandRegistry;
    public CommandRunner(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    public void run(List<Token> tokens) throws IOException {
        if (commandRegistry.contains(tokens.getFirst().getToken())) {
            String [] args = tokens.stream().skip(1).map(Token::getToken).toArray(String[]::new);
            commandRegistry.getCommand(tokens.getFirst().getToken()).execute(args);
            return;
        }
        try {
            ProcessBuilder pb = new ProcessBuilder(tokens.stream().map(Token::getToken).toArray(String[]::new));
            pb.directory(BShell.path.getPath().toFile());
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) throw new IOException("command failed with exit code " + exitCode);

        }catch (IOException e){
            throw new IOException("command not found");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
