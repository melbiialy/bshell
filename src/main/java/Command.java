import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Command {
    String command;
    CommandOperation operation;
    Map<Integer, List<Command>> subCommands;

    public Command(String command, CommandOperation operation) {
        this.operation = operation;
        this.command = command;
        subCommands = new HashMap<>();
    }
    public void execute(String ... args) throws IOException {
        operation.operate(args);
    }
    public void addSubCommand(int index, Command command) {
        subCommands.computeIfAbsent(index, k -> List.of()).add(command);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Command other = (Command) obj;
        return command.equals(other.command);
    }
}
