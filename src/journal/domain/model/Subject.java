package journal.domain.model;

import java.util.Locale;
import java.util.Objects;

public final class Subject {
    private final String name;

    public Subject(String name) {
        this.name = Objects.requireNonNull(name).trim();
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Subject other)) {
            return false;
        }
        return name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase(Locale.ROOT).hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
