package journal.observer;

import journal.domain.model.Student;

public record JournalEvent(EventType eventType, Student student, Object payload) {
    public enum EventType {
        STUDENT_ADDED,
        GRADE_ADDED,
        ATTENDANCE_ADDED,
        STRATEGY_CHANGED
    }
}
