package com.cera;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database management class for CERA application
 * 
 * Handles SQLite database connection, initialization, and table creation.
 * Uses embedded SQLite database for local data storage.
 * 
 * @author CERA Development Team
 * @version 1.0
 */
public class Database {

  /** SQLite database URL */
  private static final String DB_URL = "jdbc:sqlite:cera.db";

  /**
   * Gets a connection to the SQLite database
   * 
   * @return Database connection
   * @throws SQLException If connection fails
   */
  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(DB_URL);
  }

  /**
   * Initializes the database by creating all necessary tables
   * Creates users, reports, attachments, and live_incidents tables
   * Also inserts default admin user if not exists
   */
  public static void initialize() {
    try (Connection conn = getConnection();
        Statement stmt = conn.createStatement()) {

      // Create users table
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
          "id INTEGER PRIMARY KEY AUTOINCREMENT," +
          "first_name TEXT NOT NULL," +
          "other_name TEXT," +
          "surname TEXT NOT NULL," +
          "email TEXT NOT NULL UNIQUE," +
          "password_hash TEXT NOT NULL," +
          "role TEXT NOT NULL DEFAULT 'user'," +
          "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
          ");");

      // Create reports table
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS reports (" +
          "id INTEGER PRIMARY KEY AUTOINCREMENT," +
          "user_id INTEGER," +
          "category TEXT NOT NULL," +
          "location TEXT NOT NULL," +
          "description TEXT NOT NULL," +
          "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
          "is_anonymous INTEGER NOT NULL," +
          "resolved INTEGER NOT NULL DEFAULT 0," +
          "FOREIGN KEY(user_id) REFERENCES users(id)" +
          ");");

      // Create attachments table
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS attachments (" +
          "id INTEGER PRIMARY KEY AUTOINCREMENT," +
          "report_id INTEGER NOT NULL," +
          "file_path TEXT NOT NULL," +
          "file_type TEXT," +
          "uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
          "FOREIGN KEY(report_id) REFERENCES reports(id)" +
          ");");

      // Create live incidents table for future real-time features
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS live_incidents (" +
          "id INTEGER PRIMARY KEY AUTOINCREMENT," +
          "report_id INTEGER NOT NULL," +
          "file_path TEXT NOT NULL," +
          "recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
          "FOREIGN KEY(report_id) REFERENCES reports(id)" +
          ");");

      // Insert default admin user if not exists
      stmt.executeUpdate("INSERT OR IGNORE INTO users (first_name, other_name, surname, email, password_hash, role) " +
          "VALUES ('Admin', '', 'User', 'admin@cera.com', 'admin123', 'admin');");

    } catch (SQLException e) {
      System.err.println("Database initialization failed: " + e.getMessage());
    }
  }
}