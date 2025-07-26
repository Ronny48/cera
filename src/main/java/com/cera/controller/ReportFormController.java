package com.cera.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.cera.App;
import com.cera.ReportDAO;
import com.cera.AttachmentDAO;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;

/**
 * Controller for the anonymous report form
 * 
 * Handles anonymous incident report submission with file upload functionality.
 * Provides form validation, file attachment support, and user interface
 * interactions.
 * 
 * @author CERA Development Team
 * @version 1.0
 */
public class ReportFormController implements Initializable {
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
  private Label statusLabel;
  @FXML
  private ProgressIndicator progressIndicator;
  @FXML
  private VBox attachmentsContainer;

  private String selectedCategory = null;
  private List<File> uploadedFiles = new ArrayList<>();
  private boolean isRecording = false;
  private Stage primaryStage;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    setupLocationCombo();
    setupCategoryButtons();
    setupFileUploadArea();
    setupFormValidation();

    // Hide progress indicator initially
    if (progressIndicator != null) {
      progressIndicator.setVisible(false);
    }
  }

  private void setupLocationCombo() {
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

    // Add change listener for validation
    locationCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
      validateForm();
    });
  }

  private void setupCategoryButtons() {
    Button[] categoryButtons = { securityBtn, theftBtn, harassmentBtn, abuseBtn,
        bullyBtn, fraudBtn, healthBtn, otherBtn };

    for (Button btn : categoryButtons) {
      if (btn != null) {
        setupButtonAnimations(btn);
        setupButtonHoverEffects(btn);
      }
    }
  }

  private void setupButtonAnimations(Button button) {
    // Add click animation
    button.setOnMousePressed(e -> {
      ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
      st.setToX(0.95);
      st.setToY(0.95);
      st.play();
    });

    button.setOnMouseReleased(e -> {
      ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
      st.setToX(1.0);
      st.setToY(1.0);
      st.play();
    });
  }

  private void setupButtonHoverEffects(Button button) {
    DropShadow glow = new DropShadow();
    glow.setColor(Color.rgb(110, 209, 231, 0.8)); // Light blue glow
    glow.setWidth(20);
    glow.setHeight(20);
    glow.setRadius(10);

    button.setOnMouseEntered(e -> {
      button.setEffect(glow);
      button.setStyle(button.getStyle() + "; -fx-background-color: #4A3F2E;");
    });

    button.setOnMouseExited(e -> {
      button.setEffect(null);
      button.setStyle(button.getStyle().replace("; -fx-background-color: #4A3F2E;", ""));
    });
  }

  private void setupFileUploadArea() {
    if (fileUploadArea != null) {
      // Enable drag and drop
      fileUploadArea.setOnDragOver(this::handleDragOver);
      fileUploadArea.setOnDragDropped(this::handleDragDropped);

      // Add click to browse functionality
      fileUploadArea.setOnMouseClicked(e -> browseFiles());

      // Add tooltip
      Tooltip tooltip = new Tooltip("Click to browse files or drag and drop files here");
      Tooltip.install(fileUploadArea, tooltip);
    }
  }

  private void setupFormValidation() {
    // Add listeners for real-time validation
    if (descriptionArea != null) {
      descriptionArea.textProperty().addListener((obs, oldVal, newVal) -> {
        validateForm();
      });
    }
  }

  private void handleDragOver(DragEvent event) {
    if (event.getGestureSource() != fileUploadArea &&
        event.getDragboard().hasFiles()) {
      event.acceptTransferModes(TransferMode.COPY);
    }
    event.consume();
  }

  private void handleDragDropped(DragEvent event) {
    Dragboard db = event.getDragboard();
    boolean success = false;

    if (db.hasFiles()) {
      for (File file : db.getFiles()) {
        if (isValidFile(file)) {
          uploadedFiles.add(file);
          addFilePreview(file);
          success = true;
        }
      }
    }

    event.setDropCompleted(success);
    event.consume();

    if (success) {
      showNotification("Files uploaded successfully!", "success");
    }
  }

  private boolean isValidFile(File file) {
    String name = file.getName().toLowerCase();
    return name.endsWith(".jpg") || name.endsWith(".jpeg") ||
        name.endsWith(".png") || name.endsWith(".gif") ||
        name.endsWith(".mp4") || name.endsWith(".avi") ||
        name.endsWith(".mp3") || name.endsWith(".wav") ||
        name.endsWith(".pdf") || name.endsWith(".doc") ||
        name.endsWith(".docx") || name.endsWith(".txt");
  }

  private void browseFiles() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select Files to Upload");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("All Supported Files",
            "*.jpg", "*.jpeg", "*.png", "*.gif", "*.mp4", "*.avi",
            "*.mp3", "*.wav", "*.pdf", "*.doc", "*.docx", "*.txt"),
        new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif"),
        new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi"),
        new FileChooser.ExtensionFilter("Audio", "*.mp3", "*.wav"),
        new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx", "*.txt"));

    List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);
    if (selectedFiles != null) {
      for (File file : selectedFiles) {
        if (isValidFile(file)) {
          uploadedFiles.add(file);
          addFilePreview(file);
        }
      }
      showNotification("Files uploaded successfully!", "success");
    }
  }

  private void addFilePreview(File file) {
    if (attachmentsContainer != null) {
      HBox filePreview = new HBox(10);
      filePreview.setStyle("-fx-background-color: #4A3F2E; -fx-background-radius: 8; -fx-padding: 8;");

      ImageView fileIcon = new ImageView();
      fileIcon.setFitHeight(24);
      fileIcon.setFitWidth(24);

      // Set appropriate icon based on file type
      String fileName = file.getName().toLowerCase();
      if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
          fileName.endsWith(".png") || fileName.endsWith(".gif")) {
        fileIcon.setImage(new Image(getClass().getResourceAsStream("/com/cera/assets/add.png")));
      } else if (fileName.endsWith(".mp4") || fileName.endsWith(".avi")) {
        fileIcon.setImage(new Image(getClass().getResourceAsStream("/com/cera/assets/camera.png")));
      } else {
        fileIcon.setImage(new Image(getClass().getResourceAsStream("/com/cera/assets/add.png")));
      }

      Label fileNameLabel = new Label(file.getName());
      fileNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

      Button removeBtn = new Button("×");
      removeBtn.setStyle("-fx-background-color: #FF4444; -fx-text-fill: white; -fx-font-weight: bold;");
      removeBtn.setOnAction(e -> {
        uploadedFiles.remove(file);
        attachmentsContainer.getChildren().remove(filePreview);
      });

      filePreview.getChildren().addAll(fileIcon, fileNameLabel, removeBtn);
      attachmentsContainer.getChildren().add(filePreview);

      // Add animation
      FadeTransition fadeIn = new FadeTransition(Duration.millis(300), filePreview);
      fadeIn.setFromValue(0);
      fadeIn.setToValue(1);
      fadeIn.play();
    }
  }

  private boolean validateForm() {
    boolean isValid = selectedCategory != null &&
        locationCombo.getValue() != null &&
        !descriptionArea.getText().trim().isEmpty();

    if (submitBtn != null) {
      submitBtn.setDisable(!isValid);
      if (isValid) {
        submitBtn.setStyle(
            "-fx-background-color: #F5D77C; -fx-text-fill: #332C22; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-pref-width: 120; -fx-pref-height: 44;");
      } else {
        submitBtn.setStyle(
            "-fx-background-color: #666666; -fx-text-fill: #999999; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-pref-width: 120; -fx-pref-height: 44;");
      }
    }
    return isValid;
  }

  private void showNotification(String message, String type) {
    if (statusLabel != null) {
      statusLabel.setText(message);
      statusLabel.setVisible(true);

      // Set color based on type
      switch (type) {
        case "success":
          statusLabel.setStyle("-fx-text-fill: #44FF44; -fx-font-weight: bold;");
          break;
        case "error":
          statusLabel.setStyle("-fx-text-fill: #FF4444; -fx-font-weight: bold;");
          break;
        case "info":
          statusLabel.setStyle("-fx-text-fill: #6ED1E7; -fx-font-weight: bold;");
          break;
      }

      // Auto-hide after 3 seconds
      FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), statusLabel);
      fadeOut.setFromValue(1.0);
      fadeOut.setToValue(0.0);
      fadeOut.setOnFinished(e -> statusLabel.setVisible(false));
      fadeOut.play();
    }
  }

  @FXML
  private void handleCategory(javafx.event.ActionEvent event) {
    Button btn = (Button) event.getSource();

    // Extract the label text from the button's graphic (VBox -> Label)
    if (btn.getGraphic() instanceof VBox) {
      VBox vbox = (VBox) btn.getGraphic();
      for (Node node : vbox.getChildren()) {
        if (node instanceof Label) {
          selectedCategory = ((Label) node).getText();
          break;
        }
      }
    } else {
      selectedCategory = btn.getText();
    }

    // Visual feedback for selection - same as reportWithId
    resetButtonStyles();
    btn.setStyle(
        "-fx-background-color: #F5D77C; -fx-background-radius: 12; -fx-text-fill: #332C22; -fx-font-weight: bold;");

    validateForm();
    showNotification("Category selected: " + selectedCategory, "info");
  }

  private void resetButtonStyles() {
    Button[] buttons = { securityBtn, theftBtn, harassmentBtn, abuseBtn, bullyBtn, fraudBtn, healthBtn, otherBtn };
    for (Button button : buttons) {
      if (button != null) {
        button.setStyle(
            "-fx-background-color: #2F2920; -fx-background-radius: 12; -fx-text-fill: white; -fx-font-weight: bold;");
      }
    }
  }

  @FXML
  private void handleSubmit() {
    if (!validateForm()) {
      showNotification("Please fill in all required fields", "error");
      return;
    }
    String category = selectedCategory;
    String location = locationCombo.getValue();
    String description = descriptionArea.getText();
    // Save report (anonymous)
    boolean reportSaved = ReportDAO.createReport(null, category, location, description, true);
    if (reportSaved) {
      // Get the latest report ID (assume it's the last inserted row)
      int reportId = getLastInsertedReportId();
      // Save attachments
      for (File file : uploadedFiles) {
        AttachmentDAO.addAttachment(reportId, file.getAbsolutePath(), getFileType(file));
      }
      showNotification("Report submitted successfully!", "success");
      showSuccessDialog();
    } else {
      showNotification("Failed to submit report", "error");
    }
  }

  private int getLastInsertedReportId() {
    // Simple way: fetch all reports and get the first (most recent)
    java.util.List<ReportDAO.Report> reports = ReportDAO.getAllReports();
    return reports.isEmpty() ? -1 : reports.get(0).id;
  }

  private String getFileType(File file) {
    String name = file.getName().toLowerCase();
    if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif"))
      return "image";
    if (name.endsWith(".mp4") || name.endsWith(".avi"))
      return "video";
    if (name.endsWith(".mp3") || name.endsWith(".wav"))
      return "audio";
    if (name.endsWith(".pdf") || name.endsWith(".doc") || name.endsWith(".docx") || name.endsWith(".txt"))
      return "document";
    return "other";
  }

  private void showSuccessDialog() {
    // Create a simple but styled success dialog
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Success");
    alert.setHeaderText(null);
    alert.setContentText("Thank You!\nYour report has been successfully submitted");

    // Style the dialog
    alert.getDialogPane().setStyle("-fx-background-color: #332C22; -fx-background-radius: 20;");

    // Style the content label
    javafx.scene.Node contentLabel = alert.getDialogPane().lookup(".content.label");
    if (contentLabel != null) {
      contentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-alignment: center;");
    }

    // Style the OK button
    javafx.scene.Node okButton = alert.getDialogPane().lookupButton(ButtonType.OK);
    if (okButton != null) {
      okButton.setStyle(
          "-fx-background-color: #F5D77C; -fx-text-fill: #332C22; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-pref-width: 100; -fx-pref-height: 40;");
    }

    alert.showAndWait();

    // Return to home after success
    try {
      App.setRoot("home");
    } catch (IOException e) {
      System.err.println("Failed to navigate to home after success: " + e.getMessage());
    }
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

  @FXML
  private void handleCancel() {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Cancel Report");
    alert.setHeaderText("Are you sure you want to cancel?");
    alert.setContentText("All entered data will be lost.");

    alert.showAndWait().ifPresent(response -> {
      if (response == javafx.scene.control.ButtonType.OK) {
        try {
          App.setRoot("home");
        } catch (IOException e) {
          System.err.println("Failed to navigate to home after cancel: " + e.getMessage());
        }
      }
    });
  }

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
