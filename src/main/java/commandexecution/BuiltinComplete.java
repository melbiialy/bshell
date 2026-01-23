package commandexecution;

import builtincommands.CommandRegistry;

public class BuiltinComplete implements AutoComplete{
    private final CommandRegistry registry;
    public BuiltinComplete(CommandRegistry registry) {
        this.registry = registry;
    }
    @Override
    public void complete(String input) {
        for (String command : registry.getAllCommandNames()) {
            if (command.startsWith(input)) {
                System.out.print(command.substring(input.length()));
            }
        }

    }
}
