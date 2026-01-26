package commandexecution;

import builtincommands.BuiltInCommand;
import builtincommands.CommandRegistry;
import commandexecution.dto.RunResults;
import commandexecution.dto.Token;
import exception.CommandNotFound;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;
// todo refactor this
public class CommandRunner {
    private final CommandRegistry commandRegistry;

    public CommandRunner(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }
    public RunResults run(List<Command> commands) throws IOException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        BPath path = BPath.getInstance();
        List<Process> processes = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        Process prevProcess = null;
        OutputStream prevInputStream = null;
        OutputStream errorStream = null;
        for(Command command : commands){
            boolean isBuiltin = commandRegistry.containsCommand(command.getTokens().getFirst());
            if (isBuiltin){
                BuiltInCommand builtInCommand = commandRegistry.getCommand(command.getTokens().getFirst());
                String[] args = command.getTokens().stream().skip(1).toArray(String[]::new);
                builtInCommand.execute(args);
                prevInputStream = builtInCommand.getOutputStream();
                errorStream = builtInCommand.getErrorStream();
                prevProcess = null;
                continue;
            }
            ProcessBuilder pb = new ProcessBuilder(command.getTokens());
            pb.directory(path.getPath().toFile());
            Process process = pb.start();
            if (prevProcess != null){
                Process finalPrevProcess = prevProcess;
                Thread thread = new Thread(()->{
                    try {
                        finalPrevProcess.getInputStream().transferTo(process.getOutputStream());
                        process.getOutputStream().close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                thread.start();
                threads.add(thread);
            } else if (prevInputStream != null) {

                OutputStream finalPrevInputStream1 = prevInputStream;
                Thread thread = new Thread(()->{
                    try (InputStream finalPrevInputStream =
                                  new ByteArrayInputStream(((ByteArrayOutputStream) finalPrevInputStream1).toByteArray()); ){
                        finalPrevInputStream.transferTo(process.getOutputStream());
                        process.getOutputStream().close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                thread.start();
                threads.add(thread);


            }
            processes.add(process);
            prevProcess = process;
            prevInputStream = null;
            errorStream = null;


        }
        for (Thread thread : threads){
            thread.join();
        }
        for (Process process : processes){
            process.waitFor();
        }
        if (prevProcess != null){
            String output = new String(prevProcess.getInputStream().readAllBytes());
            String error = new String(prevProcess.getErrorStream().readAllBytes());
            while (output.endsWith("\n")){
                output = output.substring(0,output.length()-1);
            }
            while (error.endsWith("\n")){
                error = error.substring(0,error.length()-1);
            }
            return new RunResults(output, error);
        }else if (prevInputStream != null){
            String output = ((ByteArrayOutputStream) prevInputStream).toString();
            String error = errorStream == null ? "" : ((ByteArrayOutputStream) errorStream).toString();
            while (output.endsWith("\n")){
                output = output.substring(0,output.length()-1);
            }
            while (error.endsWith("\n")){
                error = error.substring(0,error.length()-1);
            }
            return new RunResults(output, error);

        }

        return new RunResults("", "Command not found");

    }

}
