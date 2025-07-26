package com.cera.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.cera.App;
import java.io.IOException;
import com.cera.UserDAO;

/**
 * Controller for the login page
 * 
 * Handles user authentication and navigation to signup page.
 * Validates user credentials and manages user session.
 * 
 * @author CERA Development Team
 * @version 1.0
 */
public class LogInController {

  @FXML
  private TextField usernameField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Button loginButton;

  @FXML
  private Button signup;

  @FXML
  private Label statusLabel;

  /**
   * Handles user login attempt
   * Validates credentials and navigates to appropriate page based on user role
   */
  @FXML
  private void handleLogin() {
    String username = usernameField.getText();
    String password = passwordField.getText();

    if (username.isEmpty() || password.isEmpty()) {
      statusLabel.setText("Please enter both username and password");
      return;
    }

    String role = UserDAO.validateLoginAndGetRole(username, password);
    if (role != null) {
      Integer userId = UserDAO.getUserIdByEmail(username);
      App.setCurrentUserId(userId);
      App.setCurrentUserRole(role);
      statusLabel.setText("Login successful");

      try {
        if ("admin".equals(role)) {
          App.setRoot("adminPage");
        } else {
          App.setRoot("home");
        }
      } catch (IOException e) {
        statusLabel.setText("Failed to load page");
        System.err.println("Failed to navigate after login: " + e.getMessage());
      }
    } else {
      statusLabel.setText("Invalid email or password");
    }
  }

  /**
   * Navigates to the signup page
   */
  @FXML
  private void handleSignup() {
    try {
      App.setRoot("signup");
    } catch (IOException e) {
      statusLabel.setText("Failed to load signup page");
      System.err.println("Failed to load signup page: " + e.getMessage());
    }
  }
}
