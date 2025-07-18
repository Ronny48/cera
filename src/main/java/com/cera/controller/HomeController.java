package com.cera.controller;

import java.io.IOException;

import com.cera.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class HomeController {
  // Add logic for home page if needed

  @FXML
  private void handleReportAnon(ActionEvent event) {
    try {
      App.setRoot("reportForm");
      System.out.println("Report form loaded");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleReportWithId(ActionEvent event) {
    try {
      App.setRoot("reportWithId");
      System.out.println("Report with ID form loaded");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleViewReports(ActionEvent event) {
    try {
      App.setRoot("viewReports");
      System.out.println("View Reports page loaded");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleAvatarClick() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to logout?", ButtonType.YES, ButtonType.NO);
    alert.setTitle("Logout");
    alert.setHeaderText(null);
    alert.showAndWait().ifPresent(response -> {
      if (response == ButtonType.YES) {
        com.cera.App.setCurrentUserId(null);
        try {
          com.cera.App.setRoot("logIn");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }

  @FXML
  private void goHome() {
    try {
      com.cera.App.setRoot("home");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}