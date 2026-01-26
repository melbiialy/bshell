package commandexecution;

import java.nio.file.Files;
import java.nio.file.Path;

public class BPath {
    private static volatile BPath instance;
    private static Path path;
    public BPath() {
        path = Path.of(System.getProperty("user.dir"));
    }
    public  Path getPath() {
        return path;
    }
    public  void moveTo(String directory) {
        if (directory.equals("~")) {
            directory = System.getenv("HOME");
            path = Path.of(directory);
            return;
        }
        Path newPath = path.resolve(directory).normalize();
        if (Files.exists(newPath) && Files.isDirectory(newPath)) {
            path = newPath;
        } else {
            System.out.println("cd: " + directory + ": No such file or directory");
        }
    }
    public static BPath getInstance() {
        if (instance == null) {
            synchronized (BPath.class) {
                if (instance == null) {
                    instance = new BPath();
                }
            }
        }
        return instance;
    }
}
