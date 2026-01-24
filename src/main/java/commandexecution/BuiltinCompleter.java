package commandexecution;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BuiltinCompleter implements Completer {
    private final Set<String> commands;

    public BuiltinCompleter(HashSet<String> commands) {
        this.commands = commands;
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {

        String prefix = parsedLine.word();
        for (String command : commands) {
            if (command.startsWith(prefix)) {
                list.add(new Candidate(command));
            }
        }

    }
}
