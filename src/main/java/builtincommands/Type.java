package builtincommands;

import commandexecution.CommandExecutor;
import commandexecution.RunResults;

import java.io.IOException;

public class Type implements BuiltInCommand {
    @Override
    public RunResults operate(String... args) throws IOException, InterruptedException {
        if (args.length < 1) {
            return new RunResults("type: missing operand", "");
        }
        String commandName = args[0];
        if (CommandRegistry.containsCommand(commandName)){
            return new RunResults(commandName+": is shell builtin", "");
        }
        String[] sysArgs = new String[]{"which", commandName};
        ProcessBuilder pb = new ProcessBuilder(sysArgs);
        Process process = pb.start();
        process.waitFor();
        String output = new String(process.getInputStream().readAllBytes());
        if (output.isEmpty()) {
            return new RunResults(commandName+": not found", "");
        }
        return new RunResults(commandName+" is a "+ output, "");
    }
}
