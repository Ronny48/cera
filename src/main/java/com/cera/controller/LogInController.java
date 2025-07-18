package com.cera.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.cera.App;
import java.io.IOException;

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

    // TODO: Implement actual login logic here
    statusLabel.setText("Login attempted for user: " + username);
    System.out.println("Login attempt - Username: " + username + ", Password: " + password);
    try {
      App.setRoot("home");
    } catch (IOException e) {
      statusLabel.setText("Failed to load home page");
      e.printStackTrace();
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
