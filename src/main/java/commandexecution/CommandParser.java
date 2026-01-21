package commandexecution;

import java.util.List;

public interface CommandParser {
    List<Token> parse(String input);
}
