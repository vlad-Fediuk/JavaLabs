import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int totalLessons = 20;

        System.out.print("Enter student full name (String): ");
        String studentName = scanner.nextLine();

        System.out.print("Enter subject name (String): ");
        String subjectName = scanner.nextLine();

        System.out.print("Enter number of attended classes out of " + totalLessons + " (int): ");
        int attendedLessons = scanner.nextInt();

        System.out.print("Enter practical work score, from 0 to 50 (int): ");
        int practicalScore = scanner.nextInt();

        System.out.print("Enter final exam score, from 0 to 50 (double): ");
        double examScore = scanner.nextDouble();

        double totalScore = practicalScore + examScore;
        double attendanceRate = ((double) attendedLessons / totalLessons) * 100.0;
        boolean isPassed = totalScore >= 60.0;

        System.out.println("\n========================================");
        System.out.println("         STUDENT GRADE REPORT");
        System.out.println("========================================");
        System.out.println("Student:  " + studentName);
        System.out.println("Subject:  " + subjectName);
        System.out.println("Attendance:  " + attendedLessons + "/" + totalLessons);
        System.out.printf("Attendance rate:  %.1f%%\n", attendanceRate);
        System.out.println("----------------------------------------");
        System.out.println("Practical score:  " + practicalScore + " / 50");
        System.out.printf("Exam score:  %.2f / 50\n", examScore);
        System.out.printf("Total score:  %.2f / 100\n", totalScore);
        System.out.println("Final status:  " + (isPassed ? "PASSED" : "FAILED"));
        System.out.println("========================================");

        scanner.close();
    }
}