package com.schooladmin.model;

import java.util.Date;

public class Attendance {
    private int attId;
    private int studentId;  // FK to Student
    private Date date;
    private String status;  // e.g., "Present", "Absent", "Late"
    private int teacherId;  // FK to Employee (Teacher)

    // Default constructor
    public Attendance() {}

    // Parameterized constructor
    public Attendance(int attId, int studentId, Date date, String status, int teacherId) {
        this.attId = attId;
        this.studentId = studentId;
        this.date = date;
        this.status = status;
        this.teacherId = teacherId;
    }

    // Getters and Setters
    public int getAttId() {
        return attId;
    }

    public void setAttId(int attId) {
        this.attId = attId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public java.sql.Date getDate() {
        return (java.sql.Date) date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "attId=" + attId +
                ", studentId=" + studentId +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", teacherId=" + teacherId +
                '}';
    }
}
