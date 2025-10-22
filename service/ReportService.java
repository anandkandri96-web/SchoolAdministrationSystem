package com.schooladmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.schooladmin.dao.AttendanceDAO;
import com.schooladmin.dao.PerformanceDAO;
import com.schooladmin.dao.StudentDAO;
import com.schooladmin.dao.SyllabusDAO;
import com.schooladmin.db.DBConnection;
import com.schooladmin.model.Employee;
import com.schooladmin.model.Student;
import com.schooladmin.model.Syllabus;
import com.schooladmin.utils.CLIUtils;

public class ReportService {
    private StudentDAO studentDAO = new StudentDAO();
    private SyllabusDAO syllabusDAO = new SyllabusDAO();
    private PerformanceDAO perfDAO = new PerformanceDAO();
    private AttendanceDAO attDAO = new AttendanceDAO();

    /**
     * Main menu for generating reports (called from role-based CLI).
     * @param user Logged-in employee (checks role).
     */
    public void generateReports(Employee user) {
        if (!"Principal".equals(user.getRole()) && !"Admin".equals(user.getRole())) {
            System.out.println("Access denied. Only Principals/Admins can generate reports.");
            return;
        }

        while (true) {
            System.out.println("\n=== REPORTS MENU ===");
            System.out.println("1. Students Admission Report");
            System.out.println("2. Syllabus Details Report");
            System.out.println("3. Classes and Students Report");
            System.out.println("4. Students Performance Report");
            System.out.println("5. Students Attendance Report");
            System.out.println("0. Back to Menu");
            int choice = CLIUtils.getMenuChoice(5);  // 1-5, 0 for back

            if (choice == 0) {
                break;
            }

            switch (choice) {
                case 1:
                    generateAdmissionReport();
                    break;
                case 2:
                    generateSyllabusReport();
                    break;
                case 3:
                    generateClassesStudentsReport();
                    break;
                case 4:
                    generatePerformanceReport();
                    break;
                case 5:
                    generateAttendanceReport();
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
            CLIUtils.pause();
        }
    }

    /**
     * Report 1: Students Admission Report (Req 8a) - List all students with details.
     * Optional filter by class.
     */
    private void generateAdmissionReport() {
        System.out.println("1. All Admissions\n2. By Class");
        int choice = CLIUtils.getMenuChoice(2);
        List<Student> students;
        if (choice == 1) {
            students = studentDAO.readAll();
        } else {
            String className = CLIUtils.getStringInput("Enter class: ");
            String section = CLIUtils.getStringInput("Enter section: ");
            students = studentDAO.searchByClass(className, section);
        }

        if (students.isEmpty()) {
            System.out.println("No admissions found.");
            return;
        }

        // Print header
        CLIUtils.printTableHeader(new String[]{"Student ID", "Name", "Class/Section", "Parent", "Admission Date", "Previous Perf"});
        for (Student s : students) {
            System.out.format("%-10d %-15s %-15s %-20s %-12s %s%n",
                    s.getStudentId(), s.getName(), s.getClassName() + "/" + s.getSection(),
                    s.getParentName(), s.getAdmissionDate(), s.getPreviousPerformance());
        }
        System.out.println("Total Admissions: " + students.size());
    }

    /**
     * Report 2: Syllabus Details Report (Req 8b) - All or filtered syllabus.
     */
    private void generateSyllabusReport() {
        System.out.println("1. All Syllabus\n2. By Class/Subject");
        int choice = CLIUtils.getMenuChoice(2);
        List<Syllabus> syllabi;
        if (choice == 1) {
            syllabi = syllabusDAO.readAll();
        } else {
            String className = CLIUtils.getStringInput("Enter class: ");
            String subject = CLIUtils.getStringInput("Enter subject: ");
            syllabi = syllabusDAO.searchByClassAndSubject(className, subject);
        }

        if (syllabi.isEmpty()) {
            System.out.println("No syllabus found.");
            return;
        }

        CLIUtils.printTableHeader(new String[]{"ID", "Class", "Subject", "Chapter", "Topic", "Last Updated"});
        for (Syllabus syl : syllabi) {
            System.out.format("%-5d %-10s %-15s %-8d %-30s %s%n",
                    syl.getSyllabusId(), syl.getClassName(), syl.getSubject(),
                    syl.getChapterNo(), syl.getTopic(), syl.getLastUpdated());
        }
        System.out.println("Total Entries: " + syllabi.size());
    }

    /**
     * Report 3: Classes and Students Report (Req 9) - JOIN Schedule and Students to show classes with enrolled students.
     * Custom query for aggregation.
     */
    private void generateClassesStudentsReport() {
        String sql = """
                SELECT s.className, s.section, COUNT(st.studentId) as studentCount, 
                       GROUP_CONCAT(DISTINCT st.name SEPARATOR ', ') as studentNames
                FROM Schedule s 
                LEFT JOIN Students st ON s.className = st.className AND s.section = st.section 
                GROUP BY s.className, s.section 
                ORDER BY s.className, s.section
                """;

        List<Object[]> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("className");
                row[1] = rs.getString("section");
                row[2] = rs.getInt("studentCount");
                row[3] = rs.getString("studentNames");
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Report generation failed.");
            return;
        }

        if (results.isEmpty()) {
            System.out.println("No classes found.");
            return;
        }

        CLIUtils.printTableHeader(new String[]{"Class", "Section", "Student Count", "Students (Names)"});
        for (Object[] row : results) {
            System.out.format("%-10s %-8s %-14d %s%n",
                    row[0], row[1], row[2], (row[3] != null ? row[3] : "None"));
        }
        System.out.println("Total Classes: " + results.size());
    }

    /**
     * Report 4: Students Performance Report (Req 10) - Marks and averages per student/class.
     * Custom query for averages.
     */
    private void generatePerformanceReport() {
        System.out.println("1. All Performance\n2. By Student\n3. Class Averages");
        int choice = CLIUtils.getMenuChoice(3);
        if (choice == 1) {
            CLIUtils.printList(perfDAO.readAll(), "Performance");
            return;
        } else if (choice == 2) {
            int studentId = CLIUtils.getIntInput("Enter student ID: ");
            CLIUtils.printList(perfDAO.readByStudentId(studentId), "Student Performance");
            return;
        }

        // Class Averages (custom query)
        String sql = """
                SELECT st.className, st.section, st.studentId, st.name, 
                       AVG(p.marks * 1.0 / p.totalMarks * 100) as avgPercentage
                FROM Students st 
                LEFT JOIN Performance p ON st.studentId = p.studentId 
                GROUP BY st.studentId 
                ORDER BY st.className, avgPercentage DESC
                """;

        List<Object[]> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getString("className");
                row[1] = rs.getString("section");
                row[2] = rs.getInt("studentId");
                row[3] = rs.getString("name");
                row[4] = String.format("%.2f", rs.getDouble("avgPercentage")) + "%";
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Report generation failed.");
            return;
        }

        if (results.isEmpty()) {
            System.out.println("No performance data found.");
            return;
        }

        CLIUtils.printTableHeader(new String[]{"Class", "Section", "Student ID", "Name", "Avg %"});
        for (Object[] row : results) {
            System.out.format("%-10s %-8s %-10d %-15s %s%n", row[0], row[1], row[2], row[3], row[4]);
        }
        System.out.println("Total Students: " + results.size());
    }

    /**
     * Report 5: Students Attendance Report (Req 10) - Summary (present/absent counts) per student or date.
     * Custom query for counts.
     */
    private void generateAttendanceReport() {
        System.out.println("1. All Attendance\n2. By Student\n3. By Date Summary");
        int choice = CLIUtils.getMenuChoice(3);
        if (choice == 1) {
            CLIUtils.printList(attDAO.readAll(), "Attendance");
            return;
        } else if (choice == 2) {
            int studentId = CLIUtils.getIntInput("Enter student ID: ");
            CLIUtils.printList(attDAO.searchByStudent(studentId), "Student Attendance");
            return;
        }

        // Date Summary (custom query: counts per student)
        java.sql.Date date = CLIUtils.getDateInput("Enter date for summary");
        String sql = """
                SELECT st.studentId, st.name, st.className, 
                       SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) as presentCount,
                       SUM(CASE WHEN a.status = 'Absent' THEN 1 ELSE 0 END) as absentCount,
                       SUM(CASE WHEN a.status = 'Late' THEN 1 ELSE 0 END) as lateCount
                FROM Students st 
                LEFT JOIN Attendance a ON st.studentId = a.studentId AND a.date = ? 
                GROUP BY st.studentId 
                ORDER BY st.className, presentCount DESC
                """;

        List<Object[]> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[6];
                    row[0] = rs.getInt("studentId");
                    row[1] = rs.getString("name");
                    row[2] = rs.getString("className");
                    row[3] = rs.getInt("presentCount");
                    row[4] = rs.getInt("absentCount");
                    row[5] = rs.getInt("lateCount");
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Report generation failed.");
            return;
        }

        if (results.isEmpty()) {
            System.out.println("No attendance data for " + date);
            return;
        }

        CLIUtils.printTableHeader(new String[]{"Student ID", "Name", "Class", "Present", "Absent", "Late"});
        for (Object[] row : results) {
            System.out.format("%-10d %-15s %-10s %-8d %-8d %-5d%n",
                    row[0], row[1], row[2], row[3], row[4], row[5]);
        }
        System.out.println("Date: " + date + " | Total Records: " + results.size());
    }
}
