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

    public RunResults run(List<String> tokens)
            throws IOException, InterruptedException {

        if (CommandRegistry.containsCommand(tokens.getFirst())) {
            String[] args = tokens.stream().skip(1).toArray(String[]::new);
            return commandRegistry
                    .getCommand(tokens.getFirst())
                    .operate(args);
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(tokens);
            pb.directory(BShell.path.getPath().toFile());

            Process process = pb.start();
            process.waitFor();

            String out = new String(
                    process.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            ).trim();

            String err = new String(
                    process.getErrorStream().readAllBytes(),
                    StandardCharsets.UTF_8
            ).trim();

            return new RunResults(out, err);

        } catch (IOException e) {
            throw new CommandNotFound(tokens.getFirst() + ": command not found");
        }
    }

    /* =========================
       Pipeline execution
       ========================= */

    public RunResults runResults(List<Command> commands)
            throws IOException, InterruptedException {

        InputStream currentInput = System.in;
        String lastStdout = "";
        String lastStderr = "";

        List<Command> osSegment = new ArrayList<>();

        for (int i = 0; i < commands.size(); i++) {
            Command cmd = commands.get(i);

            if (!CommandRegistry.containsCommand(cmd.getTokens().getFirst())) {
                osSegment.add(cmd);
                continue;
            }

            // ---- BUILTIN FOUND ----
            // 1) run previous OS segment
            if (!osSegment.isEmpty()) {
                RunResults res = runOsPipeline(osSegment, currentInput);
                lastStdout = res.output();
                lastStderr = res.error();
                currentInput = new ByteArrayInputStream(
                        lastStdout.getBytes(StandardCharsets.UTF_8)
                );
                osSegment.clear();
            }

            // 2) run builtin
            RunResults builtinResult =
                    commandRegistry
                            .getCommand(cmd.getTokens().getFirst())
                            .operate(
                                    cmd.getTokens()
                                            .stream()
                                            .skip(1)
                                            .toArray(String[]::new)
                            );

            lastStdout = builtinResult.output()+"\n";
            lastStderr = builtinResult.error()+"\n";

            currentInput = new ByteArrayInputStream(
                    lastStdout.getBytes(StandardCharsets.UTF_8)
            );
        }

        // ---- FINAL OS SEGMENT ----
        if (!osSegment.isEmpty()) {
            RunResults res = runOsPipeline(osSegment, currentInput);
            lastStdout = res.output();
            lastStderr = res.error();
        }

        return new RunResults(lastStdout, lastStderr);
    }

    /* =========================
       OS-only pipeline runner
       ========================= */

    private RunResults runOsPipeline(
            List<Command> commands,
            InputStream input
    ) throws IOException, InterruptedException {

        List<ProcessBuilder> builders = new ArrayList<>();

        for (Command command : commands) {
            ProcessBuilder pb =
                    new ProcessBuilder(command.getTokens());
            pb.directory(BShell.path.getPath().toFile());
            pb.redirectError(ProcessBuilder.Redirect.PIPE);
            builders.add(pb);
        }

        // input redirection
        builders.getFirst().redirectInput(
                input == System.in
                        ? ProcessBuilder.Redirect.INHERIT
                        : ProcessBuilder.Redirect.PIPE
        );

        // output capture
        builders.getLast().redirectOutput(
                ProcessBuilder.Redirect.PIPE
        );

        List<Process> processes =
                ProcessBuilder.startPipeline(builders);

        Process last = processes.getLast();

        // feed input if coming from builtin
        if (input != System.in) {
            try (OutputStream os = processes.getFirst().getOutputStream()) {
                input.transferTo(os);
            }
        }

        last.waitFor();

        String out = new String(
                last.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        String err = new String(
                last.getErrorStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        return new RunResults(out.trim(), err.trim());
    }
}
