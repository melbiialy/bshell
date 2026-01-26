package commandexecution.lineinputhandler;

import org.jline.terminal.Terminal;

public interface LineInputHandler {
    String handle();

    Terminal getTerminal();
}
