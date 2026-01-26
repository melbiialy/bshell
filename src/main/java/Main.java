import builtincommands.CommandRegistry;
import commandexecution.BShell;
import commandexecution.lineinputhandler.JLineHandler;
import commandexecution.parser.Tokenizer;

import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getLogger("org.jline");
        logger.setLevel(java.util.logging.Level.OFF);
        BShell shell = new BShell(new Tokenizer(),new JLineHandler(new CommandRegistry().getCommandNames()));
        shell.start();

    }
}
