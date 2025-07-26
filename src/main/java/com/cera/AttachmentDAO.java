package com.cera;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for file attachment management
 * 
 * Handles file attachment storage and retrieval for incident reports.
 * Provides methods for attachment CRUD operations against the SQLite database.
 * 
 * @author CERA Development Team
 * @version 1.0
 */
public class AttachmentDAO {

  /**
   * Adds a file attachment to a report
   * 
   * @param reportId ID of the report to attach the file to
   * @param filePath Path to the file on the local filesystem
   * @param fileType Type of file (image, video, audio, document, other)
   * @return true if attachment successful, false otherwise
   */
  public static boolean addAttachment(int reportId, String filePath, String fileType) {
    String sql = "INSERT INTO attachments (report_id, file_path, file_type) VALUES (?, ?, ?)";
    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, reportId);
      pstmt.setString(2, filePath);
      pstmt.setString(3, fileType);
      pstmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      System.err.println("Failed to add attachment: " + e.getMessage());
      return false;
    }
  }

  /**
   * Retrieves all attachments for a specific report
   * 
   * @param reportId ID of the report
   * @return List of attachments for the report
   */
  public static List<Attachment> getAttachmentsByReportId(int reportId) {
    List<Attachment> attachments = new ArrayList<>();
    String sql = "SELECT * FROM attachments WHERE report_id = ?";
    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, reportId);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        attachments.add(new Attachment(
            rs.getInt("id"),
            rs.getInt("report_id"),
            rs.getString("file_path"),
            rs.getString("file_type"),
            rs.getString("uploaded_at")));
      }
    } catch (SQLException e) {
      System.err.println("Failed to retrieve attachments: " + e.getMessage());
    }
    return attachments;
  }

  /**
   * Data class representing a file attachment
   */
  public static class Attachment {
    public int id;
    public int reportId;
    public String filePath;
    public String fileType;
    public String uploadedAt;

    /**
     * Constructor for Attachment object
     * 
     * @param id         Attachment ID
     * @param reportId   ID of the associated report
     * @param filePath   Path to the file
     * @param fileType   Type of file
     * @param uploadedAt Upload timestamp
     */
    public Attachment(int id, int reportId, String filePath, String fileType, String uploadedAt) {
      this.id = id;
      this.reportId = reportId;
      this.filePath = filePath;
      this.fileType = fileType;
      this.uploadedAt = uploadedAt;
    }
  }
}