package com.schooladmin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.schooladmin.db.DBConnection;
import com.schooladmin.db.Queries;
import com.schooladmin.model.Performance;

public class PerformanceDAO {

    public boolean create(Performance performance) {
        String sql = Queries.CREATE_PERFORMANCE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, performance.getStudentId());
            pstmt.setString(2, performance.getSubject());
            pstmt.setString(3, performance.getTestName());
            pstmt.setInt(4, performance.getMarks());
            pstmt.setInt(5, performance.getTotalMarks());
            pstmt.setDate(6, performance.getDate());
            pstmt.setInt(7, performance.getTeacherId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Performance readById(int perfId) {
        String sql = Queries.READ_PERFORMANCE_BY_ID;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, perfId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractPerformanceFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Performance> readAll() {
        List<Performance> performances = new ArrayList<>();
        String sql = "SELECT * FROM Performance";  // Custom for all
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                performances.add(extractPerformanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return performances;
    }

    public List<Performance> readByStudentId(int studentId) {
        List<Performance> performances = new ArrayList<>();
        String sql = Queries.READ_PERFORMANCE_BY_STUDENT;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    performances.add(extractPerformanceFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return performances;
    }

    public boolean update(Performance performance) {
        String sql = Queries.UPDATE_PERFORMANCE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, performance.getSubject());
            pstmt.setString(2, performance.getTestName());
            pstmt.setInt(3, performance.getMarks());
            pstmt.setInt(4, performance.getTotalMarks());
            pstmt.setDate(5, performance.getDate());
            pstmt.setInt(6, performance.getTeacherId());
            pstmt.setInt(7, performance.getPerfId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int perfId) {
        String sql = Queries.DELETE_PERFORMANCE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, perfId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes all performance records for a given studentId.
     * Useful for cascading deletes before deleting a student.
     */
    public boolean deleteByStudentId(int studentId) {
        String sql = "DELETE FROM Performance WHERE studentId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.executeUpdate(); // No need to check row count here
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Performance> searchBySubject(String subject) {
        List<Performance> performances = new ArrayList<>();
        String sql = Queries.SEARCH_PERFORMANCE_BY_SUBJECT;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + subject + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    performances.add(extractPerformanceFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return performances;
    }

    private Performance extractPerformanceFromResultSet(ResultSet rs) throws SQLException {
        Performance performance = new Performance();
        performance.setPerfId(rs.getInt("perfId"));
        performance.setStudentId(rs.getInt("studentId"));
        performance.setSubject(rs.getString("subject"));
        performance.setTestName(rs.getString("testName"));
        performance.setMarks(rs.getInt("marks"));
        performance.setTotalMarks(rs.getInt("totalMarks"));
        performance.setDate(rs.getDate("date"));
        performance.setTeacherId(rs.getInt("teacherId"));
        return performance;
    }
}
