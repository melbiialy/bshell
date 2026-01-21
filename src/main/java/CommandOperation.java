import java.io.IOException;

@FunctionalInterface
public interface CommandOperation {
    RunResults  operate(String ... args) throws IOException;
}
