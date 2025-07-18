package com.cera.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SuccessDialogController {

  @FXML
  private Button okButton;

  @FXML
  private void initialize() {
    // Set up the OK button to close the dialog
    okButton.setOnAction(event -> {
      Stage stage = (Stage) okButton.getScene().getWindow();
      stage.close();
    });
  }
}