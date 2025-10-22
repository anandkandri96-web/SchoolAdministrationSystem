package com.schooladmin.service;

import com.schooladmin.dao.SyllabusDAO;
import com.schooladmin.model.Employee;
import com.schooladmin.model.Syllabus;
import com.schooladmin.utils.CLIUtils;

import java.sql.Date;

public class SyllabusService {
    private SyllabusDAO syllabusDAO = new SyllabusDAO();

    /**
     * Main menu for managing syllabus (called from role-based CLI).
     * @param user Logged-in employee (checks role).
     */
    public void manageSyllabus(Employee user) {
        if (!"Teacher".equals(user.getRole()) && !"Admin".equals(user.getRole())) {
            System.out.println("Access denied. Only Teachers/Admins can manage syllabus.");
            return;
        }
        int choice;
        do {
            System.out.println("\n=== SYLLABUS MANAGEMENT ===");
            System.out.println("1. Add New Syllabus Entry");
            System.out.println("2. View All Syllabus");
            System.out.println("3. Search Syllabus by Class/Subject");
            System.out.println("4. Update Syllabus Entry");
            System.out.println("5. Delete Syllabus Entry");
            System.out.println("0. Back to Menu");
            choice = CLIUtils.getMenuChoice(5);  // 0-5

            switch (choice) {
                case 1:
                    addSyllabus(user);
                    break;
                case 2:
                    viewAllSyllabus();
                    break;
                case 3:
                    searchSyllabus();
                    break;
                case 4:
                    int syllabusIdToUpdate = CLIUtils.getIntInput("Enter syllabusId to update: ");
                    updateSyllabus(user, syllabusIdToUpdate);
                    break;
                case 5:
                    int syllabusIdToDelete = CLIUtils.getIntInput("Enter syllabusId to delete: ");
                    deleteSyllabus(user, syllabusIdToDelete);
                    break;
                case 0:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
            if (choice != 0) {
                CLIUtils.pause();
            }
        } while (choice != 0);
    }

    /**
     * Add a new syllabus entry (CRUD: Create).
     * @param teacher Logged-in teacher (sets teacherId).
     * @return true if successful.
     */
    private boolean addSyllabus(Employee teacher) {
        Syllabus syllabus = new Syllabus();
        syllabus.setClassName(CLIUtils.getStringInput("Enter class name (e.g., Class 10): "));
        syllabus.setSubject(CLIUtils.getStringInput("Enter subject: "));
        syllabus.setTopic(CLIUtils.getStringInput("Enter topic details: "));
        syllabus.setChapterNo(CLIUtils.getIntInput("Enter chapter number: "));
        syllabus.setTeacherId(teacher.getEmpId());
        syllabus.setLastUpdated(new Date(System.currentTimeMillis()));  // Current date for last updated

        // Basic validation
        if (syllabus.getClassName().isEmpty() || syllabus.getSubject().isEmpty() ||
            syllabus.getChapterNo() <= 0) {
            System.out.println("Invalid details: Class, subject required; chapter > 0.");
            return false;
        }

        boolean success = syllabusDAO.create(syllabus);
        if (success) {
            System.out.println("Syllabus entry added successfully for " + syllabus.getClassName() +
                               " - " + syllabus.getSubject() + " (Chapter " + syllabus.getChapterNo() + ")");
        } else {
            System.out.println("Failed to add syllabus entry.");
        }
        return success;
    }

    /**
     * View all syllabus entries (CRUD: Read All).
     */
    private void viewAllSyllabus() {
        var list = syllabusDAO.readAll();
        if (list.isEmpty()) {
            System.out.println("No syllabus entries found.");
            return;
        }
        CLIUtils.printList(list, "Syllabus Entries");
    }

    /**
     * Search syllabus by class and subject.
     */
    private void searchSyllabus() {
        String className = CLIUtils.getStringInput("Enter class name (e.g., Class 10): ");
        String subject = CLIUtils.getStringInput("Enter subject: ");
        var results = syllabusDAO.searchByClassAndSubject(className, subject);
        if (results.isEmpty()) {
            System.out.println("No syllabus entries found for " + className + " and subject " + subject);
            return;
        }
        CLIUtils.printList(results, "Syllabus Entries");
    }

    /**
     * Update an existing syllabus entry (CRUD: Update/Edit).
     * @param user Logged-in user (updates teacherId if Admin/Teacher).
     * @param syllabusId ID of entry to update.
     * @return true if successful.
     */
    private boolean updateSyllabus(Employee user, int syllabusId) {
        Syllabus existing = syllabusDAO.readById(syllabusId);
        if (existing == null) {
            System.out.println("Syllabus entry not found.");
            return false;
        }

        System.out.println("Current entry: " + existing.toString());

        // Prompt for updates (allow skipping by empty input)
        String newClass = CLIUtils.getStringInput("New class name (enter to skip): ");
        if (!newClass.isEmpty()) {
            existing.setClassName(newClass);
        }
        String newSubject = CLIUtils.getStringInput("New subject (enter to skip): ");
        if (!newSubject.isEmpty()) {
            existing.setSubject(newSubject);
        }
        String newTopic = CLIUtils.getStringInput("New topic (enter to skip): ");
        if (!newTopic.isEmpty()) {
            existing.setTopic(newTopic);
        }
        int newChapter = CLIUtils.getIntInput("New chapter number (0 to skip): ");
        if (newChapter > 0) {
            existing.setChapterNo(newChapter);
        }
        if ("Teacher".equals(user.getRole())) {
            existing.setTeacherId(user.getEmpId());  // Update to current teacher
        }
        existing.setLastUpdated(new Date(System.currentTimeMillis()));  // Always update timestamp

        // Validation for changes
        if (existing.getClassName().isEmpty() || existing.getSubject().isEmpty() ||
            existing.getChapterNo() <= 0) {
            System.out.println("Invalid updated details.");
            return false;
        }

        boolean success = syllabusDAO.update(existing);
        if (success) {
            System.out.println("Syllabus entry updated successfully.");
        } else {
            System.out.println("Update failed.");
        }
        return success;
    }

    /**
     * Delete a syllabus entry (CRUD: Delete).
     * @param user Logged-in user (role check).
     * @param syllabusId ID of entry to delete.
     * @return true if successful.
     */
    private boolean deleteSyllabus(Employee user, int syllabusId) {
        Syllabus existing = syllabusDAO.readById(syllabusId);
        if (existing == null) {
            System.out.println("Syllabus entry not found.");
            return false;
        }

        // Confirmation
        System.out.println("Delete: " + existing.toString() + "? (y/n)");
        if (!CLIUtils.getStringInput("").toLowerCase().startsWith("y")) {
            System.out.println("Delete cancelled.");
            return false;
        }

        boolean success = syllabusDAO.delete(syllabusId);
        if (success) {
            System.out.println("Syllabus entry deleted successfully.");
        } else {
            System.out.println("Delete failed.");
        }
        return success;
    }
}
