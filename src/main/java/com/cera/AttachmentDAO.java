package com.cera;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttachmentDAO {
  public static boolean addAttachment(int reportId, String filePath, String fileType) {
    String sql = "INSERT INTO attachments (report_id, file_path, file_type) VALUES (?, ?, ?)";
    try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, reportId);
      pstmt.setString(2, filePath);
      pstmt.setString(3, fileType);
      pstmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static List<Attachment> getAttachmentsByReportId(int reportId) {
    List<Attachment> attachments = new ArrayList<>();
    String sql = "SELECT * FROM attachments WHERE report_id = ?";
    try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
      e.printStackTrace();
    }
    return attachments;
  }

  public static class Attachment {
    public int id;
    public int reportId;
    public String filePath;
    public String fileType;
    public String uploadedAt;

    public Attachment(int id, int reportId, String filePath, String fileType, String uploadedAt) {
      this.id = id;
      this.reportId = reportId;
      this.filePath = filePath;
      this.fileType = fileType;
      this.uploadedAt = uploadedAt;
    }
  }
}