package commandexecution;

import builtincommands.BuiltInCommand;
import builtincommands.CommandRegistry;
import commandexecution.dto.RunResults;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class CommandRunner {
    private final CommandRegistry commandRegistry;

    public CommandRunner(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }
    public RunResults run(List<Command> commands) throws IOException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        BPath path = BPath.getInstance();
        List<Process> processes = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        InputStream prevInputStream = System.in;
        for(Command command : commands){
            boolean isBuiltin = commandRegistry.containsCommand(command.getTokens().getFirst());
            if (isBuiltin){
                PipedOutputStream outputStream = new PipedOutputStream();
                PipedInputStream inputStream = new PipedInputStream(outputStream);
                BuiltInCommand builtInCommand = commandRegistry.getCommand(command.getTokens().getFirst());
                Thread thread = new Thread(()->{
                    try(outputStream) {
                        String [] tokens = command.getTokens().stream().skip(1).toArray(String[]::new);
                        builtInCommand.execute(outputStream, tokens);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
                threads.add(thread);
                prevInputStream = inputStream;
                continue;
            }


            ProcessBuilder pb = new ProcessBuilder(command.getTokens());
            pb.directory(path.getPath().toFile());
            Process process = pb.start();
            InputStream finalPrevInputStream = prevInputStream;
            Thread thread = new Thread(()->{
                try {
                    finalPrevInputStream.transferTo(process.getOutputStream());
                    process.getOutputStream().close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
            threads.add(thread);
            processes.add(process);
            prevInputStream = process.getInputStream();
        }
        for (Thread thread : threads){
            thread.join();
        }
        for (Process process : processes){
            process.waitFor();
        }


        String output = new String(prevInputStream.readAllBytes());
        output = output.trim();
        prevInputStream.close();
        return new RunResults(output, "");

    }

}
