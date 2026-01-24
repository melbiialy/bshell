package builtincommands;


import commandexecution.dto.RunResults;

import java.io.IOException;


@FunctionalInterface
public interface BuiltInCommand {
    RunResults operate(String ... args) throws IOException, InterruptedException;
}
