package com.cera.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.cera.App;
import com.cera.ReportDAO;
import com.cera.AttachmentDAO;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.layout.HBox;

/**
 * Controller for the identified report form
 * 
 * Handles report submission with user identification.
 * Provides file upload functionality and form validation.
 * 
 * @author CERA Development Team
 * @version 1.0
 */
public class ReportWithIdController implements Initializable {

  @FXML
  private Button securityBtn;

  @FXML
  private Button theftBtn;

  @FXML
  private Button harassmentBtn;

  @FXML
  private Button abuseBtn;

  @FXML
  private Button bullyBtn;

  @FXML
  private Button fraudBtn;

  @FXML
  private Button healthBtn;

  @FXML
  private Button otherBtn;

  @FXML
  private ComboBox<String> locationCombo;

  @FXML
  private TextArea descriptionArea;

  @FXML
  private Button submitBtn;

  @FXML
  private VBox fileUploadArea;

  @FXML
  private VBox attachmentsContainer;

  @FXML
  private ProgressIndicator progressIndicator;

  @FXML
  private Label statusLabel;

  private String selectedCategory = null;
  private List<File> attachedFiles = new ArrayList<>();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    locationCombo.getItems().addAll(
        "Dormitory/Residence Hall",
        "Classroom/Lecture Hall",
        "Cafeteria/Dining Hall",
        "Library",
        "Sports Facility",
        "Parking Area",
        "Campus Grounds",
        "Administrative Building",
        "Other");

    // Set up file upload functionality
    fileUploadArea.setOnMouseClicked(e -> handleFileUpload());
    fileUploadArea.setOnDragOver(this::handleDragOver);
    fileUploadArea.setOnDragDropped(this::handleDragDropped);
    updateAttachmentsDisplay();
  }

  /**
   * Handles category button selection
   * 
   * @param event Action event
   */
  @FXML
  private void handleCategory(javafx.event.ActionEvent event) {
    Button clickedButton = (Button) event.getSource();
    selectedCategory = clickedButton.getText();

    // Reset all button styles
    resetButtonStyles();

    // Highlight selected button
    clickedButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 8;");
  }

  /**
   * Resets all category button styles to default
   */
  private void resetButtonStyles() {
    Button[] buttons = { securityBtn, theftBtn, harassmentBtn, abuseBtn, bullyBtn, fraudBtn, healthBtn, otherBtn };
    for (Button btn : buttons) {
      if (btn != null) {
        btn.setStyle("-fx-background-color: #6B5A3A; -fx-text-fill: white; -fx-background-radius: 8;");
      }
    }
  }

  /**
   * Handles file upload via file chooser
   */
  private void handleFileUpload() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select Files");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("All Files", "*.*"),
        new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif"),
        new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi"),
        new FileChooser.ExtensionFilter("Audio", "*.mp3", "*.wav"),
        new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx", "*.txt"));

    List<File> selectedFiles = fileChooser.showOpenMultipleDialog(fileUploadArea.getScene().getWindow());
    if (selectedFiles != null) {
      attachedFiles.addAll(selectedFiles);
      updateAttachmentsDisplay();
    }
  }

  /**
   * Handles drag over event for file upload
   * 
   * @param event Drag event
   */
  private void handleDragOver(DragEvent event) {
    if (event.getDragboard().hasFiles()) {
      event.acceptTransferModes(TransferMode.COPY);
    }
    event.consume();
  }

  /**
   * Handles drag dropped event for file upload
   * 
   * @param event Drag event
   */
  private void handleDragDropped(DragEvent event) {
    Dragboard db = event.getDragboard();
    boolean success = false;
    if (db.hasFiles()) {
      attachedFiles.addAll(db.getFiles());
      updateAttachmentsDisplay();
      success = true;
    }
    event.setDropCompleted(success);
    event.consume();
  }

  /**
   * Updates the display of attached files
   */
  private void updateAttachmentsDisplay() {
    attachmentsContainer.getChildren().clear();
    for (File file : attachedFiles) {
      HBox fileBox = new HBox(8);
      fileBox.setStyle("-fx-background-color: #4A4A4A; -fx-background-radius: 6; -fx-padding: 8;");

      Circle fileIcon = new Circle(8, Color.GRAY);
      Label fileName = new Label(file.getName());
      fileName.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

      Button removeBtn = new Button("×");
      removeBtn.setStyle(
          "-fx-background-color: #ff5555; -fx-text-fill: white; -fx-background-radius: 10; -fx-min-width: 20; -fx-min-height: 20;");
      removeBtn.setOnAction(e -> {
        attachedFiles.remove(file);
        updateAttachmentsDisplay();
      });

      fileBox.getChildren().addAll(fileIcon, fileName, removeBtn);
      attachmentsContainer.getChildren().add(fileBox);
    }
  }

  /**
   * Shows status message with auto-hide functionality
   * 
   * @param message Status message
   * @param error   Whether this is an error message
   */
  private void showStatus(String message, boolean error) {
    statusLabel.setText(message);
    statusLabel.setStyle(error ? "-fx-text-fill: #ff5555;" : "-fx-text-fill: #4CAF50;");
    statusLabel.setVisible(true);
    Platform.runLater(() -> {
      try {
        Thread.sleep(1800);
      } catch (InterruptedException ignored) {
        // Ignore interruption
      }
      statusLabel.setVisible(false);
    });
  }

  /**
   * Handles form submission
   * Validates form data and saves report to database
   */
  @FXML
  private void handleSubmit() {
    String category = selectedCategory;
    String location = locationCombo.getValue();
    String description = descriptionArea.getText();

    if (category == null || location == null || location.isEmpty() || description == null || description.isEmpty()) {
      showStatus("Please fill all required fields!", true);
      return;
    }

    Integer userId = App.getCurrentUserId();
    boolean isAnonymous = (userId == null);
    boolean reportSaved = ReportDAO.createReport(userId, category, location, description, isAnonymous);

    if (reportSaved) {
      int reportId = getLastInsertedReportId();
      for (File file : attachedFiles) {
        AttachmentDAO.addAttachment(reportId, file.getAbsolutePath(), getFileType(file));
      }
      showStatus("Report submitted successfully!", false);

      // Show success dialog
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Success");
      alert.setHeaderText(null);
      alert.setContentText("Your report has been submitted successfully!");
      alert.showAndWait();

      try {
        App.setRoot("home");
      } catch (IOException e) {
        System.err.println("Failed to navigate to home after report submission: " + e.getMessage());
      }
    } else {
      showStatus("Failed to submit report", true);
    }
  }

  /**
   * Gets the ID of the most recently inserted report
   * 
   * @return Report ID, or -1 if no reports exist
   */
  private int getLastInsertedReportId() {
    List<ReportDAO.Report> reports = ReportDAO.getAllReports();
    return reports.isEmpty() ? -1 : reports.get(0).id;
  }

  /**
   * Determines the file type based on file extension
   * 
   * @param file The file to analyze
   * @return File type string
   */
  private String getFileType(File file) {
    String name = file.getName().toLowerCase();
    if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif")) {
      return "image";
    }
    if (name.endsWith(".mp4") || name.endsWith(".avi")) {
      return "video";
    }
    if (name.endsWith(".mp3") || name.endsWith(".wav")) {
      return "audio";
    }
    if (name.endsWith(".pdf") || name.endsWith(".doc") || name.endsWith(".docx") || name.endsWith(".txt")) {
      return "document";
    }
    return "other";
  }

  /**
   * Navigates back to the home page
   */
  @FXML
  private void handleBack() {
    try {
      App.setRoot("home");
    } catch (IOException e) {
      System.err.println("Failed to navigate to home: " + e.getMessage());
    }
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

  /**
   * Navigates to the home page
   */
  @FXML
  private void goHome() {
    try {
      App.setRoot("home");
    } catch (IOException e) {
      System.err.println("Failed to navigate to home: " + e.getMessage());
    }
  }
}