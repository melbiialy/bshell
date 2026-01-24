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

        // If prefix changed, reset state
        if (!prefix.equals(lastPrefix)) {
            waitSecond = false;
        }

        // Find all matching commands
        List<String> matches = new ArrayList<>();
        for (String command : commands) {
            if (command.startsWith(prefix)) {
                matches.add(command);
            }
        }

        if (matches.isEmpty()) {
            waitSecond = false;
            lastPrefix = "";
            return;
        }

        // Only one match - complete it directly
        if (matches.size() == 1) {
            list.add(new Candidate(matches.get(0)));
            waitSecond = false;
            lastPrefix = "";
            return;
        }

        // Multiple matches - need to handle double-tab
        if (waitSecond) {
            // Second tab press - show all matches
            lineReader.getTerminal().writer().println();
            lineReader.getTerminal().writer().println(String.join("  ", matches));
            lineReader.getTerminal().flush();
            lineReader.callWidget(LineReader.REDRAW_LINE);
            lineReader.callWidget(LineReader.REDISPLAY);
            waitSecond = false;
            lastPrefix = "";
        } else {
            // First tab press - complete common prefix and beep
            String commonPrefix = getCommonPrefix(matches);

            if (commonPrefix.length() > prefix.length()) {
                // Add candidate without trailing space using suffix=""
                list.add(new Candidate(
                        commonPrefix,      // value
                        commonPrefix,      // display
                        null,              // group
                        null,              // description
                        null,              // suffix (null means no space)
                        null,              // key
                        false              // complete = false means don't add space
                ));
            }

            // Beep to indicate more options available
            lineReader.getTerminal().writer().print("\u0007");
            lineReader.getTerminal().flush();

            waitSecond = true;
            lastPrefix = commonPrefix.length() > prefix.length() ? commonPrefix : prefix;
        }
    }

    private String getCommonPrefix(List<String> matches) {
        if (matches.isEmpty()) return "";

        String prefix = matches.get(0);
        for (int i = 1; i < matches.size(); i++) {
            String current = matches.get(i);
            int j = 0;
            int minLen = Math.min(prefix.length(), current.length());
            while (j < minLen && prefix.charAt(j) == current.charAt(j)) {
                j++;
            }
            prefix = prefix.substring(0, j);
        }
        return prefix;
    }
}