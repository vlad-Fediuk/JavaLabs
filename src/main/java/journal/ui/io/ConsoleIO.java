package journal.ui.io;

import java.util.Scanner;

public class ConsoleIO implements UserInterface {
    private final Scanner scanner;

    public ConsoleIO(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void print(String text) {
        System.out.print(text);
    }

    @Override
    public void println(String text) {
        System.out.println(text);
    }

    @Override
    public void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

    @Override
    public String readLine(String prompt) {
        print(prompt);
        return scanner.nextLine().trim();
    }

    @Override
    public String readNonEmptyString(String prompt) {
        while (true) {
            String line = readLine(prompt);
            if (!line.isEmpty()) {
                return line;
            }
            println("Аяяй! Рядок не може бути порожнім!");
        }
    }

    @Override
    public int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                println("Аяяй! Ви ввели текст замість цілого числа!");
            }
        }
    }

    @Override
    public double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readLine(prompt).replace(',', '.'));
            } catch (NumberFormatException e) {
                println("Аяяй! Потрібно ввести коректне числове значення!");
            }
        }
    }

    @Override
    public boolean readBoolean(String prompt) {
        while (true) {
            String input = readLine(prompt).toLowerCase();
            if (input.equals("true") || input.equals("так") || input.equals("1") || input.equals("+")) {
                return true;
            }
            if (input.equals("false") || input.equals("ні") || input.equals("0") || input.equals("-")) {
                return false;
            }
            println("Аяяй! Незрозуміла відповідь. Введіть: true / false, так / ні, або 1 / 0!");
        }
    }
}
