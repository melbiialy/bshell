package commandexecution;

import commandexecution.dto.Token;

import java.util.List;

public interface CommandParser {
    List<Token> parse(String input);
}
