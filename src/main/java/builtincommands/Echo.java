package builtincommands;

import commandexecution.dto.RunResults;

import java.io.IOException;
import java.io.PipedOutputStream;


public class Echo extends BuiltInCommand {

    @Override
    public void execute(PipedOutputStream outputStream,String... args) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            if (arg == null) continue;
            sb.append(arg).append(" ");
        }
        sb.append("\n");
        outputStream.write(sb.toString().getBytes());
    }

}
