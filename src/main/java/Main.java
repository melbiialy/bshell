import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Command commandR = new Command("exit",(a)->System.exit(0));
        Command echo = new Command("echo",(a)->{
            System.out.println(String.join(" ",a).substring(4));
        });
        CommandRegistry commandRegistry = new CommandRegistry();
        commandRegistry.register(echo);
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
            command.execute(input.split(" "));
            System.out.print("$ ");
        }


    }
}
