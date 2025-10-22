package com.schooladmin.model;

import java.util.Date;

public class Student {
    private int studentId;
    private String name;
    private String parentName;
    private String parentPhone;
    private String address;
    private String className;  // e.g., "Class 10"
    private String section;   // e.g., "A"
    private String previousPerformance;  // e.g., "85% in Class 9"
    private Date admissionDate;
    private int empId;  // Clerk who admitted (FK to Employee)

    // Default constructor
    public Student() {}

    // Parameterized constructor
    public Student(int studentId, String name, String parentName, String parentPhone, String address,
                   String className, String section, String previousPerformance, Date admissionDate, int empId) {
        this.studentId = studentId;
        this.name = name;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.address = address;
        this.className = className;
        this.section = section;
        this.previousPerformance = previousPerformance;
        this.admissionDate = admissionDate;
        this.empId = empId;
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getPreviousPerformance() {
        return previousPerformance;
    }

    public void setPreviousPerformance(String previousPerformance) {
        this.previousPerformance = previousPerformance;
    }

    public java.sql.Date getAdmissionDate() {
        return (java.sql.Date) admissionDate;
    }

    public void setAdmissionDate(Date admissionDate) {
        this.admissionDate = admissionDate;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", name='" + name + '\'' +
                ", parentName='" + parentName + '\'' +
                ", parentPhone='" + parentPhone + '\'' +
                ", className='" + className + '\'' +
                ", section='" + section + '\'' +
                ", previousPerformance='" + previousPerformance + '\'' +
                ", admissionDate=" + admissionDate +
                ", empId=" + empId +
                '}';
    }
}

