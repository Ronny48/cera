package com.cera.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.cera.App;

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

public class ViewReportsController {

  @FXML
  private TextField searchField;

  @FXML
  private Button searchBtn;

  @FXML
  private ComboBox<String> categoryFilter;

  @FXML
  private ComboBox<String> statusFilter;

  @FXML
  private Button clearFiltersBtn;

  @FXML
  private VBox reportsContainer;

  private List<Report> allReports;
  private List<Report> filteredReports;

  @FXML
  public void initialize() {
    // Initialize sample data
    initializeSampleReports();
    initializeFilters();

    // Set up search functionality
    searchField.setOnKeyReleased(event -> handleSearch(null));
  }

  private void initializeSampleReports() {
    allReports = new ArrayList<>();

    // Add sample reports
    allReports.add(new Report("🚨", "Theft Incident",
        "Reported theft of personal belongings in the library area. Security has been notified.",
        "Under Investigation", "Theft", LocalDateTime.now().minusHours(2)));

    allReports.add(
        new Report("⚠️", "Health Emergency", "Medical emergency in the science building. Ambulance has been called.",
            "Resolved", "Health", LocalDateTime.now().minusDays(1)));

    allReports.add(new Report("🔒", "Security Concern",
        "Suspicious activity reported near the parking lot. Campus security is monitoring the area.",
        "Under Investigation", "Security", LocalDateTime.now().minusDays(3)));

    allReports.add(new Report("🚨", "Harassment Report",
        "Verbal harassment reported in the student center. Counseling services have been contacted.",
        "Resolved", "Harassment", LocalDateTime.now().minusWeeks(1)));

    allReports.add(new Report("🚨", "Bullying Incident",
        "Physical bullying reported in the cafeteria. School administration has been notified.",
        "Under Investigation", "Bullying", LocalDateTime.now().minusDays(2)));

    allReports.add(new Report("⚠️", "Fire Alarm",
        "Fire alarm activated in the engineering building. All students evacuated safely.",
        "Resolved", "Emergency", LocalDateTime.now().minusDays(4)));

    filteredReports = new ArrayList<>(allReports);
  }

  private void initializeFilters() {
    // Initialize category filter
    categoryFilter.getItems().addAll("All Categories", "Theft", "Health", "Security", "Harassment", "Bullying",
        "Emergency", "Other");
    categoryFilter.setValue("All Categories");

    // Initialize status filter
    statusFilter.getItems().addAll("All Status", "Under Investigation", "Resolved", "Pending", "Closed");
    statusFilter.setValue("All Status");
  }

  @FXML
  private void handleBack(ActionEvent event) {
    try {
      App.setRoot("home");
    } catch (IOException e) {
      e.printStackTrace();
      showError("Navigation Error", "Could not return to home page.");
    }
  }

  @FXML
  private void handleSearch(ActionEvent event) {
    String searchTerm = searchField.getText().toLowerCase().trim();
    applyFilters(searchTerm);
  }

  @FXML
  private void handleFilter(ActionEvent event) {
    String searchTerm = searchField.getText().toLowerCase().trim();
    applyFilters(searchTerm);
  }

  @FXML
  private void handleClearFilters(ActionEvent event) {
    searchField.clear();
    categoryFilter.setValue("All Categories");
    statusFilter.setValue("All Status");
    applyFilters("");
  }

  private void applyFilters(String searchTerm) {
    filteredReports.clear();

    String selectedCategory = categoryFilter.getValue();
    String selectedStatus = statusFilter.getValue();

    for (Report report : allReports) {
      boolean matchesSearch = searchTerm.isEmpty() ||
          report.title.toLowerCase().contains(searchTerm) ||
          report.description.toLowerCase().contains(searchTerm);

      boolean matchesCategory = selectedCategory.equals("All Categories") ||
          report.category.equals(selectedCategory);

      boolean matchesStatus = selectedStatus.equals("All Status") ||
          report.status.equals(selectedStatus);

      if (matchesSearch && matchesCategory && matchesStatus) {
        filteredReports.add(report);
      }
    }

    updateReportsDisplay();
  }

  private void updateReportsDisplay() {
    reportsContainer.getChildren().clear();

    if (filteredReports.isEmpty()) {
      Label noResultsLabel = new Label("No reports found matching your criteria.");
      noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #B38753; -fx-alignment: center;");
      reportsContainer.getChildren().add(noResultsLabel);
      return;
    }

    for (Report report : filteredReports) {
      VBox reportBox = createReportBox(report);
      reportsContainer.getChildren().add(reportBox);
    }
  }

  private VBox createReportBox(Report report) {
    VBox reportBox = new VBox(8.0);
    reportBox.setStyle("-fx-background-color: #4A4A4A; -fx-background-radius: 12; -fx-padding: 16;");

    // Header with icon, title, and time
    HBox header = new HBox(8.0);
    header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

    Label iconLabel = new Label(report.icon);
    iconLabel.setStyle("-fx-font-size: 20px;");

    Label titleLabel = new Label(report.title);
    titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

    Label separator = new Label("•");
    separator.setStyle("-fx-text-fill: #B38753;");

    Label timeLabel = new Label(formatTimeAgo(report.timestamp));
    timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #B38753;");

    header.getChildren().addAll(iconLabel, titleLabel, separator, timeLabel);

    // Description
    Label descriptionLabel = new Label(report.description);
    descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #E6E6E6; -fx-wrap-text: true;");

    // Status
    HBox statusBox = new HBox(8.0);
    Label statusLabel = new Label("Status:");
    statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #B38753;");

    Label statusValue = new Label(report.status);
    String statusColor = getStatusColor(report.status);
    statusValue.setStyle("-fx-font-size: 12px; -fx-text-fill: " + statusColor + ";");

    statusBox.getChildren().addAll(statusLabel, statusValue);

    reportBox.getChildren().addAll(header, descriptionLabel, statusBox);

    return reportBox;
  }

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

  private void showError(String title, String content) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }

  // Inner class to represent a report
  private static class Report {
    String icon;
    String title;
    String description;
    String status;
    String category;
    LocalDateTime timestamp;

    Report(String icon, String title, String description, String status, String category, LocalDateTime timestamp) {
      this.icon = icon;
      this.title = title;
      this.description = description;
      this.status = status;
      this.category = category;
      this.timestamp = timestamp;
    }
  }
}