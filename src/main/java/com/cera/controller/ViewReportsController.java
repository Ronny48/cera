package com.cera.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.cera.App;
import com.cera.ReportDAO;
import com.cera.AttachmentDAO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ButtonType;

/**
 * Controller for the reports view page
 * 
 * Handles display, filtering, and searching of incident reports.
 * Provides functionality for users to view all reports and filter by various
 * criteria.
 * 
 * @author CERA Development Team
 * @version 1.0
 */
public class ViewReportsController {

  @FXML
  private TextField searchField;

  @FXML
  private Button searchBtn;

  @FXML
  private ComboBox<String> categoryFilter;

  @FXML
  private Button clearFiltersBtn;

  @FXML
  private ToggleButton myReportsToggle;

  @FXML
  private VBox reportsContainer;

  private List<ReportDAO.Report> allReports;
  private List<ReportDAO.Report> filteredReports;

  /**
   * Initializes the reports view page
   * Loads reports and sets up filtering functionality
   */
  @FXML
  public void initialize() {
    // Load reports from database
    allReports = ReportDAO.getAllReports();
    filteredReports = new ArrayList<>(allReports);

    initializeFilters();

    // Set up search functionality
    searchField.setOnKeyReleased(event -> handleSearch(null));

    if (myReportsToggle != null) {
      myReportsToggle.setOnAction(e -> applyFilters(searchField.getText().toLowerCase().trim()));
      myReportsToggle.setDisable(App.getCurrentUserId() == null);
    }

    updateReportsDisplay();
  }

  /**
   * Initializes filter controls with available categories
   */
  private void initializeFilters() {
    categoryFilter.getItems().addAll("All Categories", "Theft", "Health", "Security", "Harassment", "Bullying",
        "Emergency", "Other");
    categoryFilter.setValue("All Categories");
  }

  /**
   * Navigates back to the home page
   * 
   * @param event Action event
   */
  @FXML
  private void handleBack(ActionEvent event) {
    try {
      App.setRoot("home");
    } catch (IOException e) {
      System.err.println("Failed to navigate to home: " + e.getMessage());
      showError("Navigation Error", "Could not return to home page.");
    }
  }

  /**
   * Handles search functionality
   * 
   * @param event Action event
   */
  @FXML
  private void handleSearch(ActionEvent event) {
    String searchTerm = searchField.getText().toLowerCase().trim();
    applyFilters(searchTerm);
  }

  /**
   * Handles category filter changes
   * 
   * @param event Action event
   */
  @FXML
  private void handleFilter(ActionEvent event) {
    String searchTerm = searchField.getText().toLowerCase().trim();
    applyFilters(searchTerm);
  }

  /**
   * Clears all filters and resets the view
   * 
   * @param event Action event
   */
  @FXML
  private void handleClearFilters(ActionEvent event) {
    searchField.clear();
    categoryFilter.setValue("All Categories");
    applyFilters("");
  }

  /**
   * Applies filters to the reports list
   * 
   * @param searchTerm Search term to filter by
   */
  private void applyFilters(String searchTerm) {
    filteredReports.clear();
    String selectedCategory = categoryFilter.getValue();
    boolean filterMyReports = myReportsToggle != null && myReportsToggle.isSelected() && App.getCurrentUserId() != null;
    Integer currentUserId = App.getCurrentUserId();

    for (ReportDAO.Report report : allReports) {
      boolean matchesSearch = searchTerm.isEmpty() ||
          report.category.toLowerCase().contains(searchTerm) ||
          report.location.toLowerCase().contains(searchTerm) ||
          report.description.toLowerCase().contains(searchTerm);
      boolean matchesCategory = selectedCategory.equals("All Categories") ||
          report.category.equals(selectedCategory);
      boolean matchesUser = !filterMyReports || (report.userId != null && report.userId.equals(currentUserId));

      if (matchesSearch && matchesCategory && matchesUser) {
        filteredReports.add(report);
      }
    }
    updateReportsDisplay();
  }

  /**
   * Updates the display of reports in the container
   */
  private void updateReportsDisplay() {
    reportsContainer.getChildren().clear();

    if (filteredReports.isEmpty()) {
      Label noResultsLabel = new Label("No reports found matching your criteria.");
      noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #B38753; -fx-alignment: center;");
      reportsContainer.getChildren().add(noResultsLabel);
      return;
    }

    for (ReportDAO.Report report : filteredReports) {
      VBox reportBox = createReportBox(report);
      reportsContainer.getChildren().add(reportBox);
    }
  }

  /**
   * Creates a visual representation of a report
   * 
   * @param report The report to display
   * @return VBox containing the report display
   */
  private VBox createReportBox(ReportDAO.Report report) {
    VBox reportBox = new VBox(8.0);
    reportBox.setStyle("-fx-background-color: #4A4A4A; -fx-background-radius: 12; -fx-padding: 16;");

    // Create header with category icon, title, and timestamp
    HBox header = new HBox(8.0);
    header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    Label iconLabel = new Label(getCategoryIcon(report.category));
    iconLabel.setStyle("-fx-font-size: 20px;");
    Label titleLabel = new Label(report.category + " Report");
    titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
    Label separator = new Label("•");
    separator.setStyle("-fx-text-fill: #B38753;");
    Label timeLabel = new Label(report.createdAt);
    timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #B38753;");
    header.getChildren().addAll(iconLabel, titleLabel, separator, timeLabel);

    // Create description label
    Label descriptionLabel = new Label(report.description);
    descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #E6E6E6; -fx-wrap-text: true;");

    // Create attachments section
    List<AttachmentDAO.Attachment> attachments = AttachmentDAO.getAttachmentsByReportId(report.id);
    VBox attachmentsBox = new VBox(4.0);
    if (!attachments.isEmpty()) {
      Label attachmentsTitle = new Label("Attachments:");
      attachmentsTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #B38753; -fx-font-weight: bold;");
      attachmentsBox.getChildren().add(attachmentsTitle);
      for (AttachmentDAO.Attachment att : attachments) {
        Label attLabel = new Label(att.filePath + " (" + att.fileType + ")");
        attLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #F5D77C;");
        attachmentsBox.getChildren().add(attLabel);
      }
    }

    // Create status section
    HBox statusBox = new HBox(8.0);
    statusBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    Label statusTitle = new Label("Status:");
    statusTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #B38753;");
    Label statusValue = new Label(report.resolved ? "Resolved" : "Under Investigation");
    statusValue.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; "
        + (report.resolved ? "-fx-text-fill: #4CAF50;" : "-fx-text-fill: #FF9800;"));
    statusBox.getChildren().addAll(statusTitle, statusValue);

    reportBox.getChildren().addAll(header, descriptionLabel, attachmentsBox, statusBox);
    return reportBox;
  }

  /**
   * Formats a timestamp to show relative time
   * 
   * @param timestamp The timestamp to format
   * @return Formatted time string
   */
  private String formatTimeAgo(LocalDateTime timestamp) {
    LocalDateTime now = LocalDateTime.now();
    long hours = java.time.Duration.between(timestamp, now).toHours();
    long days = java.time.Duration.between(timestamp, now).toDays();

    if (hours < 1) {
      return "Just now";
    } else if (hours < 24) {
      return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
    } else if (days < 7) {
      return days + " day" + (days == 1 ? "" : "s") + " ago";
    } else {
      return timestamp.format(DateTimeFormatter.ofPattern("MMM dd"));
    }
  }

  /**
   * Gets the color for a status
   * 
   * @param status The status to get color for
   * @return Color string
   */
  private String getStatusColor(String status) {
    switch (status) {
      case "Resolved":
        return "#4CAF50";
      case "Under Investigation":
        return "#FF9800";
      case "Pending":
        return "#2196F3";
      case "Closed":
        return "#9E9E9E";
      default:
        return "#FF9800";
    }
  }

  /**
   * Gets the emoji icon for a category
   * 
   * @param category The category to get icon for
   * @return Emoji string
   */
  private String getCategoryIcon(String category) {
    switch (category.toLowerCase()) {
      case "theft":
        return "🚨";
      case "health":
        return "⚠️";
      case "security":
        return "🔒";
      case "harassment":
        return "🚨";
      case "bullying":
        return "🚨";
      case "emergency":
        return "⚠️";
      default:
        return "📄";
    }
  }

  /**
   * Shows an error dialog
   * 
   * @param title   Error title
   * @param content Error content
   */
  private void showError(String title, String content) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }

  /**
   * Handles user logout with confirmation dialog
   */
  @FXML
  private void handleAvatarClick() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to logout?", ButtonType.YES, ButtonType.NO);
    alert.setTitle("Logout");
    alert.setHeaderText(null);
    alert.showAndWait().ifPresent(response -> {
      if (response == ButtonType.YES) {
        App.setCurrentUserId(null);
        try {
          App.setRoot("logIn");
        } catch (IOException e) {
          System.err.println("Failed to logout: " + e.getMessage());
        }
      }
    });
  }
}