package history;

import java.util.*;

public class HistoryManager {
    private final HistoryStorage historyStorage;
    private static volatile HistoryManager instance;

    public HistoryManager() {
        this.historyStorage = new HistoryStorage();
    }

    public  int getPublishedNumber(String filePath) {
        return historyStorage.getFileHistoryCount(filePath);
    }

    public void addCommand(String command) {
        historyStorage.add(command);
    }

    public Set<String> getHistory(int limit) {
        return historyStorage.getHistory(limit);
    }
    public int getHistorySize() {
        return historyStorage.getHistorySize();
    }
    public static HistoryManager getInstance() {
        if (instance == null) {
            synchronized (HistoryManager.class) {
                if (instance == null) {
                    instance = new HistoryManager();
                }
            }
        }
        return instance;
    }

    public boolean isPublished(String filePath) {
        return historyStorage.isContains(filePath);
    }

    public void updatePublish(String filePath, int historySize) {
        historyStorage.incrementFileHistoryCount(filePath, historySize);
    }
}
