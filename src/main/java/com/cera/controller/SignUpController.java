package com.cera.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import com.cera.App;
import java.io.IOException;
import com.cera.UserDAO;

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
    String firstName = firstNameField.getText();
    String otherName = otherNameField.getText();
    String surname = surnameField.getText();
    String email = emailField.getText();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();

    if (firstName.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()
        || confirmPassword.isEmpty()) {
      System.out.println("Please fill in all required fields");
      return;
    }
    if (!password.equals(confirmPassword)) {
      System.out.println("Passwords do not match");
      return;
    }
    if (UserDAO.userExists(email)) {
      System.out.println("Email already registered");
      return;
    }
    boolean success = UserDAO.createUser(firstName, otherName, surname, email, password, "user");
    if (success) {
      Integer userId = UserDAO.getUserIdByEmail(email);
      App.setCurrentUserId(userId);
      System.out.println("Signup successful for: " + email);
      try {
        App.setRoot("home");
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("Signup failed");
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
