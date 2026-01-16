import java.util.ArrayList;
import java.util.List;

public class CommandRegistry {

    List<Command> commands;
    public CommandRegistry() {
        this.commands = new ArrayList<>();
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
