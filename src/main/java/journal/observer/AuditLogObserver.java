package journal.observer;

import journal.domain.grade.GradedWork;
import journal.ui.io.UserInterface;

public class AuditLogObserver implements JournalObserver {
    private final UserInterface ui;

    public AuditLogObserver(UserInterface ui) {
        this.ui = ui;
    }

    @Override
    public void onEvent(JournalEvent event) {
        switch (event.eventType()) {
            case STUDENT_ADDED -> ui.println("[AUDIT] " + event.student().getName() + ": added to journal");
            case GRADE_ADDED -> {
                GradedWork work = (GradedWork) event.payload();
                ui.println(String.format("[AUDIT] %s: added %s %.1f", event.student().getName(),
                        work.getClass().getSimpleName(), work.getRawValue()));
            }
            case ATTENDANCE_ADDED -> ui.println("[AUDIT] " + event.student().getName() + ": attendance recorded");
            case STRATEGY_CHANGED -> ui.println("[AUDIT] grading strategy changed to: " + event.payload());
        }
    }
}
