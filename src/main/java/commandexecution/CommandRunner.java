package commandexecution;

import builtincommands.CommandRegistry;
import commandexecution.dto.RunResults;
import exception.CommandNotFound;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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
            boolean flag = false;
            for (String s : tokens) {
                if (s.contains("-f")){
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

        }catch (IOException e) {
            throw new CommandNotFound(tokens.getFirst()+ ": command not found");
        }
    }
    public RunResults runResults(List<Command> commands)
            throws IOException, InterruptedException {

        if (commands.isEmpty()) {
            return new RunResults("", "");
        }

        String pipelineInput = null;

        for (int i = 0; i < commands.size(); i++) {
            Command command = commands.get(i);
            boolean isLastCommand = (i == commands.size() - 1);

            if (CommandRegistry.containsCommand(command.getTokens().getFirst())) {
                // Handle built-in command
                List<String> tokens = new ArrayList<>(command.getTokens());

                // If there's input from previous command, add it as an argument
                if (pipelineInput != null && !pipelineInput.isEmpty()) {
                    tokens.add(pipelineInput );
                }

                RunResults result = run(tokens);

                if (isLastCommand) {
                    // Last command - print and return
                    return result;
                } else {
                    // Store output for next command
                    pipelineInput = result.output() + "\n";
                }

            } else {
                // Handle external command
                ProcessBuilder pb = new ProcessBuilder(command.getTokens());
                pb.directory(BShell.path.getPath().toFile());
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);

                Process process = pb.start();

                // If there's input from previous command, write it to stdin
                if (pipelineInput != null && !pipelineInput.isEmpty()) {
                    try (var outputStream = process.getOutputStream()) {
                        outputStream.write(pipelineInput.getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                    }
                }

                if (isLastCommand) {
                    // Last command - redirect output to console
                    process.getInputStream().transferTo(System.out);
                    process.waitFor();
                    return new RunResults("", "");
                } else {
                    // Store output for next command
                    process.waitFor();
                    pipelineInput = new String(
                            process.getInputStream().readAllBytes(),
                            StandardCharsets.UTF_8
                    );
                }
            }
        }

        return new RunResults("", "");
    }

}