import java.io.IOException;


public class EchoCommand implements CommandOperation{


    @Override
    public String  operate(String... args) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            if (arg == null) continue;
            sb.append(arg).append(" ");
        }
        return sb.toString().trim();
    }

}
