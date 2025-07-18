package com.cera;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Integer currentUserId = null;
    private static String currentUserRole = null;

    public static Integer getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(Integer id) {
        currentUserId = id;
        if (id == null) {
            currentUserRole = null;
        }
    }

    public static String getCurrentUserRole() {
        return currentUserRole;
    }

    public static void setCurrentUserRole(String role) {
        currentUserRole = role;
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize database
        Database.initialize();
        // Set application icon
        try {
            Image icon = new Image(getClass().getResourceAsStream("/com/cera/assets/cera img.jpg"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }

        scene = new Scene(loadFXML("logIn"));
        stage.setScene(scene);
        stage.setWidth(430);
        stage.setHeight(700);
        stage.setResizable(false);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/cera/views/" + fxml + ".fxml"));
        if ("adminPage".equals(fxml)) {
            fxmlLoader.setController(new com.cera.controller.AdminPageController());
        }
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}