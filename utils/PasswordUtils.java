/*package com.schooladmin.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {
    // Simple SHA-256 hash (for demo; use BCrypt in production)
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // Verify plain password against hashed
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        String hashed = hashPassword(plainPassword);
        return hashed.equals(hashedPassword);
    }
}*/

package com.schooladmin.utils;

public class PasswordUtils {
    // Optionally keep this, but won't use for verification
    public static String hashPassword(String password) {
        // same as before or even return the password directly
        return password;
    }

    public static boolean verifyPassword(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }
        String trimmed = plainPassword.trim();
        return trimmed.equals(storedPassword);
    }
}
