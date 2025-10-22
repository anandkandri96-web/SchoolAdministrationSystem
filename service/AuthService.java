package com.schooladmin.service;

import com.schooladmin.dao.EmployeeDAO;
import com.schooladmin.model.Employee;
import com.schooladmin.utils.CLIUtils;
import com.schooladmin.utils.PasswordUtils;

public class AuthService {
    private EmployeeDAO empDAO = new EmployeeDAO();

    public Employee login() {
        String username = CLIUtils.getStringInput("Enter username: ");
        String password = CLIUtils.getStringInput("Enter password: ");

        Employee emp = empDAO.findByUsername(username);
        if (emp != null && PasswordUtils.verifyPassword(password, emp.getPassword())) {
            System.out.println("Login successful! Welcome, " + emp.getName() + " (" + emp.getRole() + ")");
            return emp;
        }
        System.out.println("Invalid credentials. Try again.");
        return null;
    }

    public boolean register(Employee currentUser) {
        if (!"Admin".equals(currentUser.getRole())) {
            System.out.println("Access denied. Only Admin can register new employees.");
            return false;
        }

        Employee newEmp = new Employee();

        // Name input with validation loop
        String name;
        do {
            name = CLIUtils.getStringInput("Enter name: ").trim();
            if (name.isEmpty()) System.out.println("Name cannot be empty.");
        } while (name.isEmpty());
        newEmp.setName(name);

        // Username input with validation loop
        String username;
        do {
            username = CLIUtils.getStringInput("Enter username: ").trim();
            if (username.isEmpty()) System.out.println("Username cannot be empty.");
        } while (username.isEmpty());
        newEmp.setUsername(username);

        // Password input with validation loop
        String plainPass;
        do {
            plainPass = CLIUtils.getStringInput("Enter password (at least 4 characters): ");
            if (plainPass.length() < 4) System.out.println("Password must be at least 4 characters.");
        } while (plainPass.length() < 4);
        newEmp.setPassword(PasswordUtils.hashPassword(plainPass));

        // Role input with validation loop
        String roleInput;
        do {
            roleInput = CLIUtils.getStringInput("Enter role (Admin/Clerk/Teacher/Principal): ").trim();
            if (!roleInput.matches("(?i)(Admin|Clerk|Teacher|Principal)")) {
                System.out.println("Invalid role. Must be Admin, Clerk, Teacher, or Principal.");
            }
        } while (!roleInput.matches("(?i)(Admin|Clerk|Teacher|Principal)"));
        newEmp.setRole(roleInput.substring(0, 1).toUpperCase() + roleInput.substring(1).toLowerCase());

        // Email input (optional) with validation loop
        while (true) {
            String email = CLIUtils.getStringInput("Enter email (optional, press enter to skip): ").trim();
            if (email.isEmpty()) {
                newEmp.setEmail("");
                break;
            }
            if (email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
                newEmp.setEmail(email);
                break;
            } else {
                System.out.println("Invalid email format. Please enter a valid email.");
            }
        }

        // Phone input (optional) with validation loop
        while (true) {
            String phone = CLIUtils.getStringInput("Enter phone (10 digits, starts with 6/7/8/9, optional, press enter to skip): ").trim();
            if (phone.isEmpty()) {
                newEmp.setPhone("");
                break;
            }
            if (phone.matches("[6-9][0-9]{9}")) {
                newEmp.setPhone(phone);
                break;
            } else {
                System.out.println("Invalid phone number. Must be 10 digits and start with 6,7,8 or 9.");
            }
        }

        boolean success = empDAO.create(newEmp);
        if (success) {
            System.out.println("Employee registered successfully. Role: " + newEmp.getRole());
        } else {
            System.out.println("Registration failed (e.g., duplicate username).");
        }
        CLIUtils.pause();
        return success;
    }

    public void manageEmployees(Employee admin) {
        if (!"Admin".equals(admin.getRole())) {
            System.out.println("Access denied. Only Admin can manage employees.");
            return;
        }

        System.out.println("\n=== MANAGE EMPLOYEES (CRUD) ===");
        System.out.println("1. Register New Employee");
        System.out.println("2. View All Employees");
        System.out.println("3. Search Employees by Name");
        System.out.println("4. Update Employee");
        System.out.println("5. Delete Employee");
        System.out.println("0. Back to Menu");
        int choice = CLIUtils.getMenuChoice(5);
        if (choice == 0) return;

        switch (choice) {
            case 1:
                register(admin);
                break;
            case 2:
                viewAllEmployees();
                break;
            case 3:
                searchEmployees();
                break;
            case 4:
                int empId = CLIUtils.getIntInput("Enter empId to update: ");
                updateEmployee(admin, empId);
                break;
            case 5:
                empId = CLIUtils.getIntInput("Enter empId to delete: ");
                deleteEmployee(admin, empId);
                break;
        }
    }

    private void viewAllEmployees() {
        CLIUtils.printList(empDAO.readAll(), "Employees");
    }

    private void searchEmployees() {
        String name = CLIUtils.getStringInput("Enter name to search: ");
        CLIUtils.printList(empDAO.searchByName(name), "Employees");
        CLIUtils.pause();
    }

    private boolean updateEmployee(Employee admin, int empId) {
        Employee existing = empDAO.readById(empId);
        if (existing == null) {
            System.out.println("Employee not found.");
            CLIUtils.pause();
            return false;
        }

        System.out.println("Current details: " + existing.toString());

        boolean isUpdated = false;

        // New name with optional input loop
        while (true) {
            String newName = CLIUtils.getOptionalStringInput("New name (enter to skip): ").trim();
            if (newName.isEmpty()) break;
            existing.setName(newName);
            isUpdated = true;
            break;
        }

        // New username with optional input loop
        while (true) {
            String newUsername = CLIUtils.getOptionalStringInput("New username (enter to skip): ").trim();
            if (newUsername.isEmpty()) break;
            existing.setUsername(newUsername);
            isUpdated = true;
            break;
        }

        // New password with validation
        while (true) {
            String newPass = CLIUtils.getOptionalStringInput("New password (enter to skip): ");
            if (newPass.isEmpty()) break;
            if (newPass.length() < 4) {
                System.out.println("Password must be at least 4 characters. Password not changed.");
            } else {
                existing.setPassword(PasswordUtils.hashPassword(newPass));
                isUpdated = true;
                break;
            }
        }

        // New role with validation loop
        while (true) {
            String newRole = CLIUtils.getOptionalStringInput("New role (Admin/Clerk/Teacher/Principal, enter to skip): ").trim();
            if (newRole.isEmpty()) break;
            if (!newRole.matches("(?i)(Admin|Clerk|Teacher|Principal)")) {
                System.out.println("Invalid role. Must be Admin, Clerk, Teacher, or Principal.");
            } else {
                existing.setRole(newRole.substring(0, 1).toUpperCase() + newRole.substring(1).toLowerCase());
                isUpdated = true;
                break;
            }
        }

        // New email with validation loop
        while (true) {
            String newEmail = CLIUtils.getOptionalStringInput("New email (enter to skip): ").trim();
            if (newEmail.isEmpty()) break;
            if (newEmail.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
                existing.setEmail(newEmail);
                isUpdated = true;
                break;
            } else {
                System.out.println("Invalid email format. Please enter a valid email.");
            }
        }

        // New phone with validation loop
        while (true) {
            String newPhone = CLIUtils.getOptionalStringInput("New phone (10 digits, starts with 6/7/8/9, enter to skip): ").trim();
            if (newPhone.isEmpty()) break;
            if (newPhone.matches("[6-9][0-9]{9}")) {
                existing.setPhone(newPhone);
                isUpdated = true;
                break;
            } else {
                System.out.println("Invalid phone number. Must be 10 digits and start with 6,7,8 or 9.");
            }
        }

        if (!isUpdated) {
            System.out.println("Employee not updated.");
            CLIUtils.pause();
            return false;
        }

        if (existing.getName().isEmpty() || existing.getUsername().isEmpty()) {
            System.out.println("Invalid updated details: Name and username required.");
            CLIUtils.pause();
            return false;
        }

        boolean success = empDAO.update(existing);
        if (success) {
            System.out.println("Employee updated successfully.");
        } else {
            System.out.println("Update failed (e.g., duplicate username).");
        }
        CLIUtils.pause();
        return success;
    }

    private boolean deleteEmployee(Employee admin, int empId) {
        Employee existing = empDAO.readById(empId);
        if (existing == null) {
            System.out.println("Employee not found.");
            CLIUtils.pause();
            return false;
        }

        if (existing.getEmpId() == admin.getEmpId()) {
            System.out.println("Cannot delete yourself.");
            CLIUtils.pause();
            return false;
        }

        System.out.println("Delete: " + existing.toString());

        System.out.println(" WARNING: Deleting this employee may fail if records exist in related tables.");
        System.out.println("Foreign Key Constraints may exist in the following:");
        System.out.println("- Attendance (teacherId)");
        System.out.println("- Performance (teacherId)");
        System.out.println("- Syllabus (teacherId)");
        System.out.println("- Schedule (teacherId, principalId)");
        System.out.println("- Students or Fees (empId / paidByEmpId)");
        System.out.println("You must delete/update those records first.");

        String confirm;
        do {
            confirm = CLIUtils.getStringInput("Are you sure you want to delete? (y/n): ").trim().toLowerCase();
            if (!confirm.equals("y") && !confirm.equals("n") && !confirm.equals("yes") && !confirm.equals("no")) {
                System.out.println("Please enter 'y' or 'n'.");
            }
        } while (!confirm.equals("y") && !confirm.equals("n") && !confirm.equals("yes") && !confirm.equals("no"));

        if (!confirm.startsWith("y")) {
            System.out.println("Delete cancelled.");
            CLIUtils.pause();
            return false;
        }

        boolean success = empDAO.delete(empId);
        if (success) {
            System.out.println("Employee deleted successfully.");
        } else {
            System.out.println(" Delete failed due to foreign key constraints. Clean related records first.");
        }
        CLIUtils.pause();
        return success;
    }
}
