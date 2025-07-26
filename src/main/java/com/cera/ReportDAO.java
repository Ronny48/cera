package com.cera;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for report management operations
 * 
 * Handles incident report creation, retrieval, and status updates.
 * Provides methods for report CRUD operations against the SQLite database.
 * 
 * @author CERA Development Team
 * @version 1.0
 */
public class ReportDAO {

  /**
   * Creates a new incident report in the database
   * 
   * @param userId      User ID (null for anonymous reports)
   * @param category    Incident category (e.g., "Theft", "Security", "Health")
   * @param location    Incident location on campus
   * @param description Detailed description of the incident
   * @param isAnonymous Whether the report is anonymous
   * @return true if report creation successful, false otherwise
   */
  public static boolean createReport(Integer userId, String category, String location, String description,
      boolean isAnonymous) {
    String sql = "INSERT INTO reports (user_id, category, location, description, is_anonymous) VALUES (?, ?, ?, ?, ?)";
    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
      System.err.println("Failed to create report: " + e.getMessage());
      return false;
    }
  }

  /**
   * Retrieves all reports from the database, ordered by creation date (newest
   * first)
   * 
   * @return List of all reports
   */
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
      System.err.println("Failed to retrieve reports: " + e.getMessage());
    }
    return reports;
  }

  /**
   * Retrieves all unresolved reports from the database
   * 
   * @return List of unresolved reports
   */
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
      System.err.println("Failed to retrieve unresolved reports: " + e.getMessage());
    }
    return reports;
  }

  /**
   * Marks a report as resolved
   * 
   * @param reportId ID of the report to mark as resolved
   * @return true if update successful, false otherwise
   */
  public static boolean markReportResolved(int reportId) {
    String sql = "UPDATE reports SET resolved = 1 WHERE id = ?";
    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, reportId);
      int affected = pstmt.executeUpdate();
      return affected > 0;
    } catch (SQLException e) {
      System.err.println("Failed to mark report as resolved: " + e.getMessage());
      return false;
    }
  }

  /**
   * Data class representing an incident report
   */
  public static class Report {
    public int id;
    public Integer userId;
    public String category;
    public String location;
    public String description;
    public String createdAt;
    public boolean isAnonymous;
    public boolean resolved;

    /**
     * Constructor for Report object
     * 
     * @param id          Report ID
     * @param userId      User ID (null for anonymous reports)
     * @param category    Incident category
     * @param location    Incident location
     * @param description Incident description
     * @param createdAt   Creation timestamp
     * @param isAnonymous Whether report is anonymous
     * @param resolved    Whether report is resolved
     */
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

    // Getter methods
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