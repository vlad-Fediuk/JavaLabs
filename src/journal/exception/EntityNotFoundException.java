package journal.exception;

public class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final long id;

    public EntityNotFoundException(long id) {
        super("Сутність з ID=" + id + " не знайдена в системі");
        this.id = id;
    }

    public EntityNotFoundException(long id, String entityType) {
        super(String.format("Сутність '%s' з ID=%d не знайдена в системі", entityType, id));
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
