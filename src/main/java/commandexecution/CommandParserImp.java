package commandexecution;

import commandexecution.dto.Token;

import java.util.List;

public class CommandParserImp implements CommandParser {


    @Override
    public List<Token> parse(String input) {

        return Tokenizer.tokenize(input);

    }
}

