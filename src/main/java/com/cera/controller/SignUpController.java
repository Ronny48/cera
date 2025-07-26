package com.cera.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import com.cera.App;
import java.io.IOException;
import com.cera.UserDAO;

/**
 * Controller for the signup page
 * 
 * Handles user registration and validation of signup form data.
 * Creates new user accounts and manages navigation after successful signup.
 * 
 * @author CERA Development Team
 * @version 1.0
 */
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

  /**
   * Handles user signup form submission
   * Validates form data and creates new user account
   */
  @FXML
  private void handleSubmit() {
    String firstName = firstNameField.getText();
    String otherName = otherNameField.getText();
    String surname = surnameField.getText();
    String email = emailField.getText();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();

    // Validate required fields
    if (firstName.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()
        || confirmPassword.isEmpty()) {
      System.err.println("Please fill in all required fields");
      return;
    }

    // Validate password confirmation
    if (!password.equals(confirmPassword)) {
      System.err.println("Passwords do not match");
      return;
    }

    // Check if user already exists
    if (UserDAO.userExists(email)) {
      System.err.println("Email already registered");
      return;
    }

    // Create new user
    boolean success = UserDAO.createUser(firstName, otherName, surname, email, password, "user");
    if (success) {
      Integer userId = UserDAO.getUserIdByEmail(email);
      App.setCurrentUserId(userId);

      try {
        App.setRoot("home");
      } catch (IOException e) {
        System.err.println("Failed to navigate after signup: " + e.getMessage());
      }
    } else {
      System.err.println("Signup failed");
    }
  }

  /**
   * Navigates back to the login page
   * 
   * @param event Action event
   */
  @FXML
  private void goBack(ActionEvent event) {
    try {
      App.setRoot("logIn");
    } catch (IOException e) {
      System.err.println("Failed to navigate to login: " + e.getMessage());
    }
  }
}
