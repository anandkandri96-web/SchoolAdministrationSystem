package com.schooladmin.utils;

import java.sql.Date;
import java.util.List;
import java.util.Scanner;

public class CLIUtils {
    private static Scanner scanner = new Scanner(System.in);

    // Print a formatted table header for lists (e.g., reports)
    public static void printTableHeader(String[] columns) {
        System.out.println("\n=== " + String.join(" | ", columns) + " ===");
        for (int i = 0; i < 80; i++) System.out.print("-");
        System.out.println();
    }

    // Print a list of objects (e.g., students, fees) in table format
    public static <T> void printList(List<T> list, String entityName) {
        if (list.isEmpty()) {
            System.out.println("No " + entityName + " found.");
            return;
        }
        System.out.println("\n=== " + entityName.toUpperCase() + " LIST ===");
        for (T item : list) {
            System.out.println(item.toString());  // Uses model's toString()
        }
    }

    // Get integer input with prompt and validation
    public static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    // Get double input (for fees)
    public static double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    // Get string input (OPTIONAL: empty input allowed)
    public static String getStringInput(String prompt) {
        // Now this method allows empty input (optional input)
        System.out.print(prompt);
        return scanner.nextLine().trim();  // Accepts empty input
    }

    // Get MANDATORY string input (non-empty, use this if input is required)
    public static String getMandatoryStringInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty.");
        }
    }

    // Get optional string input (empty input allowed - used for update skip)
    // (Kept for backward compatibility and explicit usage)
    public static String getOptionalStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();  // Accepts empty input
    }

    // Get date input (format: yyyy-MM-dd)
    public static Date getDateInput(String prompt) {
        while (true) {
            System.out.print(prompt + " (yyyy-MM-dd): ");
            try {
                String dateStr = scanner.nextLine().trim();
                return Date.valueOf(dateStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date format. Use yyyy-MM-dd.");
            }
        }
    }

    /**
     * Get menu choice from user with control over whether zero is valid choice.
     * @param maxOption Maximum valid choice number.
     * @param allowZero If true, zero is accepted as valid input.
     * @return user's choice.
     */
    public static int getMenuChoice(int maxOption, boolean allowZero) {
        int minOption = allowZero ? 0 : 1;
        int choice;
        do {
            System.out.print("Enter choice (" + minOption + "-" + maxOption + "): ");
            String input = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
                if (choice < minOption || choice > maxOption) {
                    System.out.println("Invalid choice. Try again.");
                } else {
                    return choice;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        } while (true);
    }

    // Overloaded method to maintain backward compatibility - allows zero by default
    public static int getMenuChoice(int maxOption) {
        return getMenuChoice(maxOption, true);
    }

    // Role-based main menu after login
    public static void showRoleMenu(String role, Scanner scanner) {
        int choice;
        do {
            switch (role) {
                case "Admin":
                    choice = showAdminMenu();
                    handleAdminChoice(choice);
                    break;
                case "Clerk":
                    choice = showClerkMenu();
                    handleClerkChoice(choice);
                    break;
                case "Teacher":
                    choice = showTeacherMenu();
                    handleTeacherChoice(choice);
                    break;
                case "Principal":
                    choice = showPrincipalMenu();
                    handlePrincipalChoice(choice);
                    break;
                default:
                    System.out.println("Unknown role.");
                    return;
            }
            System.out.print("\nContinue? (y/n): ");
        } while (scanner.nextLine().toLowerCase().startsWith("y"));
    }

    // Admin Menu (full access) - zero not allowed, choices 1 to 3
    private static int showAdminMenu() {
        System.out.println("\n=== ADMIN MENU ===");
        System.out.println("1. Manage Employees (CRUD)");
        System.out.println("2. View All Modules");
        System.out.println("3. Logout");
        return CLIUtils.getMenuChoice(3, false);  // zero NOT allowed here
    }

    private static void handleAdminChoice(int choice) {
        switch (choice) {
            case 1:
                System.out.println("Manage Employees - Implement CRUD via EmployeeService.");
                break;
            case 2:
                System.out.println("Access all modules.");
                break;
            case 3:
                System.out.println("Logging out.");
                break;
        }
    }

    // Clerk Menu (Admission, Fee) - zero not allowed
    private static int showClerkMenu() {
        System.out.println("\n=== CLERK MENU ===");
        System.out.println("1. Admission Process");
        System.out.println("2. Fee Payment");
        System.out.println("3. Search Students");
        System.out.println("4. Logout");
        return CLIUtils.getMenuChoice(4, false);
    }

    private static void handleClerkChoice(int choice) {
        switch (choice) {
            case 1:
                System.out.println("Admission - Use AdmissionService.");
                break;
            case 2:
                System.out.println("Fee Payment - Use FeeService.");
                break;
            case 3:
                System.out.println("Search Students.");
                break;
            case 4:
                System.out.println("Logging out.");
                break;
        }
    }

    // Teacher Menu (Attendance, Performance, Syllabus) - zero not allowed
    private static int showTeacherMenu() {
        System.out.println("\n=== TEACHER MENU ===");
        System.out.println("1. Mark Attendance");
        System.out.println("2. Update Student Performance");
        System.out.println("3. Manage Syllabus");
        System.out.println("4. Logout");
        return CLIUtils.getMenuChoice(4, false);
    }

    private static void handleTeacherChoice(int choice) {
        switch (choice) {
            case 1:
                System.out.println("Mark Attendance - Use AttendanceService.");
                break;
            case 2:
                System.out.println("Update Performance - Use PerformanceService.");
                break;
            case 3:
                System.out.println("Manage Syllabus - Use SyllabusService.");
                break;
            case 4:
                System.out.println("Logging out.");
                break;
        }
    }

    // Principal Menu (Scheduling, Reports, Analysis) - zero not allowed
    private static int showPrincipalMenu() {
        System.out.println("\n=== PRINCIPAL MENU ===");
        System.out.println("1. Class Scheduling");
        System.out.println("2. Generate Reports");
        System.out.println("3. Analysis (Academic/Financial)");
        System.out.println("4. Logout");
        return CLIUtils.getMenuChoice(4, false);
    }

    private static void handlePrincipalChoice(int choice) {
        switch (choice) {
            case 1:
                System.out.println("Scheduling - Use SchedulingService.");
                break;
            case 2:
                System.out.println("Reports - Use ReportService.");
                break;
            case 3:
                System.out.println("Analysis - Use AnalysisService.");
                break;
            case 4:
                System.out.println("Logging out.");
                break;
        }
    }

    // Pause for user (press enter to continue)
    public static void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
