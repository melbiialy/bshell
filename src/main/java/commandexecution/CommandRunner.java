package commandexecution;

import builtincommands.CommandRegistry;
import commandexecution.dto.RunResults;
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

            String out = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String err = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

            if (err.isEmpty()) err = "";
            else {
                err = err.trim();

            }
            if (out.isEmpty()) out = "";
            else {
                out = out.trim();

            }


            return new RunResults(out, err);

        }catch (IOException e) {
            throw new CommandNotFound(tokens.getFirst()+ ": command not found");
        }
    }


    public RunResults runWithInput(List<String> tokens, RunResults output) {
        if (CommandRegistry.containsCommand(tokens.getFirst())) {
            String[] args = tokens.stream().skip(1).toArray(String[]::new);
            try {
                return commandRegistry.getCommand(tokens.getFirst()).operate(args);
            } catch (IOException | InterruptedException e) {
                return new RunResults("", e.getMessage());
            }
        }
        try {
            ProcessBuilder pb = new ProcessBuilder(tokens);
            pb.directory(BShell.path.getPath().toFile());
            Process process = pb.start();

            if (!output.output().isEmpty()) {
                process.getOutputStream().write(output.output().getBytes(StandardCharsets.UTF_8));
            }
            process.getOutputStream().close();

            process.waitFor();

            String out = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String err = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

            if (err.isEmpty()) err = "";
            else {
                err = err.trim();

            }
            if (out.isEmpty()) out = "";
            else {
                out = out.trim();

            }
            return new RunResults(out, err);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    }