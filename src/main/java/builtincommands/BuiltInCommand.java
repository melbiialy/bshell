package builtincommands;


import commandexecution.RunResults;

import java.io.IOException;


@FunctionalInterface
public interface Command {
    RunResults operate(String ... args) throws IOException;
}
