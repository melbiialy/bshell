import java.util.List;

public class CommandParserImp implements CommandParser{
    private final CommandRegistry commandRegistry;
    public CommandParserImp(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }
    @Override
    public Command parse(String input) {
        return commandRegistry.getCommand(input);
    }
}
