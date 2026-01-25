package history;

import java.util.*;


public class HistoryStorage {
    private final Set<String > history;
    private final Map<String , Integer> fileHistoryCount;
    private  int historySize = 0;
    public HistoryStorage() {
        history = new LinkedHashSet<>();
        fileHistoryCount = new HashMap<>();
    }

    public  void add(String command) {
        history.add((historySize + 1) + "  " + command);
        historySize++;
    }

    public Set<String> getHistory(int limit) {
        if (historySize < limit) limit = 0;
        List<String> historyList = new ArrayList<>(history).subList(historySize-limit,historySize);
        return new LinkedHashSet<>(historyList);
    }

    public int getHistorySize() {
        return historySize;
    }

    public int getFileHistoryCount(String filename) {
        return fileHistoryCount.getOrDefault(filename, 0);
    }
    public void incrementFileHistoryCount(String filename, int incrementBy) {
        fileHistoryCount.put(filename,incrementBy);
    }


    public boolean isContains(String filePath) {
        return fileHistoryCount.containsKey(filePath);
    }
}
