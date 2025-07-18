package com.cera;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
  public static boolean createReport(Integer userId, String category, String location, String description,
      boolean isAnonymous) {
    String sql = "INSERT INTO reports (user_id, category, location, description, is_anonymous) VALUES (?, ?, ?, ?, ?)";
    try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
      if (userId == null) {
        pstmt.setNull(1, Types.INTEGER);
      } else {
        pstmt.setInt(1, userId);
      }
      pstmt.setString(2, category);
      pstmt.setString(3, location);
      pstmt.setString(4, description);
      pstmt.setInt(5, isAnonymous ? 1 : 0);
      pstmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static List<Report> getAllReports() {
    List<Report> reports = new ArrayList<>();
    String sql = "SELECT * FROM reports ORDER BY created_at DESC";
    try (Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        reports.add(new Report(
            rs.getInt("id"),
            rs.getObject("user_id") != null ? rs.getInt("user_id") : null,
            rs.getString("category"),
            rs.getString("location"),
            rs.getString("description"),
            rs.getString("created_at"),
            rs.getInt("is_anonymous") == 1,
            rs.getInt("resolved") == 1));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return reports;
  }

  public static List<Report> getUnresolvedReports() {
    List<Report> reports = new ArrayList<>();
    String sql = "SELECT * FROM reports WHERE resolved = 0 ORDER BY created_at DESC";
    try (Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        reports.add(new Report(
            rs.getInt("id"),
            rs.getObject("user_id") != null ? rs.getInt("user_id") : null,
            rs.getString("category"),
            rs.getString("location"),
            rs.getString("description"),
            rs.getString("created_at"),
            rs.getInt("is_anonymous") == 1,
            rs.getInt("resolved") == 1));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return reports;
  }

  public static boolean markReportResolved(int reportId) {
    String sql = "UPDATE reports SET resolved = 1 WHERE id = ?";
    try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, reportId);
      int affected = pstmt.executeUpdate();
      return affected > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static class Report {
    public int id;
    public Integer userId;
    public String category;
    public String location;
    public String description;
    public String createdAt;
    public boolean isAnonymous;
    public boolean resolved;

    public Report(int id, Integer userId, String category, String location, String description, String createdAt,
        boolean isAnonymous, boolean resolved) {
      this.id = id;
      this.userId = userId;
      this.category = category;
      this.location = location;
      this.description = description;
      this.createdAt = createdAt;
      this.isAnonymous = isAnonymous;
      this.resolved = resolved;
    }

    public int getId() {
      return id;
    }

    public Integer getUserId() {
      return userId;
    }

    public String getCategory() {
      return category;
    }

    public String getLocation() {
      return location;
    }

    public String getDescription() {
      return description;
    }

    public String getCreatedAt() {
      return createdAt;
    }

    public boolean isAnonymous() {
      return isAnonymous;
    }

    public boolean isResolved() {
      return resolved;
    }
  }
}