package com.schooladmin.service;

import com.schooladmin.dao.FeeDAO;
import com.schooladmin.dao.StudentDAO;
import com.schooladmin.model.Employee;
import com.schooladmin.model.Fee;
import com.schooladmin.model.Student;
import com.schooladmin.utils.CLIUtils;

import java.sql.Date;

public class FeeService {
    private FeeDAO feeDAO = new FeeDAO();
    private StudentDAO studentDAO = new StudentDAO();

    /**
     * Main menu for managing fees (called from role-based CLI).
     * @param user Logged-in employee (checks role).
     */
    public void manageFees(Employee user) {
        if (!"Clerk".equals(user.getRole()) && !"Admin".equals(user.getRole())) {
            System.out.println("Access denied. Only Clerks/Admins can manage fees.");
            return;
        }

        int choice;
        do {
            System.out.println("\n=== FEE MANAGEMENT (CRUD) ===");
            System.out.println("1. Process New Fee");
            System.out.println("2. View All Fees");
            System.out.println("3. View Student Fees");
            System.out.println("4. View Pending/Overdue Fees");
            System.out.println("5. Update Fee");
            System.out.println("6. Delete Fee");
            System.out.println("0. Back to Menu");
            choice = CLIUtils.getMenuChoice(6);  // 0-6
            
            switch (choice) {
                case 1:
                    processFee(user);
                    break;
                case 2:
                    viewAllFees();
                    CLIUtils.pause();
                    break;
                case 3:
                    int studentId = CLIUtils.getIntInput("Enter student ID: ");
                    viewStudentFees(studentId);
                    CLIUtils.pause();
                    break;
                case 4:
                    viewPendingFees();
                    CLIUtils.pause();
                    break;
                case 5:
                    int feeIdToUpdate = CLIUtils.getIntInput("Enter feeId to update: ");
                    updateFee(user, feeIdToUpdate);
                    break;
                case 6:
                    int feeIdToDelete = CLIUtils.getIntInput("Enter feeId to delete: ");
                    deleteFee(user, feeIdToDelete);
                    break;
                case 0:
                    System.out.println("Returning to previous menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 0);
    }

    /**
     * Process a new fee payment (CRUD: Create).
     * @param clerk Logged-in Clerk/Admin (sets paidByEmpId).
     * @return true if successful.
     */
    private boolean processFee(Employee clerk) {
        Student student = null;
        int studentId;
        while (true) {
            studentId = CLIUtils.getIntInput("Enter student ID: ");
            student = studentDAO.readById(studentId);
            if (student == null) {
                System.out.println("Student not found. Please enter a valid student ID.");
            } else {
                break;
            }
        }

        Fee fee = new Fee();
        fee.setStudentId(studentId);

        double amount;
        while (true) {
            amount = CLIUtils.getDoubleInput("Enter fee amount: ");
            if (amount <= 0) {
                System.out.println("Invalid amount: Must be positive.");
            } else {
                break;
            }
        }
        fee.setAmount(amount);

        fee.setPaymentDate(CLIUtils.getDateInput("Enter payment date"));

        String statusInput;
        while (true) {
            statusInput = CLIUtils.getStringInput("Enter status (Paid/Pending/Overdue): ");
            if (!statusInput.matches("(?i)(Paid|Pending|Overdue)")) {
                System.out.println("Invalid status. Must be Paid, Pending, or Overdue.");
            } else {
                break;
            }
        }
        fee.setStatus(statusInput.substring(0, 1).toUpperCase() + statusInput.substring(1).toLowerCase());  // Standardize case
        fee.setPaidByEmpId(clerk.getEmpId());  // Link to clerk who processed

        boolean success = feeDAO.create(fee);
        if (success) {
            System.out.println("Fee processed successfully for " + student.getName() + 
                               " (Amount: $" + amount + ", Status: " + fee.getStatus() + ")");
        } else {
            System.out.println("Fee processing failed (e.g., database error).");
        }
        CLIUtils.pause();
        return success;
    }

    /**
     * View all fees (CRUD: Read All).
     */
    private void viewAllFees() {
        CLIUtils.printList(feeDAO.readAll(), "Fees");
    }

    /**
     * View fees for a specific student.
     * @param studentId ID of student.
     */
    private void viewStudentFees(int studentId) {
        Student student = studentDAO.readById(studentId);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        CLIUtils.printList(feeDAO.readByStudentId(studentId), "Fees for " + student.getName());
    }

    /**
     * View pending/overdue fees.
     */
    private void viewPendingFees() {
        int choice;
        do {
            System.out.println("1. Pending Fees\n2. Overdue Fees\n0. Back to previous menu");
            choice = CLIUtils.getMenuChoice(2);
            if (choice == 0) {
                break;
            } else if (choice == 1) {
                CLIUtils.printList(feeDAO.searchPendingFees(), "Pending Fees");  // Assumes searchPendingFees returns Pending only; adjust DAO if needed
            } else if (choice == 2) {
                System.out.println("Overdue Fees Report (implement custom query if needed):");
                CLIUtils.printList(feeDAO.searchPendingFees(), "Pending/Overdue Fees (Filter Overdue in View)");
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 0);
    }

    /**
     * Update an existing fee (CRUD: Update).
     * @param user Logged-in user (updates paidByEmpId).
     * @param feeId ID of fee to update.
     * @return true if successful.
     */
    private boolean updateFee(Employee user, int feeId) {
        Fee existing = feeDAO.readById(feeId);
        if (existing == null) {
            System.out.println("Fee not found.");
            CLIUtils.pause();
            return false;
        }

        Student student = studentDAO.readById(existing.getStudentId());
        System.out.println("Current fee for " + (student != null ? student.getName() : "Unknown Student") + ": " + existing.toString());

        boolean updated = false;

        // Update amount
        while (true) {
            double newAmount = CLIUtils.getDoubleInput("New amount (0 to skip): ");
            if (newAmount == 0) {
                break; // skip update
            } else if (newAmount > 0) {
                existing.setAmount(newAmount);
                updated = true;
                break;
            } else {
                System.out.println("Invalid amount. Must be positive or 0 to skip.");
            }
        }

        // Update payment date
        while (true) {
            String dateInput = CLIUtils.getStringInput("New payment date (yyyy-MM-dd, enter to skip): ");
            if (dateInput.isEmpty()) break;
            try {
                existing.setPaymentDate(Date.valueOf(dateInput));
                updated = true;
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date format. Please try again or enter to skip.");
            }
        }

        // Update status
        while (true) {
            String newStatus = CLIUtils.getStringInput("New status (Paid/Pending/Overdue, enter to skip): ");
            if (newStatus.isEmpty()) break;
            if (newStatus.matches("(?i)(Paid|Pending|Overdue)")) {
                existing.setStatus(newStatus.substring(0, 1).toUpperCase() + newStatus.substring(1).toLowerCase());
                updated = true;
                break;
            } else {
                System.out.println("Invalid status. Please enter Paid, Pending, or Overdue or enter to skip.");
            }
        }

        existing.setPaidByEmpId(user.getEmpId());  // Re-link to current user

        if (!updated) {
            System.out.println("No changes made to fee.");
            CLIUtils.pause();
            return false;
        }

        if (existing.getAmount() <= 0) {
            System.out.println("Invalid amount: Must be positive.");
            CLIUtils.pause();
            return false;
        }

        boolean success = feeDAO.update(existing);
        if (success) {
            System.out.println("Fee updated successfully (ID: " + feeId + ")");
        } else {
            System.out.println("Update failed (e.g., database error).");
        }
        CLIUtils.pause();
        return success;
    }

    /**
     * Delete a fee (CRUD: Delete).
     * @param user Logged-in user (role check).
     * @param feeId ID of fee to delete.
     * @return true if successful.
     */
    private boolean deleteFee(Employee user, int feeId) {
        Fee existing = feeDAO.readById(feeId);
        if (existing == null) {
            System.out.println("Fee not found.");
            CLIUtils.pause();
            return false;
        }

        Student student = studentDAO.readById(existing.getStudentId());
        System.out.println("Delete fee for " + (student != null ? student.getName() : "Unknown") + 
                           "? Amount: $" + existing.getAmount() + ", Status: " + existing.getStatus());

        String confirm;
        while (true) {
            confirm = CLIUtils.getStringInput("Confirm delete? (y/n): ");
            if (confirm.trim().toLowerCase().startsWith("y")) {
                boolean success = feeDAO.delete(feeId);
                if (success) {
                    System.out.println("Fee deleted successfully (ID: " + feeId + ")");
                } else {
                    System.out.println("Delete failed (e.g., database error).");
                }
                CLIUtils.pause();
                return success;
            } else if (confirm.trim().toLowerCase().startsWith("n")) {
                System.out.println("Delete cancelled.");
                CLIUtils.pause();
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }
        }
    }
}
