package com.schooladmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.schooladmin.db.DBConnection;
import com.schooladmin.model.Employee;
import com.schooladmin.utils.CLIUtils;

public class AnalysisService {
    /**
     * Main menu for performing analyses (called from role-based CLI).
     * @param user Logged-in employee (checks role).
     */
    public void performAnalysis(Employee user) {
        if (!"Principal".equals(user.getRole()) && !"Admin".equals(user.getRole())) {
            System.out.println("Access denied. Only Principals/Admins can perform analyses.");
            return;
        }

        while (true) {
            System.out.println("\n=== ANALYSIS MENU ===");
            System.out.println("1. Academic Performance Analysis");
            System.out.println("2. Financial Performance Analysis");
            System.out.println("0. Back to Menu");
            int choice = CLIUtils.getMenuChoice(2);  // 1-2, but 0 for back
            if (choice == 0) return;

            switch (choice) {
                case 1:
                    analyzeAcademicPerformance();
                    break;
                case 2:
                    analyzeFinancialPerformance();
                    break;
            }
            CLIUtils.pause();
        }
    }

    /**
     * Academic Analysis (Req 11): Overall school performance, class-wise averages, top/bottom students.
     * Uses custom queries on Performance and Students tables.
     */
    private void analyzeAcademicPerformance() {
        System.out.println("\n=== ACADEMIC PERFORMANCE ANALYSIS ===");

        // 1. School-wide average percentage
        double schoolAvg = getSchoolAveragePercentage();
        System.out.println("School-Wide Average Performance: " + String.format("%.2f", schoolAvg) + "%");

        // 2. Class-wise averages
        List<Object[]> classAverages = getClassAverages();
        if (!classAverages.isEmpty()) {
            CLIUtils.printTableHeader(new String[]{"Class/Section", "Avg %", "Student Count"});
            for (Object[] row : classAverages) {
                System.out.format("%-15s %-8s %-14d%n",
                        row[0] + "/" + row[1], String.format("%.2f", row[2]) + "%", row[3]);
            }
        }

        // 3. Top 5 and Bottom 5 performers (overall)
        System.out.println("\nTop 5 Performers:");
        List<Object[]> topPerformers = getTopBottomPerformers(5, true);
        printPerformers(topPerformers);

        System.out.println("\nBottom 5 Performers:");
        List<Object[]> bottomPerformers = getTopBottomPerformers(5, false);
        printPerformers(bottomPerformers);

        System.out.println("\nAnalysis Complete. Focus on low-performing classes/students for improvement.");
    }

    /**
     * Financial Analysis (Req 11): Total revenue, pending/overdue, per-class summary.
     * Uses custom queries on Fees and Students tables.
     */
    private void analyzeFinancialPerformance() {
        System.out.println("\n=== FINANCIAL PERFORMANCE ANALYSIS ===");

        // 1. Total revenue (paid fees)
        double totalRevenue = getTotalRevenue();
        System.out.println("Total Fee Revenue (Paid): $" + String.format("%.2f", totalRevenue));

        // 2. Pending/Overdue summary
        Object[] feeSummary = getFeeSummary();
        System.out.println("Pending Fees: $" + String.format("%.2f", feeSummary[0]) + " (Count: " + feeSummary[1] + ")");
        System.out.println("Overdue Fees: $" + String.format("%.2f", feeSummary[2]) + " (Count: " + feeSummary[3] + ")");

        // 3. Per-class financial summary (total fees per class)
        List<Object[]> classFinancials = getClassFinancials();
        if (!classFinancials.isEmpty()) {
            CLIUtils.printTableHeader(new String[]{"Class", "Total Fees ($)", "Paid ($)", "Pending ($)", "Student Count"});
            for (Object[] row : classFinancials) {
                System.out.format("%-10s %-14s %-10s %-12s %-14d%n",
                        row[0], String.format("%.2f", row[1]), String.format("%.2f", row[2]),
                        String.format("%.2f", row[3]), row[4]);
            }
        }

        System.out.println("\nFinancial Health: " + (totalRevenue > 0 ? "Stable" : "Needs Attention") + 
                           ". Prioritize collecting pending fees.");
    }

    // Helper: School-wide average % from Performance
    private double getSchoolAveragePercentage() {
        String sql = "SELECT AVG(marks * 1.0 / totalMarks * 100) as schoolAvg FROM Performance";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                Double avg = rs.getDouble("schoolAvg");
                return avg != 0 ? avg : 0.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Helper: Class-wise averages
    private List<Object[]> getClassAverages() {
        List<Object[]> results = new ArrayList<>();
        String sql = """
                SELECT st.className, st.section, AVG(p.marks * 1.0 / p.totalMarks * 100) as avgPerc, COUNT(DISTINCT st.studentId) as studentCount
                FROM Students st 
                LEFT JOIN Performance p ON st.studentId = p.studentId 
                GROUP BY st.className, st.section 
                ORDER BY st.className
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("className");
                row[1] = rs.getString("section");
                row[2] = rs.getDouble("avgPerc");
                row[3] = rs.getInt("studentCount");
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Helper: Top/Bottom N performers
    private List<Object[]> getTopBottomPerformers(int n, boolean top) {
        List<Object[]> results = new ArrayList<>();
        String order = top ? "DESC" : "ASC";
        String sql = """
                SELECT st.studentId, st.name, st.className, AVG(p.marks * 1.0 / p.totalMarks * 100) as avgPerc
                FROM Students st 
                LEFT JOIN Performance p ON st.studentId = p.studentId 
                GROUP BY st.studentId 
                ORDER BY avgPerc """ + order + """ 
                LIMIT ?
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, n);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[4];
                    row[0] = rs.getInt("studentId");
                    row[1] = rs.getString("name");
                    row[2] = rs.getString("className");
                    row[3] = String.format("%.2f", rs.getDouble("avgPerc")) + "%";
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Helper: Print performers list
    private void printPerformers(List<Object[]> performers) {
        CLIUtils.printTableHeader(new String[]{"Student ID", "Name", "Class", "Avg %"});
        for (Object[] row : performers) {
            System.out.format("%-10d %-15s %-10s %s%n", row[0], row[1], row[2], row[3]);
        }
    }

    // Helper: Total revenue (SUM paid fees)
    private double getTotalRevenue() {
        String sql = "SELECT SUM(amount) as total FROM Fees WHERE status = 'Paid'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Helper: Fee summary (pending/overdue amounts and counts)
    private Object[] getFeeSummary() {
        Object[] summary = new Object[4];  // [pendingAmt, pendingCount, overdueAmt, overdueCount]
        String sql = """
                SELECT status, SUM(amount) as amt, COUNT(*) as cnt 
                FROM Fees 
                WHERE status IN ('Pending', 'Overdue') 
                GROUP BY status
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String status = rs.getString("status");
                double amt = rs.getDouble("amt");
                int cnt = rs.getInt("cnt");
                if ("Pending".equals(status)) {
                    summary[0] = amt;
                    summary[1] = cnt;
                } else if ("Overdue".equals(status)) {
                    summary[2] = amt;
                    summary[3] = cnt;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Defaults to 0 if no data
        if (summary[0] == null) summary[0] = 0.0;
        if (summary[1] == null) summary[1] = 0;
        if (summary[2] == null) summary[2] = 0.0;
        if (summary[3] == null) summary[3] = 0;
        return summary;
    }

    // Helper: Per-class financials
    private List<Object[]> getClassFinancials() {
        List<Object[]> results = new ArrayList<>();
        String sql = """
                SELECT st.className, 
                       SUM(f.amount) as totalFees,
                       SUM(CASE WHEN f.status = 'Paid' THEN f.amount ELSE 0 END) as paidFees,
                       SUM(CASE WHEN f.status != 'Paid' THEN f.amount ELSE 0 END) as pendingFees,
                       COUNT(DISTINCT st.studentId) as studentCount
                FROM Students st 
                LEFT JOIN Fees f ON st.studentId = f.studentId 
                GROUP BY st.className 
                ORDER BY st.className
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getString("className");
                row[1] = rs.getDouble("totalFees");
                row[2] = rs.getDouble("paidFees");
                row[3] = rs.getDouble("pendingFees");
                row[4] = rs.getInt("studentCount");
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}
