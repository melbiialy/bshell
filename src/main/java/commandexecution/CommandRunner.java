package commandexecution;

import builtincommands.CommandRegistry;
import commandexecution.dto.RunResults;
import exception.CommandNotFound;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
// todo refactor this
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
            throw new CommandNotFound(tokens.getFirst() + ": command not found");
        }
    }


    public RunResults runResults(List<Command> commands) throws IOException, InterruptedException {
        boolean hasBuiltin = commands.stream().anyMatch(c ->
                CommandRegistry.containsCommand(c.getTokens().getFirst())
        );

        if (hasBuiltin) {
            return runIt(commands);
        }

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

        for (Command cmd : commands) {
            boolean isBuiltin = CommandRegistry.containsCommand(cmd.getTokens().getFirst());

            if (!isBuiltin) {
                osSegment.add(cmd);
                continue;
            }

            // ---- RUN PREVIOUS OS SEGMENT ----
            if (!osSegment.isEmpty()) {
                RunResults res = runOsPipeline(osSegment, currentInput);
                lastStdout = res.output();
                lastStderr = res.error();
                currentInput = new ByteArrayInputStream(lastStdout.getBytes(StandardCharsets.UTF_8));
                osSegment.clear();
            }

            // ---- RUN BUILTIN ----
            RunResults builtinResult = commandRegistry
                    .getCommand(cmd.getTokens().get(0))
                    .operate(cmd.getTokens().stream().skip(1).toArray(String[]::new));

            lastStdout = builtinResult.output();
            lastStderr = builtinResult.error();

            if (!lastStdout.isEmpty()) lastStdout += "\n";
            if (!lastStderr.isEmpty()) lastStderr += "\n";

            // Feed builtin output to next OS segment safely
            PipedOutputStream pos = new PipedOutputStream();
            PipedInputStream pis = new PipedInputStream(pos);
            currentInput = pis;

            String finalLastStdout = lastStdout;
            Thread writerThread = new Thread(() -> {
                try (OutputStream os = pos) {
                    os.write(finalLastStdout.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            writerThread.start();
        }

        // ---- FINAL OS SEGMENT ----
        if (!osSegment.isEmpty()) {
            RunResults res = runOsPipeline(osSegment, currentInput);
            lastStdout = res.output();
            lastStderr = res.error();
        }

        // safe trim of trailing newlines
        lastStdout = lastStdout.isEmpty() ? "" : lastStdout.replaceAll("\\n+$", "");
        lastStderr = lastStderr.isEmpty() ? "" : lastStderr.replaceAll("\\n+$", "");

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

        builders.get(0).redirectInput(
                input == System.in ? ProcessBuilder.Redirect.INHERIT : ProcessBuilder.Redirect.PIPE
        );

        builders.get(builders.size() - 1).redirectOutput(ProcessBuilder.Redirect.PIPE);

        List<Process> processes = ProcessBuilder.startPipeline(builders);
        Process last = processes.get(processes.size() - 1);

        if (input != System.in) {
            try (OutputStream os = processes.get(0).getOutputStream()) {
                input.transferTo(os);
            }
        }

        last.waitFor();

        String out = new String(last.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String err = new String(last.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

        out = out.isEmpty() ? "" : out.replaceAll("\\n+$", "");
        err = err.isEmpty() ? "" : err.replaceAll("\\n+$", "");

        return new RunResults(out, err);
    }
}
