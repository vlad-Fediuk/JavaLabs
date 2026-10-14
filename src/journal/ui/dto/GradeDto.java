package journal.ui.dto;

import journal.factory.GradedWorkCreator;

public record GradeDto(
        GradedWorkCreator creator,
        double score,
        boolean wasPresent
) {
}
