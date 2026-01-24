package commandexecution.lineinputhandler;

import commandexecution.autocompletion.BuiltinCompleter;
import commandexecution.autocompletion.SystemCommandsCompleter;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JLineHandler implements LineInputHandler {
    private final LineReader reader;
    public JLineHandler(Set<String> commands) throws IOException {
        DefaultParser defaultParser = new DefaultParser();
        defaultParser.setEofOnUnclosedQuote(false);
        defaultParser.setEscapeChars(null);
        Terminal terminal = TerminalBuilder.builder().system(true).dumb(true).build();
        Completer completer = new AggregateCompleter(
                new BuiltinCompleter((HashSet<String>) commands),
                new SystemCommandsCompleter()
        );
        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .parser(defaultParser)
                .completer(completer)
                .build();
    }
    @Override
    public String handle() {
        return reader.readLine("$ ");
    }
}
