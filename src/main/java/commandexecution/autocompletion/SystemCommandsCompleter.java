package commandexecution.autocompletion;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SystemCommandsCompleter implements Completer {
    private final Set<String> commands;
    private String lastPrefix = "";
    private boolean waitSecond = false;

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
        String prefix = parsedLine.word();
        if (!prefix.equals(lastPrefix)) {
            reset("");
        }
        List<String> matches = new ArrayList<>();
        for (String command : commands) {
            if (command.startsWith(prefix)) {
                matches.add(command.trim());
            }
        }
        if (matches.isEmpty()) return;
        if (matches.size() == 1) {
            list.add(new Candidate(matches.getFirst()));
        }
        else if (waitSecond){
            // Use Candidate constructor with display parameter
            for (String match : matches) {
                list.add(new Candidate(
                        match,           // value
                        match,           // display
                        null,            // group
                        null,            // description
                        null,            // suffix
                        null,            // key
                        true             // complete
                ));
            }
            reset("");
        }
        else {
            lineReader.getTerminal().writer().print("\u0007");
            lineReader.getTerminal().flush();
            waitSecond = true;
            this.lastPrefix = prefix;
        }
    }

    private void reset(String prefix) {
        lastPrefix = prefix;
        waitSecond = false;
    }
}
