package commandexecution;

import builtincommands.CommandRegistry;

import commandexecution.autocompletion.BuiltinCompleter;
import commandexecution.autocompletion.SystemCommandsCompleter;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.List;


public class BShell {
    public static BPath path;
    public final CommandParser parser;
    private final CommandExecutor commandRunner;

    public BShell(CommandParser parser) {
        path = new BPath();
        this.parser = parser;
        commandRunner = new CommandExecutor();
    }


    public void start() throws IOException, InterruptedException {
        DefaultParser parserJ = new DefaultParser();
        parserJ.setEofOnUnclosedQuote(false);
        parserJ.setEscapeChars(null);
        org.jline.terminal.Terminal terminal = TerminalBuilder.builder().system(true).dumb(true).build();
        CommandRegistry commandRegistry = new CommandRegistry();
        Completer completer = new AggregateCompleter(new BuiltinCompleter(commandRegistry.getCommandNames()), new SystemCommandsCompleter());
        LineReader lineReader = LineReaderBuilder.builder().terminal(terminal)
                .parser(parserJ)
                .completer(completer)
                .build();
        while (true) {

            String input = lineReader.readLine("$ ");
//            System.out.println(input);
            if (input.isBlank()) continue;
            List<Token> tokens = parser.parse(input);
            try {
                commandRunner.execute(tokens);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}



