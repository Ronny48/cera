package com.cera.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controller for the success dialog
 * 
 * Handles the display and interaction with the success dialog
 * that appears after successful report submission.
 * 
 * @author CERA Development Team
 * @version 1.0
 */
public class SuccessDialogController {

  @FXML
  private Button okButton;

  /**
   * Initializes the success dialog
   * Sets up the OK button to close the dialog when clicked
   */
  @FXML
  private void initialize() {
    // Set up the OK button to close the dialog
    okButton.setOnAction(event -> {
      Stage stage = (Stage) okButton.getScene().getWindow();
      stage.close();
    });
  }
}