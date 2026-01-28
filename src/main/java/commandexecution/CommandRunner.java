package commandexecution;

import builtincommands.BuiltInCommand;
import builtincommands.CommandRegistry;
import commandexecution.dto.RunResults;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CommandRunner {
    private final CommandRegistry commandRegistry;

    public CommandRunner(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }
    public RunResults run(List<Command> commands) throws IOException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
      BPath bPath = BPath.getInstance();
      List<Process> processes = new ArrayList<>();
      List<Closeable> closeables = new ArrayList<>();
      List<Future<?>> futures = new ArrayList<>();
      ExecutorService executor = Executors.newCachedThreadPool();

      try {
          InputStream lastInputStream = null;
          OutputStream errorStream = null;
          for (Command command : commands){
              boolean isBuiltin = commandRegistry.containsCommand(command.getTokens().getFirst());
              if (isBuiltin){
                  PipedOutputStream outputStream = new PipedOutputStream();
                  PipedInputStream inputStream = new PipedInputStream(outputStream);
                  closeables.add(outputStream);
                  closeables.add(inputStream);
                  BuiltInCommand builtInCommand = commandRegistry.getCommand(command.getTokens().getFirst());
                  errorStream = builtInCommand.getErrorStream();
                  String [] args = command.getTokens().stream().skip(1).toArray(String[]::new);
                  lastInputStream = inputStream;
                  Future<?> future = executor.submit(()->{
                      try(outputStream) {
                          builtInCommand.execute(outputStream,args);
                      } catch (IOException | InterruptedException e) {
                          throw new RuntimeException(e);
                      }

                  });
                  futures.add(future);
              }else {
                  ProcessBuilder pb = new ProcessBuilder(command.getTokens());
                  pb.directory(bPath.getPath().toFile());


                  Process process = pb.start();
                  processes.add(process);


                  if (lastInputStream != null) {
                      InputStream input = lastInputStream ;
                      Future<?> future = executor.submit(() -> {
                          try (OutputStream os = process.getOutputStream()) {
                              input.transferTo(os);
                          } catch (IOException e) {
                              throw new RuntimeException("Failed to pipe input", e);
                          }
                      });
                      futures.add(future);
                  }

                  lastInputStream  = process.getInputStream();
              }
          }
          for (Future<?> future : futures) {
              future.get();
          }
          StringBuilder error = new StringBuilder();
          if (errorStream != null) {
              error.append(errorStream.toString());
          }
          for (Process process : processes){
              process.waitFor();
              error.append(new String(process.getErrorStream().readAllBytes()));
          }
          String output = "";
          if (lastInputStream != null) {
              output = new String(lastInputStream.readAllBytes());
          }
          return new RunResults(output,error.toString());
      } catch (ExecutionException e) {
          throw new RuntimeException(e);
      } finally {
          executor.shutdown();
          for (Closeable closeable : closeables) {
              closeable.close();
          }
          for (Process process : processes){
              if (process.isAlive()) process.destroyForcibly();
          }
      }

      }



}
