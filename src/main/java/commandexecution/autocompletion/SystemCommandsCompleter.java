package commandexecution.autocompletion;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SystemCommandsCompleter implements Completer {
    private final Set<String> commands;

    public SystemCommandsCompleter() {
        commands = new HashSet<>();
        String path = System.getenv("PATH");
        if (path != null) {
            for (String dir : path.split(":")){
                File d = new File(dir);
                if (d.exists() && d.isDirectory()) {
                    File [] files = d.listFiles(File::canExecute);
                    if (files == null) continue;
                    for (File file : files) {
                        commands.add(file.getName());
                    }
                }
            }
        }


    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        if (parsedLine.wordIndex() != 0) return;
        String prefix = parsedLine.word();
        for (String command : commands) {
            if (command.startsWith(prefix)) {
                list.add(new Candidate(command));
            }
        }


    }
}
