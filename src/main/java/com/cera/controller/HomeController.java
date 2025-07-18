package com.cera.controller;

import java.io.IOException;

import com.cera.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

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
}