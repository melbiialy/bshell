import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandOld {
    String command;
    CommandOperation operation;
    Map<Integer, List<CommandOld>> subCommands;

    public CommandOld(String command, CommandOperation operation) {
        this.operation = operation;
        this.command = command;
        subCommands = new HashMap<>();
    }
    public String  execute(String ... args) throws IOException {
       return operation.operate(args);
    }
    public void addSubCommand(int index, CommandOld commandOld) {
        subCommands.computeIfAbsent(index, k -> List.of()).add(commandOld);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CommandOld other = (CommandOld) obj;
        return command.equals(other.command);
    }
}
