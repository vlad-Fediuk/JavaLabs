package journal.ui.view;

import java.util.List;
import java.util.function.ToDoubleFunction;

import journal.adapter.external.SchoolAssessment;
import journal.domain.grade.AbstractGrade;
import journal.domain.grade.DefenseLabGrade;
import journal.domain.grade.ExamGrade;
import journal.domain.grade.GradedWork;
import journal.domain.grade.LabGrade;
import journal.domain.grade.RegularGrade;
import journal.domain.model.Attendance;
import journal.domain.model.Student;
import journal.domain.model.Subject;
import journal.exception.JournalException;
import journal.factory.DefenseLabGradeCreator;
import journal.factory.ExamGradeCreator;
import journal.factory.GradedWorkCreator;
import journal.factory.LabGradeCreator;
import journal.factory.RegularGradeCreator;
import journal.strategy.AmnestyStrategy;
import journal.strategy.ExamPriorityStrategy;
import journal.strategy.GradingStrategy;
import journal.strategy.StandardSemesterStrategy;
import journal.ui.dto.GradeDto;
import journal.ui.dto.SchoolAssessmentDto;
import journal.ui.dto.SearchCriteriaDto;
import journal.ui.dto.StudentDto;
import journal.ui.io.UserInterface;
import journal.util.EntityPrinter;

public class ConsoleView implements AppView {
    private static final int MIN_MENU_OPTION = 0;
    private static final int MAX_MENU_OPTION = 13;
    private static final int MIN_GRADE_KIND = 1;
    private static final int MAX_GRADE_KIND = 4;
    private static final int MIN_STRATEGY_OPTION = 1;
    private static final int MAX_STRATEGY_OPTION = 3;

    private final UserInterface ui;

    public ConsoleView(UserInterface ui) {
        this.ui = ui;
    }

    @Override
    public String requestDate() {
        return ui.readNonEmptyString("Введіть поточну дату (дд.мм.рр): ");
    }

    @Override
    public String readMenuChoice(String todayDate, String activeStrategyName) {
        ui.println("\n========== МЕНЮ (Поточна дата: " + todayDate + ") ==========");
        ui.println("Поточна стратегія розрахунку: " + activeStrategyName);
        ui.println("1  - Додати студента");
        ui.println("2  - Показати всіх студентів (з балами за активною стратегією)");
        ui.println("3  - Додати оцінку за заняття (Звичайна / Лабораторна / Іспит / Захист лаб.)");
        ui.println("4  - Показати всі оцінки студента");
        ui.println("5  - Додати відвідуваність");
        ui.println("6  - Показати відвідуваність студента");
        ui.println("7  - Сортувати студентів за балом активної стратегії (Bubble Sort)");
        ui.println("8  - Підсумок (студенти з підсумковим балом >= 60)");
        ui.println("9  - Пошук студента");
        ui.println("10 - Змінити стратегію оцінювання групи (Strategy Pattern)");
        ui.println("11 - Демонстрація поліморфізму та ієрархії наслідування (ЛР №6)");
        ui.println("12 - Імпорт шкільної 12-бальної оцінки (Adapter Pattern)");
        ui.println("13 - Демонстрація типізованого CRUD-сервісу та узагальнень (ЛР №7-9)");
        ui.println("0  - Вихід");
        return ui.readLine("Оберіть опцію: ");
    }

    @Override
    public String requestSecretNewDate() {
        return ui.readNonEmptyString("Таємна команда активована! Введіть нову дату (дд.мм.рр): ");
    }

    @Override
    public void showSecretDateChanged(String newDate) {
        ui.println("Дату успішно змінено на: " + newDate);
    }

    @Override
    public void showInvalidMenuOption() {
        ui.println("Аяяй! Такого пункту в меню немає. Оберіть від " + MIN_MENU_OPTION + " до " + MAX_MENU_OPTION + "!");
    }

    @Override
    public void showInvalidNumberError() {
        ui.println("Аяяй! Потрібно ввести число, а не текст чи сміття!");
    }

    @Override
    public void showExitMessage() {
        ui.println("Завершення роботи програми.");
    }

    @Override
    public void showEmptyJournalWarning() {
        ui.println("Аяяй! Список студентів порожній. Спочатку додайте хоча б одного!");
    }

    @Override
    public void showIndexOutOfBoundsError() {
        ui.println("Аяяй! Вказано неіснуючий індекс!");
    }

    @Override
    public void showJournalError(String message) {
        ui.println("Аяяй! Помилка журналу: " + message);
    }

    @Override
    public void showInvalidGradeError(String message, double invalidScore) {
        ui.println("Аяяй! " + message + "! (Некоректне значення: " + invalidScore + ")");
    }

    @Override
    public void showStudentNotFoundError(String message, String searchTarget) {
        ui.println("Аяяй! " + message + "! (Критерій: " + searchTarget + ")");
    }

    @Override
    public StudentDto promptStudentData() {
        String name = ui.readNonEmptyString("Введіть ПІБ студента: ");
        String group = ui.readNonEmptyString("Введіть групу студента: ");
        return new StudentDto(name, group);
    }

    @Override
    public void showStudentAdded() {
        ui.println("Студента успішно додано!");
    }

    @Override
    public void displayAllStudents(List<Student> students, ToDoubleFunction<Student> scoreCalculator) {
        if (students.isEmpty()) {
            ui.println("Список студентів порожній.");
            return;
        }
        ui.println("\n--- Список усіх студентів ---");
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            ui.printf("[%d] %s | Рейтинг за стратегією: %5.2f%n", i, s, scoreCalculator.applyAsDouble(s));
        }
    }

    @Override
    public Student chooseStudent(List<Student> students, ToDoubleFunction<Student> scoreCalculator) {
        displayAllStudents(students, scoreCalculator);
        int index = ui.readInt("Введіть індекс студента: ");
        if (index < 0 || index >= students.size()) {
            throw new IndexOutOfBoundsException("Некоректний індекс студента: " + index);
        }
        return students.get(index);
    }

    @Override
    public Subject chooseSubject(List<Subject> subjects) {
        ui.println("\n--- Доступні предмети ---");
        for (int i = 0; i < subjects.size(); i++) {
            ui.println("[" + i + "] " + subjects.get(i));
        }
        int index = ui.readInt("Введіть індекс предмета: ");
        if (index < 0 || index >= subjects.size()) {
            throw new IndexOutOfBoundsException("Некоректний індекс предмета: " + index);
        }
        return subjects.get(index);
    }

    @Override
    public GradeDto promptGradeData() throws JournalException {
        ui.println("\n--- Оберіть тип заняття для оцінки ---");
        ui.println("1 - Звичайна пара (вимагає обов'язкової присутності)");
        ui.println("2 - Лабораторна робота (можна здати при пропуску зі штрафом -20%)");
        ui.println("3 - Іспит (підвищена вага x2.0, жорсткий поріг 60)");
        ui.println("4 - Захист лабораторної роботи (3-й рівень ієрархії, бонус за захист)");
        int kind = ui.readInt("Ваш вибір (" + MIN_GRADE_KIND + "-" + MAX_GRADE_KIND + "): ");
        GradedWorkCreator creator = switch (kind) {
            case 1 -> new RegularGradeCreator();
            case 2 -> new LabGradeCreator(ui.readInt("Введіть номер лабораторної роботи: "));
            case 3 -> new ExamGradeCreator(ui.readInt("Введіть номер білета на іспиті: "));
            case 4 -> new DefenseLabGradeCreator(
                    ui.readInt("Введіть номер лабораторної роботи: "),
                    ui.readDouble("Введіть бонус за захист (від " + DefenseLabGrade.MIN_DEFENSE_BONUS + " до "
                            + DefenseLabGrade.MAX_DEFENSE_BONUS + "): "));
            default -> throw new JournalException("Невідомий тип заняття: " + kind);
        };
        boolean wasPresent = ui.readBoolean("Студент був присутній на занятті? (true/false, так/ні, 1/0): ");
        double score = ui.readDouble("Введіть базовий бал (від " + (int) AbstractGrade.MIN_SCORE + " до "
                + (int) AbstractGrade.MAX_SCORE + "): ");
        return new GradeDto(creator, score, wasPresent);
    }

    @Override
    public void showGradeAdded(GradedWork work) {
        ui.println("Оцінку успішно виставлено!");
        ui.println("Підсумок запису: " + work);
    }

    @Override
    public SchoolAssessmentDto promptSchoolAssessmentData() {
        int grade12 = ui.readInt("Введіть оцінку за 12-бальною шкалою (" + SchoolAssessment.MIN_GRADE + "-"
                + SchoolAssessment.MAX_GRADE + "): ");
        boolean present = ui.readBoolean("Студент був присутній? (true/false, так/ні, 1/0): ");
        String code = ui.readNonEmptyString("Введіть код дисципліни: ");
        return new SchoolAssessmentDto(grade12, present, code);
    }

    @Override
    public void showSchoolAssessmentImported(GradedWork work) {
        ui.println("Шкільну оцінку імпортовано!");
        ui.println("Підсумок запису: " + work);
    }

    @Override
    public void displayStudentGrades(Student student) {
        ui.println("\n--- Оцінки студента: " + student.getName() + " ---");
        if (student.getGradedWorks().isEmpty()) {
            ui.println("Оцінок немає.");
            return;
        }
        for (GradedWork work : student.getGradedWorks()) {
            ui.println(work.toString());
        }
    }

    @Override
    public boolean promptAttendancePresence() {
        return ui.readBoolean("Студент був на занятті? (true/false, так/ні, 1/0): ");
    }

    @Override
    public void showAttendanceRecorded() {
        ui.println("Відвідуваність зафіксовано!");
    }

    @Override
    public void displayStudentAttendances(Student student) {
        ui.println("\n--- Відвідуваність студента: " + student.getName() + " ---");
        if (student.getAttendances().isEmpty()) {
            ui.println("Записів відвідуваності немає.");
            return;
        }
        for (Attendance attendance : student.getAttendances()) {
            ui.println(attendance.toString());
        }
    }

    @Override
    public void displayStudentsBeforeSorting(List<Student> students, ToDoubleFunction<Student> scoreCalculator) {
        ui.println("\nМасив ДО сортування:");
        displayAllStudents(students, scoreCalculator);
    }

    @Override
    public void displayStudentsAfterSorting(List<Student> students, ToDoubleFunction<Student> scoreCalculator) {
        ui.println("\nМасив ПІСЛЯ Bubble Sort (за спаданням балу активної стратегії):");
        displayAllStudents(students, scoreCalculator);
    }

    @Override
    public void displaySummary(int passedCount, int totalCount, double threshold) {
        ui.println("\nКількість студентів із підсумковим балом >= " + threshold
                + " за поточною стратегією: " + passedCount + " з " + totalCount);
    }

    @Override
    public SearchCriteriaDto promptSearchCriteria() {
        String name = ui.readNonEmptyString("Введіть ПІБ для пошуку: ");
        String group = ui.readNonEmptyString("Введіть групу для пошуку: ");
        return new SearchCriteriaDto(name, group);
    }

    @Override
    public void displayFoundStudent(Student student, double score) {
        ui.println("\nСтудента успішно знайдено:");
        ui.println(student.toString());
        ui.println("Аудит: " + student.getAuditSummary());
        ui.printf("Рейтинговий бал за активною стратегією: %.2f%n", score);
    }

    @Override
    public GradingStrategy promptStrategyChoice() {
        ui.println("\n--- Оберіть стратегію підрахунку рейтингу групи ---");
        ui.println("1 - Стандартна семестрова (лаби зі штрафом -20% за пропуски, ваги робіт)");
        ui.println("2 - Амністія (лояльний підрахунок без штрафів за пропуски занять)");
        ui.println("3 - Екзаменаційний пріоритет (іспит 70%, обов'язкове складання іспиту)");
        int choice = ui.readInt("Ваш вибір (" + MIN_STRATEGY_OPTION + "-" + MAX_STRATEGY_OPTION + "): ");
        return switch (choice) {
            case 1 -> new StandardSemesterStrategy();
            case 2 -> new AmnestyStrategy();
            case 3 -> new ExamPriorityStrategy();
            default -> {
                ui.println("Невірний вибір. Стратегію не змінено.");
                yield null;
            }
        };
    }

    @Override
    public void showStrategyChanged(String strategyName) {
        ui.println("Стратегію успішно змінено на: " + strategyName);
        ui.println("Тепер перегляньте список студентів (пункт 2), щоб побачити миттєвий перерахунок балів!");
    }

    @Override
    public void displayPolymorphismDemo(GradedWork[] works) {
        ui.println("\n========== ДЕМОНСТРАЦІЯ ПОЛІМОРФІЗМУ (GradedWork[]) ==========");
        for (GradedWork work : works) {
            ui.printf("Тип: %-30s | Базовий: %5.1f | Ефективний: %5.1f | Здано: %-5s | Статус: %s%n",
                    work.getWorkType(),
                    work.getRawValue(),
                    work.getEffectiveScore(),
                    work.isPassed() ? "Так" : "Ні",
                    work.getStatusText());
        }
        ui.println("=============================================================");
    }

    @Override
    public void displayInheritanceHierarchyDemo(
            List<AbstractGrade> grades,
            LabGrade baseLab,
            LabGrade duplicateLab,
            DefenseLabGrade subLab) {
        ui.println("\n========== ЛР №6: ІЄРАРХІЯ НАСЛІДУВАННЯ (AbstractGrade) ==========");
        ui.println("--- 1. Поліморфний виклик toString() через super.toString() ---");
        for (AbstractGrade grade : grades) {
            ui.println("  " + grade.toString());
        }

        ui.println("\n--- 2. Звернення до специфічних методів через instanceof (Pattern Matching) ---");
        for (AbstractGrade grade : grades) {
            String staticType = "AbstractGrade";
            String dynamicType = grade.getClass().getSimpleName();
            if (grade instanceof DefenseLabGrade defenseLab) {
                ui.printf("  [Статичний: %s | Динамічний: %-16s] Лаб №%d, специфічний бонус захисту: +%.1f%n",
                        staticType, dynamicType, defenseLab.getLabNumber(), defenseLab.getDefenseBonus());
            } else if (grade instanceof LabGrade lab) {
                ui.printf("  [Статичний: %s | Динамічний: %-16s] Специфічний метод getLabNumber(): №%d%n",
                        staticType, dynamicType, lab.getLabNumber());
            } else if (grade instanceof ExamGrade exam) {
                ui.printf("  [Статичний: %s | Динамічний: %-16s] Специфічний метод getTicketNumber(): білет №%d%n",
                        staticType, dynamicType, exam.getTicketNumber());
            } else if (grade instanceof RegularGrade regular) {
                ui.printf("  [Статичний: %s | Динамічний: %-16s] Тип роботи: %s%n",
                        staticType, dynamicType, regular.getWorkType());
            }
        }

        ui.println("\n--- 3. Запечатана ієрархія (sealed) та вичерпний switch без default ---");
        for (AbstractGrade grade : grades) {
            String details = switch (grade) {
                case DefenseLabGrade d -> "Рівень 3 (DefenseLabGrade): лаб №" + d.getLabNumber()
                        + ", бонус +" + d.getDefenseBonus() + ", підсумок=" + d.getEffectiveScore();
                case LabGrade l -> "Рівень 2 (LabGrade): лаб №" + l.getLabNumber()
                        + ", підсумок=" + l.getEffectiveScore();
                case ExamGrade e -> "Рівень 2 (ExamGrade): білет №" + e.getTicketNumber()
                        + ", підсумок=" + e.getEffectiveScore();
                case RegularGrade r -> "Рівень 2 (RegularGrade): аудиторне заняття, підсумок="
                        + r.getEffectiveScore();
            };
            ui.println("  " + details);
        }

        ui.println("\n--- 4. Перевірка equals(), hashCode() та симетрії (getClass vs instanceof) ---");
        ui.printf("  baseLab.equals(duplicateLab): %b | hashCode збігається: %b%n",
                baseLab.equals(duplicateLab), baseLab.hashCode() == duplicateLab.hashCode());
        ui.printf("  Підхід getClass()   -> baseLab.equals(subLab): %-5b | subLab.equals(baseLab): %-5b (Симетрія збережена)%n",
                baseLab.equals(subLab), subLab.equals(baseLab));
        ui.printf("  Підхід instanceof -> baseLab.equalsUsingInstanceof(subLab): %-5b | subLab.equalsUsingInstanceof(baseLab): %-5b (Порушення симетрії!)%n",
                baseLab.equalsUsingInstanceof(subLab), subLab.equalsUsingInstanceof(baseLab));
        ui.println("==================================================================");
    }

    @Override
    public void displayCrudServiceDemo(
            List<Student> insertedStudents,
            Student updatedStudent,
            int sizeAfterUpdate,
            boolean deleteSuccess,
            boolean deleteFailed,
            Student foundStudent,
            String notFoundMessage,
            List<Subject> demoSubjects,
            List<Student> initialStorageOrder,
            List<Student> sortedByName,
            List<Student> sortedByNameAndGroup,
            List<Student> finalStorageOrder) {
        ui.println("\n========== ЛР №7-9: ТИПІЗОВАНИЙ CRUD-СЕРВІС ТА УЗАГАЛЬНЕННЯ (Generics) ==========");
        ui.println("--- 1. Рівень 1: Базовий CRUD над Student (save, update, delete, findById) ---");
        ui.println("  [1.1 Додавання сутностей (save)]");
        for (Student student : insertedStudents) {
            ui.println("    Додано: " + student);
        }
        ui.println("    Кількість у сховищі після додавання: " + insertedStudents.size());

        ui.println("\n  [1.2 Оновлення наявної сутності (save з тим самим ID)]");
        ui.println("    Оновлено: " + updatedStudent);
        ui.println("    Кількість у сховищі після оновлення: " + sizeAfterUpdate + " (розмір не збільшився)");

        ui.println("\n  [1.3 Видалення сутностей (delete)]");
        ui.println("    Успішне видалення наявного ID: " + deleteSuccess);
        ui.println("    Неуспішне видалення неіснуючого ID: " + deleteFailed);

        ui.println("\n  [1.4 Пошук за ID та обробка винятку EntityNotFoundException (findById)]");
        ui.println("    Успішний пошук ID=" + foundStudent.getId() + ": " + foundStudent.getName() + " (" + foundStudent.getGroup() + ")");
        ui.println("    Пошук неіснуючого ID: перехоплено виняток -> " + notFoundMessage);

        ui.println("\n--- 2. Рівень 1: Універсальність InMemoryCrudService<T> з другою сутністю (Subject) ---");
        for (Subject subject : demoSubjects) {
            ui.println("    " + subject);
        }

        ui.println("\n--- 3. Рівень 2: Сортування та перевірка незмінності сховища ---");
        ui.println("  [3.1 Початковий порядок у LinkedHashMap сховища]");
        for (Student s : initialStorageOrder) {
            ui.println("    " + s);
        }

        ui.println("\n  [3.2 Сортування за назвою: findAllSortedByName()]");
        for (Student s : sortedByName) {
            ui.println("    " + s);
        }

        ui.println("\n  [3.3 Вторинне сортування: findAllSortedByNameAndByGroup() (збіг за ім'ям)]");
        for (Student s : sortedByNameAndGroup) {
            ui.println("    " + s);
        }

        ui.println("\n  [3.4 Перевірка незмінності сховища після сортувань]");
        for (Student s : finalStorageOrder) {
            ui.println("    " + s);
        }
        boolean storageUnmodified = initialStorageOrder.equals(finalStorageOrder);
        ui.println("    Порядок та склад сховища залишилися незмінними: " + storageUnmodified);

        ui.println("\n--- 4. Рівень 3: Wildcards (? super T, ? extends BaseEntity) та printAll ---");
        ui.println("  [4.1 printAll(Collection<? extends BaseEntity>) для списку Student]");
        EntityPrinter.printAll(sortedByNameAndGroup);
        ui.println("\n  [4.2 printAll(Collection<? extends BaseEntity>) для списку Subject]");
        EntityPrinter.printAll(demoSubjects);
        ui.println("==================================================================================");
    }
}
