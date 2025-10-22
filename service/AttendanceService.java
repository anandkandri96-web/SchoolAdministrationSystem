package com.schooladmin.service;

import com.schooladmin.dao.AttendanceDAO;
import com.schooladmin.dao.StudentDAO;
import com.schooladmin.model.Attendance;
import com.schooladmin.model.Employee;
import com.schooladmin.model.Student;
import com.schooladmin.utils.CLIUtils;

import java.sql.Date;
import java.util.List;

public class AttendanceService {
    private AttendanceDAO attDAO = new AttendanceDAO();
    private StudentDAO studentDAO = new StudentDAO();

    /**
     * Main menu for managing attendance (called from role-based CLI).
     * @param user Logged-in employee (checks role).
     */
    public void manageAttendance(Employee user) {
        if (!"Teacher".equals(user.getRole()) && !"Admin".equals(user.getRole())) {
            System.out.println("Access denied. Only Teachers/Admins can manage attendance.");
            return;
        }

        while (true) {
            System.out.println("\n=== ATTENDANCE MANAGEMENT (CRUD) ===");
            System.out.println("1. Mark Attendance");
            System.out.println("2. View All Attendance");
            System.out.println("3. View Attendance by Date");
            System.out.println("4. View Student Attendance");
            System.out.println("5. Update Attendance");
            System.out.println("6. Delete Attendance");
            System.out.println("0. Back to Menu");
            int choice = CLIUtils.getMenuChoice(6);  // 1-6, 0 for back
            if (choice == 0) {
                System.out.println("Returning to previous menu.");
                break;
            }

            switch (choice) {
                case 1:
                    markAttendance(user);
                    break;
                case 2:
                    viewAllAttendance();
                    break;
                case 3:
                    Date date = CLIUtils.getDateInput("Enter date for attendance view");
                    viewAttendanceByDate(date);
                    break;
                case 4:
                    int studentId = CLIUtils.getIntInput("Enter student ID: ");
                    viewStudentAttendance(studentId);
                    break;
                case 5:
                    int attId = CLIUtils.getIntInput("Enter attId to update: ");
                    updateAttendance(user, attId);
                    break;
                case 6:
                    int studentIdForDelete = CLIUtils.getIntInput("Enter student ID to delete attendance for: ");
                    deleteAttendanceByStudent(user, studentIdForDelete);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
            CLIUtils.pause();
        }
    }

    /**
     * Mark attendance for students on a date (CRUD: Create/Update).
     * Loops for multiple students until 0 entered.
     * @param teacher Logged-in Teacher/Admin (sets teacherId).
     * @return true if any success.
     */
    private boolean markAttendance(Employee teacher) {
        System.out.println("\nAvailable Students:");
        for (Student s : studentDAO.readAll()) {
            System.out.println("ID: " + s.getStudentId() + ", Name: " + s.getName());
        }

        Date date = CLIUtils.getDateInput("Enter date for marking attendance");
        System.out.println("Enter student IDs one by one (0 to finish marking):");
        boolean anySuccess = false;

        while (true) {
            int studentId = CLIUtils.getIntInput("Student ID: ");
            if (studentId == 0) {
                break;  // Finish marking
            }

            Student student = studentDAO.readById(studentId);
            if (student == null) {
                System.out.println("Student not found. Skipping.");
                continue;
            }

            // Check if record exists for this student/date
            Attendance existingAtt = attDAO.readByStudentAndDate(studentId, date);
            Attendance att;
            if (existingAtt == null) {
                att = new Attendance();
                att.setStudentId(studentId);
                att.setDate(date);
            } else {
                att = existingAtt;
                System.out.println("Existing status for " + student.getName() + ": " + att.getStatus());
            }

            String statusInput = CLIUtils.getStringInput("Status (Present/Absent/Late): ");
            if (!statusInput.matches("(?i)(Present|Absent|Late)")) {
                System.out.println("Invalid status. Must be Present, Absent, or Late. Skipping.");
                continue;
            }
            att.setStatus(statusInput.substring(0, 1).toUpperCase() + statusInput.substring(1).toLowerCase());  // Standardize case
            att.setTeacherId(teacher.getEmpId());  // Link to teacher who marked

            boolean success;
            if (existingAtt == null) {
                success = attDAO.create(att);
                System.out.println("New attendance marked for " + student.getName() + ": " + att.getStatus());
            } else {
                success = attDAO.update(att);
                System.out.println("Attendance updated for " + student.getName() + ": " + att.getStatus());
            }

            if (success) {
                anySuccess = true;
            } else {
                System.out.println("Failed to mark/update attendance for " + student.getName());
            }
        }

        if (anySuccess) {
            System.out.println("Attendance marking session completed.");
        } else {
            System.out.println("No attendance was marked/updated.");
        }
        return anySuccess;
    }

    /**
     * View all attendance records (CRUD: Read All).
     */
    private void viewAllAttendance() {
        CLIUtils.printList(attDAO.readAll(), "All Attendance Records");
    }

    /**
     * View attendance by date.
     * @param date The date to view.
     */
    private void viewAttendanceByDate(Date date) {
        CLIUtils.printList(attDAO.readByDate(date), "Attendance for " + date);
    }

    /**
     * View attendance for a specific student (Search by Student).
     * @param studentId ID of student.
     */
    private void viewStudentAttendance(int studentId) {
        Student student = studentDAO.readById(studentId);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        CLIUtils.printList(attDAO.searchByStudent(studentId), "Attendance for " + student.getName());
    }

    /**
     * Update an existing attendance record (CRUD: Update).
     * @param user Logged-in user (updates teacherId).
     * @param attId ID of attendance to update.
     * @return true if successful.
     */
    private boolean updateAttendance(Employee user, int attId) {
        Attendance existing = attDAO.readById(attId);
        if (existing == null) {
            System.out.println("Attendance record not found.");
            return false;
        }

        Student student = studentDAO.readById(existing.getStudentId());
        System.out.println("Current record for " + (student != null ? student.getName() : "Unknown Student") +
                ": Date " + existing.getDate() + ", Status: " + existing.getStatus());

        String newStatusInput = CLIUtils.getStringInput("New status (Present/Absent/Late, enter to skip): ");
        if (newStatusInput.isEmpty()) {
            System.out.println("No changes. Skipping update.");
            return false;
        }
        if (!newStatusInput.matches("(?i)(Present|Absent|Late)")) {
            System.out.println("Invalid status. Update cancelled.");
            return false;
        }
        existing.setStatus(newStatusInput.substring(0, 1).toUpperCase() + newStatusInput.substring(1).toLowerCase());
        existing.setTeacherId(user.getEmpId());  // Re-link to current user

        boolean success = attDAO.update(existing);
        if (success) {
            System.out.println("Attendance updated successfully: " + existing.getStatus() +
                    " for " + (student != null ? student.getName() : "Student ID " + existing.getStudentId()));
        } else {
            System.out.println("Update failed (e.g., database error).");
        }
        CLIUtils.pause();
        return success;
    }

    /**
     * Delete attendance records by student - lists all records, lets user pick which to delete.
     * @param user Logged-in user
     * @param studentId Student whose attendance records are to be deleted
     * @return true if deletion successful
     */
    private boolean deleteAttendanceByStudent(Employee user, int studentId) {
        Student student = studentDAO.readById(studentId);
        if (student == null) {
            System.out.println("Student not found.");
            CLIUtils.pause();
            return false;
        }

        List<Attendance> attendances = attDAO.searchByStudent(studentId);
        if (attendances.isEmpty()) {
            System.out.println("No attendance records found for " + student.getName());
            CLIUtils.pause();
            return false;
        }

        System.out.println("Attendance records for " + student.getName() + ":");
        for (int i = 0; i < attendances.size(); i++) {
            Attendance att = attendances.get(i);
            System.out.printf("%d. Date: %s, Status: %s%n", i + 1, att.getDate(), att.getStatus());
        }

        int choice = CLIUtils.getIntInput("Enter the number of the attendance record to delete (0 to cancel): ");
        if (choice == 0) {
            System.out.println("Delete cancelled.");
            CLIUtils.pause();
            return false;
        }
        if (choice < 1 || choice > attendances.size()) {
            System.out.println("Invalid choice.");
            CLIUtils.pause();
            return false;
        }

        Attendance toDelete = attendances.get(choice - 1);

        String confirm = CLIUtils.getStringInput(
                "Delete attendance for " + student.getName() + "? Date: " + toDelete.getDate() + ", Status: " + toDelete.getStatus() + " (y/n): "
        );
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Delete cancelled.");
            CLIUtils.pause();
            return false;
        }

        boolean success = attDAO.delete(toDelete.getAttId());
        if (success) {
            System.out.println("Attendance record deleted successfully.");
        } else {
            System.out.println("Failed to delete attendance record.");
        }
        CLIUtils.pause();
        return success;
    }
}
