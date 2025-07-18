package com.cera;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
  public static boolean createUser(String firstName, String otherName, String surname, String email, String password,
      String role) {
    String sql = "INSERT INTO users (first_name, other_name, surname, email, password_hash, role) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, firstName);
      pstmt.setString(2, otherName);
      pstmt.setString(3, surname);
      pstmt.setString(4, email);
      pstmt.setString(5, password); // TODO: Hash password in production
      pstmt.setString(6, role);
      pstmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static String validateLoginAndGetRole(String email, String password) {
    String sql = "SELECT role FROM users WHERE email = ? AND password_hash = ?";
    try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, email);
      pstmt.setString(2, password); // TODO: Hash password in production
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getString("role");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static boolean userExists(String email) {
    String sql = "SELECT 1 FROM users WHERE email = ?";
    try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, email);
      ResultSet rs = pstmt.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static Integer getUserIdByEmail(String email) {
    String sql = "SELECT id FROM users WHERE email = ?";
    try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, email);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getInt("id");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String getUserRoleByEmail(String email) {
    String sql = "SELECT role FROM users WHERE email = ?";
    try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, email);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getString("role");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}