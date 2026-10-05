public class LabGrade extends AbstractGrade {
    private final int labNumber;

    public LabGrade(double value, Attendance attendance, String subject, int labNumber)
            throws InvalidGradeException {
        super(value, attendance, subject);
        this.labNumber = labNumber;
    }

    public int getLabNumber() {
        return labNumber;
    }

    @Override
    public double getEffectiveScore() {
        if (!wasPresent()) {
            return getRawValue() * 0.8;
        }
        return getRawValue() * 1.0;
    }

    @Override
    public boolean isPassed() {
        return getEffectiveScore() >= 40.0;
    }

    @Override
    public String getWorkType() {
        return "Лабораторна #" + labNumber;
    }
}
