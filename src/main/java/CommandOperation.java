import java.io.IOException;

@FunctionalInterface
public interface CommandOperation {
    void operate(String ... args) throws IOException;
}
