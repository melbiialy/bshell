import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class BShell {
    public static Path path;
    private CommandRegistry commandRegistry;
    private CommandParser commandParser;
    public BShell() {
        path = Path.of(System.getProperty("user.dir"));
        commandRegistry = CommandRegistry.registerBuiltinCommands();
        commandParser = new CommandParserImp(commandRegistry);
    }
    public Path getPath() {
        return path;
    }
    public void moveTo(Path path) {
        if (Files.exists(path)&&Files.isDirectory(path)) {
            BShell.path = path;
        }else {
            throw new NoSuchDirectory("No such directory "+path.toString());
        }
    }
    public void moveForward(String forwardDirectory) {
        this.moveTo(path.resolve(forwardDirectory));
    }
    public void moveBackward(){
        this.moveTo(path.resolve(".."));
    }
    public void start() throws IOException {
//        System.out.println("Welcome to BShell");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;
        System.out.print("$ ");
        while ((input = reader.readLine())!=null){
            Command command;
            try {
                command = commandParser.parse(input);
            } catch (CommandNotFound e) {
                Process process = Runtime.getRuntime().exec(new String[]{"which",input.split(" ")[0]});
                BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = processReader.readLine();
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
