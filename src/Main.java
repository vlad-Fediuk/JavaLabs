import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Student> students = new ArrayList<>();
        final List<String> subjects = List.of("Java", "Cs", "Cpp");

        String todayDate = readNonEmptyString(scanner, "Введіть поточну дату (дд.мм.рр): ");

        while (true) {
            System.out.println("\n========== МЕНЮ (Поточна дата: " + todayDate + ") ==========");
            System.out.println("1 - Додати студента");
            System.out.println("2 - Показати всіх студентів");
            System.out.println("3 - Додати оцінку студентові");
            System.out.println("4 - Показати всі оцінки студента");
            System.out.println("5 - Додати відвідуваність студентові");
            System.out.println("6 - Показати відвідуваність студента");
            System.out.println("7 - Сортувати студентів за середнім балом (Bubble Sort)");
            System.out.println("8 - Підсумок (студенти з середнім балом >= 60)");
            System.out.println("9 - Пошук студента");
            System.out.println("0 - Вихід");
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
                System.out.println("Аяяй! Потрібно ввести цифру від 0 до 9, а не текст чи сміття!");
                continue;
            }

            if (option == 0) {
                System.out.println("Завершення роботи програми.");
                break;
            }

            switch (option) {
                case 1 -> addStudent(scanner, students);
                case 2 -> showAllStudents(students);
                case 3 -> {
                    if (students.isEmpty()) {
                        System.out.println("Аяяй! Список студентів порожній. Спочатку додайте хоча б одного!");
                        break;
                    }
                    Student st = chooseStudent(scanner, students);
                    String sub = chooseSubject(scanner, subjects);
                    addGrade(scanner, st, todayDate, sub);
                }
                case 4 -> {
                    if (students.isEmpty()) {
                        System.out.println("Аяяй! Список студентів порожній!");
                        break;
                    }
                    Student st = chooseStudent(scanner, students);
                    showStudentGrades(st);
                }
                case 5 -> {
                    if (students.isEmpty()) {
                        System.out.println("Аяяй! Список студентів порожній!");
                        break;
                    }
                    Student st = chooseStudent(scanner, students);
                    String sub = chooseSubject(scanner, subjects);
                    addAttendance(scanner, st, todayDate, sub);
                }
                case 6 -> {
                    if (students.isEmpty()) {
                        System.out.println("Аяяй! Список студентів порожній!");
                        break;
                    }
                    Student st = chooseStudent(scanner, students);
                    showStudentAttendances(st);
                }
                case 7 -> sortStudents(students);
                case 8 -> showSummary(students);
                case 9 -> searchStudent(scanner, students);
                default -> System.out.println("Аяяй! Такого пункту в меню немає. Оберіть від 0 до 9!");
            }
        }

        scanner.close();
    }

    public static void addStudent(Scanner scanner, ArrayList<Student> students) {
        String name = readNonEmptyString(scanner, "Введіть ПІБ студента: ");
        String group = readNonEmptyString(scanner, "Введіть групу студента: ");

        students.add(new Student(name, group));
        System.out.println("Студента успішно додано!");
    }

    public static void showAllStudents(ArrayList<Student> students) {
        if (students.isEmpty()) {
            System.out.println("Список студентів порожній.");
            return;
        }
        System.out.println("\n--- Список усіх студентів ---");
        for (int i = 0; i < students.size(); i++) {
            System.out.println("[" + i + "] " + students.get(i));
        }
    }

    public static Student chooseStudent(Scanner scanner, ArrayList<Student> students) {
        showAllStudents(students);
        int index = readIntInRange(scanner, "Введіть індекс студента: ", 0, students.size() - 1);
        return students.get(index);
    }

    public static String chooseSubject(Scanner scanner, List<String> subjects) {
        System.out.println("\n--- Доступні предмети ---");
        for (int i = 0; i < subjects.size(); i++) {
            System.out.println("[" + i + "] " + subjects.get(i));
        }
        int index = readIntInRange(scanner, "Введіть індекс предмета: ", 0, subjects.size() - 1);
        return subjects.get(index);
    }

    public static void addGrade(Scanner scanner, Student student, String todayDate, String subject) {
        double score = readDoubleInRange(scanner, "Введіть бал (від 0 до 100): ", 0.0, 100.0);
        String type = readNonEmptyString(scanner, "Введіть тип оцінювання (Практика, Тест, Лабораторна): ");

        student.addGrade(new Grade(score, subject, todayDate, type));
        System.out.println("Оцінку успішно додано!");
    }

    public static void addAttendance(Scanner scanner, Student student, String todayDate, String subject) {
        boolean was = readBoolean(scanner, "Студент був на занятті? (true/false, так/ні, 1/0): ");
        student.addAttendance(new Attendance(was, subject, todayDate));
        System.out.println("Відвідуваність зафіксовано!");
    }

    public static void showStudentGrades(Student student) {
        System.out.println("\n--- Оцінки студента: " + student.getName() + " ---");
        if (student.getGrades().isEmpty()) {
            System.out.println("Оцінок немає.");
            return;
        }
        for (Grade g : student.getGrades()) {
            System.out.println(g);
        }
    }

    public static void showStudentAttendances(Student student) {
        System.out.println("\n--- Відвідуваність студента: " + student.getName() + " ---");
        if (student.getAttendances().isEmpty()) {
            System.out.println("Записів відвідуваності немає.");
            return;
        }
        for (Attendance a : student.getAttendances()) {
            System.out.println(a);
        }
    }

    public static void sortStudents(ArrayList<Student> students) {
        if (students.isEmpty()) {
            System.out.println("Аяяй! Список порожній, сортувати нікого!");
            return;
        }
        System.out.println("\nМасив ДО сортування:");
        showAllStudents(students);

        int n = students.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (students.get(j).getAverageGrade() < students.get(j + 1).getAverageGrade()) {
                    Student temp = students.get(j);
                    students.set(j, students.get(j + 1));
                    students.set(j + 1, temp);
                }
            }
        }

        System.out.println("\nМасив ПІСЛЯ Bubble Sort (за спаданням середнього балу):");
        showAllStudents(students);
    }

    public static void showSummary(ArrayList<Student> students) {
        if (students.isEmpty()) {
            System.out.println("Аяяй! Спочатку додайте студентів для підсумку!");
            return;
        }
        double threshold = 60.0;
        int count = 0;
        for (Student s : students) {
            if (s.getAverageGrade() >= threshold) {
                count++;
            }
        }
        System.out.println("\nКількість студентів із середнім балом >= " + threshold + ": " + count + " з " + students.size());
    }

    public static void searchStudent(Scanner scanner, ArrayList<Student> students) {
        if (students.isEmpty()) {
            System.out.println("Аяяй! Список студентів порожній, шукати нікого!");
            return;
        }
        String name = readNonEmptyString(scanner, "Введіть ПІБ для пошуку: ");
        String group = readNonEmptyString(scanner, "Введіть групу для пошуку: ");

        Student target = new Student(name, group);
        int foundIndex = -1;

        for (int i = 0; i < students.size(); i++) {
            if (target.equals(students.get(i))) {
                foundIndex = i;
                break;
            }
        }

        if (foundIndex != -1) {
            System.out.println("\nСтудента знайдено за індексом [" + foundIndex + "]:");
            System.out.println(students.get(foundIndex));
        } else {
            System.out.println("\nСтудента з такими даними не знайдено.");
        }
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

    public static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val < min || val > max) {
                    System.out.println("Аяяй! Число має бути від " + min + " до " + max + " (ніяких від'ємних чи неіснуючих індексів)!");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Аяяй! Ви ввели текст замість цілого числа!");
            }
        }
    }

    public static double readDoubleInRange(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().replace(',', '.');
            try {
                double val = Double.parseDouble(input);
                if (val < min || val > max) {
                    System.out.println("Аяяй! Бал не може бути від'ємним чи більшим за " + (int) max + "!");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Аяяй! Потрібно ввести число (наприклад 85 або 74.5), а не текст!");
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