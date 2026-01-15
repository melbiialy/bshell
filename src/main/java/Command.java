public class Command {
    String command;
    public Command(String command) {
        this.command = command;
    }
    public void execute() {
        System.out.println("Executing command: " + command);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Command other = (Command) obj;
        return command.equals(other.command);
    }
}
