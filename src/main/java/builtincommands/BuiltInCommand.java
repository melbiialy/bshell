package builtincommands;


import commandexecution.dto.RunResults;

import java.io.*;


public abstract class BuiltInCommand {
    private InputStream inputStream;
    private OutputStream outputStream;
    private OutputStream errorStream;

    public BuiltInCommand() {
        this.inputStream =  new ByteArrayInputStream(new byte[0]);
        this.outputStream = new ByteArrayOutputStream();
        this.errorStream =  new ByteArrayOutputStream();
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public OutputStream getErrorStream() {
        return errorStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setErrorStream(OutputStream errorStream) {
        this.errorStream = errorStream;
    }
    public abstract void execute(String... args) throws IOException, InterruptedException;
}
