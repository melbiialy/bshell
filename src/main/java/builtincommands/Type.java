package builtincommands;


import java.io.IOException;
import java.io.PipedOutputStream;

public class Type extends BuiltInCommand {
    @Override
    public void execute(PipedOutputStream outputStream,String... args) throws IOException, InterruptedException {
        if (args.length < 1) {
            this.getErrorStream().write("type: missing operand".getBytes());
            return;
        }
        String commandName = args[0];
        if (isBuiltInCommand(commandName)) {
            outputStream.write(("type: "+commandName+" is a shell builtin command").getBytes());
            return;
        }
        String[] sysArgs = new String[]{"which", commandName};
        ProcessBuilder pb = new ProcessBuilder(sysArgs);
        Process process = pb.start();
        process.waitFor();
        String output = new String(process.getInputStream().readAllBytes());
        output = output.trim();
        if (output.isEmpty()) {
            this.getErrorStream().write((commandName+" is not found").getBytes());
            return;
        }
        outputStream.write(("type: "+commandName+" is "+output+"\n").getBytes());
    }

    private boolean isBuiltInCommand(String commandName) {
        String[] builtInCommands = {
                "cd", "exit", "pwd", "echo", "history", "type"
        };
        for (String cmd : builtInCommands) {
            if (cmd.equals(commandName)) {
                return true;
            }
        }
        return false;
    }
}
