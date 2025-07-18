package com.cera.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.cera.App;

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
import javafx.scene.control.Alert.AlertType;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

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
  @FXML
  private VBox liveIncidentArea;
  @FXML
  private Label recordingStatusLabel;

  private String selectedCategory = null;
  private List<File> attachedFiles = new ArrayList<>();
  private boolean isRecording = false;

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
    // Add drag & drop and click listeners for file upload
    fileUploadArea.setOnMouseClicked(e -> handleFileUpload());
    fileUploadArea.setOnDragOver(this::handleDragOver);
    fileUploadArea.setOnDragDropped(this::handleDragDropped);
    updateAttachmentsDisplay();
    // Live recording simulation
    liveIncidentArea.setOnMouseClicked(e -> handleLiveRecording());
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
    // Visual feedback - highlight selected button
    resetButtonStyles();
    btn.setStyle(
        "-fx-background-color: #F5D77C; -fx-background-radius: 12; -fx-text-fill: #332C22; -fx-font-weight: bold;");
  }

  private void resetButtonStyles() {
    Button[] buttons = { securityBtn, theftBtn, harassmentBtn, abuseBtn, bullyBtn, fraudBtn, healthBtn, otherBtn };
    for (Button button : buttons) {
      button.setStyle(
          "-fx-background-color: #2F2920; -fx-background-radius: 12; -fx-text-fill: white; -fx-font-weight: bold;");
    }
  }

  private void handleFileUpload() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select Files");
    List<File> files = fileChooser.showOpenMultipleDialog(fileUploadArea.getScene().getWindow());
    if (files != null) {
      attachedFiles.addAll(files);
      updateAttachmentsDisplay();
      showStatus("File(s) attached successfully!", false);
    }
  }

  private void handleDragOver(DragEvent event) {
    if (event.getGestureSource() != fileUploadArea && event.getDragboard().hasFiles()) {
      event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
    }
    event.consume();
  }

  private void handleDragDropped(DragEvent event) {
    Dragboard db = event.getDragboard();
    boolean success = false;
    if (db.hasFiles()) {
      attachedFiles.addAll(db.getFiles());
      updateAttachmentsDisplay();
      showStatus("File(s) attached successfully!", false);
      success = true;
    }
    event.setDropCompleted(success);
    event.consume();
  }

  private void updateAttachmentsDisplay() {
    attachmentsContainer.getChildren().clear();
    if (attachedFiles.isEmpty()) {
      attachmentsContainer.setVisible(false);
      return;
    }
    attachmentsContainer.setVisible(true);
    for (File file : attachedFiles) {
      Label fileLabel = new Label(file.getName());
      fileLabel.setStyle("-fx-text-fill: #F5D77C; -fx-font-size: 13px;");
      attachmentsContainer.getChildren().add(fileLabel);
    }
  }

  private void showStatus(String message, boolean error) {
    statusLabel.setText(message);
    statusLabel.setStyle(error ? "-fx-text-fill: #ff5555;" : "-fx-text-fill: #4CAF50;");
    statusLabel.setVisible(true);
    Platform.runLater(() -> {
      try {
        Thread.sleep(1800);
      } catch (InterruptedException ignored) {
      }
      statusLabel.setVisible(false);
    });
  }

  private void handleLiveRecording() {
    if (isRecording) {
      showStatus("Already recording!", true);
      return;
    }
    isRecording = true;
    recordingStatusLabel.setText("Recording...");
    progressIndicator.setVisible(true);
    // Simulate recording for 3 seconds
    new Thread(() -> {
      try {
        Thread.sleep(3000);
      } catch (InterruptedException ignored) {
      }
      Platform.runLater(() -> {
        isRecording = false;
        recordingStatusLabel.setText("Recording saved!");
        progressIndicator.setVisible(false);
        showStatus("Live recording attached!", false);
      });
    }).start();
  }

  @FXML
  private void handleSubmit() {
    String location = locationCombo.getValue();
    String description = descriptionArea.getText();
    if (selectedCategory == null || location == null || location.isEmpty() || description == null
        || description.isEmpty()) {
      showStatus("Please fill all required fields!", true);
      return;
    }
    progressIndicator.setVisible(true);
    // Simulate submission
    new Thread(() -> {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException ignored) {
      }
      Platform.runLater(() -> {
        progressIndicator.setVisible(false);
        showStatus("Report submitted successfully!", false);
        // Show success dialog
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Your report has been submitted successfully!");
        alert.showAndWait();
        try {
          App.setRoot("home");
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    }).start();
  }

  @FXML
  private void handleBack() {
    try {
      App.setRoot("home");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}