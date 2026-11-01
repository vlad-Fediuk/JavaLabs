package journal.adapter.external;

public class SchoolAssessment {
    public static final int MIN_GRADE = 0;
    public static final int MAX_GRADE = 12;

    private final int grade12;
    private final boolean present;
    private final String disciplineCode;

    public SchoolAssessment(int grade12, boolean present, String disciplineCode) {
        this.grade12 = grade12;
        this.present = present;
        this.disciplineCode = disciplineCode;
    }

    public int getGrade12() {
        return grade12;
    }

    public boolean wasPresent() {
        return present;
    }

    public String getDisciplineCode() {
        return disciplineCode;
    }
}
