package history;

import java.util.*;

public class HistoryManager {
    private static Set<String > history;
    private static int historySize = 0;
    public HistoryManager() {
        history = new LinkedHashSet<>();
    }
    public static void add(String command) {
        history.add((historySize+1) + "  " +command);
        historySize++;
    }
    public static Set<String> getHistory(int limit) {
        if (historySize < limit) limit = 0;
        List<String> historyList = new ArrayList<>(history).subList(historySize-limit,historySize);
        return new LinkedHashSet<>(historyList);
    }
    public static void clearHistory() {
        history.clear();
    }
    public static int getHistorySize() {
        return historySize;
    }
}
