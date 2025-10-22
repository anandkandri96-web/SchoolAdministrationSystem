package com.schooladmin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.schooladmin.db.DBConnection;
import com.schooladmin.db.Queries;
import com.schooladmin.model.Employee;

public class EmployeeDAO {

    public boolean create(Employee employee) {
        String sql = Queries.CREATE_EMPLOYEE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getUsername());
            pstmt.setString(3, employee.getPassword());
            pstmt.setString(4, employee.getRole());
            pstmt.setString(5, employee.getEmail());
            pstmt.setString(6, employee.getPhone());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Employee readById(int empId) {
        String sql = "SELECT * FROM Employees WHERE empId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, empId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractEmployeeFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Employee> readAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = Queries.READ_ALL_EMPLOYEES;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public boolean update(Employee employee) {
        String sql = Queries.UPDATE_EMPLOYEE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getUsername());
            pstmt.setString(3, employee.getPassword());
            pstmt.setString(4, employee.getRole());
            pstmt.setString(5, employee.getEmail());
            pstmt.setString(6, employee.getPhone());
            pstmt.setInt(7, employee.getEmpId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int empId) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Begin transaction

            // Step 1: Delete from attendance where teacherId = empId
            String deleteAttendance = "DELETE FROM attendance WHERE teacherId = ?";
            try (PreparedStatement pstmt1 = conn.prepareStatement(deleteAttendance)) {
                pstmt1.setInt(1, empId);
                pstmt1.executeUpdate();
            }

            // Step 2: Delete from employees table
            String deleteEmployee = Queries.DELETE_EMPLOYEE;
            try (PreparedStatement pstmt2 = conn.prepareStatement(deleteEmployee)) {
                pstmt2.setInt(1, empId);
                int rows = pstmt2.executeUpdate();

                conn.commit(); // Commit transaction
                return rows > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // You may want to rollback here if needed
            return false;
        }
    }

    public Employee findByUsername(String username) {
        String sql = Queries.READ_EMPLOYEE_BY_USERNAME;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractEmployeeFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Employee> searchByName(String name) {
        List<Employee> employees = new ArrayList<>();
        String sql = Queries.SEARCH_EMPLOYEE_BY_NAME;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(extractEmployeeFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setEmpId(rs.getInt("empId"));
        emp.setName(rs.getString("name"));
        emp.setUsername(rs.getString("username"));
        emp.setPassword(rs.getString("password"));
        emp.setRole(rs.getString("role"));
        emp.setEmail(rs.getString("email"));
        emp.setPhone(rs.getString("phone"));
        return emp;
    }
}
