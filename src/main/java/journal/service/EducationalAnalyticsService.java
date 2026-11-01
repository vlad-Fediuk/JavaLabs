package journal.service;

import java.util.List;

import journal.domain.grade.AbstractGrade;
import journal.domain.grade.GradedWork;
import journal.domain.model.GradeRecord;
import journal.domain.model.Student;
import journal.domain.model.Subject;
import journal.exception.JournalException;

public interface EducationalAnalyticsService {
    GradedWork[] preparePolymorphicWorksDemo() throws JournalException;

    List<AbstractGrade> prepareInheritanceHierarchyDemo() throws JournalException;

    CrudDemoResult executeCrudLabDemo();

    JdbcDemoResult executeJdbcLabDemo();

    record CrudDemoResult(
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
            List<Student> finalStorageOrder
    ) {}

    record JdbcDemoResult(
            boolean connectionActive,
            Student insertedStudent,
            List<GradeRecord> insertedGrades,
            boolean transactionCommitSuccess,
            boolean transactionRollbackSuccess,
            boolean cascadeDeleteSuccess,
            List<Student> pagedStudentsPage1,
            List<Student> pagedStudentsPage2,
            long singleInsertTimeMs,
            long batchInsertTimeMs,
            int statementVulnerableCount,
            int preparedStatementSafeCount
    ) {}
}
