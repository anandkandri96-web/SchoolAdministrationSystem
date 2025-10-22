package com.schooladmin.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.schooladmin.db.DBConnection;
import com.schooladmin.db.Queries;
import com.schooladmin.model.Schedule;

public class ScheduleDAO {
    public boolean create(Schedule schedule) {
        String sql = Queries.CREATE_SCHEDULE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, schedule.getClassName());
            pstmt.setString(2, schedule.getSection());
            pstmt.setString(3, schedule.getSubject());
            pstmt.setInt(4, schedule.getTeacherId());
            pstmt.setString(5, schedule.getDayOfWeek());
            pstmt.setString(6, schedule.getTimeSlot());
            pstmt.setString(7, schedule.getRoomNo());
            pstmt.setInt(8, schedule.getPrincipalId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Schedule readById(int scheduleId) {
        String sql = Queries.READ_SCHEDULE_BY_ID;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, scheduleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractScheduleFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Schedule> readAll() {
        List<Schedule> schedules = new ArrayList<>();
        String sql = Queries.READ_ALL_SCHEDULE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                schedules.add(extractScheduleFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }

    public boolean update(Schedule schedule) {
        String sql = Queries.UPDATE_SCHEDULE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, schedule.getClassName());
            pstmt.setString(2, schedule.getSection());
            pstmt.setString(3, schedule.getSubject());
            pstmt.setInt(4, schedule.getTeacherId());
            pstmt.setString(5, schedule.getDayOfWeek());
            pstmt.setString(6, schedule.getTimeSlot());
            pstmt.setString(7, schedule.getRoomNo());
            pstmt.setInt(8, schedule.getPrincipalId());
            pstmt.setInt(9, schedule.getScheduleId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int scheduleId) {
        String sql = Queries.DELETE_SCHEDULE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, scheduleId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Schedule> searchByClass(String className, String section) {
        List<Schedule> schedules = new ArrayList<>();
        String sql = Queries.SEARCH_SCHEDULE_BY_CLASS;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, className);
            pstmt.setString(2, section);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(extractScheduleFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }

    private Schedule extractScheduleFromResultSet(ResultSet rs) throws SQLException {
        Schedule schedule = new Schedule();
        schedule.setScheduleId(rs.getInt("scheduleId"));
        schedule.setClassName(rs.getString("className"));
        schedule.setSection(rs.getString("section"));
        schedule.setSubject(rs.getString("subject"));
        schedule.setTeacherId(rs.getInt("teacherId"));
        schedule.setDayOfWeek(rs.getString("dayOfWeek"));
        schedule.setTimeSlot(rs.getString("timeSlot"));
        schedule.setRoomNo(rs.getString("roomNo"));
        schedule.setPrincipalId(rs.getInt("principalId"));
        return schedule;
    }
}

