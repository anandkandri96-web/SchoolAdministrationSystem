package com.schooladmin.service;

import com.schooladmin.dao.ScheduleDAO;
import com.schooladmin.dao.EmployeeDAO;
import com.schooladmin.model.Employee;
import com.schooladmin.model.Schedule;
import com.schooladmin.utils.CLIUtils;

public class SchedulingService {
    private ScheduleDAO scheduleDAO = new ScheduleDAO();
    private EmployeeDAO empDAO = new EmployeeDAO();  // To validate teacher IDs

    /**
     * Main menu for managing class scheduling (called from role-based CLI).
     * @param user Logged-in employee (checks role).
     */
    public void manageScheduling(Employee user) {
        if (!"Principal".equals(user.getRole()) && !"Admin".equals(user.getRole())) {
            System.out.println("Access denied. Only Principals/Admins can manage scheduling.");
            return;
        }
        System.out.println("\n=== CLASS SCHEDULING MANAGEMENT ===");
        System.out.println("1. Add New Schedule");
        System.out.println("2. View All Schedules");
        System.out.println("3. Search Schedules by Class/Section");
        System.out.println("4. Update Schedule");
        System.out.println("5. Delete Schedule");
        System.out.println("0. Back to Menu");
        int choice = CLIUtils.getMenuChoice(5);  // 1-5, but 0 for back
        if (choice == 0) return;

        switch (choice) {
            case 1:
                addSchedule(user);
                break;
            case 2:
                viewAllSchedules();
                break;
            case 3:
                searchSchedules();
                break;
            case 4:
                int scheduleId = CLIUtils.getIntInput("Enter scheduleId to update: ");
                updateSchedule(user, scheduleId);
                break;
            case 5:
                scheduleId = CLIUtils.getIntInput("Enter scheduleId to delete: ");
                deleteSchedule(user, scheduleId);
                break;
        }
        CLIUtils.pause();
    }

    /**
     * Add a new schedule entry (CRUD: Create) - Assign class, subject, teacher, day, time, room.
     * @param principal Logged-in principal (sets principalId).
     * @return true if successful.
     */
    private boolean addSchedule(Employee principal) {
        Schedule schedule = new Schedule();
        schedule.setClassName(CLIUtils.getStringInput("Enter class name (e.g., Class 10): "));
        schedule.setSection(CLIUtils.getStringInput("Enter section (e.g., A): "));
        schedule.setSubject(CLIUtils.getStringInput("Enter subject: "));
        
        // Validate and get teacher ID
        int teacherId = CLIUtils.getIntInput("Enter teacher ID: ");
        Employee teacher = empDAO.readById(teacherId);
        if (teacher == null || !"Teacher".equals(teacher.getRole())) {
            System.out.println("Invalid teacher ID or not a Teacher.");
            return false;
        }
        schedule.setTeacherId(teacherId);
        
        schedule.setDayOfWeek(CLIUtils.getStringInput("Enter day (e.g., Monday): "));
        schedule.setTimeSlot(CLIUtils.getStringInput("Enter time slot (e.g., 9:00-10:00 AM): "));
        schedule.setRoomNo(CLIUtils.getStringInput("Enter room number (e.g., Room 101): "));
        schedule.setPrincipalId(principal.getEmpId());

        // Basic validation
        if (schedule.getClassName().isEmpty() || schedule.getSubject().isEmpty() || 
            schedule.getDayOfWeek().isEmpty() || schedule.getTimeSlot().isEmpty()) {
            System.out.println("Invalid details: Class, subject, day, and time required.");
            return false;
        }

        boolean success = scheduleDAO.create(schedule);
        if (success) {
            System.out.println("Schedule added successfully: " + schedule.getClassName() + 
                               " " + schedule.getSection() + " - " + schedule.getSubject() + 
                               " with Teacher " + teacher.getName() + " on " + schedule.getDayOfWeek());
        } else {
            System.out.println("Failed to add schedule.");
        }
        return success;
    }

    /**
     * View all schedule entries (CRUD: Read All).
     */
    private void viewAllSchedules() {
        var schedules = scheduleDAO.readAll();
        if (schedules.isEmpty()) {
            System.out.println("No schedules found.");
            return;
        }
        // Use CLIUtils to print nicely formatted list
        CLIUtils.printList(schedules, "Schedules");
    }

    /**
     * Search schedules by class and section.
     */
    private void searchSchedules() {
        String className = CLIUtils.getStringInput("Enter class name (e.g., Class 10): ");
        String section = CLIUtils.getStringInput("Enter section (e.g., A): ");
        var results = scheduleDAO.searchByClass(className, section);
        if (results.isEmpty()) {
            System.out.println("No schedules found for " + className + " section " + section);
            return;
        }
        CLIUtils.printList(results, "Schedules");
    }

    /**
     * Update an existing schedule entry (CRUD: Update/Edit).
     * @param user Logged-in user (updates principalId if Principal/Admin).
     * @param scheduleId ID of entry to update.
     * @return true if successful.
     */
    private boolean updateSchedule(Employee user, int scheduleId) {
        Schedule existing = scheduleDAO.readById(scheduleId);
        if (existing == null) {
            System.out.println("Schedule not found.");
            return false;
        }

        System.out.println("Current schedule: " + existing.toString());

        // Prompt for updates (allow skipping by empty input)
        String newClass = CLIUtils.getStringInput("New class name (enter to skip): ");
        if (!newClass.isEmpty()) {
            existing.setClassName(newClass);
        }
        String newSection = CLIUtils.getStringInput("New section (enter to skip): ");
        if (!newSection.isEmpty()) {
            existing.setSection(newSection);
        }
        String newSubject = CLIUtils.getStringInput("New subject (enter to skip): ");
        if (!newSubject.isEmpty()) {
            existing.setSubject(newSubject);
        }
        int newTeacherId = CLIUtils.getIntInput("New teacher ID (0 to skip): ");
        if (newTeacherId > 0) {
            Employee newTeacher = empDAO.readById(newTeacherId);
            if (newTeacher != null && "Teacher".equals(newTeacher.getRole())) {
                existing.setTeacherId(newTeacherId);
            } else {
                System.out.println("Invalid new teacher ID.");
                return false;
            }
        }
        String newDay = CLIUtils.getStringInput("New day (enter to skip): ");
        if (!newDay.isEmpty()) {
            existing.setDayOfWeek(newDay);
        }
        String newTime = CLIUtils.getStringInput("New time slot (enter to skip): ");
        if (!newTime.isEmpty()) {
            existing.setTimeSlot(newTime);
        }
        String newRoom = CLIUtils.getStringInput("New room (enter to skip): ");
        if (!newRoom.isEmpty()) {
            existing.setRoomNo(newRoom);
        }
        if ("Principal".equals(user.getRole())) {
            existing.setPrincipalId(user.getEmpId());  // Update to current principal
        }

        // Validation for changes
        if (existing.getClassName().isEmpty() || existing.getSubject().isEmpty() || 
            existing.getDayOfWeek().isEmpty() || existing.getTimeSlot().isEmpty()) {
            System.out.println("Invalid updated details.");
            return false;
        }

        boolean success = scheduleDAO.update(existing);
        if (success) {
            System.out.println("Schedule updated successfully.");
        } else {
            System.out.println("Update failed.");
        }
        return success;
    }

    /**
     * Delete a schedule entry (CRUD: Delete).
     * @param user Logged-in user (role check).
     * @param scheduleId ID of entry to delete.
     * @return true if successful.
     */
    private boolean deleteSchedule(Employee user, int scheduleId) {
        Schedule existing = scheduleDAO.readById(scheduleId);
        if (existing == null) {
            System.out.println("Schedule not found.");
            return false;
        }

        // Confirmation
        System.out.println("Delete: " + existing.toString() + "? (y/n)");
        if (!CLIUtils.getStringInput("").toLowerCase().startsWith("y")) {
            System.out.println("Delete cancelled.");
            return false;
        }

        boolean success = scheduleDAO.delete(scheduleId);
        if (success) {
            System.out.println("Schedule deleted successfully.");
        } else {
            System.out.println("Delete failed.");
        }
        return success;
    }
}
