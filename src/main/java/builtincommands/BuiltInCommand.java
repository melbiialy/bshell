package builtincommands;


import commandexecution.RunResults;

import java.io.IOException;


@FunctionalInterface
public interface BuiltInCommand {
    RunResults operate(String ... args) throws IOException, InterruptedException;
}
