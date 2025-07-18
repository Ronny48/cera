package com.cera.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.cera.App;
import java.io.IOException;
import com.cera.UserDAO;

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
      System.out.println("Login successful for user: " + username);
      try {
        if ("admin".equals(role)) {
          App.setRoot("adminPage");
        } else {
          App.setRoot("home");
        }
      } catch (IOException e) {
        statusLabel.setText("Failed to load page");
        e.printStackTrace();
      }
    } else {
      statusLabel.setText("Invalid email or password");
      System.out.println("Login failed for user: " + username);
    }
  }

  @FXML
  private void handleSignup() {
    try {
      App.setRoot("signup");
    } catch (IOException e) {
      statusLabel.setText("Failed to load signup page");
      e.printStackTrace();
    }
  }
}
