package com.cera.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.cera.App;
import com.cera.AttachmentDAO;
import com.cera.ReportDAO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

/**
 * Controller for the reports view page
 * 
 * Handles display, filtering, and searching of incident reports.
 * Provides functionality for users to view all reports and filter by various
 * criteria. Includes image preview functionality for attached images.
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
      System.err.println("Navigation error: " + e.getMessage());
      showError("Navigation Error", "Failed to navigate to home page.");
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
    applyFilters(searchField.getText().toLowerCase().trim());
  }

  /**
   * Clears all filters and search terms
   * 
   * @param event Action event
   */
  @FXML
  private void handleClearFilters(ActionEvent event) {
    searchField.clear();
    categoryFilter.setValue("All Categories");
    if (myReportsToggle != null) {
      myReportsToggle.setSelected(false);
    }
    applyFilters("");
  }

  /**
   * Applies filters to the reports list
   * 
   * @param searchTerm Search term to filter by
   */
  private void applyFilters(String searchTerm) {
    filteredReports.clear();

    for (ReportDAO.Report report : allReports) {
      boolean matchesSearch = searchTerm.isEmpty() ||
          report.description.toLowerCase().contains(searchTerm) ||
          report.category.toLowerCase().contains(searchTerm) ||
          report.location.toLowerCase().contains(searchTerm);

      boolean matchesCategory = categoryFilter.getValue().equals("All Categories") ||
          report.category.equals(categoryFilter.getValue());

      boolean matchesUser = !myReportsToggle.isSelected() ||
          (App.getCurrentUserId() != null && report.userId == App.getCurrentUserId());

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
   * Creates a visual representation of a report with image previews
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

    // Create attachments section with image previews
    List<AttachmentDAO.Attachment> attachments = AttachmentDAO.getAttachmentsByReportId(report.id);
    VBox attachmentsBox = new VBox(8.0);
    if (!attachments.isEmpty()) {
      Label attachmentsTitle = new Label("Attachments:");
      attachmentsTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #B38753; -fx-font-weight: bold;");
      attachmentsBox.getChildren().add(attachmentsTitle);

      // Create horizontal container for image thumbnails
      HBox imageContainer = new HBox(8.0);
      imageContainer.setAlignment(Pos.CENTER_LEFT);

      for (AttachmentDAO.Attachment att : attachments) {
        if (isImageFile(att.fileType)) {
          // Create image thumbnail
          VBox imageBox = createImageThumbnail(att);
          imageContainer.getChildren().add(imageBox);
        } else {
          // Create file link for non-image files
          Label fileLabel = new Label("📎 " + getFileName(att.filePath) + " (" + att.fileType + ")");
          fileLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #F5D77C; -fx-cursor: hand;");
          fileLabel.setOnMouseClicked(e -> openFile(att.filePath));
          attachmentsBox.getChildren().add(fileLabel);
        }
      }

      if (!imageContainer.getChildren().isEmpty()) {
        attachmentsBox.getChildren().add(imageContainer);
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
   * Creates an image thumbnail for display
   * 
   * @param attachment The attachment containing the image
   * @return VBox containing the image thumbnail
   */
  private VBox createImageThumbnail(AttachmentDAO.Attachment attachment) {
    VBox imageBox = new VBox(4.0);
    imageBox.setAlignment(Pos.CENTER);

    try {
      File imageFile = new File(attachment.filePath);
      if (imageFile.exists()) {
        // Create image view with thumbnail size
        ImageView imageView = new ImageView(new Image(imageFile.toURI().toString()));
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-background-color: #333; -fx-background-radius: 8;");

        // Add click handler to show full image
        imageView.setOnMouseClicked(e -> showFullImage(attachment.filePath, getFileName(attachment.filePath)));

        // Add hover effect
        imageView.setOnMouseEntered(
            e -> imageView.setStyle("-fx-background-color: #555; -fx-background-radius: 8; -fx-cursor: hand;"));
        imageView.setOnMouseExited(e -> imageView.setStyle("-fx-background-color: #333; -fx-background-radius: 8;"));

        // Add file name label
        Label fileNameLabel = new Label(getFileName(attachment.filePath));
        fileNameLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #F5D77C; -fx-alignment: center;");
        fileNameLabel.setMaxWidth(80);
        fileNameLabel.setWrapText(true);

        imageBox.getChildren().addAll(imageView, fileNameLabel);
      } else {
        // Show placeholder if file doesn't exist
        Label placeholder = new Label("❌");
        placeholder.setStyle("-fx-font-size: 24px; -fx-text-fill: #666;");
        imageBox.getChildren().add(placeholder);
      }
    } catch (Exception e) {
      System.err.println("Error loading image thumbnail: " + e.getMessage());
      Label errorLabel = new Label("⚠️");
      errorLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #FF9800;");
      imageBox.getChildren().add(errorLabel);
    }

    return imageBox;
  }

  /**
   * Shows a full-size image in a dialog
   * 
   * @param imagePath Path to the image file
   * @param fileName  Name of the file
   */
  private void showFullImage(String imagePath, String fileName) {
    try {
      File imageFile = new File(imagePath);
      if (!imageFile.exists()) {
        showError("File Not Found", "The image file could not be found.");
        return;
      }

      // Create dialog
      Dialog<Void> dialog = new Dialog<>();
      dialog.setTitle("Image Preview: " + fileName);
      dialog.initModality(Modality.APPLICATION_MODAL);

      // Create dialog pane
      DialogPane dialogPane = new DialogPane();
      dialog.setDialogPane(dialogPane);

      // Create image view
      ImageView fullImageView = new ImageView(new Image(imageFile.toURI().toString()));
      fullImageView.setFitWidth(600);
      fullImageView.setFitHeight(400);
      fullImageView.setPreserveRatio(true);

      // Create scroll pane for large images
      ScrollPane scrollPane = new ScrollPane(fullImageView);
      scrollPane.setFitToWidth(true);
      scrollPane.setFitToHeight(true);
      scrollPane.setPrefWidth(620);
      scrollPane.setPrefHeight(420);

      dialogPane.setContent(scrollPane);

      // Add close button
      dialog.setResultConverter(dialogButton -> null);
      dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

      // Show dialog
      dialog.showAndWait();

    } catch (Exception e) {
      System.err.println("Error showing full image: " + e.getMessage());
      showError("Image Error", "Failed to display the image.");
    }
  }

  /**
   * Opens a file using the system default application
   * 
   * @param filePath Path to the file to open
   */
  private void openFile(String filePath) {
    try {
      File file = new File(filePath);
      if (file.exists()) {
        // Use ProcessBuilder to open file with system default application
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;

        if (os.contains("win")) {
          // Windows
          pb = new ProcessBuilder("cmd", "/c", "start", "", filePath);
        } else if (os.contains("mac")) {
          // macOS
          pb = new ProcessBuilder("open", filePath);
        } else {
          // Linux/Unix
          pb = new ProcessBuilder("xdg-open", filePath);
        }

        pb.start();
      } else {
        showError("File Not Found", "The file could not be found.");
      }
    } catch (Exception e) {
      System.err.println("Error opening file: " + e.getMessage());
      showError("File Error", "Failed to open the file. You can manually open: " + filePath);
    }
  }

  /**
   * Checks if a file type is an image
   * 
   * @param fileType The file type to check
   * @return true if the file is an image, false otherwise
   */
  private boolean isImageFile(String fileType) {
    if (fileType == null)
      return false;
    String lowerType = fileType.toLowerCase();
    return lowerType.contains("image") ||
        lowerType.contains("jpg") || lowerType.contains("jpeg") ||
        lowerType.contains("png") || lowerType.contains("gif") ||
        lowerType.contains("bmp") || lowerType.contains("webp");
  }

  /**
   * Extracts the file name from a file path
   * 
   * @param filePath The full file path
   * @return The file name
   */
  private String getFileName(String filePath) {
    if (filePath == null)
      return "Unknown";
    File file = new File(filePath);
    return file.getName();
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
    switch (status.toLowerCase()) {
      case "resolved":
        return "#4CAF50";
      case "under investigation":
        return "#FF9800";
      case "pending":
        return "#2196F3";
      default:
        return "#B38753";
    }
  }

  /**
   * Gets the appropriate icon for a category
   * 
   * @param category The category to get icon for
   * @return Icon string
   */
  private String getCategoryIcon(String category) {
    switch (category.toLowerCase()) {
      case "theft":
        return "🔒";
      case "health":
        return "🏥";
      case "security":
        return "🛡️";
      case "harassment":
        return "🚨";
      case "bullying":
        return "⚠️";
      case "emergency":
        return "🚨";
      default:
        return "📋";
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
   * Handles logout functionality
   */
  @FXML
  private void handleAvatarClick() {
    try {
      App.setCurrentUserId(null);
      App.setRoot("logIn");
    } catch (IOException e) {
      System.err.println("Logout error: " + e.getMessage());
      showError("Logout Error", "Failed to logout. Please try again.");
    }
  }
}