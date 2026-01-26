package commandexecution;

import builtincommands.CommandRegistry;
import commandexecution.dto.RunResults;
import exception.CommandNotFound;

import java.io.*;
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
        if (CommandRegistry.containsCommand(tokens.getFirst())) {
            String[] args = tokens.stream().skip(1).toArray(String[]::new);
            return commandRegistry.getCommand(tokens.getFirst()).operate(args);
        }

        try {
            boolean flag = false;
            for (String s : tokens) {
                if (s.contains("-f")) {
                    flag = true;
                }
            }

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
            throw new CommandNotFound(tokens.getFirst() + ": command not found");
        }
    }

    /* =========================
       Pipeline execution with builtin support
       ========================= */
    public RunResults runResults(List<Command> commands) throws IOException, InterruptedException {

        InputStream currentInput = System.in;
        String lastStdout = "";
        String lastStderr = "";

        List<Command> osSegment = new ArrayList<>();

        for (Command cmd : commands) {
            boolean isBuiltin = CommandRegistry.containsCommand(cmd.getTokens().getFirst());

            if (!isBuiltin) {
                osSegment.add(cmd);
                continue;
            }

            // ---- BUILTIN FOUND ----
            // 1) Run previous OS segment if there is one
            if (!osSegment.isEmpty()) {
                RunResults res = runOsPipeline(osSegment, currentInput);
                lastStdout = res.output();
                lastStderr = res.error();

                // Feed output to builtin
                currentInput = new ByteArrayInputStream(lastStdout.getBytes(StandardCharsets.UTF_8));
                osSegment.clear();
            }

            // 2) Run builtin
            RunResults builtinResult = commandRegistry
                    .getCommand(cmd.getTokens().getFirst())
                    .operate(cmd.getTokens().stream().skip(1).toArray(String[]::new));

            lastStdout = builtinResult.output();
            lastStderr = builtinResult.error();

            // Ensure newline at the end so downstream OS commands like 'wc' work correctly
            if (!lastStdout.endsWith("\n")) {
                lastStdout += "\n";
            }

            // Feed builtin output to next OS segment
            currentInput = new ByteArrayInputStream(lastStdout.getBytes(StandardCharsets.UTF_8));
        }

        // ---- FINAL OS SEGMENT ----
        if (!osSegment.isEmpty()) {
            RunResults res = runOsPipeline(osSegment, currentInput);
            lastStdout = res.output();
            lastStderr = res.error();
        }

        // ---- strip trailing newlines ----
        while (lastStdout.endsWith("\n")) lastStdout = lastStdout.substring(0, lastStdout.length() - 1);
        while (lastStderr.endsWith("\n")) lastStderr = lastStderr.substring(0, lastStderr.length() - 1);

        return new RunResults(lastStdout, lastStderr);
    }

    /* =========================
       OS-only pipeline runner (original logic)
       ========================= */
    private RunResults runOsPipeline(List<Command> commands, InputStream input) throws IOException, InterruptedException {

        List<ProcessBuilder> builders = new ArrayList<>();
        for (Command command : commands) {
            ProcessBuilder pb = new ProcessBuilder(command.getTokens());
            pb.directory(BShell.path.getPath().toFile());
            pb.redirectError(ProcessBuilder.Redirect.PIPE);
            builders.add(pb);
        }

        // input redirection
        builders.get(0).redirectInput(input == System.in ? ProcessBuilder.Redirect.INHERIT : ProcessBuilder.Redirect.PIPE);

        // output capture
        builders.get(builders.size() - 1).redirectOutput(ProcessBuilder.Redirect.PIPE);

        List<Process> processes = ProcessBuilder.startPipeline(builders);
        Process last = processes.get(processes.size() - 1);

        // feed input if coming from builtin
        if (input != System.in) {
            try (OutputStream os = processes.get(0).getOutputStream()) {
                input.transferTo(os);
            }
        }

        last.waitFor();

        String out = new String(last.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String err = new String(last.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

        while (out.endsWith("\n")) out = out.substring(0, out.length() - 1);
        while (err.endsWith("\n")) err = err.substring(0, err.length() - 1);

        return new RunResults(out, err);
    }
}
