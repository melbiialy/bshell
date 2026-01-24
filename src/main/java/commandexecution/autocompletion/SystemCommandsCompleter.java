package commandexecution.autocompletion;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.io.File;
import java.util.*;

public class SystemCommandsCompleter implements Completer {
    private final Set<String> commands;
    private String lastPrefix = "";
    private boolean waitSecond = false;

    public SystemCommandsCompleter() {
        commands = new TreeSet<>();
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
            reset();
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
            lineReader.getTerminal().writer().println();
            lineReader.getTerminal().writer().println(String.join("  ", matches));
            lineReader.getTerminal().flush();
            lineReader.callWidget(LineReader.REDRAW_LINE);
            lineReader.callWidget(LineReader.REDISPLAY);
        }
        else {
            lineReader.getTerminal().writer().print("\u0007");
            lineReader.getTerminal().flush();
            waitSecond = true;
            this.lastPrefix = prefix;
        }
    }

    private void reset() {

        waitSecond = false;
    }
}
