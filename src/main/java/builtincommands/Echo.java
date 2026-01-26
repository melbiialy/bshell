package builtincommands;

import commandexecution.dto.RunResults;

import java.io.IOException;


public class Echo extends BuiltInCommand {

    @Override
    public void execute(String... args) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            if (arg == null) continue;
            sb.append(arg).append(" ");
        }
        this.getOutputStream().write(sb.toString().getBytes());
    }

}
