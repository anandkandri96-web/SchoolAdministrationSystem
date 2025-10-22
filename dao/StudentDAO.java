package com.schooladmin.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.schooladmin.db.DBConnection;
import com.schooladmin.db.Queries;
import com.schooladmin.model.Student;

public class StudentDAO {
    public boolean create(Student student) {
        String sql = Queries.CREATE_STUDENT;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getParentName());
            pstmt.setString(3, student.getParentPhone());
            pstmt.setString(4, student.getAddress());
            pstmt.setString(5, student.getClassName());
            pstmt.setString(6, student.getSection());
            pstmt.setString(7, student.getPreviousPerformance());
            pstmt.setDate(8, student.getAdmissionDate());
            pstmt.setInt(9, student.getEmpId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Student readById(int studentId) {
        String sql = Queries.READ_STUDENT_BY_ID;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractStudentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> readAll() {
        List<Student> students = new ArrayList<>();
        String sql = Queries.READ_ALL_STUDENTS;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public boolean update(Student student) {
        String sql = Queries.UPDATE_STUDENT;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getParentName());
            pstmt.setString(3, student.getParentPhone());
            pstmt.setString(4, student.getAddress());
            pstmt.setString(5, student.getClassName());
            pstmt.setString(6, student.getSection());
            pstmt.setString(7, student.getPreviousPerformance());
            pstmt.setDate(8, student.getAdmissionDate());
            pstmt.setInt(9, student.getEmpId());
            pstmt.setInt(10, student.getStudentId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int studentId) {
        String sql = Queries.DELETE_STUDENT;
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

    public List<Student> searchByName(String name) {
        List<Student> students = new ArrayList<>();
        String sql = Queries.SEARCH_STUDENT_BY_NAME;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(extractStudentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public List<Student> searchByClass(String className, String section) {
        List<Student> students = new ArrayList<>();
        String sql = Queries.SEARCH_STUDENT_BY_CLASS;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, className);
            pstmt.setString(2, section);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(extractStudentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("studentId"));
        student.setName(rs.getString("name"));
        student.setParentName(rs.getString("parentName"));
        student.setParentPhone(rs.getString("parentPhone"));
        student.setAddress(rs.getString("address"));
        student.setClassName(rs.getString("className"));
        student.setSection(rs.getString("section"));
        student.setPreviousPerformance(rs.getString("previousPerformance"));
        student.setAdmissionDate(rs.getDate("admissionDate"));
        student.setEmpId(rs.getInt("empId"));
        return student;
    }
}

