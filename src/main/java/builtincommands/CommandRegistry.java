package builtincommands;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class CommandRegistry {
    private final Map<String, Class<?>> commands;

    public CommandRegistry() {
        commands = new HashMap<>();
        initRegistry();
    }

    public  boolean containsCommand(String commandName) {
        return commands.containsKey(commandName);
    }

    private void initRegistry() {
       commands.put("cd",Cd.class);
       commands.put("echo",Echo.class);
       commands.put("exit",Exit.class);
       commands.put("history",History.class);
       commands.put("pwd",Pwd.class);
       commands.put("type", Type.class);
    }

    public BuiltInCommand getCommand(String first) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return commands.get(first).asSubclass(BuiltInCommand.class).getDeclaredConstructor().newInstance();
    }

    public String[] getAllCommandNames() {
        return commands.keySet().toArray(new String[0]);
    }

    public HashSet<String> getCommandNames() {
        return new HashSet<>(commands.keySet());
    }
}