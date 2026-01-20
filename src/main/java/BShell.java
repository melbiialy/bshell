
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class BShell {
    public static Path path;
    public final CommandParser parser;
    private final CommandRunner commandRunner;

    public BShell(CommandParser parser) {
        path = Path.of(System.getProperty("user.dir"));
        this.commandRunner = new CommandRunner(CommandRegistry.registerBuiltinCommands());
        this.parser = parser;
    }

    public Path getPath() {
        return path;
    }
    public static void moveTo(String directory) {
        if (directory.equals("~")) {
            directory = System.getenv("HOME");
            BShell.path = Path.of(directory);
            return;
        }
        Path newPath = path.resolve(directory).normalize();
        if (Files.exists(newPath) && Files.isDirectory(newPath)) {
            path = newPath;
        } else {
            System.out.println("cd: " + directory + ": No such file or directory");
        }


    }

    public void start() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("$ ");
        String input;
        while (true){
            input = reader.readLine();
            if (input == null) continue;
            List<Token> tokens = parser.parse(input);
            try {
                commandRunner.run(tokens);
            }catch (Exception e){
                System.out.println(tokens.getFirst().getToken() + ": " +e.getMessage());
            }
            System.out.print("$ ");
        }




        }

    }



