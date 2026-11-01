package journal.ui.dto;

public record SchoolAssessmentDto(
        int grade12,
        boolean wasPresent,
        String disciplineCode
) {
}
