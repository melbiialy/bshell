import java.io.BufferedReader;
import java.io.InputStreamReader;
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
            Process process = Runtime.getRuntime().exec(new String[]{
                    "which",commandName
            });
            if (commandRegistry.contains(commandName)) {
                System.out.println(commandName + " is a shell builtin");
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null){
                System.out.println(commandName + " is " + line);
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
                Process process = Runtime.getRuntime().exec(new String[]{"which",input.split(" ")[0]});
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = reader.readLine();
                if (line != null) {
                    Process execProcess = Runtime.getRuntime().exec(input);
                    BufferedReader execReader = new BufferedReader(new InputStreamReader(execProcess.getInputStream()));
                    String execLine;
                    while ((execLine = execReader.readLine()) != null) {
                        System.out.println(execLine);
                    }
                } else {
                    System.out.println(input.split(" ")[0] + ": command not found");
                }
                System.out.print("$ ");
                continue;
            }
            command.execute(input.split(" "));
            System.out.print("$ ");
        }


    }
}
