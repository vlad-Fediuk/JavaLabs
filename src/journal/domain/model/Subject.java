package journal.domain.model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class Subject extends BaseEntity implements Nameable {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(100);
    private final String name;
    private final int credits;

    public Subject(long id, String name, int credits) {
        super(id);
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.credits = credits;
    }

    public Subject(long id, String name) {
        this(id, name, 3);
    }

    public Subject(String name, int credits) {
        this(ID_GENERATOR.getAndIncrement(), name, credits);
    }

    public Subject(String name) {
        this(name, 3);
    }

    @Override
    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    @Override
    public String toString() {
        return String.format("Предмет [ID: %2d]: %-30s | Кредити ECTS: %d", getId(), name, credits);
    }
}
