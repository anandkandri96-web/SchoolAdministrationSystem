package com.schooladmin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.schooladmin.db.DBConnection;
import com.schooladmin.db.Queries;
import com.schooladmin.model.Fee;

public class FeeDAO {

    public boolean create(Fee fee) {
        String sql = Queries.CREATE_FEE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fee.getStudentId());
            pstmt.setDouble(2, fee.getAmount());
            if (fee.getPaymentDate() != null) {
                pstmt.setDate(3, fee.getPaymentDate());
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            pstmt.setString(4, fee.getStatus());
            pstmt.setInt(5, fee.getPaidByEmpId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Fee readById(int feeId) {
        String sql = Queries.READ_FEE_BY_ID;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, feeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractFeeFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Fee> readAll() {
        List<Fee> fees = new ArrayList<>();
        String sql = "SELECT * FROM Fees";  // custom query since not in Queries
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                fees.add(extractFeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fees;
    }

    public List<Fee> readByStudentId(int studentId) {
        List<Fee> fees = new ArrayList<>();
        String sql = Queries.READ_FEES_BY_STUDENT;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    fees.add(extractFeeFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fees;
    }

    public boolean update(Fee fee) {
        String sql = Queries.UPDATE_FEE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, fee.getAmount());
            if (fee.getPaymentDate() != null) {
                pstmt.setDate(2, fee.getPaymentDate());
            } else {
                pstmt.setNull(2, Types.DATE);
            }
            pstmt.setString(3, fee.getStatus());
            pstmt.setInt(4, fee.getPaidByEmpId());
            pstmt.setInt(5, fee.getFeeId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int feeId) {
        String sql = Queries.DELETE_FEE;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, feeId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes all fees records for a given studentId.
     * Useful for cascading deletes before deleting a student.
     */
    public boolean deleteByStudentId(int studentId) {
        String sql = "DELETE FROM Fees WHERE studentId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.executeUpdate(); // We don't check rows count here — delete may be zero and still okay
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Fee> searchPendingFees() {
        List<Fee> fees = new ArrayList<>();
        String sql = Queries.SEARCH_FEE_PENDING;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                fees.add(extractFeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fees;
    }

    private Fee extractFeeFromResultSet(ResultSet rs) throws SQLException {
        Fee fee = new Fee();
        fee.setFeeId(rs.getInt("feeId"));
        fee.setStudentId(rs.getInt("studentId"));
        fee.setAmount(rs.getDouble("amount"));
        fee.setPaymentDate(rs.getDate("paymentDate"));
        fee.setStatus(rs.getString("status"));
        fee.setPaidByEmpId(rs.getInt("paidByEmpId"));
        return fee;
    }
}
