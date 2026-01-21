import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandRegistry {

    List<CommandOld> commandOlds;
    public CommandRegistry() {
        this.commandOlds = new ArrayList<>();
    }

    public static CommandRegistry registerBuiltinCommands() {
        CommandRegistry commandRegistry = new CommandRegistry();
        CommandOld exit = new CommandOld("exit",(a)->{
            System.exit(0);
            return "";
        });
        CommandOld echo = new CommandOld("echo",(a)->{return new EchoCommand().operate(a);
        });
        CommandOld type = new CommandOld("type",(a)-> {
            if (a.length < 1) {
                return "type: missing operand";

            }
            String commandName = a[0];
            Process process = Runtime.getRuntime().exec(new String[]{
                    "which", commandName
            });
            if (commandRegistry.contains(commandName)) {
                return commandName + " is a shell builtin";

            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null) {
                return line;
            }else {
                return commandName + ": not found";
            }

        });
        CommandOld pwd = new CommandOld("pwd",(a)->{
            return BShell.path.getPath().toAbsolutePath().toString();
        });
        CommandOld cd = new CommandOld("cd",(a)->{
            if (a.length < 1) {
                return "cd: missing operand";
            }
            BShell.path.moveTo(a[0]);
            return "";
        });
        commandRegistry.register(cd);
        commandRegistry.register(exit);
        commandRegistry.register(echo);
        commandRegistry.register(type);
        commandRegistry.register(pwd);
        return commandRegistry;
    }

    public void register(CommandOld commandOld) {
        commandOlds.add(commandOld);
    }
    public void unregister(CommandOld commandOld) {
        commandOlds.remove(commandOld);
    }
    public boolean contains(String commandName) {
        return commandOlds.stream().anyMatch(command -> command.command.equals(commandName));
    }
    public CommandOld getCommand(String commandName) {
        for (CommandOld commandOld : commandOlds) {
            if (commandOld.command.equals(commandName)) {
                return commandOld;
            }
        }
        throw new CommandNotFound(commandName + ":" + " command not found");

    }


}
