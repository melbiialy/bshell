package exception;

public class NoSuchDirectory extends RuntimeException {
    public NoSuchDirectory(String message) {
        super(message);
    }
}
