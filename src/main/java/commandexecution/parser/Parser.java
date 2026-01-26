package commandexecution.parser;

import commandexecution.dto.Token;

import java.util.List;

public interface Parser {
    List<Token> parse(String input);
}
