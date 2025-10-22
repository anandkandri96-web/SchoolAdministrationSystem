package com.schooladmin.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.schooladmin.db.DBConnection;
import com.schooladmin.db.Queries;
import com.schooladmin.model.Syllabus;

public class SyllabusDAO {
    public boolean create(Syllabus syllabus) {
        String sql = Queries.CREATE_SYLLABUS;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, syllabus.getClassName());
            pstmt.setString(2, syllabus.getSubject());
            pstmt.setString(3, syllabus.getTopic());
            pstmt.setInt(4, syllabus.getChapterNo());
            pstmt.setInt(5, syllabus.getTeacherId());
            pstmt.setDate(6, syllabus.getLastUpdated());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Syllabus readById(int syllabusId) {
        String sql = Queries.READ_SYLLABUS_BY_ID;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, syllabusId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractSyllabusFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Syllabus> readAll() {
        List<Syllabus> syllabi = new ArrayList<>();
        String sql = Queries.READ_ALL_SYLLABUS;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                syllabi.add(extractSyllabusFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return syllabi;
    }

    public boolean update(Syllabus syllabus) {
        String sql = Queries.UPDATE_SYLLABUS;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, syllabus.getClassName());
            pstmt.setString(2, syllabus.getSubject());
            pstmt.setString(3, syllabus.getTopic());
            pstmt.setInt(4, syllabus.getChapterNo());
            pstmt.setInt(5, syllabus.getTeacherId());
            pstmt.setDate(6, syllabus.getLastUpdated());
            pstmt.setInt(7, syllabus.getSyllabusId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int syllabusId) {
        String sql = Queries.DELETE_SYLLABUS;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, syllabusId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Syllabus> searchByClassAndSubject(String className, String subject) {
        List<Syllabus> syllabi = new ArrayList<>();
        String sql = Queries.SEARCH_SYLLABUS_BY_CLASS_SUBJECT;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, className);
            pstmt.setString(2, subject);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    syllabi.add(extractSyllabusFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return syllabi;
    }

    private Syllabus extractSyllabusFromResultSet(ResultSet rs) throws SQLException {
        Syllabus syllabus = new Syllabus();
        syllabus.setSyllabusId(rs.getInt("syllabusId"));
        syllabus.setClassName(rs.getString("className"));
        syllabus.setSubject(rs.getString("subject"));
        syllabus.setTopic(rs.getString("topic"));
        syllabus.setChapterNo(rs.getInt("chapterNo"));
        syllabus.setTeacherId(rs.getInt("teacherId"));
        syllabus.setLastUpdated(rs.getDate("lastUpdated"));
        return syllabus;
    }
}
