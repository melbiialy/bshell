import java.nio.file.Files;
import java.nio.file.Path;

public class BShell {
    public static Path path;
    CommandRegistry commandRegistry;
    public BShell() {
        path = Path.of(System.getProperty("user.dir"));
        // todo add helper method to register the builtin commands
        commandRegistry = CommandRegistry.registerBuiltinCommands();
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
    public void start(){

    }


}
