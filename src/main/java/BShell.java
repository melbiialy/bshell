
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class BShell {
    public static BPath path;
    public final CommandParser parser;
    private final CommandRunner commandRunner;

    public BShell(CommandParser parser) {
        path = new BPath();
        this.commandRunner = new CommandRunner(CommandRegistry.registerBuiltinCommands());
        this.parser = parser;
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



