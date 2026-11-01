package journal.ui.command;

import journal.exception.JournalException;

@FunctionalInterface
public interface MenuCommand {
    void execute() throws JournalException;
}
