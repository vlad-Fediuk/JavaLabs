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

    @Override
    public String toString() {
        return "Дата: " + date + " | Предмет: " + subject + " | Оцінка: " + value + " | Тип: " + type;
    }
}
