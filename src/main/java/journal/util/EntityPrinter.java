package journal.util;

import java.util.Collection;

import journal.domain.model.BaseEntity;

public final class EntityPrinter {
    private EntityPrinter() {
    }

    public static void printAll(Collection<? extends BaseEntity> items) {
        if (items == null || items.isEmpty()) {
            System.out.println("Колекція порожня.");
            return;
        }
        for (BaseEntity item : items) {
            System.out.println(item);
        }
    }
}
