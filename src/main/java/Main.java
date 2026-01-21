import commandexecution.BShell;
import commandexecution.CommandParserImp;

public class Main {
    public static void main(String[] args) throws Exception {
        BShell shell = new BShell(new CommandParserImp());
        shell.start();



    }
}
