import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Command commandR = new Command("exit",()->System.exit(0));
        CommandRegistry commandRegistry = new CommandRegistry();
        commandRegistry.register(commandR);
        CommandParser commandParser = new CommandParserImp(commandRegistry);
        System.out.print("$ ");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            Command command;
            try {
                command = commandParser.parse(input);
            } catch (CommandNotFound e) {
                System.out.println(e.getMessage());
                System.out.print("$ ");
                continue;
            }
            command.execute();
            System.out.print("$ ");
        }


    }
}
