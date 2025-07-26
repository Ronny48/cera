package com.cera.controller;

import com.cera.ReportDAO;
import com.cera.AttachmentDAO;
import com.cera.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.List;

/**
 * Controller for the admin dashboard page
 * 
 * Handles display and management of unresolved incident reports.
 * Provides functionality for admins to view reports and mark them as resolved.
 * 
 * @author CERA Development Team
 * @version 1.0
 */
public class AdminPageController {

  @FXML
  private TableView<ReportDAO.Report> reportsTable;

  @FXML
  private TableColumn<ReportDAO.Report, String> categoryCol;

  @FXML
  private TableColumn<ReportDAO.Report, String> locationCol;

  @FXML
  private TableColumn<ReportDAO.Report, String> descriptionCol;

  @FXML
  private TableColumn<ReportDAO.Report, String> attachmentsCol;

  @FXML
  private TableColumn<ReportDAO.Report, String> createdAtCol;

  @FXML
  private TableColumn<ReportDAO.Report, Void> actionCol;

  /**
   * Initializes the admin dashboard
   * Sets up table columns and loads unresolved reports
   */
  @FXML
  public void initialize() {
    // Configure table columns
    categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
    locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
    descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
    createdAtCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

    // Configure attachments column to show file names
    attachmentsCol.setCellValueFactory(cellData -> {
      int reportId = cellData.getValue().id;
      List<AttachmentDAO.Attachment> attachments = AttachmentDAO.getAttachmentsByReportId(reportId);
      StringBuilder sb = new StringBuilder();
      for (AttachmentDAO.Attachment att : attachments) {
        sb.append(att.filePath.substring(att.filePath.lastIndexOf("/") + 1)).append(", ");
      }
      String result = sb.toString();
      if (result.endsWith(", ")) {
        result = result.substring(0, result.length() - 2);
      }
      return new javafx.beans.property.SimpleStringProperty(result);
    });

    addActionButtonToTable();
    loadReports();
  }

  /**
   * Loads unresolved reports into the table
   */
  private void loadReports() {
    List<ReportDAO.Report> reports = ReportDAO.getUnresolvedReports();
    ObservableList<ReportDAO.Report> data = FXCollections.observableArrayList(reports);
    reportsTable.setItems(data);
  }

  /**
   * Adds action buttons to the table for marking reports as resolved
   */
  private void addActionButtonToTable() {
    actionCol.setCellFactory(col -> new TableCell<>() {
      private final Button resolveBtn = new Button("Mark as Resolved");

      {
        resolveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 6;");
        resolveBtn.setOnAction((ActionEvent event) -> {
          ReportDAO.Report report = getTableView().getItems().get(getIndex());
          boolean success = ReportDAO.markReportResolved(report.id);
          if (success) {
            getTableView().getItems().remove(report);
            showAlert("Success", "Report marked as resolved.", AlertType.INFORMATION);
          } else {
            showAlert("Error", "Failed to mark report as resolved.", AlertType.ERROR);
          }
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null);
        } else {
          setGraphic(resolveBtn);
        }
      }
    });
  }

  /**
   * Shows an alert dialog with the specified title, content, and type
   * 
   * @param title   Alert title
   * @param content Alert content
   * @param type    Alert type
   */
  private void showAlert(String title, String content, AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }

  /**
   * Handles admin logout
   * 
   * @param event Action event
   */
  @FXML
  private void handleLogout(ActionEvent event) {
    App.setCurrentUserId(null);
    try {
      App.setRoot("logIn");
    } catch (IOException e) {
      showAlert("Error", "Failed to logout.", AlertType.ERROR);
      System.err.println("Failed to logout: " + e.getMessage());
    }
  }
}