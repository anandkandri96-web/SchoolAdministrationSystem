package com.schooladmin.service;

import com.schooladmin.dao.StudentDAO;
import com.schooladmin.model.Employee;
import com.schooladmin.model.Student;
import com.schooladmin.utils.CLIUtils;

import java.sql.Date;

public class AdmissionService {
    private StudentDAO studentDAO = new StudentDAO();

    public void manageAdmissions(Employee user) {
        if (!"Clerk".equals(user.getRole()) && !"Admin".equals(user.getRole())) {
            System.out.println("Access denied. Only Clerks/Admins can manage admissions.");
            return;
        }

        System.out.println("\n=== ADMISSION MANAGEMENT (CRUD) ===");
        System.out.println("1. Admit New Student(s)");
        System.out.println("2. View All Students");
        System.out.println("3. Search Students");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("0. Back to Menu");
        int choice = CLIUtils.getMenuChoice(5);
        if (choice == 0) return;

        switch (choice) {
            case 1:
                admitMultipleStudents(user);
                break;
            case 2:
                viewAllStudents();
                break;
            case 3:
                searchStudents();
                break;
            case 4:
                int studentId = CLIUtils.getIntInput("Enter studentId to update: ");
                updateStudent(user, studentId);
                break;
            case 5:
                studentId = CLIUtils.getIntInput("Enter studentId to delete: ");
                deleteStudent(user, studentId);
                break;
        }
        CLIUtils.pause();
    }

    /**
     * Admit multiple students in a loop until 0 is entered.
     * @param clerk Logged-in clerk/admin.
     */
    private void admitMultipleStudents(Employee clerk) {
        System.out.println("\nEnter student details to admit new students.");
        System.out.println("Enter 0 as student name to stop admitting.");

        while (true) {
            String name = CLIUtils.getStringInput("Enter student name (0 to stop): ").trim();
            if ("0".equals(name)) {
                System.out.println("Admission session completed.");
                break;
            }
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty. Please try again.");
                continue;
            }

            Student student = new Student();
            student.setName(name);

            // Parent name
            String parentName = CLIUtils.getStringInput("Enter parent name: ").trim();
            student.setParentName(parentName);

            // Parent phone with validation
            while (true) {
                String phone = CLIUtils.getStringInput("Enter parent phone (10 digits, starts with 6-9): ").trim();
                if (phone.matches("[6-9][0-9]{9}")) {
                    student.setParentPhone(phone);
                    break;
                } else {
                    System.out.println("Invalid phone number. Please try again.");
                }
            }

            // Address
            String address = CLIUtils.getStringInput("Enter address: ").trim();
            student.setAddress(address);

            // Class name validation
            while (true) {
                String className = CLIUtils.getStringInput("Enter class name (e.g., Class 10): ").trim();
                if (className.matches("[a-zA-Z0-9 ]+")) {
                    student.setClassName(className);
                    break;
                } else {
                    System.out.println("Invalid class name. Use alphanumeric and spaces only.");
                }
            }

            // Section validation
            while (true) {
                String section = CLIUtils.getStringInput("Enter section (single letter, e.g., A): ").trim().toUpperCase();
                if (section.matches("[A-Z]")) {
                    student.setSection(section);
                    break;
                } else {
                    System.out.println("Invalid section. Enter a single uppercase letter (A-Z).");
                }
            }

            // Previous performance (optional)
            String prevPerf = CLIUtils.getStringInput("Enter previous performance (e.g., 85%, optional): ").trim();
            student.setPreviousPerformance(prevPerf);

            // Admission date
            Date admissionDate = CLIUtils.getDateInput("Enter admission date (yyyy-MM-dd): ");
            student.setAdmissionDate(admissionDate);

            student.setEmpId(clerk.getEmpId());

            boolean success = studentDAO.create(student);
            if (success) {
                System.out.println(" Student admitted successfully: " + student.getName() +
                        " (" + student.getClassName() + " " + student.getSection() + ")");
            } else {
                System.out.println(" Admission failed (possible database error).");
            }

            System.out.println();  // Extra line for clarity between entries
        }
    }

    private void viewAllStudents() {
        CLIUtils.printList(studentDAO.readAll(), "Students");
    }

    private void searchStudents() {
        System.out.println("1. Search by Name\n2. Search by Class/Section");
        int searchChoice = CLIUtils.getMenuChoice(2);
        if (searchChoice == 1) {
            String name = CLIUtils.getStringInput("Enter name to search: ");
            CLIUtils.printList(studentDAO.searchByName(name), "Students");
        } else {
            String className = CLIUtils.getStringInput("Enter class name: ");
            String section = CLIUtils.getStringInput("Enter section: ");
            CLIUtils.printList(studentDAO.searchByClass(className, section), "Students");
        }
    }

    private boolean updateStudent(Employee user, int studentId) {
        Student existing = studentDAO.readById(studentId);
        if (existing == null) {
            System.out.println("Student not found.");
            return false;
        }

        System.out.println("Current details: " + existing);

        String input;

        input = CLIUtils.getStringInput("New name (enter to skip): ").trim();
        if (!input.isEmpty()) existing.setName(input);

        input = CLIUtils.getStringInput("New parent name (enter to skip): ").trim();
        if (!input.isEmpty()) existing.setParentName(input);

        input = CLIUtils.getStringInput("New parent phone (enter to skip): ").trim();
        if (!input.isEmpty()) {
            if (input.matches("[6-9][0-9]{9}")) {
                existing.setParentPhone(input);
            } else {
                System.out.println("Invalid phone. Skipping update.");
            }
        }

        input = CLIUtils.getStringInput("New address (enter to skip): ").trim();
        if (!input.isEmpty()) existing.setAddress(input);

        input = CLIUtils.getStringInput("New class name (enter to skip): ").trim();
        if (!input.isEmpty()) {
            if (input.matches("[a-zA-Z0-9 ]+")) {
                existing.setClassName(input);
            } else {
                System.out.println("Invalid class name. Skipping.");
            }
        }

        input = CLIUtils.getStringInput("New section (enter to skip): ").trim().toUpperCase();
        if (!input.isEmpty()) {
            if (input.matches("[A-Z]")) {
                existing.setSection(input);
            } else {
                System.out.println("Invalid section. Skipping.");
            }
        }

        input = CLIUtils.getStringInput("New previous performance (enter to skip): ").trim();
        if (!input.isEmpty()) existing.setPreviousPerformance(input);

        input = CLIUtils.getStringInput("New admission date (yyyy-MM-dd, enter to skip): ").trim();
        if (!input.isEmpty()) {
            try {
                existing.setAdmissionDate(Date.valueOf(input));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date format. Skipping.");
            }
        }

        existing.setEmpId(user.getEmpId());

        if (existing.getName().isEmpty() || existing.getClassName().isEmpty() || existing.getSection().isEmpty()) {
            System.out.println(" Update failed: Name, class, and section are required.");
            return false;
        }

        boolean success = studentDAO.update(existing);
        if (success) {
            System.out.println(" Student updated successfully.");
        } else {
            System.out.println(" Update failed.");
        }

        CLIUtils.pause();
        return success;
    }

    private boolean deleteStudent(Employee user, int studentId) {
        Student existing = studentDAO.readById(studentId);
        if (existing == null) {
            System.out.println("Student not found.");
            CLIUtils.pause();
            return false;
        }

        System.out.println(" WARNING: Deleting this student may fail if records exist in related tables like:");
        System.out.println("- Fees (studentId)");
        System.out.println("- Attendance (studentId)");
        System.out.println("- Performance (studentId)");
        System.out.println("You must delete those records first if constraints exist.");

        System.out.println("Delete: " + existing.getName() + " (ID: " + studentId + ")");
        String confirm = CLIUtils.getStringInput("Are you sure? (y/n): ");
        if (!confirm.trim().toLowerCase().startsWith("y")) {
            System.out.println("Delete cancelled.");
            CLIUtils.pause();
            return false;
        }

        boolean success = studentDAO.delete(studentId);
        if (success) {
            System.out.println(" Student deleted successfully.");
        } else {
            System.out.println(" Delete failed. Possibly due to foreign key constraints.");
        }
        CLIUtils.pause();
        return success;
    }
}
