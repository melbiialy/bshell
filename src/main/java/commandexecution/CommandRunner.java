package commandexecution;

import builtincommands.CommandRegistry;
import commandexecution.dto.RunResults;
import exception.CommandNotFound;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CommandRunner {
    private final CommandRegistry commandRegistry;

    public CommandRunner(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    /* =========================
       Single command execution
       ========================= */
    public RunResults run(List<String> tokens) throws IOException, InterruptedException {
        if (CommandRegistry.containsCommand(tokens.get(0))) {
            String[] args = tokens.stream().skip(1).toArray(String[]::new);
            return commandRegistry.getCommand(tokens.get(0)).operate(args);
        }

        try {
            boolean flag = tokens.stream().anyMatch(s -> s.contains("-f"));
            ProcessBuilder pb = new ProcessBuilder(tokens);
            pb.directory(BShell.path.getPath().toFile());
            Process process = pb.start();

            if (flag) {
                Thread thread = new Thread(() -> {
                    try {
                        process.getInputStream().transferTo(System.out);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                thread.start();
                thread.join();
                return new RunResults("", "");
            }

            process.waitFor();

            String out = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            String err = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8).trim();

            return new RunResults(out, err);

        } catch (IOException e) {
            throw new CommandNotFound(tokens.get(0) + ": command not found");
        }
    }

    /* =========================
       Pipeline execution with builtin support
       ========================= */
    public RunResults runResults(List<Command> commands)
            throws IOException, InterruptedException {

        // First, check if there is any builtin in the pipeline
        boolean hasBuiltin = commands.stream()
                .anyMatch(cmd -> CommandRegistry.containsCommand(cmd.getTokens().get(0)));

        if (!hasBuiltin) {
            // No builtins: use your original OS-only pipeline logic
            List<ProcessBuilder> builders = new ArrayList<>();
            for (Command command : commands) {
                ProcessBuilder pb = new ProcessBuilder(command.getTokens());
                pb.directory(BShell.path.getPath().toFile());
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                builders.add(pb);
            }

            builders.get(0).redirectInput(ProcessBuilder.Redirect.INHERIT);
            builders.get(builders.size() - 1).redirectOutput(ProcessBuilder.Redirect.INHERIT);

            List<Process> processes = ProcessBuilder.startPipeline(builders);
            processes.get(processes.size() - 1).waitFor();

            return new RunResults("", "");
        }

        // --------------------------
        // Pipeline contains builtins
        // --------------------------
        InputStream currentInput = System.in;
        String lastStdout = "";
        String lastStderr = "";

        List<Command> osSegment = new ArrayList<>();

        for (Command cmd : commands) {
            boolean isBuiltin = CommandRegistry.containsCommand(cmd.getTokens().get(0));

            if (!isBuiltin) {
                osSegment.add(cmd);
                continue;
            }

            // ---- BUILTIN FOUND ----
            // Run previous OS segment if there is one
            if (!osSegment.isEmpty()) {
                RunResults res = runOsSegment(osSegment, currentInput);
                lastStdout = res.output();
                lastStderr = res.error();

                // Feed output to builtin
                currentInput = new ByteArrayInputStream(lastStdout.getBytes(StandardCharsets.UTF_8));
                osSegment.clear();
            }

            // Run builtin
            RunResults builtinResult = commandRegistry
                    .getCommand(cmd.getTokens().get(0))
                    .operate(cmd.getTokens().stream().skip(1).toArray(String[]::new));

            lastStdout = builtinResult.output();
            lastStderr = builtinResult.error();

            if (!lastStdout.endsWith("\n")) lastStdout += "\n";

            // Feed builtin output to next OS segment
            currentInput = new ByteArrayInputStream(lastStdout.getBytes(StandardCharsets.UTF_8));
        }

        // Final OS segment after the last builtin
        if (!osSegment.isEmpty()) {
            RunResults res = runOsSegment(osSegment, currentInput);
            lastStdout = res.output();
            lastStderr = res.error();
        }

        // Strip trailing newlines
        while (lastStdout.endsWith("\n")) lastStdout = lastStdout.substring(0, lastStdout.length() - 1);
        while (lastStderr.endsWith("\n")) lastStderr = lastStderr.substring(0, lastStderr.length() - 1);

        return new RunResults(lastStdout, lastStderr);
    }

    /* =========================
       OS segment runner using your original logic
       ========================= */
    private RunResults runOsSegment(List<Command> commands, InputStream input)
            throws IOException, InterruptedException {

        List<ProcessBuilder> builders = new ArrayList<>();
        for (Command command : commands) {
            ProcessBuilder pb = new ProcessBuilder(command.getTokens());
            pb.directory(BShell.path.getPath().toFile());
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            builders.add(pb);
        }

        builders.get(0).redirectInput(input == System.in ? ProcessBuilder.Redirect.INHERIT : ProcessBuilder.Redirect.PIPE);
        builders.get(builders.size() - 1).redirectOutput(ProcessBuilder.Redirect.INHERIT);

        List<Process> processes = ProcessBuilder.startPipeline(builders);

        // Feed input if coming from builtin
        if (input != System.in) {
            try (var os = processes.get(0).getOutputStream()) {
                input.transferTo(os);
            }
        }

        processes.get(processes.size() - 1).waitFor();

        return new RunResults("", "");
    }
}
