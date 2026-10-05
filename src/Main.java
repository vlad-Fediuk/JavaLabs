import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Journal journal = new Journal();
        final List<String> subjects = List.of("Java", "Cs", "Cpp");

        String todayDate = readNonEmptyString(scanner, "Введіть поточну дату (дд.мм.рр): ");

        while (true) {
            System.out.println("\n========== МЕНЮ (Поточна дата: " + todayDate + ") ==========");
            System.out.println("Поточна стратегія розрахунку: " + journal.getGradingStrategy().getStrategyName());
            System.out.println("1  - Додати студента");
            System.out.println("2  - Показати всіх студентів (з балами за активною стратегією)");
            System.out.println("3  - Додати оцінку за заняття (Звичайна пара / Лабораторна / Іспит)");
            System.out.println("4  - Показати всі оцінки студента");
            System.out.println("5  - Додати відвідуваність");
            System.out.println("6  - Показати відвідуваність студента");
            System.out.println("7  - Сортувати студентів за балом активної стратегії (Bubble Sort)");
            System.out.println("8  - Підсумок (студенти з підсумковим балом >= 60)");
            System.out.println("9  - Пошук студента");
            System.out.println("10 - Змінити стратегію оцінювання групи (Strategy Pattern)");
            System.out.println("11 - Демонстрація поліморфізму оцінок (для захисту)");
            System.out.println("0  - Вихід");
            System.out.print("Оберіть опцію: ");

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("SecretNextDay")) {
                todayDate = readNonEmptyString(scanner, "Таємна команда активована! Введіть нову дату (дд.мм.рр): ");
                System.out.println("Дату успішно змінено на: " + todayDate);
                continue;
            }

            int option;
            try {
                option = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Аяяй! Потрібно ввести число, а не текст чи сміття!");
                continue;
            }

            if (option == 0) {
                System.out.println("Завершення роботи програми.");
                break;
            }

            switch (option) {
                case 1 -> addStudent(scanner, journal);
                case 2 -> showAllStudents(journal);
                case 3 -> handleAddGrade(scanner, journal, todayDate, subjects);
                case 4 -> handleShowGrades(scanner, journal);
                case 5 -> handleAddAttendance(scanner, journal, todayDate, subjects);
                case 6 -> handleShowAttendances(scanner, journal);
                case 7 -> sortStudents(journal);
                case 8 -> showSummary(journal);
                case 9 -> searchStudent(scanner, journal);
                case 10 -> changeStrategy(scanner, journal);
                case 11 -> demonstratePolymorphism();
                default -> System.out.println("Аяяй! Такого пункту в меню немає. Оберіть від 0 до 11!");
            }
        }

        scanner.close();
    }

    public static void addStudent(Scanner scanner, Journal journal) {
        String name = readNonEmptyString(scanner, "Введіть ПІБ студента: ");
        String group = readNonEmptyString(scanner, "Введіть групу студента: ");
        journal.addStudent(new Student(name, group));
        System.out.println("Студента успішно додано!");
    }

    public static void showAllStudents(Journal journal) {
        if (journal.isEmpty()) {
            System.out.println("Список студентів порожній.");
            return;
        }
        System.out.println("\n--- Список усіх студентів ---");
        for (int i = 0; i < journal.size(); i++) {
            Student s = journal.get(i);
            double score = journal.calculateStudentScore(s);
            System.out.printf("[%d] %s | Рейтинг за стратегією: %5.2f%n", i, s, score);
        }
    }

    public static Student chooseStudent(Scanner scanner, Journal journal) {
        showAllStudents(journal);
        int index = readInt(scanner, "Введіть індекс студента: ");
        if (index < 0 || index >= journal.size()) {
            throw new IndexOutOfBoundsException("Некоректний індекс студента: " + index);
        }
        return journal.get(index);
    }

    public static String chooseSubject(Scanner scanner, List<String> subjects) {
        System.out.println("\n--- Доступні предмети ---");
        for (int i = 0; i < subjects.size(); i++) {
            System.out.println("[" + i + "] " + subjects.get(i));
        }
        int index = readInt(scanner, "Введіть індекс предмета: ");
        if (index < 0 || index >= subjects.size()) {
            throw new IndexOutOfBoundsException("Некоректний індекс предмета: " + index);
        }
        return subjects.get(index);
    }

    public static void handleAddGrade(Scanner scanner, Journal journal, String todayDate, List<String> subjects) {
        if (journal.isEmpty()) {
            System.out.println("Аяяй! Список студентів порожній. Спочатку додайте хоча б одного!");
            return;
        }
        try {
            Student student = chooseStudent(scanner, journal);
            String subject = chooseSubject(scanner, subjects);

            System.out.println("\n--- Оберіть тип заняття для оцінки ---");
            System.out.println("1 - Звичайна пара (вимагає обов'язкової присутності)");
            System.out.println("2 - Лабораторна робота (можна здати при пропуску зі штрафом -20%)");
            System.out.println("3 - Іспит (підвищена вага x2.0, жорсткий поріг 60)");
            int gradeKind = readInt(scanner, "Ваш вибір (1-3): ");

            boolean wasPresent = readBoolean(scanner, "Студент був присутній на занятті? (true/false, так/ні, 1/0): ");
            Attendance attendance = new Attendance(wasPresent, subject, todayDate);
            student.addAttendance(attendance);

            System.out.print("Введіть базовий бал (від 0 до 100): ");
            String scoreRaw = scanner.nextLine().trim().replace(',', '.');
            double score = Double.parseDouble(scoreRaw);

            GradedWork work = createGradedWork(gradeKind, score, attendance, subject, scanner);
            student.addGrade(work);
            System.out.println("Оцінку успішно виставлено!");
            System.out.println("Підсумок запису: " + work);
        } catch (InvalidGradeException e) {
            System.out.println("Аяяй! " + e.getMessage() + "! (Некоректне значення: " + e.getInvalidScore() + ")");
        } catch (JournalException e) {
            System.out.println("Аяяй! Помилка журналу: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Аяяй! Потрібно ввести коректне числове значення балу!");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Аяяй! Вказано неіснуючий індекс!");
        }
    }

    public static GradedWork createGradedWork(int kind, double score, Attendance attendance, String subject, Scanner scanner)
            throws InvalidGradeException, JournalException {
        return switch (kind) {
            case 1 -> new RegularGrade(score, attendance, subject);
            case 2 -> {
                int labNum = readInt(scanner, "Введіть номер лабораторної роботи: ");
                yield new LabGrade(score, attendance, subject, labNum);
            }
            case 3 -> {
                int ticket = readInt(scanner, "Введіть номер білета на іспиті: ");
                yield new ExamGrade(score, attendance, subject, ticket);
            }
            default -> throw new JournalException("Невідомий тип заняття: " + kind);
        };
    }

    public static void handleShowGrades(Scanner scanner, Journal journal) {
        if (journal.isEmpty()) {
            System.out.println("Аяяй! Список студентів порожній!");
            return;
        }
        try {
            Student student = chooseStudent(scanner, journal);
            System.out.println("\n--- Оцінки студента: " + student.getName() + " ---");
            if (student.getGradedWorks().isEmpty()) {
                System.out.println("Оцінок немає.");
                return;
            }
            for (GradedWork w : student.getGradedWorks()) {
                System.out.println(w);
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Аяяй! Індекс студента виходить за межі списку!");
        }
    }

    public static void handleAddAttendance(Scanner scanner, Journal journal, String todayDate, List<String> subjects) {
        if (journal.isEmpty()) {
            System.out.println("Аяяй! Список студентів порожній!");
            return;
        }
        try {
            Student student = chooseStudent(scanner, journal);
            String subject = chooseSubject(scanner, subjects);
            boolean was = readBoolean(scanner, "Студент був на занятті? (true/false, так/ні, 1/0): ");
            student.addAttendance(new Attendance(was, subject, todayDate));
            System.out.println("Відвідуваність зафіксовано!");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Аяяй! Зазначений індекс недоступний!");
        }
    }

    public static void handleShowAttendances(Scanner scanner, Journal journal) {
        if (journal.isEmpty()) {
            System.out.println("Аяяй! Список студентів порожній!");
            return;
        }
        try {
            Student student = chooseStudent(scanner, journal);
            System.out.println("\n--- Відвідуваність студента: " + student.getName() + " ---");
            if (student.getAttendances().isEmpty()) {
                System.out.println("Записів відвідуваності немає.");
                return;
            }
            for (Attendance a : student.getAttendances()) {
                System.out.println(a);
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Аяяй! Зазначений індекс недоступний!");
        }
    }

    public static void sortStudents(Journal journal) {
        if (journal.isEmpty()) {
            System.out.println("Аяяй! Список порожній, сортувати нікого!");
            return;
        }
        System.out.println("\nМасив ДО сортування:");
        showAllStudents(journal);

        journal.bubbleSortByStrategyScore();

        System.out.println("\nМасив ПІСЛЯ Bubble Sort (за спаданням балу активної стратегії):");
        showAllStudents(journal);
    }

    public static void showSummary(Journal journal) {
        if (journal.isEmpty()) {
            System.out.println("Аяяй! Спочатку додайте студентів для підсумку!");
            return;
        }
        double threshold = 60.0;
        int count = journal.countPassedStudents(threshold);
        System.out.println("\nКількість студентів із підсумковим балом >= " + threshold + " за поточною стратегією: "
                + count + " з " + journal.size());
    }

    public static void searchStudent(Scanner scanner, Journal journal) {
        if (journal.isEmpty()) {
            System.out.println("Аяяй! Список студентів порожній, шукати нікого!");
            return;
        }
        String name = readNonEmptyString(scanner, "Введіть ПІБ для пошуку: ");
        String group = readNonEmptyString(scanner, "Введіть групу для пошуку: ");

        try {
            Student found = journal.findStudent(name, group);
            System.out.println("\nСтудента успішно знайдено:");
            System.out.println(found);
            System.out.println("Аудит: " + found.getAuditSummary());
            System.out.printf("Рейтинговий бал за активною стратегією: %.2f%n", journal.calculateStudentScore(found));
        } catch (StudentNotFoundException e) {
            System.out.println("Аяяй! " + e.getMessage() + "! (Критерій: " + e.getSearchTarget() + ")");
        }
    }

    public static void changeStrategy(Scanner scanner, Journal journal) {
        System.out.println("\n--- Оберіть стратегію підрахунку рейтингу групи ---");
        System.out.println("1 - Стандартна семестрова (лаби зі штрафом -20% за пропуски, ваги робіт)");
        System.out.println("2 - Амністія (лояльний підрахунок без штрафів за пропуски занять)");
        System.out.println("3 - Екзаменаційний пріоритет (іспит 70%, обов'язкове складання іспиту)");
        int choice = readInt(scanner, "Ваш вибір (1-3): ");

        switch (choice) {
            case 1 -> journal.setGradingStrategy(new StandardSemesterStrategy());
            case 2 -> journal.setGradingStrategy(new AmnestyStrategy());
            case 3 -> journal.setGradingStrategy(new ExamPriorityStrategy());
            default -> {
                System.out.println("Невірний вибір. Стратегію не змінено.");
                return;
            }
        }
        System.out.println("Стратегію успішно змінено на: " + journal.getGradingStrategy().getStrategyName());
        System.out.println("Тепер перегляньте список студентів (пункт 2), щоб побачити миттєвий перерахунок балів!");
    }

    public static void demonstratePolymorphism() {
        System.out.println("\n========== ДЕМОНСТРАЦІЯ ПОЛІМОРФІЗМУ (GradedWork[]) ==========");
        Attendance present = new Attendance(true, "Java", "27.09.26");
        Attendance absent = new Attendance(false, "Java", "27.09.26");

        try {
            GradedWork[] works = new GradedWork[] {
                new RegularGrade(80.0, present, "Java"),
                new LabGrade(80.0, present, "Java", 4),
                new LabGrade(80.0, absent, "Java", 4),
                new ExamGrade(80.0, present, "Java", 12)
            };

            for (GradedWork work : works) {
                System.out.printf("Тип: %-22s | Базовий: %5.1f | Ефективний: %5.1f | Здано: %-5s | Статус: %s%n",
                        work.getWorkType(),
                        work.getRawValue(),
                        work.getEffectiveScore(),
                        work.isPassed() ? "Так" : "Ні",
                        work.getStatusText());
            }
        } catch (Exception e) {
            System.out.println("Помилка створення демонстраційних об'єктів: " + e.getMessage());
        }
        System.out.println("=============================================================");
    }

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Аяяй! Рядок не може бути порожнім!");
        }
    }

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Аяяй! Ви ввели текст замість цілого числа!");
            }
        }
    }

    public static boolean readBoolean(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("true") || input.equals("так") || input.equals("1") || input.equals("+")) {
                return true;
            }
            if (input.equals("false") || input.equals("ні") || input.equals("0") || input.equals("-")) {
                return false;
            }
            System.out.println("Аяяй! Незрозуміла відповідь. Введіть: true / false, так / ні, або 1 / 0!");
        }
    }
}