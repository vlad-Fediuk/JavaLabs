package journal.ui.io;

public interface UserInterface {
    void print(String text);

    void println(String text);

    void printf(String format, Object... args);

    String readLine(String prompt);

    String readNonEmptyString(String prompt);

    int readInt(String prompt);

    double readDouble(String prompt);

    boolean readBoolean(String prompt);
}
