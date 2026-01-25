package commandexecution;

import builtincommands.CommandRegistry;
import commandexecution.dto.RunResults;
import exception.CommandNotFound;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


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

            // Read streams concurrently to avoid deadlock
            CompletableFuture<byte[]> stdoutFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return process.getInputStream().readAllBytes();
                } catch (IOException e) {
                    return new byte[0];
                }
            });
            CompletableFuture<byte[]> stderrFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return process.getErrorStream().readAllBytes();
                } catch (IOException e) {
                    return new byte[0];
                }
            });

            process.waitFor();

            String out = "";
            String err = "";
            try {
                byte[] outBytes = stdoutFuture.get();
                byte[] errBytes = stderrFuture.get();
                out = new String(outBytes, StandardCharsets.UTF_8);
                err = new String(errBytes, StandardCharsets.UTF_8);
            } catch (ExecutionException e) {
                // Use empty strings if reading fails
            }

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
            Process p = pb.start();
            
            // Read streams incrementally
            ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
            ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();
            
            CompletableFuture<Void> stdoutFuture = CompletableFuture.runAsync(() -> {
                try (InputStream is = p.getInputStream()) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        stdoutBuffer.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    // Stream closed
                }
            });
            
            CompletableFuture<Void> stderrFuture = CompletableFuture.runAsync(() -> {
                try (InputStream is = p.getErrorStream()) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        stderrBuffer.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    // Stream closed
                }
            });
            
            p.waitFor();
            
            try {
                stdoutFuture.get();
                stderrFuture.get();
            } catch (ExecutionException e) {
                // Ignore
            }
            
            return new RunResults(
                    stdoutBuffer.toString(StandardCharsets.UTF_8).trim(),
                    stderrBuffer.toString(StandardCharsets.UTF_8).trim()
            );
        }

        // Use shell to handle pipeline - it properly handles SIGPIPE
        // Build the pipeline command by joining tokens
        StringBuilder pipelineCmd = new StringBuilder();
        for (int i = 0; i < commands.size(); i++) {
            if (i > 0) pipelineCmd.append(" | ");
            pipelineCmd.append(String.join(" ", commands.get(i).getTokens()));
        }
        
        List<String> shellCmd = new ArrayList<>();
        shellCmd.add("/bin/sh");
        shellCmd.add("-c");
        shellCmd.add(pipelineCmd.toString());

        ProcessBuilder pb = new ProcessBuilder(shellCmd);
        pb.directory(BShell.path.getPath().toFile());
        Process p = pb.start();
        
        // Read streams incrementally - critical for "tail -f | head" cases
        ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();
        
        CompletableFuture<Void> stdoutFuture = CompletableFuture.runAsync(() -> {
            try (InputStream is = p.getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    stdoutBuffer.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                // Stream closed
            }
        });
        
        CompletableFuture<Void> stderrFuture = CompletableFuture.runAsync(() -> {
            try (InputStream is = p.getErrorStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    stderrBuffer.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                // Stream closed
            }
        });
        
        // Wait for process to complete
        // The shell will properly handle SIGPIPE when head exits
        p.waitFor();
        
        // Wait for streams to finish reading
        try {
            stdoutFuture.get();
            stderrFuture.get();
        } catch (ExecutionException e) {
            // Ignore execution exceptions
        }

       String out = stdoutBuffer.toString(StandardCharsets.UTF_8);
        if (out.endsWith("\n")) {
            out = out.substring(0, out.length() - 1);
        }
        String err = stderrBuffer.toString(StandardCharsets.UTF_8);
        if (err.endsWith("\n")) {
            err = err.substring(0, err.length() - 1);
        }

        return new RunResults(
                out,
                err
        );
    }
}

