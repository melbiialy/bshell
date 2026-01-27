package commandexecution.lineinputhandler;

import commandexecution.BPath;
import commandexecution.autocompletion.BuiltinCompleter;
import commandexecution.autocompletion.SystemCommandsCompleter;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
public class JLineHandler implements LineInputHandler {
    private final LineReader reader;
    private final Terminal terminal;
    public JLineHandler(Set<String> commands) throws IOException {
        DefaultParser defaultParser = new DefaultParser();
        defaultParser.setEofOnUnclosedQuote(false);
        defaultParser.setEscapeChars(null);
         terminal = TerminalBuilder.builder().system(true).dumb(true).build();
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
        BPath bPath = BPath.getInstance();
        terminal.writer().print(
                new AttributedString(System.getenv("USER"),AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN).bold()).toAnsi()
                +new AttributedString(
                        bPath.getPath().toString(),
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE)
                                .bold()
                ).toAnsi() + new AttributedString(" $ ",
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN
                        ).bold()).toAnsi()
        );
        terminal.writer().flush();
        String input = reader.readLine();
        if (input.equals("clear")){
            terminal.puts(InfoCmp.Capability.clear_screen);
            terminal.puts(InfoCmp.Capability.cursor_home);
            terminal.writer().print("\033[H");
            terminal.flush();
        }
        return input;
    }

    @Override
    public Terminal getTerminal() {
        return terminal;
    }
}
