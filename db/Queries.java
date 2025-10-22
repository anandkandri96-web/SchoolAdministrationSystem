package com.schooladmin.db;

public class Queries {
    // Employee Queries
    public static final String CREATE_EMPLOYEE = "INSERT INTO Employees (name, username, password, role, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String READ_EMPLOYEE_BY_USERNAME = "SELECT * FROM Employees WHERE username = ?";
    public static final String READ_ALL_EMPLOYEES = "SELECT * FROM Employees";
    public static final String UPDATE_EMPLOYEE = "UPDATE Employees SET name=?, username=?, password=?, role=?, email=?, phone=? WHERE empId=?";
    public static final String DELETE_EMPLOYEE = "DELETE FROM Employees WHERE empId=?";
    public static final String SEARCH_EMPLOYEE_BY_NAME = "SELECT * FROM Employees WHERE name LIKE ?";

    // Student Queries
    public static final String CREATE_STUDENT = "INSERT INTO Students (name, parentName, parentPhone, address, className, section, previousPerformance, admissionDate, empId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String READ_STUDENT_BY_ID = "SELECT * FROM Students WHERE studentId = ?";
    public static final String READ_ALL_STUDENTS = "SELECT * FROM Students";
    public static final String UPDATE_STUDENT = "UPDATE Students SET name=?, parentName=?, parentPhone=?, address=?, className=?, section=?, previousPerformance=?, admissionDate=?, empId=? WHERE studentId=?";
    public static final String DELETE_STUDENT = "DELETE FROM Students WHERE studentId=?";
    public static final String SEARCH_STUDENT_BY_NAME = "SELECT * FROM Students WHERE name LIKE ?";
    public static final String SEARCH_STUDENT_BY_CLASS = "SELECT * FROM Students WHERE className = ? AND section = ?";

    // Fee Queries
    public static final String CREATE_FEE = "INSERT INTO Fees (studentId, amount, paymentDate, status, paidByEmpId) VALUES (?, ?, ?, ?, ?)";
    public static final String READ_FEE_BY_ID = "SELECT * FROM Fees WHERE feeId = ?";
    public static final String READ_FEES_BY_STUDENT = "SELECT * FROM Fees WHERE studentId = ?";
    public static final String UPDATE_FEE = "UPDATE Fees SET amount=?, paymentDate=?, status=?, paidByEmpId=? WHERE feeId=?";
    public static final String DELETE_FEE = "DELETE FROM Fees WHERE feeId=?";
    public static final String SEARCH_FEE_PENDING = "SELECT * FROM Fees WHERE status = 'Pending'";

    // Attendance Queries
    public static final String CREATE_ATTENDANCE = "INSERT INTO Attendance (studentId, date, status, teacherId) VALUES (?, ?, ?, ?)";
    public static final String READ_ATTENDANCE_BY_ID = "SELECT * FROM Attendance WHERE attId = ?";
    public static final String READ_ATTENDANCE_BY_STUDENT_DATE = "SELECT * FROM Attendance WHERE studentId = ? AND date = ?";
    public static final String READ_ATTENDANCE_BY_DATE = "SELECT * FROM Attendance WHERE date = ?";
    public static final String UPDATE_ATTENDANCE = "UPDATE Attendance SET status=?, teacherId=? WHERE attId=?";
    public static final String DELETE_ATTENDANCE = "DELETE FROM Attendance WHERE attId=?";
    public static final String SEARCH_ATTENDANCE_BY_STUDENT = "SELECT * FROM Attendance WHERE studentId = ?";

    // Performance Queries
    public static final String CREATE_PERFORMANCE = "INSERT INTO Performance (studentId, subject, testName, marks, totalMarks, date, teacherId) VALUES (?, ?, ?, ?, ?, ?, ?)";
    public static final String READ_PERFORMANCE_BY_ID = "SELECT * FROM Performance WHERE perfId = ?";
    public static final String READ_PERFORMANCE_BY_STUDENT = "SELECT * FROM Performance WHERE studentId = ?";
    public static final String UPDATE_PERFORMANCE = "UPDATE Performance SET subject=?, testName=?, marks=?, totalMarks=?, date=?, teacherId=? WHERE perfId=?";
    public static final String DELETE_PERFORMANCE = "DELETE FROM Performance WHERE perfId=?";
    public static final String SEARCH_PERFORMANCE_BY_SUBJECT = "SELECT * FROM Performance WHERE subject LIKE ?";

    // Syllabus Queries
    public static final String CREATE_SYLLABUS = "INSERT INTO Syllabus (className, subject, topic, chapterNo, teacherId, lastUpdated) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String READ_SYLLABUS_BY_ID = "SELECT * FROM Syllabus WHERE syllabusId = ?";
    public static final String READ_ALL_SYLLABUS = "SELECT * FROM Syllabus";
    public static final String UPDATE_SYLLABUS = "UPDATE Syllabus SET className=?, subject=?, topic=?, chapterNo=?, teacherId=?, lastUpdated=? WHERE syllabusId=?";
    public static final String DELETE_SYLLABUS = "DELETE FROM Syllabus WHERE syllabusId=?";
    public static final String SEARCH_SYLLABUS_BY_CLASS_SUBJECT = "SELECT * FROM Syllabus WHERE className = ? AND subject = ?";

    // Schedule Queries
    public static final String CREATE_SCHEDULE = "INSERT INTO Schedule (className, section, subject, teacherId, dayOfWeek, timeSlot, roomNo, principalId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String READ_SCHEDULE_BY_ID = "SELECT * FROM Schedule WHERE scheduleId = ?";
    public static final String READ_ALL_SCHEDULE = "SELECT * FROM Schedule";
    public static final String UPDATE_SCHEDULE = "UPDATE Schedule SET className=?, section=?, subject=?, teacherId=?, dayOfWeek=?, timeSlot=?, roomNo=?, principalId=? WHERE scheduleId=?";
    public static final String DELETE_SCHEDULE = "DELETE FROM Schedule WHERE scheduleId=?";
    public static final String SEARCH_SCHEDULE_BY_CLASS = "SELECT * FROM Schedule WHERE className = ? AND section = ?";
}

