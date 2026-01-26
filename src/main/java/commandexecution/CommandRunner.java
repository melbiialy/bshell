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
        if (CommandRegistry.containsCommand(tokens.get(0))) {
            String[] args = tokens.stream().skip(1).toArray(String[]::new);
            return commandRegistry.getCommand(tokens.get(0)).operate(args);
        }

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
    }

    /* =========================
       Pipeline execution with streaming support
       ========================= */
    public RunResults runResults(List<Command> commands) throws IOException, InterruptedException {

        InputStream currentInput = System.in; // feed for the first segment
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
            if (!osSegment.isEmpty()) {
                RunResults res = runOsPipelineStreaming(osSegment, currentInput);
                lastStdout = res.output();
                lastStderr = res.error();
                currentInput = new ByteArrayInputStream((lastStdout + "\n").getBytes(StandardCharsets.UTF_8));
                osSegment.clear();
            }

            // ---- run builtin ----
            RunResults builtinResult = commandRegistry
                    .getCommand(cmd.getTokens().get(0))
                    .operate(cmd.getTokens().stream().skip(1).toArray(String[]::new));

            lastStdout = builtinResult.output();
            lastStderr = builtinResult.error();

            if (!lastStdout.endsWith("\n")) lastStdout += "\n";

            currentInput = new ByteArrayInputStream(lastStdout.getBytes(StandardCharsets.UTF_8));
        }

        // ---- FINAL OS SEGMENT ----
        if (!osSegment.isEmpty()) {
            RunResults res = runOsPipelineStreaming(osSegment, currentInput);
            lastStdout = res.output();
            lastStderr = res.error();
        }

        // strip trailing newlines
        while (lastStdout.endsWith("\n")) lastStdout = lastStdout.substring(0, lastStdout.length() - 1);
        while (lastStderr.endsWith("\n")) lastStderr = lastStderr.substring(0, lastStderr.length() - 1);

        return new RunResults(lastStdout, lastStderr);
    }

    /* =========================
       OS pipeline runner with streaming
       ========================= */
    private RunResults runOsPipelineStreaming(List<Command> commands, InputStream input)
            throws IOException, InterruptedException {

        List<Process> processes = new ArrayList<>();
        List<ProcessBuilder> builders = new ArrayList<>();

        for (Command cmd : commands) {
            ProcessBuilder pb = new ProcessBuilder(cmd.getTokens());
            pb.directory(BShell.path.getPath().toFile());
            pb.redirectError(ProcessBuilder.Redirect.PIPE);
            builders.add(pb);
        }

        // Build pipeline
        for (int i = 0; i < builders.size(); i++) {
            ProcessBuilder pb = builders.get(i);
            if (i == 0) {
                pb.redirectInput(input == System.in ? ProcessBuilder.Redirect.INHERIT : ProcessBuilder.Redirect.PIPE);
            }
            if (i == builders.size() - 1) {
                pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
            }
        }

        // Start pipeline
        processes = ProcessBuilder.startPipeline(builders);
        Process last = processes.get(processes.size() - 1);

        // Feed input from builtin if needed
        if (input != System.in) {
            try (OutputStream os = processes.get(0).getOutputStream()) {
                input.transferTo(os);
            }
        }

        // Stream output while the processes are running (important for tail -f | head)
        ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();

        Thread outThread = new Thread(() -> {
            try {
                InputStream in = last.getInputStream();
                byte[] buf = new byte[1024];
                int n;
                while ((n = in.read(buf)) != -1) {
                    stdoutBuffer.write(buf, 0, n);
                    System.out.write(buf, 0, n); // optional: print live output
                    System.out.flush();
                }
            } catch (IOException ignored) {}
        });

        Thread errThread = new Thread(() -> {
            try {
                InputStream err = last.getErrorStream();
                byte[] buf = new byte[1024];
                int n;
                while ((n = err.read(buf)) != -1) {
                    stderrBuffer.write(buf, 0, n);
                    System.err.write(buf, 0, n);
                    System.err.flush();
                }
            } catch (IOException ignored) {}
        });

        outThread.start();
        errThread.start();

        last.waitFor();
        outThread.join();
        errThread.join();

        String out = stdoutBuffer.toString(StandardCharsets.UTF_8);
        String err = stderrBuffer.toString(StandardCharsets.UTF_8);

        return new RunResults(out, err);
    }
}
