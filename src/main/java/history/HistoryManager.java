package history;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryManager {
    private static Set<String > history;
    public HistoryManager() {
        history = new HashSet<>();
    }
    public void add(String command) {
        history.add(command);
    }
    public static Set<String> getHistory() {
        return history;
    }
}
