package commandexecution;

import builtincommands.CommandRegistry;
import commandexecution.dto.RunResults;
import exception.CommandNotFound;

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
    public RunResults runPipeline(List<Command> commands) throws IOException, InterruptedException {
        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < commands.size(); i++) {
            Command command = commands.get(i);
            ProcessBuilder pb = new ProcessBuilder(command.getTokens());
            pb.directory(BShell.path.getPath().toFile());

            // Pipe input from previous process
            if (i > 0) {
                pb.redirectInput(ProcessBuilder.Redirect.PIPE);
            }

            // Pipe output to next process (except for last command)
            if (i < commands.size() - 1) {
                pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
            }

            Process process = pb.start();
            processes.add(process);

            // Connect previous process output to current process input
            if (i > 0) {
                Process prevProcess = processes.get(i - 1);
                pipe(prevProcess.getInputStream(), process.getOutputStream());
            }
        }

        // Wait for all processes and get output from last one
        Process lastProcess = processes.get(processes.size() - 1);
        lastProcess.waitFor();

        String out = new String(lastProcess.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String err = new String(lastProcess.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        if (out.endsWith("\n")) {
            out = out.substring(0, out.length() - 1);
        }
        if (err.endsWith("\n")) {
            err = err.substring(0, err.length() - 1);
        }

        // Clean up other processes
        for (int i = 0; i < processes.size() - 1; i++) {
            processes.get(i).waitFor();
        }

        return new RunResults(out, err);
    }

    private void pipe(InputStream input, OutputStream output) {
        new Thread(() -> {
            try {
                input.transferTo(output);
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    }

