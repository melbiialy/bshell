package commandexecution;

import builtincommands.CommandRegistry;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        org.jline.terminal.Terminal terminal = TerminalBuilder.builder().system(true).build();
        LineReader lineReader = LineReaderBuilder.builder().terminal(terminal)
                .parser(parserJ)
                .completer(new StringsCompleter("echo", "exit", "type", "cd", "pwd")).build();

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



