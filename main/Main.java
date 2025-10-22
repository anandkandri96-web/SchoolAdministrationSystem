package com.schooladmin.main;


import java.util.Scanner;

import com.schooladmin.model.Employee;
import com.schooladmin.service.AdmissionService;
import com.schooladmin.service.AnalysisService;
import com.schooladmin.service.AttendanceService;
import com.schooladmin.service.AuthService;
import com.schooladmin.service.FeeService;
import com.schooladmin.service.PerformanceService;
import com.schooladmin.service.ReportService;
import com.schooladmin.service.SchedulingService;
import com.schooladmin.service.SyllabusService;
import com.schooladmin.utils.CLIUtils;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Welcome to School Administration System ===");
        AuthService authService = new AuthService();
        AdmissionService admissionService = new AdmissionService();
        FeeService feeService = new FeeService();
        AttendanceService attendanceService = new AttendanceService();
        PerformanceService performanceService = new PerformanceService();
        SyllabusService syllabusService = new SyllabusService();
        SchedulingService schedulingService = new SchedulingService();
        ReportService reportService = new ReportService();
        AnalysisService analysisService = new AnalysisService();

        while (true) {
            System.out.println("\n1. Login");
            System.out.println("2. Register (Admin only after login)");
            System.out.println("3. Exit");
            int mainChoice = CLIUtils.getMenuChoice(3, false);

            if (mainChoice == 3) {
                System.out.println("Thank you for using School Administration System. Goodbye!");
                scanner.close();
                System.exit(0);
            }

            Employee user = null;
            if (mainChoice == 1) {
                user = authService.login();
                if (user == null) {
                    System.out.println("Login failed. Try again.");
                    CLIUtils.pause();
                    continue;
                }
            } else if (mainChoice == 2) {
                // Register requires an admin login first (for security)
                System.out.println("Please login as Admin first to register new users.");
                Employee admin = authService.login();
                if (admin != null && "Admin".equals(admin.getRole())) {
                    authService.register(admin);
                } else {
                    System.out.println("Admin login required for registration.");
                }
                CLIUtils.pause();
                continue;
            }

            // Role-based menu loop after successful login
            if (user != null) {
                handleRoleMenu(user, authService, admissionService, feeService, attendanceService,
                               performanceService, syllabusService, schedulingService, reportService, analysisService);
            }
        }
    }

    /**
     * Handles the role-based menu loop after login.
     * @param user Logged-in employee.
     * @param services All instantiated services.
     */
    private static void handleRoleMenu(Employee user, AuthService authService,
                                       AdmissionService admissionService, FeeService feeService,
                                       AttendanceService attendanceService, PerformanceService performanceService,
                                       SyllabusService syllabusService, SchedulingService schedulingService,
                                       ReportService reportService, AnalysisService analysisService) {
        String role = user.getRole();
        System.out.println("\nLogged in as: " + user.getName() + " (" + role + ")");

        while (true) {
            int choice = showRoleSpecificMenu(role);
            if (choice == 0) break;  // Logout

            switch (role) {
                case "Admin":
                    handleAdminMenu(choice, user, authService, admissionService, feeService,
                                    attendanceService, performanceService, syllabusService,
                                    schedulingService, reportService, analysisService);
                    break;
                case "Clerk":
                    handleClerkMenu(choice, user, admissionService, feeService);
                    break;
                case "Teacher":
                    handleTeacherMenu(choice, user, attendanceService, performanceService, syllabusService);
                    break;
                case "Principal":
                    handlePrincipalMenu(choice, user, schedulingService, reportService, analysisService);
                    break;
                default:
                    System.out.println("Unknown role. Logging out.");
                    return;
            }

            System.out.print("\nContinue in this role? (y/n): ");
            if (!scanner.nextLine().toLowerCase().startsWith("y")) {
                break;
            }
        }
        System.out.println("Logging out. Goodbye!");
    }

    // Role-specific menus (returns choice 1-N or 0 for logout)
    private static int showRoleSpecificMenu(String role) {
        switch (role) {
            case "Admin":
                System.out.println("\n=== ADMIN MENU (Full Access) ===");
                System.out.println("1. Manage Employees");
                System.out.println("2. Admissions");
                System.out.println("3. Fees");
                System.out.println("4. Attendance");
                System.out.println("5. Performance");
                System.out.println("6. Syllabus");
                System.out.println("7. Scheduling");
                System.out.println("8. Reports");
                System.out.println("9. Analysis");
                System.out.println("0. Logout");
                return CLIUtils.getMenuChoice(9);
            case "Clerk":
                System.out.println("\n=== CLERK MENU ===");
                System.out.println("1. Manage Admissions");
                System.out.println("2. Manage Fees");
                System.out.println("0. Logout");
                return CLIUtils.getMenuChoice(2);
            case "Teacher":
                System.out.println("\n=== TEACHER MENU ===");
                System.out.println("1. Manage Attendance");
                System.out.println("2. Manage Performance");
                System.out.println("3. Manage Syllabus");
                System.out.println("0. Logout");
                return CLIUtils.getMenuChoice(3);
            case "Principal":
                System.out.println("\n=== PRINCIPAL MENU ===");
                System.out.println("1. Manage Scheduling");
                System.out.println("2. Generate Reports");
                System.out.println("3. Perform Analysis");
                System.out.println("0. Logout");
                return CLIUtils.getMenuChoice(3);
            default:
                return 0;
        }
    }

    // Admin menu handler (full access)
    private static void handleAdminMenu(int choice, Employee user, AuthService authService,
                                        AdmissionService admissionService, FeeService feeService,
                                        AttendanceService attendanceService, PerformanceService performanceService,
                                        SyllabusService syllabusService, SchedulingService schedulingService,
                                        ReportService reportService, AnalysisService analysisService) {
        switch (choice) {
            case 1:
                authService.manageEmployees(user);
                break;
            case 2:
                admissionService.manageAdmissions(user);
                break;
            case 3:
                feeService.manageFees(user);
                break;
            case 4:
                attendanceService.manageAttendance(user);
                break;
            case 5:
                performanceService.managePerformance(user);
                break;
            case 6:
                syllabusService.manageSyllabus(user);
                break;
            case 7:
                schedulingService.manageScheduling(user);
                break;
            case 8:
                reportService.generateReports(user);
                break;
            case 9:
                analysisService.performAnalysis(user);
                break;
            case 0:
                return;  // Logout
        }
    }

    // Clerk menu handler
    private static void handleClerkMenu(int choice, Employee user, AdmissionService admissionService, FeeService feeService) {
        switch (choice) {
            case 1:
                admissionService.manageAdmissions(user);
                break;
            case 2:
                feeService.manageFees(user);
                break;
            case 0:
                return;  // Logout
        }
    }

    // Teacher menu handler
    private static void handleTeacherMenu(int choice, Employee user, AttendanceService attendanceService,
                                          PerformanceService performanceService, SyllabusService syllabusService) {
        switch (choice) {
            case 1:
                attendanceService.manageAttendance(user);
                break;
            case 2:
                performanceService.managePerformance(user);
                break;
            case 3:
                syllabusService.manageSyllabus(user);
                break;
            case 0:
                return;  // Logout
        }
    }

    // Principal menu handler
    private static void handlePrincipalMenu(int choice, Employee user, SchedulingService schedulingService,
                                            ReportService reportService, AnalysisService analysisService) {
        switch (choice) {
            case 1:
                schedulingService.manageScheduling(user);
                break;
            case 2:
                reportService.generateReports(user);
                break;
            case 3:
                analysisService.performAnalysis(user);
                break;
            case 0:
                return;  // Logout
        }
    }
}
