package journal.observer;

import journal.domain.grade.ExamGrade;
import journal.domain.grade.GradedWork;
import journal.ui.io.UserInterface;

public class AcademicRiskObserver implements JournalObserver {
    private static final double PASSING_SCORE = 60.0;

    private final UserInterface ui;

    public AcademicRiskObserver(UserInterface ui) {
        this.ui = ui;
    }

    @Override
    public void onEvent(JournalEvent event) {
        if (event.eventType() != JournalEvent.EventType.GRADE_ADDED) {
            return;
        }
        GradedWork work = (GradedWork) event.payload();
        String studentName = event.student().getName();
        if (work instanceof ExamGrade) {
            if (!work.isPassed()) {
                ui.println("[ACADEMIC ALERT] " + studentName + " failed ExamGrade!");
            }
            return;
        }
        if (work.getEffectiveScore() < PASSING_SCORE) {
            ui.println(String.format("[ACADEMIC ALERT] %s scored below %.0f on %s!", studentName, PASSING_SCORE,
                    work.getWorkType()));
        }
    }
}
