public class Command {
    String command;
    CommandOperation operation;
    public Command(String command, CommandOperation operation) {
        this.operation = operation;
        this.command = command;
    }
    public void execute() {
        operation.operate();
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Command other = (Command) obj;
        return command.equals(other.command);
    }
}
