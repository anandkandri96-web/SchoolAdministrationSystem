package com.schooladmin.model;

import java.util.Date;

public class Fee {
    private int feeId;
    private int studentId;  // FK to Student
    private double amount;
    private Date paymentDate;
    private String status;  // e.g., "Paid", "Pending", "Overdue"
    private int paidByEmpId;  // Clerk who processed (FK to Employee)

    // Default constructor
    public Fee() {}

    // Parameterized constructor
    public Fee(int feeId, int studentId, double amount, Date paymentDate, String status, int paidByEmpId) {
        this.feeId = feeId;
        this.studentId = studentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
        this.paidByEmpId = paidByEmpId;
    }

    // Getters and Setters
    public int getFeeId() {
        return feeId;
    }

    public void setFeeId(int feeId) {
        this.feeId = feeId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public java.sql.Date getPaymentDate() {
        return (java.sql.Date) paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPaidByEmpId() {
        return paidByEmpId;
    }

    public void setPaidByEmpId(int paidByEmpId) {
        this.paidByEmpId = paidByEmpId;
    }

    @Override
    public String toString() {
        return "Fee{" +
                "feeId=" + feeId +
                ", studentId=" + studentId +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", status='" + status + '\'' +
                ", paidByEmpId=" + paidByEmpId +
                '}';
    }
}

