package journal.observer;

public interface JournalObserver {
    void onEvent(JournalEvent event);
}
