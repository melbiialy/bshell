import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandRegistry {

    List<Command> commands;
    public CommandRegistry() {
        this.commands = new ArrayList<>();
    }

    public static CommandRegistry registerBuiltinCommands() {
        CommandRegistry commandRegistry = new CommandRegistry();
        Command exit = new Command("exit",(a)->System.exit(0));
        Command echo = new Command("echo",(a)->{
            System.out.println(String.join(" ",a).substring(4).trim());});
        Command type = new Command("type",(a)-> {
            if (a.length < 2) {
                System.out.println("type: missing operand");
                return;
            }
            String commandName = a[1];
            Process process = Runtime.getRuntime().exec(new String[]{
                    "which", commandName
            });
            if (commandRegistry.contains(commandName)) {
                System.out.println(commandName + " is a shell builtin");
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null) {
                System.out.println(line);
            }else {
                System.out.println(commandName + ": not found");
            }

        });
        Command pwd = new Command("pwd",(a)->{
            System.out.println(BShell.path.toAbsolutePath().toString());
        });
        Command cd = new Command("cd",(a)->{
            if (a.length < 2) {
                System.out.println("cd: missing operand");
                return;
            }
            BShell.moveTo(a[1]);
        });
        commandRegistry.register(cd);
        commandRegistry.register(exit);
        commandRegistry.register(echo);
        commandRegistry.register(type);
        commandRegistry.register(pwd);
        return commandRegistry;
    }

    public void register(Command command) {
        commands.add(command);
    }
    public void unregister(Command command) {
        commands.remove(command);
    }
    public boolean contains(String commandName) {
        return commands.stream().anyMatch(command -> command.command.equals(commandName));
    }
    public Command getCommand(String commandName) {
        for (Command command : commands) {
            if (command.command.equals(commandName)) {
                return command;
            }
        }
        throw new CommandNotFound(commandName + ":" + " command not found");

    }


}
