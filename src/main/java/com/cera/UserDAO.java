package com.cera;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for user management operations
 * 
 * Handles user authentication, registration, and user data retrieval.
 * Provides methods for user CRUD operations against the SQLite database.
 * 
 * @author CERA Development Team
 * @version 1.0
 */
public class UserDAO {

  /**
   * Creates a new user in the database
   * 
   * @param firstName User's first name
   * @param otherName User's other/middle name (optional)
   * @param surname   User's surname
   * @param email     User's email address (unique)
   * @param password  User's password (should be hashed in production)
   * @param role      User's role (user/admin)
   * @return true if user creation successful, false otherwise
   */
  public static boolean createUser(String firstName, String otherName, String surname, String email, String password,
      String role) {
    String sql = "INSERT INTO users (first_name, other_name, surname, email, password_hash, role) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, firstName);
      pstmt.setString(2, otherName);
      pstmt.setString(3, surname);
      pstmt.setString(4, email);
      pstmt.setString(5, password); // Note: Should implement password hashing for production
      pstmt.setString(6, role);
      pstmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      System.err.println("Failed to create user: " + e.getMessage());
      return false;
    }
  }

  /**
   * Validates user login credentials and returns user role
   * 
   * @param email    User's email address
   * @param password User's password
   * @return User role if authentication successful, null otherwise
   */
  public static String validateLoginAndGetRole(String email, String password) {
    String sql = "SELECT role FROM users WHERE email = ? AND password_hash = ?";
    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, email);
      pstmt.setString(2, password); // Note: Should implement password hashing for production
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getString("role");
      }
    } catch (SQLException e) {
      System.err.println("Login validation failed: " + e.getMessage());
    }
    return null;
  }

  /**
   * Checks if a user with the given email already exists
   * 
   * @param email Email address to check
   * @return true if user exists, false otherwise
   */
  public static boolean userExists(String email) {
    String sql = "SELECT 1 FROM users WHERE email = ?";
    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, email);
      ResultSet rs = pstmt.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      System.err.println("User existence check failed: " + e.getMessage());
      return false;
    }
  }

  /**
   * Gets user ID by email address
   * 
   * @param email User's email address
   * @return User ID if found, null otherwise
   */
  public static Integer getUserIdByEmail(String email) {
    String sql = "SELECT id FROM users WHERE email = ?";
    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, email);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getInt("id");
      }
    } catch (SQLException e) {
      System.err.println("Failed to get user ID: " + e.getMessage());
    }
    return null;
  }

  /**
   * Gets user role by email address
   * 
   * @param email User's email address
   * @return User role if found, null otherwise
   */
  public static String getUserRoleByEmail(String email) {
    String sql = "SELECT role FROM users WHERE email = ?";
    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, email);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getString("role");
      }
    } catch (SQLException e) {
      System.err.println("Failed to get user role: " + e.getMessage());
    }
    return null;
  }
}