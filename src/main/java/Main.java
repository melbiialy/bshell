import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Command commandR = new Command("exit",(a)->System.exit(0));
        Command echo = new Command("echo",(a)->{
            System.out.println(String.join(" ",a).substring(4).trim());
        });
        CommandRegistry commandRegistry = new CommandRegistry();
        commandRegistry.register(echo);
        commandRegistry.register(commandR);
        Command type = new Command("type",(a)->{
            if (a.length < 2) {
                System.out.println("type: missing operand");
                return;
            }
            String commandName = a[1];
            if (commandRegistry.contains(commandName)) {
                System.out.println(commandName + " is a shell builtin");
            }
            else {
                System.out.println(commandName + ": not found");
            }
        });
        commandRegistry.register(type);
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
            command.execute(input.split(" "));
            System.out.print("$ ");
        }


    }
}
