package journal;

import java.util.List;
import java.util.Scanner;

import journal.domain.model.Subject;
import journal.observer.AcademicRiskObserver;
import journal.observer.AuditLogObserver;
import journal.repository.Journal;
import journal.ui.AppController;
import journal.ui.io.ConsoleIO;
import journal.ui.io.UserInterface;
import journal.ui.view.AppView;
import journal.ui.view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        UserInterface ui = new ConsoleIO(new Scanner(System.in));
        Journal journal = new Journal();
        journal.registerObserver(new AuditLogObserver(ui));
        journal.registerObserver(new AcademicRiskObserver(ui));
        List<Subject> subjects = List.of(new Subject("Java"), new Subject("Cs"), new Subject("Cpp"));
        AppView view = new ConsoleView(ui);
        new AppController(view, journal, subjects).run();
    }
}
