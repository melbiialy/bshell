import java.io.IOException;

@FunctionalInterface
public interface CommandOperation {
    String  operate(String ... args) throws IOException;
}
