import java.util.Objects;

public class Grade {
    private double value;
    private String subject;
    private String date;
    private String type;

    public Grade(double value, String subject, String date, String type) {
        this.value = value;
        this.subject = subject;
        this.date = date;
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Дата: " + date + " | Предмет: " + subject + " | Оцінка: " + value + " | Тип: " + type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Grade other)) return false;
        return Double.compare(this.value, other.value) == 0
                && Objects.equals(this.subject, other.subject)
                && Objects.equals(this.date, other.date)
                && Objects.equals(this.type, other.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, subject, date, type);
    }
}
