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

        List<ProcessBuilder> builders = new ArrayList<>();


        for (Command command : commands) {

                ProcessBuilder pb = new ProcessBuilder(command.getTokens());
                pb.directory(BShell.path.getPath().toFile());
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                builders.add(pb);
            }


        builders.getFirst()
                .redirectInput(ProcessBuilder.Redirect.INHERIT);

        builders.getLast()
                .redirectOutput(ProcessBuilder.Redirect.INHERIT);

        List<Process> processes =
                ProcessBuilder.startPipeline(builders);

        processes.getLast().waitFor();

        return new RunResults("", "");
    }


}