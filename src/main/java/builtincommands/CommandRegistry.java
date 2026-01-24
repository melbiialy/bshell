package builtincommands;

import java.util.HashMap;
import java.util.HashSet;
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
        commands.put("exit", new Exit());
        commands.put("history", new History());
    }

    public BuiltInCommand getCommand(String first) {
        return commands.get(first);
    }

    public String[] getAllCommandNames() {
        return commands.keySet().toArray(new String[0]);
    }

    public HashSet<String> getCommandNames() {
        return new HashSet<>(commands.keySet());
    }
}