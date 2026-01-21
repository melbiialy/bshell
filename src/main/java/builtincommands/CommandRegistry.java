package builtincommands;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private static Map<String, BuiltInCommand> commands;

    public CommandRegistry() {
        commands = new HashMap<>();
        initRegistry();
    }

    public static boolean containsCommand(String commandName) {
        return commands.containsKey(commandName);
    }

    private void initRegistry() {
        commands.put("echo", new Echo());
        commands.put("pwd", new Pwd());
        commands.put("cd", new Cd());
        commands.put("type", new Type());

    }

    public BuiltInCommand getCommand(String first) {
        return commands.get(first);
    }
}