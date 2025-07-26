package com.cera;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * CERA (Campus Emergency Reporting App) - Main Application Class
 * 
 * This is the main entry point for the JavaFX application. It handles:
 * - Application initialization and startup
 * - Database setup
 * - User session management
 * - Scene navigation
 * 
 * @author CERA Development Team
 * @version 1.0
 */
public class App extends Application {

    private static Scene scene;
    private static Integer currentUserId = null;
    private static String currentUserRole = null;

    /**
     * Gets the current user ID
     * 
     * @return The current user ID, or null if no user is logged in
     */
    public static Integer getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Sets the current user ID and clears role if user is null
     * 
     * @param id The user ID to set
     */
    public static void setCurrentUserId(Integer id) {
        currentUserId = id;
        if (id == null) {
            currentUserRole = null;
        }
    }

    /**
     * Gets the current user role
     * 
     * @return The current user role, or null if no user is logged in
     */
    public static String getCurrentUserRole() {
        return currentUserRole;
    }

    /**
     * Sets the current user role
     * 
     * @param role The role to set
     */
    public static void setCurrentUserRole(String role) {
        currentUserRole = role;
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize database tables
        Database.initialize();

        // Set application icon
        try {
            Image icon = new Image(getClass().getResourceAsStream("/com/cera/assets/cera img.jpg"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }

        // Configure and display the main window
        scene = new Scene(loadFXML("logIn"));
        stage.setScene(scene);
        stage.setWidth(430);
        stage.setHeight(700);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Changes the current scene to the specified FXML file
     * 
     * @param fxml The name of the FXML file (without extension)
     * @throws IOException If the FXML file cannot be loaded
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Loads an FXML file and returns the root node
     * 
     * @param fxml The name of the FXML file (without extension)
     * @return The root node of the loaded FXML
     * @throws IOException If the FXML file cannot be loaded
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/cera/views/" + fxml + ".fxml"));

        // Set controller for admin page
        if ("adminPage".equals(fxml)) {
            fxmlLoader.setController(new com.cera.controller.AdminPageController());
        }

        return fxmlLoader.load();
    }

    /**
     * Main method - launches the JavaFX application
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        launch();
    }
}