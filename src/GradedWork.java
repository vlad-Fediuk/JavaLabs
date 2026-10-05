public interface GradedWork {
    double getRawValue();

    double getEffectiveScore();

    boolean isPassed();

    String getWorkType();

    Attendance getAttendance();

    default String getStatusText() {
        return isPassed() ? "Зараховано" : "Потрібна перездача";
    }
}
