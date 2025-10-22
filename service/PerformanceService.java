package com.schooladmin.service;

import com.schooladmin.dao.PerformanceDAO;
import com.schooladmin.dao.StudentDAO;
import com.schooladmin.model.Employee;
import com.schooladmin.model.Performance;
import com.schooladmin.model.Student;
import com.schooladmin.utils.CLIUtils;

import java.sql.Date;

public class PerformanceService {
    private PerformanceDAO perfDAO = new PerformanceDAO();
    private StudentDAO studentDAO = new StudentDAO();

    /**
     * Main menu for managing student performance (called from role-based CLI).
     * @param user Logged-in employee (checks role).
     */
    public void managePerformance(Employee user) {
        if (!"Teacher".equals(user.getRole()) && !"Admin".equals(user.getRole())) {
            System.out.println("Access denied. Only Teachers/Admins can manage performance.");
            return;
        }

        while (true) {  // Loop added for repeated menu display
            System.out.println("\n=== STUDENT PERFORMANCE MANAGEMENT (CRUD) ===");
            System.out.println("1. Add Performance Record");
            System.out.println("2. View All Performance");
            System.out.println("3. View Student Performance");
            System.out.println("4. Search Performance by Subject");
            System.out.println("5. Update Performance");
            System.out.println("6. Delete Performance");
            System.out.println("0. Back to Menu");
            int choice = CLIUtils.getMenuChoice(6);  // 1-6, 0 for back
            if (choice == 0) {
                break;  // Exit the menu loop
            }

            switch (choice) {
                case 1:
                    addPerformance(user);
                    break;
                case 2:
                    viewAllPerformance();
                    break;
                case 3:
                    System.out.println("\nAvailable Students:");
                    for (Student s : studentDAO.readAll()) {
                        System.out.println("ID: " + s.getStudentId() + ", Name: " + s.getName());
                    }
                    int studentId = CLIUtils.getIntInput("Enter student ID: ");
                    viewStudentPerformance(studentId);
                    break;
                case 4:
                    String subject = CLIUtils.getStringInput("Enter subject to search: ");
                    searchPerformanceBySubject(subject);
                    break;
                case 5:
                    int perfId = CLIUtils.getIntInput("Enter perfId to update: ");
                    updatePerformance(user, perfId);
                    break;
                case 6:
                    perfId = CLIUtils.getIntInput("Enter perfId to delete: ");
                    deletePerformance(user, perfId);
                    break;
            }
            CLIUtils.pause();
        }
    }

    // ... rest of the methods unchanged ...

    private boolean addPerformance(Employee teacher) {
        int studentId = CLIUtils.getIntInput("Enter student ID: ");
        Student student = studentDAO.readById(studentId);
        if (student == null) {
            System.out.println("Student not found.");
            return false;
        }

        Performance perf = new Performance();
        perf.setStudentId(studentId);
        perf.setSubject(CLIUtils.getStringInput("Enter subject: "));
        perf.setTestName(CLIUtils.getStringInput("Enter test name (e.g., Unit Test 1): "));

        int marks = CLIUtils.getIntInput("Enter marks obtained: ");
        int totalMarks = CLIUtils.getIntInput("Enter total marks: ");

        perf.setMarks(marks);
        perf.setTotalMarks(totalMarks);

        perf.setDate(CLIUtils.getDateInput("Enter test date"));
        perf.setTeacherId(teacher.getEmpId());

        if (perf.getSubject().isEmpty() || perf.getTestName().isEmpty() || 
            marks < 0 || totalMarks <= 0) {
            System.out.println("Invalid details: Subject/test name required; marks >=0, total >0.");
            return false;
        }

        boolean success = perfDAO.create(perf);
        if (success) {
            double percentage = (totalMarks > 0) ? (marks * 100.0 / totalMarks) : 0;
            System.out.println("Performance added successfully for " + student.getName() + 
                               ": " + perf.getTestName() + " (" + marks + "/" + totalMarks + 
                               ", " + String.format("%.2f", percentage) + "%)");
        } else {
            System.out.println("Failed to add performance record.");
        }
        return success;
    }

    private void viewAllPerformance() {
        CLIUtils.printList(perfDAO.readAll(), "All Performance Records");
    }

    private void viewStudentPerformance(int studentId) {
        Student student = studentDAO.readById(studentId);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        CLIUtils.printList(perfDAO.readByStudentId(studentId), "Performance for " + student.getName());
    }

    private void searchPerformanceBySubject(String subject) {
        CLIUtils.printList(perfDAO.searchBySubject(subject), "Performance for Subject '" + subject + "'");
    }

    private boolean updatePerformance(Employee user, int perfId) {
        Performance existing = perfDAO.readById(perfId);
        if (existing == null) {
            System.out.println("Performance record not found.");
            return false;
        }

        Student student = studentDAO.readById(existing.getStudentId());
        System.out.println("Current record for " + (student != null ? student.getName() : "Unknown Student") + 
                           ": " + existing.getTestName() + " (" + existing.getSubject() + 
                           ", " + existing.getMarks() + "/" + existing.getTotalMarks() + 
                           ", Date: " + existing.getDate() + ")");

        String newSubject = CLIUtils.getStringInput("New subject (enter to skip): ");
        if (!newSubject.isEmpty()) {
            existing.setSubject(newSubject);
        }
        String newTestName = CLIUtils.getStringInput("New test name (enter to skip): ");
        if (!newTestName.isEmpty()) {
            existing.setTestName(newTestName);
        }
        int newMarks = CLIUtils.getIntInput("New marks ( -1 to skip): ");
        if (newMarks >= 0) {
            existing.setMarks(newMarks);
        }
        int newTotal = CLIUtils.getIntInput("New total marks (0 to skip): ");
        if (newTotal > 0) {
            existing.setTotalMarks(newTotal);
        }
        String dateInput = CLIUtils.getStringInput("New test date (yyyy-MM-dd, enter to skip): ");
        if (!dateInput.isEmpty()) {
            try {
                existing.setDate(Date.valueOf(dateInput));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date format. Skipping.");
            }
        }
        existing.setTeacherId(user.getEmpId());

        if (existing.getSubject().isEmpty() || existing.getTestName().isEmpty() || 
            existing.getMarks() < 0 || existing.getTotalMarks() <= 0) {
            System.out.println("Invalid updated details: Subject/test name required; marks >=0, total >0.");
            return false;
        }

        boolean success = perfDAO.update(existing);
        if (success) {
            double percentage = (existing.getTotalMarks() > 0) ? (existing.getMarks() * 100.0 / existing.getTotalMarks()) : 0;
            System.out.println("Performance updated successfully: " + existing.getTestName() + 
                               " (" + existing.getMarks() + "/" + existing.getTotalMarks() + 
                               ", " + String.format("%.2f", percentage) + "%)");
        } else {
            System.out.println("Update failed (e.g., database error).");
        }
        CLIUtils.pause();
        return success;
    }

    private boolean deletePerformance(Employee user, int perfId) {
        Performance existing = perfDAO.readById(perfId);
        if (existing == null) {
            System.out.println("Performance record not found.");
            CLIUtils.pause();
            return false;
        }

        Student student = studentDAO.readById(existing.getStudentId());
        System.out.println("Delete performance for " + (student != null ? student.getName() : "Unknown") + 
                           "? " + existing.getTestName() + " (" + existing.getSubject() + 
                           ", " + existing.getMarks() + "/" + existing.getTotalMarks() + 
                           ", Date: " + existing.getDate() + ") (y/n)");

        String confirm = CLIUtils.getStringInput("");
        if (!confirm.toLowerCase().startsWith("y")) {
            System.out.println("Delete cancelled.");
            CLIUtils.pause();
            return false;
        }

        boolean success = perfDAO.delete(perfId);
        if (success) {
            System.out.println("Performance record deleted successfully (ID: " + perfId + ")");
        } else {
            System.out.println("Delete failed (e.g., database error).");
        }
        CLIUtils.pause();
        return success;
    }
}
