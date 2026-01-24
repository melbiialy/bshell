package history;

import java.util.*;

public class HistoryManager {
    private static Set<String > history;
    public HistoryManager() {
        history = new LinkedHashSet<>();
    }
    public void add(String command) {
        history.add(command);
    }
    public static Set<String> getHistory() {
        return history;
    }
}
