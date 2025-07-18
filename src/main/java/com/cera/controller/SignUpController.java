package com.cera.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import com.cera.App;
import java.io.IOException;

public class SignUpController {
  @FXML
  private TextField firstNameField;
  @FXML
  private TextField otherNameField;
  @FXML
  private TextField surnameField;
  @FXML
  private TextField emailField;
  @FXML
  private PasswordField passwordField;
  @FXML
  private PasswordField confirmPasswordField;
  @FXML
  private Button submitButton;

  @FXML
  private void handleSubmit() {
    // TODO: Implement signup logic
    System.out.println("Signup submitted: " +
        firstNameField.getText() + ", " +
        otherNameField.getText() + ", " +
        surnameField.getText() + ", " +
        emailField.getText());
    try {
      App.setRoot("home");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void goBack(ActionEvent event) {
    try {
      App.setRoot("logIn");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
