# Student Journal

Консольний застосунок для ведення студентського журналу. Програма зберігає список студентів у пам’яті, фіксує відвідуваність і оцінки за типами занять, підтримує ієрархію програмних інтерфейсів, множинну реалізацію та патерн проєктування Strategy для динамічного перемикання формул рейтингу (Лабораторна робота №4).

## Можливості (ЛР №4)

- **Ієрархія оцінюваних робіт (Рівні 1–2)**:
  - інтерфейс `GradedWork` із default-методом `getStatusText()`;
  - абстрактний клас `AbstractGrade` для усунення дублювання стану (бал, відвідуваність, предмет) та валідації 0–100;
  - `RegularGrade` — звичайна пара (вимагає обов'язкової присутності);
  - `LabGrade` — лабораторна робота (можна здати при пропуску заняття зі штрафом -20%);
  - `ExamGrade` — іспит (підвищена вага x2.0, поріг зарахування 60 балів, перевизначений статус);
  - поліморфна обробка масиву робіт `GradedWork[]` у циклі.
- **Множинна реалізація інтерфейсів**:
  - клас `Student` реалізує стандартний `Comparable<Student>` (сортування за рейтингом) та `Auditable` (аудит зведення).
- **Патерн проєктування Strategy (Рівень 3)**:
  - контекстний клас-репозиторій `Journal`;
  - інтерфейс `GradingStrategy`;
  - стратегії: `StandardSemesterStrategy` (базова зі штрафами), `AmnestyStrategy` (лояльна без штрафів за пропуски), `ExamPriorityStrategy` (екзаменаційний пріоритет);
  - динамічне перемикання стратегії в runtime через консольне меню без перезапуску програми.
- **Комплексна валідація та обробка винятків (ЛР №3)**:
  - `JournalException`, `InvalidGradeException`, `StudentNotFoundException`.

## Архітектура інтерфейсів та класів

```text
<<interface>> GradedWork (getEffectiveScore(), isPassed(), getStatusText() [default])
      ▲
<<abstract>> AbstractGrade (value, attendance, subject, валідація)
      ▲
      ├── RegularGrade (обов'язкова присутність, вага 1.0)
      ├── LabGrade (штраф -20% за пропуск, поріг 40)
      └── ExamGrade (вага 2.0, поріг 60, перевизначений getStatusText())
```

## Патерн Strategy в класі Journal

```text
Journal (Контекст / Репозиторій)
  ├── List<Student> students
  ├── GradingStrategy gradingStrategy
  └── setGradingStrategy(GradingStrategy strategy) ──► delegates ──► <<interface>> GradingStrategy
                                                                            ▲
                                            ┌───────────────────────────────┼───────────────────────────────┐
                                  StandardSemesterStrategy            AmnestyStrategy              ExamPriorityStrategy
                                (урахування штрафів -20%)         (чистий бал без штрафів)     (іспит 70%, лаби 30%, поріг)
```

## Структура проєкту

```text
src/
├── AbstractGrade.java           # Абстрактний базовий клас оцінки (скелетна реалізація)
├── AmnestyStrategy.java         # Стратегія рейтингу без урахування штрафів
├── Attendance.java              # Запис про відвідування заняття
├── Auditable.java               # Інтерфейс аудиту (множинна реалізація)
├── ExamGrade.java               # Оцінка за іспит
├── ExamPriorityStrategy.java    # Стратегія рейтингу з пріоритетом іспиту
├── GradedWork.java              # Головний інтерфейс оцінюваної роботи
├── GradingStrategy.java         # Інтерфейс стратегії розрахунку рейтингу
├── InvalidGradeException.java   # Checked-виняток для некоректного балу
├── Journal.java                 # Контекст Strategy та репозиторій сутностей
├── JournalException.java        # Базовий checked-виняток предметної області
├── LabGrade.java                # Оцінка за лабораторну зі штрафом -20%
├── Main.java                    # Консольний інтерфейс користувача та меню
├── RegularGrade.java            # Оцінка за звичайну пару
├── StandardSemesterStrategy.java# Базова семестрова стратегія зі штрафами
├── Student.java                 # Сутність студента (Comparable, Auditable)
└── StudentNotFoundException.java# Checked-виняток відсутності студента
```

## Вимоги

- Java Development Kit (JDK) 17 або новіший;
- PowerShell або термінал із підтримкою UTF-8.

## Компіляція та запуск

Виконуйте команди з кореня папки лабораторної роботи (`result/Labs`):

### 1. Компіляція

```powershell
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
javac -encoding UTF-8 -d out src\*.java
```

### 2. Запуск програми

```powershell
java -cp out Main
```

## Меню програми

```text
1  - Додати студента
2  - Показати всіх студентів (з балами за активною стратегією)
3  - Додати оцінку за заняття (Звичайна пара / Лабораторна / Іспит)
4  - Показати всі оцінки студента
5  - Додати відвідуваність
6  - Показати відвідуваність студента
7  - Сортувати студентів за балом активної стратегії (Bubble Sort)
8  - Підсумок (студенти з підсумковим балом >= 60)
9  - Пошук студента
10 - Змінити стратегію оцінювання групи (Strategy Pattern)
11 - Демонстрація поліморфізму оцінок (для захисту)
0  - Вихід
```