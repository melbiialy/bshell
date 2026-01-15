import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        CommandParser commandParser = new CommandParserImp(new CommandRegistry());
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
