package com.schooladmin.model;

import java.util.Date;

public class Performance {
    private int perfId;
    private int studentId;  // FK to Student
    private String subject;
    private String testName;  // e.g., "Unit Test 1"
    private int marks;
    private int totalMarks;
    private Date date;
    private int teacherId;  // FK to Employee (Teacher)

    // Default constructor
    public Performance() {}

    // Parameterized constructor
    public Performance(int perfId, int studentId, String subject, String testName, int marks, int totalMarks,
                       Date date, int teacherId) {
        this.perfId = perfId;
        this.studentId = studentId;
        this.subject = subject;
        this.testName = testName;
        this.marks = marks;
        this.totalMarks = totalMarks;
        this.date = date;
        this.teacherId = teacherId;
    }

    // Getters and Setters
    public int getPerfId() {
        return perfId;
    }

    public void setPerfId(int perfId) {
        this.perfId = perfId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public java.sql.Date getDate() {
        return (java.sql.Date) date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "Performance{" +
                "perfId=" + perfId +
                ", studentId=" + studentId +
                ", subject='" + subject + '\'' +
                ", testName='" + testName + '\'' +
                ", marks=" + marks +
                ", totalMarks=" + totalMarks +
                ", date=" + date +
                ", teacherId=" + teacherId +
                '}';
    }
}
