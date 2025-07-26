package com.cera.controller;

import java.io.IOException;
import com.cera.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Controller for the home page
 * 
 * Handles navigation between different sections of the application
 * and user logout functionality.
 * 
 * @author CERA Development Team
 * @version 1.0
 */
public class HomeController {

  /**
   * Navigates to the anonymous report form
   * 
   * @param event Action event
   */
  @FXML
  private void handleReportAnon(ActionEvent event) {
    try {
      App.setRoot("reportForm");
    } catch (IOException e) {
      System.err.println("Failed to load report form: " + e.getMessage());
    }
  }

  /**
   * Navigates to the identified report form
   * 
   * @param event Action event
   */
  @FXML
  private void handleReportWithId(ActionEvent event) {
    try {
      App.setRoot("reportWithId");
    } catch (IOException e) {
      System.err.println("Failed to load report with ID form: " + e.getMessage());
    }
  }

  /**
   * Navigates to the reports view page
   * 
   * @param event Action event
   */
  @FXML
  private void handleViewReports(ActionEvent event) {
    try {
      App.setRoot("viewReports");
    } catch (IOException e) {
      System.err.println("Failed to load view reports page: " + e.getMessage());
    }
  }

  /**
   * Handles user logout with confirmation dialog
   */
  @FXML
  private void handleAvatarClick() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to logout?", ButtonType.YES, ButtonType.NO);
    alert.setTitle("Logout");
    alert.setHeaderText(null);
    alert.showAndWait().ifPresent(response -> {
      if (response == ButtonType.YES) {
        App.setCurrentUserId(null);
        try {
          App.setRoot("logIn");
        } catch (IOException e) {
          System.err.println("Failed to logout: " + e.getMessage());
        }
      }
    });
  }

  /**
   * Navigates back to the home page
   */
  @FXML
  private void goHome() {
    try {
      App.setRoot("home");
    } catch (IOException e) {
      System.err.println("Failed to navigate to home: " + e.getMessage());
    }
  }
}