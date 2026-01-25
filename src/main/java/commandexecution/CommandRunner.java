package commandexecution;

import builtincommands.CommandRegistry;
import commandexecution.dto.RunResults;
import exception.CommandNotFound;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


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

        } catch (IOException e) {
            throw new CommandNotFound(tokens.getFirst() + ": command not found");
        }
    }

    public RunResults runPipeline(List<Command> commands) throws IOException, InterruptedException {
        if (commands.size() == 1) {
            Command command = commands.get(0);
            ProcessBuilder pb = new ProcessBuilder(command.getTokens());
            pb.directory(BShell.path.getPath().toFile());

            Process process = pb.start();
            process.waitFor();

            String out = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            String err = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8).trim();

            return new RunResults(out, err);
        }

        List<ProcessBuilder> builders = new ArrayList<>();
        for (Command command : commands) {
            ProcessBuilder pb = new ProcessBuilder(command.getTokens());
            pb.directory(BShell.path.getPath().toFile());
            builders.add(pb);
        }

        List<Process> processes = ProcessBuilder.startPipeline(builders);
        Process lastProcess = processes.get(processes.size() - 1);

        // Capture output in separate threads
        StringBuilder outBuilder = new StringBuilder();
        StringBuilder errBuilder = new StringBuilder();

        Thread outThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(lastProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    outBuilder.append(line).append("\n");
                }
            } catch (IOException e) {
                // Ignore
            }
        });

        Thread errThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(lastProcess.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    errBuilder.append(line).append("\n");
                }
            } catch (IOException e) {
                // Ignore
            }
        });

        outThread.start();
        errThread.start();

        // Wait for last process with a timeout
        boolean finished = lastProcess.waitFor(10, TimeUnit.SECONDS);

        if (!finished) {
            // Last process didn't finish - force kill everything
            for (Process p : processes) {
                p.destroyForcibly();
            }
            lastProcess.waitFor();
        }

        outThread.join(1000);
        errThread.join(1000);

        // Force cleanup any remaining processes
        for (Process p : processes) {
            if (p.isAlive()) {
                p.destroyForcibly();
            }
        }

        String out = outBuilder.toString().trim();
        String err = errBuilder.toString().trim();

        return new RunResults(out, err);
    }
}

