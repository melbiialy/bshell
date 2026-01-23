import commandexecution.BShell;
import commandexecution.CommandParserImp;

import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getLogger("org.jline");
        logger.setLevel(java.util.logging.Level.OFF);
        BShell shell = new BShell(new CommandParserImp());
        shell.start();

    }
}
