package com.schooladmin.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.schooladmin.db.DBConnection;
import com.schooladmin.db.Queries;
import com.schooladmin.model.Attendance;

public class AttendanceDAO {

    public boolean create(Attendance attendance) {
        String sql = Queries.CREATE_ATTENDANCE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setDate(2, attendance.getDate());
            pstmt.setString(3, attendance.getStatus());
            pstmt.setInt(4, attendance.getTeacherId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Attendance readById(int attId) {
        String sql = Queries.READ_ATTENDANCE_BY_ID;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAttendanceFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Attendance> readAll() {
        List<Attendance> attendances = new ArrayList<>();
        String sql = "SELECT * FROM Attendance";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                attendances.add(extractAttendanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendances;
    }

    public List<Attendance> readByDate(Date date) {
        List<Attendance> attendances = new ArrayList<>();
        String sql = Queries.READ_ATTENDANCE_BY_DATE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    attendances.add(extractAttendanceFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendances;
    }

    public Attendance readByStudentAndDate(int studentId, Date date) {
        String sql = Queries.READ_ATTENDANCE_BY_STUDENT_DATE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setDate(2, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAttendanceFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Attendance attendance) {
        String sql = Queries.UPDATE_ATTENDANCE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, attendance.getStatus());
            pstmt.setInt(2, attendance.getTeacherId());
            pstmt.setInt(3, attendance.getAttId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int attId) {
        String sql = Queries.DELETE_ATTENDANCE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Attendance> searchByStudent(int studentId) {
        List<Attendance> attendances = new ArrayList<>();
        String sql = Queries.SEARCH_ATTENDANCE_BY_STUDENT;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    attendances.add(extractAttendanceFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendances;
    }

    // ✅ NEW: Delete all attendance records for a student (to handle FK constraints)
    public boolean deleteByStudentId(int studentId) {
        String sql = "DELETE FROM Attendance WHERE studentId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Attendance extractAttendanceFromResultSet(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setAttId(rs.getInt("attId"));
        attendance.setStudentId(rs.getInt("studentId"));
        attendance.setDate(rs.getDate("date"));
        attendance.setStatus(rs.getString("status"));
        attendance.setTeacherId(rs.getInt("teacherId"));
        return attendance;
    }
}
