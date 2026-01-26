package commandexecution;

import builtincommands.CommandRegistry;
import commandexecution.dto.RunResults;
import exception.CommandNotFound;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class CommandRunner {
    private final CommandRegistry commandRegistry;

    public CommandRunner(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

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

        } catch (IOException e) {
            throw new CommandNotFound(tokens.getFirst() + ": command not found");
        }
    }

    public RunResults runResults(List<Command> commands)
            throws IOException, InterruptedException {
        boolean flag = false;
        for (Command command : commands) {
            if (CommandRegistry.containsCommand(command.getTokens().getFirst())) {
                flag = true;
                break;
            }

        }
        if (flag) return runIt(commands);

        List<ProcessBuilder> builders = new ArrayList<>();

        for (Command command : commands) {
            ProcessBuilder pb = new ProcessBuilder(command.getTokens());
            pb.directory(BShell.path.getPath().toFile());
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            builders.add(pb);
        }

        builders.getFirst().redirectInput(ProcessBuilder.Redirect.INHERIT);
        builders.getLast().redirectOutput(ProcessBuilder.Redirect.INHERIT);

        List<Process> processes = ProcessBuilder.startPipeline(builders);
        processes.getLast().waitFor();

        return new RunResults("", "");
    }
    private RunResults runIt(List<Command> commands) throws IOException, InterruptedException {
        InputStream currentInput = System.in;
        String lastStdout = "";
        String lastStderr = "";
        List<Command> osSegment = new ArrayList<>();

        for (int i = 0; i < commands.size(); i++) {
            Command cmd = commands.get(i);

            if (!CommandRegistry.containsCommand(cmd.getTokens().get(0))) {
                osSegment.add(cmd);
                continue;
            }

            // ---- BUILTIN FOUND ----
            // 1) run previous OS segment if any
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
            RunResults builtinResult = commandRegistry
                    .getCommand(cmd.getTokens().get(0))
                    .operate(
                            cmd.getTokens()
                                    .stream()
                                    .skip(1)
                                    .toArray(String[]::new)
                    );

            lastStdout = builtinResult.output() + "\n";
            lastStderr = builtinResult.error() + "\n";

            // feed builtin output to next OS segment
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

        // remove trailing newlines
        while (lastStdout.endsWith("\n")) lastStdout = lastStdout.substring(0, lastStdout.length() - 1);
        while (lastStderr.endsWith("\n")) lastStderr = lastStderr.substring(0, lastStderr.length() - 1);

        return new RunResults(lastStdout, lastStderr);
    }

    /* =========================
       OS-only pipeline runner
       ========================= */
    private RunResults runOsPipeline(List<Command> commands, InputStream input)
            throws IOException, InterruptedException {

        List<ProcessBuilder> builders = new ArrayList<>();
        for (Command command : commands) {
            ProcessBuilder pb = new ProcessBuilder(command.getTokens());
            pb.directory(BShell.path.getPath().toFile());
            pb.redirectError(ProcessBuilder.Redirect.PIPE);
            builders.add(pb);
        }

        // input redirection
        builders.get(0).redirectInput(
                input == System.in ? ProcessBuilder.Redirect.INHERIT : ProcessBuilder.Redirect.PIPE
        );

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
